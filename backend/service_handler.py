from google.appengine.ext import ndb

import service_helper
import status
import time

from backend_store.main_proto.server_proto import Status
from backend_store.main_proto.server_proto import StatusCode
from backend_store.main_proto.server_proto import *
from backend_store.main_proto.entity_proto import *
from backend_store.model import model
from google.appengine.datastore.datastore_query import Cursor

import uuid
import sys

POST_QUERY_LIMIT = 3
COMMENT_QUERY_LIMIT = 3
POPULAR_POST_QUERY_LIMIT = 10
LOCATION_QUERY_DECIMAL_PLACES = 2

class ServiceHandler(object):

    def handle_create_user(self, request):
        user = request.user
        if self._user_exists(user.username):
            return CreateUserResponse(
                status=Status(status_code=StatusCode.USER_ALREADY_EXISTS))

        model.UserModel(
            id=user.username,
            Username=user.username,
            PhoneNumber=user.phone_number,
            CreationTimestampSec=time.time).put()

        return CreateUserResponse(
            status=Status(status_code=StatusCode.OK))

    def handle_insert_post(self, request):
        post = request.post
        username = post.username
        if not self._user_exists(username):
            return InsertPostResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        ok, lat, lon = self._verify_and_get_latitude_longitude(post.location)
        if not ok:
            return InsertPostResponse(
                status=Status(status_code=StatusCode.NO_LOCATION_PROVIDED))
        # Truncate user location before querying.
        loc_lat, loc_long = self._truncate_latitude_longitude(lat, lon)
        post_id = uuid.uuid4().hex
        timestamp = time.time()

        # Store a version of the location thats only up to 2 decimal places.
        model.PostModel(
            id=post_id,
            PostText=request.post.post_text,
            Username=username,
            OriginalLongitude=lon,
            OriginalLatitude=lat,
            LocationLongitude=loc_long,
            LocationLatitude=loc_lat,
            CreationTimestampSec=timestamp
        ).put()

        post.post_id = post_id
        post.creation_timestamp_sec = timestamp
        return InsertPostResponse(
            status=Status(status_code=StatusCode.OK), posts=[post])

    def handle_update_post(self, request):
        post = request.post
        post_model = ndb.Key('PostModel', post.post_id).get()
        if post_model is None:
            return UpdatePostResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))

        action_type = request.action_type
        ok_status = Status(status_code=StatusCode.OK)
        results = ndb.gql(('SELECT * FROM ActionModel '
                          'WHERE Username = :1 AND '
                          'PostID = :2'), request.user.username, post.post_id)

        if results.count() > 1:
            print 'ERROR: Got %d ActionType query results.' % results.count()
            return UpdatePostResponse(
                status=Status(status_code=StatusCode.INTERNAL_ERROR))

        action_entity = results.get()
        if action_type == ActionType.NO_ACTION:
            if not action_entity is None:
                ndb.Key('ActionModel', action_entity.key.id()).delete()
        elif (action_type == ActionType.LIKE or
            action_type == ActionType.DISLIKE):
            if action_entity is None:
                model.ActionModel(
                    id=uuid.uuid4().hex,
                    Username=request.user.username,
                    PostID=post.post_id,
                    ActionType=str(action_type),
                    CreationTimestampSec=time.time()).put()
            else:
                action_entity.ActionType = str(action_type)
                action_entity.put()
        else:
            print 'Unsupported ActionType: %d ' % action_type
            return UpdatePostResponse(
                status=Status(status_code=StatusCode.UNSUPPORTED_ACTION_TYPE))

        return UpdatePostResponse(status=ok_status)

    def handle_insert_comment(self, request):
        comment = request.comment
        if not self._user_exists(comment.username):
            return InsertCommentResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        if not self._post_exists(comment.post_id):
            return InsertCommentResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))

        comment_id = uuid.uuid4().hex
        timestamp = time.time()
        new_model = model.CommentModel(
            id=comment_id,
            Username=comment.username,
            PostID=comment.post_id,
            CommentText=comment.comment_text,
            CreationTimestampSec=timestamp)
        new_model.put()

        comment.comment_id = comment_id
        comment.creation_timestamp_sec = timestamp
        return InsertCommentResponse(
            status=Status(status_code=StatusCode.OK), comments=[comment])

    def handle_get_all_posts_around_user(self, request):
        before = request.timestamp_before_sec
        after = request.timestamp_after_sec
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsAroundUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        ok, lat, lon = self._verify_and_get_latitude_longitude(user.location)
        if not ok:
            return GetAllPostsAroundUserResponse(
                status=Status(status_code=StatusCode.NO_LOCATION_PROVIDED))
        # Truncate user location before querying.
        loc_lat, loc_long = self._truncate_latitude_longitude(lat, lon)

        query = model.PostModel.query().filter(
            model.PostModel.LocationLatitude == loc_lat).filter(
            model.PostModel.LocationLongitude == loc_long).order(
            -model.PostModel.CreationTimestampSec)
        results, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=POST_QUERY_LIMIT)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPostsAroundUserResponse(
            posts=post_list, query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_by_user(self, request):
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))

        query = model.PostModel.query().filter(
            model.PostModel.Username == user.username).order(
            -model.PostModel.CreationTimestampSec)
        results, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=POST_QUERY_LIMIT)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPostsByUserResponse(
            posts=post_list, query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_commented_on_by_user(self, request):
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsCommentedOnByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        query = ndb.gql(('SELECT DISTINCT PostID FROM CommentModel '
                          'WHERE Username = :1 '
                          'ORDER BY CreationTimestampSec DESC'), user.username)
        comments, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=COMMENT_QUERY_LIMIT)

        results = []
        for comment_model in comments:
            results.append(ndb.Key('PostModel', comment_model.PostID).get())

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPostsCommentedOnByUserResponse(
            posts=post_list, query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_comments_for_post(self, request):
        post = request.post
        if not self._post_exists(post.post_id):
            return GetAllCommentsForPostResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))
        query = ndb.gql(('SELECT * FROM CommentModel '
                          'WHERE PostID = :1 '
                          'ORDER BY CreationTimestampSec'), post.post_id)
        comments, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=COMMENT_QUERY_LIMIT)

        comment_list = []
        helper = service_helper.ServiceHelper
        for comment_model in comments:
            comment_list.append(helper.comment_model_to_proto(comment_model))
        return GetAllCommentsForPostResponse(
            comments=comment_list,
            query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_popular_posts_at_location(self, request):
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPopularPostsAtLocationResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        ok, lat, lon = self._verify_and_get_latitude_longitude(user.location)
        if not ok:
            return GetAllPopularPostsAtLocationResponse(
                status=Status(status_code=StatusCode.NO_LOCATION_PROVIDED))
        # Truncate user location before querying.
        loc_lat, loc_long = self._truncate_latitude_longitude(lat, lon)

        query = ndb.gql(('SELECT * '
                 'FROM PostModel WHERE LocationLatitude = :1 AND '
                 'LocationLongitude = :2 AND PopularityIndex > 0 '
                 'ORDER BY PopularityIndex DESC'),
                 loc_lat, loc_long)
        results, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=POST_QUERY_LIMIT)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPopularPostsAtLocationResponse(
            posts=post_list, status=Status(status_code=StatusCode.OK))

    def handle_get_popular_locations(self, request):
        query = ndb.gql(('SELECT LocationLatitude, LocationLongitude '
                 'FROM LocationModel ORDER BY PopularityIndex DESC'))
        locations = []
        for result in query.fetch(POPULAR_POST_QUERY_LIMIT):
            locations.append(self._latitude_longitude_to_str(
                result.LocationLatitude, result.LocationLongitude))
        return GetPopularLocationsResponse(locations=locations,
            status=Status(status_code=StatusCode.OK))

    def handle_calculate_all_popularity_index(self, request):
        update_key = uuid.uuid4().hex
        # Let t = last x seconds
        # Popularity = likes in t + dislikes in t + comments in t

        time_thresh = 0 # time.time() - (60 * 60)  # Last hour, in sec
        results = model.PostModel.query()
        for post in results:
            id = post.key.id()
            count = model.ActionModel.query(
                model.ActionModel.PostID == id,
                model.ActionModel.CreationTimestampSec > time_thresh).count()
            count += model.CommentModel.query(
                model.CommentModel.PostID == id,
                model.CommentModel.CreationTimestampSec > time_thresh).count()
            post.PopularityIndex = count

            # Add to the LocationModel
            location = self._latitude_longitude_to_str(post.LocationLatitude,
                post.LocationLongitude)
            location_model = ndb.Key('LocationModel', location).get()
            if location_model and location_model.UpdateKey == update_key:
                location_model.PopularityIndex += post.PopularityIndex
                location_model.put()
            elif post.PopularityIndex > 0:
                model.LocationModel(
                    id=str(location),
                    LocationLatitude=post.LocationLatitude,
                    LocationLongitude=post.LocationLongitude,
                    PopularityIndex=post.PopularityIndex,
                    UpdateKey=update_key
                ).put()
            post.put()

        return CalculateAllPopularityIndexResponse(
            status=Status(status_code=StatusCode.OK))

    def _run_query_with_cursor(self, query, params, fetch_limit):
        query_metadata = QueryMetadata()
        results = None
        if params.get_newer:
            if not params.curr_top_cursor_str:
                first_result, top_cursor, _ = query.fetch_page(1)
                results, bottom_cursor, more = query.fetch_page(
                    POST_QUERY_LIMIT-1, start_cursor=top_cursor)
                results = first_result + results
                query_metadata.new_top_cursor_str = top_cursor.urlsafe()
                query_metadata.new_bottom_cursor_str = bottom_cursor.urlsafe()
                query_metadata.has_more_older_data = more
            else:
                cursor = Cursor(urlsafe=params.curr_top_cursor_str)
                results, new_top_cursor, _ = query.fetch_page(
                    POST_QUERY_LIMIT, end_cursor=cursor)
                results = results if len(results) > 1 else []
                query_metadata.new_top_cursor_str = new_top_cursor.urlsafe()
        elif params.get_older:
            cursor = Cursor(urlsafe=params.curr_bottom_cursor_str)
            results, new_bottom_cusor, more = query.fetch_page(
                POST_QUERY_LIMIT, start_cursor=cursor)
            results = results if len(results) > 1 else []
            query_metadata.new_bottom_cursor_str = new_bottom_cusor.urlsafe()
            query_metadata.has_more_older_data = more
        return results, query_metadata

    def _truncate_float(self, float_str, dec_places):
        float_str = '%s.' % (float_str) if not '.' in float_str else float_str
        float_str = '%s%s' % (float_str, '0'*20)
        return float_str[0:float_str.find('.') + 1 + dec_places]

    def _get_post_likes_dislikes_comment_count(self, post_id):
        likes = ndb.gql(('SELECT * FROM ActionModel WHERE PostID = :1 '
                            'AND ActionType = :2'), post_id, 'LIKE').count()
        dislikes = ndb.gql(('SELECT * FROM ActionModel '
                    'WHERE PostID = :1 AND ActionType = :2'),
                    post_id, 'DISLIKE').count()
        number_of_comments = ndb.gql(('SELECT * FROM CommentModel '
                                  'WHERE PostID = :1'), post_id).count()
        return likes, dislikes, number_of_comments

    def _fill_post_likes_dislikes_comment_count(self, post):
        info = self._get_post_likes_dislikes_comment_count(post.post_id)
        post.likes = info[0]
        post.dislikes = info[1]
        post.number_of_comments = info[2]

    def _truncate_latitude_longitude(self, lat, lon):
        dec = LOCATION_QUERY_DECIMAL_PLACES
        return self._truncate_float(lat, dec), self._truncate_float(lon, dec)

    def _verify_and_get_latitude_longitude(self, location):
        if not isinstance(location, basestring) or not ':' in location:
            return False, None, None
        # Latitude should be first, then longitude
        lat_lon = location.split(':')
        # Truncate such that both quanties have the same decimal places.
        dec_places = max(len(lat_lon[1]), len(lat_lon[0]))
        lon = self._truncate_float(lat_lon[1], dec_places)
        lat = self._truncate_float(lat_lon[0], dec_places)
        return True, lat, lon

    def _latitude_longitude_to_str(self, lat, lon):
        return '%s:%s' % (lat, lon)

    def _post_exists(self, post_id):
        return not ndb.Key('PostModel', post_id).get() is None

    def _user_exists(self, username):
        return not ndb.Key('UserModel', username).get() is None

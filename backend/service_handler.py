from google.appengine.ext import ndb

import service_helper
import status
import time

from backend_store.main_proto.server_proto import Status
from backend_store.main_proto.server_proto import StatusCode
from backend_store.main_proto.server_proto import *
from backend_store.main_proto.entity_proto import *
from backend_store.model import model

import uuid

POST_QUERY_LIMIT = 3
COMMENT_QUERY_LIMIT = 3
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

        results = None
        if before is None and after is None:
            results = ndb.gql(('SELECT * FROM PostModel '
                          'WHERE '
                          'LocationLongitude = :1 AND '
                          'LocationLatitude = :2 '
                          'LIMIT %d') % (POST_QUERY_LIMIT),
                          loc_long, loc_lat)
        elif before is not None:
            results = ndb.gql(('SELECT * FROM PostModel '
                          'WHERE '
                          'LocationLongitude = :1 AND '
                          'LocationLatitude = :2 AND '
                          'CreationTimestampSec < :3 '
                          'ORDER BY CreationTimestampSec DESC '
                          'LIMIT %d') % (POST_QUERY_LIMIT),
                          loc_long, loc_lat, before)
        elif after is not None:
            results = ndb.gql(('SELECT * FROM PostModel '
                          'WHERE '
                          'LocationLongitude = :1 AND '
                          'LocationLatitude = :2 AND '
                          'CreationTimestampSec > :3 '
                          'ORDER BY CreationTimestampSec ASC '
                          'LIMIT %d') % (POST_QUERY_LIMIT),
                          loc_long, loc_lat, after)
        else:
            results = ndb.gql(('SELECT * FROM PostModel '
                          'WHERE '
                          'LocationLongitude = :1 AND '
                          'LocationLatitude = :2 AND '
                          'CreationTimestampSec < :3 AND '
                          'CreationTimestampSec > :4 '),
                          loc_long, loc_lat, before, after)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPostsAroundUserResponse(
            posts=post_list, status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_by_user(self, request):
        before = request.timestamp_before_sec
        after = request.timestamp_after_sec
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))

        results = None
        if before is None and after is None:
            results = ndb.gql(('SELECT * FROM PostModel '
                              'WHERE Username = :1 '
                              'LIMIT %d') % (POST_QUERY_LIMIT),
                              user.username)
        elif before is not None:
            results = ndb.gql(('SELECT * FROM PostModel '
                              'WHERE Username = :1 AND '
                              'CreationTimestampSec < :2 '
                              'LIMIT %d') % (POST_QUERY_LIMIT),
                              user.username, before)
        elif after is not None:
            results = ndb.gql(('SELECT * FROM PostModel '
                              'WHERE Username = :1 AND '
                              'CreationTimestampSec > :2 '
                              'LIMIT %d') % (POST_QUERY_LIMIT),
                              user.username, after)
        else:
            results = ndb.gql(('SELECT * FROM PostModel '
                              'WHERE Username = :1 AND '
                              'CreationTimestampSec < :2 AND '
                              'CreationTimestampSec > :3 '
                              'LIMIT %d') % (POST_QUERY_LIMIT),
                              user.username, before, after)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPostsByUserResponse(
            posts=post_list, status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_commented_on_by_user(self, request):
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsCommentedOnByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        comments = ndb.gql(('SELECT * FROM CommentModel '
                          'WHERE Username = :1'), user.username)
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
            posts=post_list, status=Status(status_code=StatusCode.OK))

    def handle_get_all_comments_for_post(self, request):
        post = request.post
        if not self._post_exists(post.post_id):
            return GetAllCommentsForPostResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))
        comments = ndb.gql(('SELECT * FROM CommentModel '
                          'WHERE PostID = :1'), post.post_id)

        comment_list = []
        helper = service_helper.ServiceHelper
        for comment_model in comments:
            comment_list.append(helper.comment_model_to_proto(comment_model))
        return GetAllCommentsForPostResponse(
            comments=comment_list, status=Status(status_code=StatusCode.OK))

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

        results = ndb.gql(('SELECT * '
                 'FROM PostModel WHERE LocationLatitude = :1 AND '
                 'LocationLongitude = :2 AND PopularityIndex > 0 '
                 'ORDER BY PopularityIndex DESC LIMIT 10'),
                 loc_lat, loc_long)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPopularPostsAtLocationResponse(
            posts=post_list, status=Status(status_code=StatusCode.OK))

    def handle_get_popular_locations(self, request):
        results = ndb.gql(('SELECT LocationLatitude, LocationLongitude '
                 'FROM LocationModel ORDER BY PopularityIndex DESC LIMIT 5'))
        locations = []
        for result in results:
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

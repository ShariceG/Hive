from google.appengine.ext import ndb

import geo_helper
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

# Max # of posts to return to a client
POST_QUERY_LIMIT = 3

# Max # of comments to return to a client
COMMENT_QUERY_LIMIT = 10000

# Max # of popular posts to return to a client
POPULAR_POST_QUERY_LIMIT = 10

# Number of decimal places to use to match users in the same location
LOCATION_QUERY_DECIMAL_PLACES = 2

# How many seconds ago to consider a post for popularity
POPULAR_TIME_THRESHOLD_SEC = 3 * 24 * 60 * 60

class ServiceHandler(object):

    def __init__(self):
        self._helper = service_helper.ServiceHelper

    def handle_create_user(self, request):
        if self._user_exists(request.username):
            return CreateUserResponse(
                status=Status(status_code=StatusCode.USER_ALREADY_EXISTS))

        model.UserModel(
            id=request.username,
            Username=request.username,
            PhoneNumber=request.phone_number,
            CreationTimestampSec=time.time()).put()
        return CreateUserResponse(
            status=Status(status_code=StatusCode.OK))

    def handle_insert_post(self, request):
        username = request.username
        location = request.location
        if not self._user_exists(username):
            return InsertPostResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        # TODO: validate post
        ok, location = self._validate_and_get_location_with_precision(location)
        if not ok:
            return InsertPostResponse(
                status=Status(status_code=StatusCode.INVALID_LOCATION))
        # Truncate user location before putting in database.
        lat = location.latitude
        lon = location.longitude
        area_lat, area_long = self._get_area_latitude_longitude(lat, lon)

        # Get reverse geo area
        location.area = geo_helper.geo_to_area(area_lat, area_long)

        post_id = uuid.uuid4().hex
        timestamp = time.time()

        model.PostModel(
            id=post_id,
            PostText=request.post_text,
            Username=username,
            Location=model.Location(
                Longitude=lon,
                Latitude=lat,
                Area=model.Area(
                    Latitude=area_lat,
                    Longitude=area_long,
                    City=location.area.city,
                    State=location.area.state,
                    Country=location.area.country
                )
            ),
            CreationTimestampSec=timestamp
        ).put()

        post = entity_proto.Post()
        post.post_text = request.post_text
        post.username = username
        post.location = location
        post.post_id = post_id
        post.creation_timestamp_sec = timestamp

        return InsertPostResponse(
            status=Status(status_code=StatusCode.OK), posts=[post])

    def handle_update_post(self, request):
        post_model = ndb.Key('PostModel', request.post_id).get()
        if post_model is None:
            return UpdatePostResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))

        action_type = request.action_type
        ok_status = Status(status_code=StatusCode.OK)
        results = ndb.gql(('SELECT * FROM ActionModel '
                          'WHERE Username = :1 AND '
                          'PostID = :2'),
                          request.username, request.post_id)

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
                    Username=request.username,
                    PostID=request.post_id,
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

    def handle_update_comment(self, request):
        if ndb.Key('CommentModel', request.comment_id).get() is None:
            return UpdateCommentResponse(
                status=Status(status_code=StatusCode.COMMENT_NOT_FOUND))

        action_type = request.action_type
        ok_status = Status(status_code=StatusCode.OK)
        results = ndb.gql(('SELECT * FROM ActionModel '
                          'WHERE Username = :1 AND '
                          'CommentID = :2'),
                          request.username, request.comment_id)

        if results.count() > 1:
            print 'ERROR: Got %d ActionType query results.' % results.count()
            return UpdateCommentResponse(
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
                    Username=request.username,
                    CommentID=request.comment_id,
                    ActionType=str(action_type),
                    CreationTimestampSec=time.time()).put()
            else:
                action_entity.ActionType = str(action_type)
                action_entity.put()
        else:
            print 'Unsupported ActionType: %d ' % action_type
            return UpdateCommentResponse(
                status=Status(status_code=StatusCode.UNSUPPORTED_ACTION_TYPE))
        return UpdateCommentResponse(status=ok_status)

    def handle_insert_comment(self, request):
        if not self._user_exists(request.username):
            return InsertCommentResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        if not self._post_exists(request.post_id):
            return InsertCommentResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))

        comment_id = uuid.uuid4().hex
        timestamp = time.time()
        new_model = model.CommentModel(
            id=comment_id,
            Username=request.username,
            PostID=request.post_id,
            CommentText=request.comment_text,
            CreationTimestampSec=timestamp)
        new_model.put()

        comment = entity_proto.Comment()
        comment.username = request.username
        comment.post_id = request.post_id
        comment.comment_text = request.comment_text
        comment.comment_id = comment_id
        comment.creation_timestamp_sec = timestamp
        return InsertCommentResponse(
            status=Status(status_code=StatusCode.OK), comments=[comment])

    def handle_get_all_post_locations(self, request):
        query = ndb.gql(('SELECT * FROM LocationModel'))
        locations = []
        for result in query.fetch():
            locations.append(self._helper.location_model_to_proto(
                result.Location))
        return GetAllPostLocationsResponse(locations=locations,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_by_user(self, request):
        if not self._user_exists(request.username):
            return GetAllPostsByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))

        query = model.PostModel.query().filter(
            model.PostModel.Username == request.username)
        results, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=POST_QUERY_LIMIT,
            order_property=model.PostModel.CreationTimestampSec)

        post_list = []
        for post_model in results:
            the_post = self._helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            self._fill_post_requesting_user_action(request.username, the_post)
            post_list.append(the_post)
        return GetAllPostsByUserResponse(
            posts=post_list, query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_commented_on_by_user(self, request):
        if not self._user_exists(request.username):
            return GetAllPostsCommentedOnByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        query = ndb.gql(('SELECT DISTINCT PostID FROM CommentModel '
                          'WHERE Username = :1'), request.username)
        comments, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=COMMENT_QUERY_LIMIT,
            order_property=model.CommentModel.CreationTimestampSec)

        results = []
        for comment_model in comments:
            results.append(ndb.Key('PostModel', comment_model.PostID).get())

        post_list = []
        for post_model in results:
            the_post = self._helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            self._fill_post_requesting_user_action(request.username, the_post)
            post_list.append(the_post)
        return GetAllPostsCommentedOnByUserResponse(
            posts=post_list, query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_comments_for_post(self, request):
        if not self._post_exists(request.post_id):
            return GetAllCommentsForPostResponse(
                status=Status(status_code=StatusCode.POST_NOT_FOUND))
        query = ndb.gql(('SELECT * FROM CommentModel '
                          'WHERE PostID = :1 '), request.post_id)
        comments, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=COMMENT_QUERY_LIMIT,
            order_property=model.CommentModel.CreationTimestampSec)

        comment_list = []
        for comment_model in comments:
            the_comment = self._helper.comment_model_to_proto(
                comment_model)
            self._fill_comment_likes_dislikes(the_comment)
            self._fill_comment_requesting_user_action(request.username,
                the_comment)
            comment_list.append(the_comment)

        return GetAllCommentsForPostResponse(
            comments=comment_list,
            query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_at_location(self, request):
        if not self._user_exists(request.username):
            return GetAllPostsAtLocationResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        location = request.location
        query_params = request.query_params
        ok, location = self._validate_and_get_location_with_precision(location)
        if not ok:
            return GetAllPostsAtLocationResponse(
                status=Status(status_code=StatusCode.INVALID_LOCATION))

        lat = location.latitude
        lon = location.longitude
        # Truncate user location before querying.
        area_lat, area_long = self._get_area_latitude_longitude(lat, lon)

        query = model.PostModel.query().filter(
            model.PostModel.Location.Area.Latitude == area_lat).filter(
            model.PostModel.Location.Area.Longitude == area_long)
        results, metadata = self._run_query_with_cursor(query=query,
            params=query_params, fetch_limit=POST_QUERY_LIMIT,
            order_property=model.PostModel.CreationTimestampSec)

        post_list = []
        for post_model in results:
            the_post = self._helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            self._fill_post_requesting_user_action(request.username, the_post)
            post_list.append(the_post)
        return GetAllPostsAtLocationResponse(
            posts=post_list, query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))


    def handle_get_all_popular_posts_at_location(self, request):
        location = request.location
        if not self._user_exists(request.username):
            return GetAllPopularPostsAtLocationResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        ok, location = self._validate_and_get_location_with_precision(location)
        if not ok:
            return GetAllPopularPostsAtLocationResponse(
                status=Status(status_code=StatusCode.INVALID_LOCATION))

        lat = location.latitude
        lon = location.longitude
        # Truncate user location before querying.
        area_lat, area_long = self._get_area_latitude_longitude(lat, lon)

        query = ndb.gql(('SELECT * '
                 'FROM PostModel WHERE Location.Area.Latitude = :1 AND '
                 'Location.Area.Longitude = :2 AND PopularityIndex > 0 '),
                 area_lat, area_long)
        results, metadata = self._run_query_with_cursor(query=query,
            params=request.query_params, fetch_limit=POST_QUERY_LIMIT,
            order_property=model.PostModel.PopularityIndex)

        post_list = []
        for post_model in results:
            the_post = self._helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPopularPostsAtLocationResponse(
            posts=post_list,
            query_metadata=metadata,
            status=Status(status_code=StatusCode.OK))

    def handle_get_popular_locations(self, request):
        query = ndb.gql(('SELECT * FROM LocationModel '
                         'ORDER BY PopularityIndex DESC'))
        locations = []
        for result in query.fetch(POPULAR_POST_QUERY_LIMIT):
            locations.append(self._helper.location_model_to_proto(
                result.Location))
        return GetPopularLocationsResponse(locations=locations,
            status=Status(status_code=StatusCode.OK))

    def handle_calculate_all_popularity_index(self, request):
        update_key = uuid.uuid4().hex
        # Let t = last x seconds
        # Popularity = likes in t + dislikes in t + comments in t

        time_thresh = time.time() - POPULAR_TIME_THRESHOLD_SEC
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
            location_key = self._get_location_key(post.Location)
            location_model = ndb.Key('LocationModel', location_key).get()
            if location_model and location_model.UpdateKey == update_key:
                location_model.PopularityIndex += post.PopularityIndex
                location_model.put()
            elif post.PopularityIndex > 0:
                model.LocationModel(
                    id=location_key,
                    Location=post.Location,
                    PopularityIndex=post.PopularityIndex,
                    UpdateKey=update_key
                ).put()
            post.put()

        # Remove locations with previous update key.
        ndb.delete_multi(
            model.LocationModel.query(
                model.LocationModel.UpdateKey != update_key).fetch(
                keys_only=True)
        )
        return CalculateAllPopularityIndexResponse(
            status=Status(status_code=StatusCode.OK))

    def _run_query_with_cursor(self, query, params, fetch_limit,
        order_property):
        qm = QueryMetadata()
        results = []
        forward_query = query.order(order_property)  # Sort ascending
        reverse_query = query.order(-order_property)  # Sort descending
        # By default, set new cursors to old cursors, to begin
        qm.new_top_cursor_str = params.curr_top_cursor_str
        qm.new_bottom_cursor_str = params.curr_bottom_cursor_str
        if params.get_newer:
            if not params.curr_top_cursor_str:
                # If we do not get a top cursor from the client, we assume
                # that they want the first fetch_limit results.
                results, bottom_cursor, more = reverse_query.fetch_page(
                    fetch_limit)
                if bottom_cursor:
                    _, top_cursor, _ = forward_query.fetch_page(fetch_limit,
                        start_cursor=bottom_cursor)
                    qm.new_top_cursor_str = top_cursor.urlsafe()
                    qm.new_bottom_cursor_str = bottom_cursor.urlsafe()
                    qm.has_more_older_data = more  # does this make sense??
            else:
                cursor = Cursor(urlsafe=params.curr_top_cursor_str)
                # With a forward_query, the bottom_cursor eventually becomes the
                # new top. So we get the new posts so we can figure out what
                # the new top cursor should be, ignoring the results.
                _, bottom_cursor, _ = forward_query.fetch_page(
                    fetch_limit, start_cursor=cursor)
                if (bottom_cursor and
                    not self._cursors_are_eq(cursor, bottom_cursor)):
                    qm.new_top_cursor_str = bottom_cursor.urlsafe()

                # Then we fetch again, this time from the new top cursor, to the
                # original bottom cursor so we can grab all the posts that we
                # have served the client thus far. This is so that if there are
                # any updates to any posts, the client can get them.
                results, _, _ = reverse_query.fetch_page(
                    page_size=sys.getsizeof(int()),
                    start_cursor=Cursor(urlsafe=qm.new_top_cursor_str),
                    end_cursor=Cursor(urlsafe=params.curr_bottom_cursor_str))
        else:
            # By default, assume we have no older data to serve.
            qm.has_more_older_data = False
            if params.curr_bottom_cursor_str:
                cursor = Cursor(urlsafe=params.curr_bottom_cursor_str)
                results, new_bottom_cursor, more = reverse_query.fetch_page(
                    fetch_limit, start_cursor=cursor)
                # If there are actual results
                if new_bottom_cursor and not self._cursors_are_eq(
                    new_bottom_cursor, cursor):
                    qm.new_bottom_cursor_str = new_bottom_cursor.urlsafe()
                qm.has_more_older_data = more
        return results, qm

    def _cursors_are_eq(self, c1, c2):
        if c1 is None and c2 is None:
            return True
        if c1 is None and not c2 is None:
            return False
        if not c1 is None and c2 is None:
            return False
        return c1.urlsafe() == c2.urlsafe()

    def _truncate_float_str(self, float_str, dec_places):
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

    def _get_comment_likes_dislikes(self, comment_id):
        likes = ndb.gql(('SELECT * FROM ActionModel WHERE CommentID = :1 '
                            'AND ActionType = :2'), comment_id, 'LIKE').count()
        dislikes = ndb.gql(('SELECT * FROM ActionModel '
                    'WHERE CommentID = :1 AND ActionType = :2'),
                    comment_id, 'DISLIKE').count()
        return likes, dislikes

    def _fill_post_likes_dislikes_comment_count(self, post):
        info = self._get_post_likes_dislikes_comment_count(post.post_id)
        post.likes = info[0]
        post.dislikes = info[1]
        post.number_of_comments = info[2]

    def _fill_comment_likes_dislikes(self, comment):
        info = self._get_post_likes_dislikes_comment_count(comment.comment_id)
        comment.likes = info[0]
        comment.dislikes = info[1]

    def _fill_post_requesting_user_action(self, username, post):
        results = ndb.gql(('SELECT * FROM ActionModel '
                                'WHERE Username = :1 AND PostID = :2'),
                                username, post.post_id)
        if results.count() == 0:
            post.user_action_type = ActionType.NO_ACTION
        else:
            action = self._helper.action_model_to_proto(results.get())
            post.user_action_type = action.action_type

    def _fill_comment_requesting_user_action(self, username, comment):
        results = ndb.gql(('SELECT * FROM ActionModel '
                            'WHERE Username = :1 AND CommentID = :2'),
                            username, comment.comment_id)
        if results.count() == 0:
            comment.user_action_type = ActionType.NO_ACTION
        else:
            action = self._helper.action_model_to_proto(results.get())
            comment.user_action_type = action.action_type

    def _get_area_latitude_longitude(self, lat, lon):
        dec = LOCATION_QUERY_DECIMAL_PLACES
        return (self._truncate_float_str(lat, dec),
            self._truncate_float_str(lon, dec))

    def _validate_location(self, location):
        if not location:
            return False
        if location.latitude == "" or location.longitude == "":
            return False
        try:
            float(location.latitude)
            float(location.longitude)
        except ValueError:
            return False
        return True

    def _get_location_with_equal_precision(self, location):
        '''Make sure lat and lon have the same decimal precision.'''
        lat = location.latitude
        lon = location.longitude
        dec_places = max(len(lat), len(lon))
        lat = self._truncate_float_str(lat, dec_places)
        lon = self._truncate_float_str(lon, dec_places)
        location.latitude = lat
        location.longitude = lon
        return location

    def _validate_and_get_location_with_precision(self, location):
        if not self._validate_location(location):
            return False, None
        return True, self._get_location_with_equal_precision(location)

    def _get_location_key(self, location):
        return location.Area.Latitude + ':' + location.Area.Longitude

    def _post_exists(self, post_id):
        return not ndb.Key('PostModel', post_id).get() is None

    def _user_exists(self, username):
        return not ndb.Key('UserModel', username).get() is None

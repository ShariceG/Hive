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

class ServiceHandler(object):

    def handle_create_user(self, request):
        user = request.user
        if self._user_exists(user.username):
            return CreateUserResponse(
                status=Status(status_code=StatusCode.USER_ALREADY_EXISTS))

        model.UserModel(
            id=user.username,
            Username=user.username,
            PhoneNumber=user.phone_number).put()

        return CreateUserResponse(
            status=Status(status_code=StatusCode.OK))

    def handle_insert_post(self, request):
        post = request.post
        username = post.username
        if not self._user_exists(username):
            return InsertPostResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        if post.location is None or not ':' in post.location:
            return InsertPostResponse(
                status=Status(status_code=StatusCode.NO_LOCATION_PROVIDED))

        location = request.post.location.split(':')
        loc_long = location[0]
        loc_lat = location[1]
        # Truncate such that both quanties have the same decimal places.
        dec_places = max(len(loc_long), len(loc_lat))
        loc_long = self._truncate_float(loc_long, dec_places)
        loc_lat = self._truncate_float(loc_lat, dec_places)
        post_id = uuid.uuid4().hex

        # Store a version of the location thats only up to 2 decimal places.
        model.PostModel(
            id=post_id,
            PostText=request.post.post_text,
            Username=username,
            OriginalLongitude=loc_long,
            OriginalLatitude=loc_lat,
            LocationLongitude=self._truncate_float(loc_long, 2),
            LocationLatitude=self._truncate_float(loc_lat, 2),
            TrendingLongitude=self._truncate_float(loc_long, 1),
            TrendingLatitude=self._truncate_float(loc_lat, 1)).put()

        post.post_id = post_id
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
                    TrendingLongitude=post_model.TrendingLongitude,
                    TrendingLatitude=post_model.TrendingLatitude,
                    CreationTimeSec=time.time()).put()
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
        model.CommentModel(
            id=comment_id,
            Username=comment.username,
            PostID=comment.post_id,
            CommentText=comment.comment_text,
            CreationTimeSec=time.time()).put()

        comment.comment_id = comment_id
        return InsertCommentResponse(
            status=Status(status_code=StatusCode.OK), comments=[comment])

    def handle_get_all_posts_around_user(self, request):
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsAroundUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        if user.location is None or not ':' in user.location:
            return GetAllPostsAroundUserResponse(
                status=Status(status_code=StatusCode.NO_LOCATION_PROVIDED))
        location = user.location.split(':')

        # Truncate user location before querying.
        loc_long = self._truncate_float(location[0], 2)
        loc_lat = self._truncate_float(location[1], 2)
        results = ndb.gql(('SELECT * FROM PostModel '
                          'WHERE '
                          'LocationLongitude = :1 AND '
                          'LocationLatitude = :2'),
                          loc_long, loc_lat)

        post_list = []
        helper = service_helper.ServiceHelper
        for post_model in results:
            the_post = helper.post_model_to_proto(post_model)
            self._fill_post_likes_dislikes_comment_count(the_post)
            post_list.append(the_post)
        return GetAllPostsAroundUserResponse(
            posts=post_list, status=Status(status_code=StatusCode.OK))

    def handle_get_all_posts_by_user(self, request):
        user = request.user
        if not self._user_exists(user.username):
            return GetAllPostsByUserResponse(
                status=Status(status_code=StatusCode.USER_NOT_FOUND))
        results = ndb.gql(('SELECT * FROM PostModel '
                          'WHERE Username = :1'), user.username)

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

    def handle_update_trending_posts(self):
        cut_off_time_sec = time.time() - 1800
        target_long = 98.1
        target_lat = 42.5
        comment_count = ndb.gql(('SELECT * FROM CommentModel '
                          'WHERE '
                          'TrendingLongitude = :1 AND '
                          'TrendingLatitude = :2 AND '
                          'CreationTimeSec > :3'),
                          str(target_long), str(target_lat),
                          cut_off_time_sec).count()
        actions_count = ndb.gql(('SELECT * FROM ActionModel '
                          'WHERE '
                          'TrendingLongitude = :1 AND '
                          'TrendingLatitude = :2 AND '
                          'CreationTimeSec > :3'),
                          str(target_long), str(target_lat),
                          cut_off_time_sec).count()


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

    def _post_exists(self, post_id):
        return not ndb.Key('PostModel', post_id).get() is None

    def _user_exists(self, username):
        return not ndb.Key('UserModel', username).get() is None

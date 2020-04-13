from google.appengine.ext import endpoints
from google.appengine.ext import ndb

from protorpc import remote
from protorpc import message_types

import logging

from backend_store.main_proto import entity_proto
from backend_store.main_proto import server_proto

from backend_store.main_proto.server_proto import Status

from service_handler import ServiceHandler

@endpoints.api(name='media_api', version='v1', description='The API')
class LocationBasedSocialMediaAPI(remote.Service):

    def __init__(self):
        self._handler = ServiceHandler()

    @endpoints.method(server_proto.VerifyCodeRequest,
        server_proto.VerifyCodeResponse, name='verify_code',
        path='app.verify_code', http_method='POST')
    def verify_code(self, request):
        return self._handler.handle_verify_code(request)

    @endpoints.method(server_proto.CreateUserRequest,
        server_proto.CreateUserResponse, name='create_user',
        path='app.create_user', http_method='POST')
    def create_user(self, request):
        return self._handler.handle_create_user(request)

    # @endpoints.method(server_proto.DeleteUserRequest,
    #     server_proto.DeleteUserResponse, name='delete_user',
    #     path='app.delete_user', http_method='POST')
    # def delete_user(self, request):
    #     return server_proto.DeleteUserResponse(
    #         status=Status(is_ok=True, status_message='Deleted.'))

    @endpoints.method(server_proto.InsertPostRequest,
        server_proto.InsertPostResponse, name='insert_post',
        path='app.insert_post', http_method='POST')
    def insert_post(self, request):
        return self._handler.handle_insert_post(request)

    # @endpoints.method(server_proto.DeletePostRequest,
    #     server_proto.DeletePostResponse, name='delete_post',
    #     path='app.delete_post')
    # def delete_post(self, request):
    #     return server_proto.DeletePostResponse()

    @endpoints.method(server_proto.UpdatePostRequest,
        server_proto.UpdatePostResponse, name='update_post',
        path='app.update_post')
    def update_post(self, request):
        return self._handler.handle_update_post(request)

    @endpoints.method(server_proto.UpdateCommentRequest,
        server_proto.UpdateCommentResponse, name='update_comment',
        path='app.update_comment')
    def update_comment(self, request):
        return self._handler.handle_update_comment(request)

    @endpoints.method(server_proto.InsertCommentRequest,
        server_proto.InsertCommentResponse, name='insert_comment',
        path='app.insert_comment', http_method='POST')
    def insert_comment(self, request):
        return self._handler.handle_insert_comment(request)

    @endpoints.method(server_proto.GetAllPostsAtLocationRequest,
        server_proto.GetAllPostsAtLocationResponse,
        name='get_all_posts_at_location',
        path='app.get_all_posts_at_location', http_method='GET')
    def get_all_posts_at_location(self, request):
        return self._handler.handle_get_all_posts_at_location(request)

    @endpoints.method(server_proto.GetAllPostLocationsRequest,
        server_proto.GetAllPostLocationsResponse,
        name='get_all_post_locations',
        path='app.get_all_post_locations', http_method='GET')
    def get_all_post_locations(self, request):
        return self._handler.handle_get_all_post_locations(request)

    @endpoints.method(server_proto.GetAllPostsByUserRequest,
        server_proto.GetAllPostsByUserResponse,
        name='get_all_posts_by_user',
        path='app.get_all_posts_by_user', http_method='GET')
    def get_all_posts_by_user(self, request):
        return self._handler.handle_get_all_posts_by_user(request)

    @endpoints.method(server_proto.GetAllPostsCommentedOnByUserRequest,
        server_proto.GetAllPostsCommentedOnByUserResponse,
        name='get_all_posts_commented_on_by_user',
        path='app.get_all_posts_commented_on_by_user', http_method='GET')
    def get_all_posts_commented_on_by_user(self, request):
        return self._handler.handle_get_all_posts_commented_on_by_user(request)

    @endpoints.method(server_proto.GetAllCommentsForPostRequest,
        server_proto.GetAllCommentsForPostResponse,
        name='get_all_comments_for_post',
        path='app.get_all_comments_for_post', http_method='GET')
    def get_all_comments_for_post(self, request):
        return self._handler.handle_get_all_comments_for_post(request)

    @endpoints.method(server_proto.GetAllPopularPostsAtLocationRequest,
        server_proto.GetAllPopularPostsAtLocationResponse,
        name='get_all_popular_posts_at_location',
        path='app.get_all_popular_posts_at_location', http_method='GET')
    def get_all_popular_posts_at_location(self, request):
        return self._handler.handle_get_all_popular_posts_at_location(request)

    @endpoints.method(server_proto.GetPopularLocationsRequest,
        server_proto.GetPopularLocationsResponse,
        name='get_popular_locations',
        path='app.get_popular_locations', http_method='GET')
    def get_popular_locations(self, request):
        return self._handler.handle_get_popular_locations(request)

    @endpoints.method(server_proto.CalculateAllPopularityIndexRequest,
        server_proto.CalculateAllPopularityIndexResponse,
        name='calculate_all_popularity_index',
        path='app.calculate_all_popularity_index', http_method='POST')
    def calculate_all_popularity_index(self, request):
        return self._handler.handle_calculate_all_popularity_index(request)

endpoints_application = endpoints.api_server([LocationBasedSocialMediaAPI])

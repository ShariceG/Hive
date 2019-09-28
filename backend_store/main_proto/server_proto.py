from protorpc import messages

import entity_proto
from backend.status import Status
from backend.status import StatusCode

'''
    Only need to provide username and phone_number.
    The server will decide the radius.
'''
class CreateUserRequest(messages.Message):
    username = messages.StringField(1, required=False)
    phone_number = messages.StringField(2, required=False)

class CreateUserResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)

# '''
#     Only need to provide the username
# '''
# class DeleteUserRequest(messages.Message):
#     user = messages.MessageField(entity_proto.User, 1, required=False)
#
# class DeleteUserResponse(messages.Message):
#     status = messages.MessageField(Status, 1, required=False)

class InsertPostRequest(messages.Message):
    username = messages.StringField(1, required=False)
    post_text = messages.StringField(2, required=False)
    location = messages.MessageField(entity_proto.Location, 3, required=False)

class InsertPostResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)
    posts = messages.MessageField(entity_proto.Post, 2, repeated=True)

# '''Only need to provide a username for the user and the post id.'''
# class DeletePostRequest(messages.Message):
#     post = messages.MessageField(entity_proto.Post, 2, required=False)
#
# class DeletePostResponse(messages.Message):
#     status = messages.MessageField(Status, 1, required=False)

class UpdatePostRequest(messages.Message):
    post_id = messages.StringField(1, required=False)
    action_type = messages.EnumField(entity_proto.ActionType, 2, required=False)
    username = messages.StringField(3, required=False)

class UpdatePostResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)

class InsertCommentRequest(messages.Message):
    username = messages.StringField(1, required=False)
    post_id = messages.StringField(2, required=False)
    comment_text = messages.StringField(3, required=False)

class InsertCommentResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)
    comments = messages.MessageField(entity_proto.Comment, 2, repeated=True)

class GetAllPostsByUserRequest(messages.Message):
    username = messages.StringField(1, required=False)
    query_params = messages.MessageField(
        entity_proto.QueryParams, 2, required=False)

class GetAllPostsByUserResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)
    query_metadata = messages.MessageField(
        entity_proto.QueryMetadata, 3, required=False)

class GetAllPostsCommentedOnByUserRequest(messages.Message):
    username = messages.StringField(1, required=False)
    query_params = messages.MessageField(
        entity_proto.QueryParams, 2, required=False)

class GetAllPostsCommentedOnByUserResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)
    query_metadata = messages.MessageField(
        entity_proto.QueryMetadata, 3, required=False)

# '''Only need post id'''
# class GetPostRequest(messages.Message):
#     post = messages.MessageField(entity_proto.Post, 1, required=False)
#
# class GetPostResponse(messages.Message):
#     post = messages.MessageField(entity_proto.Post, 1, required=False)

class GetAllCommentsForPostRequest(messages.Message):
    post_id = messages.StringField(1, required=False)
    query_params = messages.MessageField(
        entity_proto.QueryParams, 2, required=False)

class GetAllCommentsForPostResponse(messages.Message):
    comments = messages.MessageField(entity_proto.Comment, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)
    query_metadata = messages.MessageField(
        entity_proto.QueryMetadata, 3, required=False)

class GetAllPopularPostsAtLocationRequest(messages.Message):
    username = messages.StringField(1, required=False)
    location = messages.MessageField(entity_proto.Location, 2, required=False)
    query_params = messages.MessageField(
        entity_proto.QueryParams, 3, required=False)

class GetAllPopularPostsAtLocationResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)
    query_metadata = messages.MessageField(
        entity_proto.QueryMetadata, 3, required=False)

class GetAllPostsAtLocationRequest(messages.Message):
    location = messages.MessageField(entity_proto.Location, 1, required=False)
    query_params = messages.MessageField(
        entity_proto.QueryParams, 2, required=False)

class GetAllPostsAtLocationResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)
    query_metadata = messages.MessageField(
        entity_proto.QueryMetadata, 3, required=False)

class GetAllPostLocationsRequest(messages.Message):
    pass

class GetAllPostLocationsResponse(messages.Message):
    locations = messages.MessageField(entity_proto.Location, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)

class GetPopularLocationsRequest(messages.Message):
    pass

class GetPopularLocationsResponse(messages.Message):
    locations = messages.MessageField(entity_proto.Location, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)

class CalculateAllPopularityIndexRequest(messages.Message):
    pass

class CalculateAllPopularityIndexResponse(messages.Message):
    status = messages.MessageField(Status, 2, required=False)

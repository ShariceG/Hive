from protorpc import messages

import entity_proto
from backend.status import Status
from backend.status import StatusCode

'''
    Only need to provide username and phone_number.
    The server will decide the radius.
'''
class CreateUserRequest(messages.Message):
    user = messages.MessageField(entity_proto.User, 1, required=False)

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

'''
    Only need to provide a username for the user and the text and location
    where the post was made. The location can simply be the current location
    of the user.
'''
class InsertPostRequest(messages.Message):
    post = messages.MessageField(entity_proto.Post, 2, required=False)

class InsertPostResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)
    posts = messages.MessageField(entity_proto.Post, 2, repeated=True)

# '''Only need to provide a username for the user and the post id.'''
# class DeletePostRequest(messages.Message):
#     post = messages.MessageField(entity_proto.Post, 2, required=False)
#
# class DeletePostResponse(messages.Message):
#     status = messages.MessageField(Status, 1, required=False)

'''Only need post id'''
class UpdatePostRequest(messages.Message):
    post = messages.MessageField(entity_proto.Post, 1, required=False)
    action_type = messages.EnumField(entity_proto.ActionType, 2, required=False)
    user = messages.MessageField(entity_proto.User, 3, required=False)

class UpdatePostResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)

'''Need username, post_id and comment_text'''
class InsertCommentRequest(messages.Message):
    comment = messages.MessageField(entity_proto.Comment, 1, required=False)

class InsertCommentResponse(messages.Message):
    status = messages.MessageField(Status, 1, required=False)
    comments = messages.MessageField(entity_proto.Comment, 2, repeated=True)

'''Only need username'''
class GetAllPostsAroundUserRequest(messages.Message):
    user = messages.MessageField(entity_proto.User, 1, required=False)

class GetAllPostsAroundUserResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)

'''Only need username'''
class GetAllPostsByUserRequest(messages.Message):
    user = messages.MessageField(entity_proto.User, 1, required=False)

class GetAllPostsByUserResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)

'''Only need username'''
class GetAllPostsCommentedOnByUserRequest(messages.Message):
    user = messages.MessageField(entity_proto.User, 1, required=False)

class GetAllPostsCommentedOnByUserResponse(messages.Message):
    posts = messages.MessageField(entity_proto.Post, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)

# '''Only need post id'''
# class GetPostRequest(messages.Message):
#     post = messages.MessageField(entity_proto.Post, 1, required=False)
#
# class GetPostResponse(messages.Message):
#     post = messages.MessageField(entity_proto.Post, 1, required=False)

'''Only need post id'''
class GetAllCommentsForPostRequest(messages.Message):
    post = messages.MessageField(entity_proto.Post, 1, required=False)

class GetAllCommentsForPostResponse(messages.Message):
    comments = messages.MessageField(entity_proto.Comment, 1, repeated=True)
    status = messages.MessageField(Status, 2, required=False)

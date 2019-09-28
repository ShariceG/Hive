from backend_store.main_proto import entity_proto
from backend_store.model import model

class ServiceHelper(object):

    @staticmethod
    def user_model_to_proto(user_model):
        return entity_proto.User(
            username=user_model.Username,
            phone_number=user_model.PhoneNumber)

    @staticmethod
    def location_model_to_proto(location_model):
        return entity_proto.Location(
            latitude=location_model.Latitude,
            longitude=location_model.Longitude,
            area=location_model.Area
        )

    @staticmethod
    def post_model_to_proto(post_model):
        return entity_proto.Post(
            post_id=post_model.key.id(),
            username=post_model.Username,
            post_text=post_model.PostText,
            location=ServiceHelper.location_model_to_proto(post_model.Location),
            creation_timestamp_sec=post_model.CreationTimestampSec)
            # likes=post_model.Likes,
            # dislikes=post_model.Dislikes,
            # number_of_comments=post_model.NumbersOfComments)

    @staticmethod
    def comment_model_to_proto(comment_model):
        return entity_proto.Comment(
            comment_id=comment_model.key.id(),
            username=comment_model.Username,
            post_id=comment_model.PostID,
            comment_text=comment_model.CommentText,
            creation_timestamp_sec=comment_model.CreationTimestampSec)

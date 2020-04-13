from protorpc import messages

class User(messages.Message):
    username = messages.StringField(1, required=False)
    phone_number = messages.StringField(2, required=False)
    email = messages.StringField(3, required=False)

class ActionType(messages.Enum):
    NO_ACTION = 0
    LIKE = 1
    DISLIKE = 2
    NUMBER_OF_ACTIONS = 3

class Area(messages.Message):
    # Lat/Lon truncated to the precision of city, state, country.
    longitude = messages.StringField(1, required=False)
    latitude = messages.StringField(2, required=False)
    city = messages.StringField(3, required=False)
    state = messages.StringField(4, required=False)
    country = messages.StringField(5, required=False)

class Location(messages.Message):
    longitude = messages.StringField(1, required=False)
    latitude = messages.StringField(2, required=False)
    area = messages.MessageField(Area, 3)

class Post(messages.Message):
    post_id = messages.StringField(1, required=False)
    # Username of person who wrote the post.
    username = messages.StringField(2, required=False)
    post_text = messages.StringField(3, required=False)
    location = messages.MessageField(Location, 4)
    likes = messages.IntegerField(5, required=False)
    dislikes = messages.IntegerField(6, required=False)
    number_of_comments = messages.IntegerField(7, required=False)
    creation_timestamp_sec = messages.FloatField(8, required=False)
    # The action on the post by the user that requested for the post.
    # This field should only be set when returning posts not inserting them.
    # Yes, this is kind of a hack.
    user_action_type = messages.EnumField(ActionType, 9, required=False)

class Comment(messages.Message):
    comment_id = messages.StringField(1, required=False)
    username = messages.StringField(2, required=False)
    post_id = messages.StringField(3, required=False)
    comment_text = messages.StringField(4, required=False)
    creation_timestamp_sec = messages.FloatField(5, required=False)
    likes = messages.IntegerField(6, required=False)
    dislikes = messages.IntegerField(7, required=False)
    user_action_type = messages.EnumField(ActionType, 8, required=False)

class Action(messages.Message):
    # Username of person who made the action.
    username = messages.StringField(1, required=False)
    post_id = messages.StringField(2, required=False)
    action_type = messages.EnumField(ActionType, 3, required=False);

class QueryParams(messages.Message):
    # If you're not getting newer info, you're getting older info
    get_newer = messages.BooleanField(1, required=False)
    curr_top_cursor_str = messages.StringField(2, required=False)
    curr_bottom_cursor_str = messages.StringField(3, required=False)

class QueryMetadata(messages.Message):
    new_top_cursor_str = messages.StringField(1, required=False)
    new_bottom_cursor_str = messages.StringField(2, required=False)
    has_more_older_data = messages.BooleanField(3, required=False)

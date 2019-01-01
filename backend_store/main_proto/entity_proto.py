from protorpc import messages

class User(messages.Message):
    username = messages.StringField(1, required=False)
    phone_number = messages.StringField(2, required=False)
    # Not stored in datastore. Used mainly for transport to client.
    location = messages.StringField(3, required=False)

class Post(messages.Message):
    post_id = messages.StringField(1, required=False)
    username = messages.StringField(2, required=False)
    post_text = messages.StringField(3, required=False)
    location = messages.StringField(4, required=False)
    likes = messages.IntegerField(5, required=False)
    dislikes = messages.IntegerField(6, required=False)
    number_of_comments = messages.IntegerField(7, required=False)
    creation_timestamp_sec = messages.FloatField(8, required=False)

class Comment(messages.Message):
    comment_id = messages.StringField(1, required=False)
    username = messages.StringField(2, required=False)
    post_id = messages.StringField(3, required=False)
    comment_text = messages.StringField(4, required=False)
    creation_timestamp_sec = messages.FloatField(5, required=False)

class ActionType(messages.Enum):
    NO_ACTION = 0
    LIKE = 1
    DISLIKE = 2
    NUMBER_OF_ACTIONS = 3

class Action(messages.Message):
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

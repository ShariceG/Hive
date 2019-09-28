from protorpc import messages

class StatusCode(messages.Enum):
    OK = 0
    USER_NOT_FOUND = 1
    USER_ALREADY_EXISTS = 2
    POST_NOT_FOUND = 3
    COMMENT_NOT_FOUND = 4
    UNKNOWN_ERROR = 5
    UNSUPPORTED_ACTION_TYPE = 6
    INTERNAL_ERROR = 7
    NO_LOCATION_PROVIDED = 8
    INVALID_LOCATION = 9

class Status(messages.Message):
    is_ok = messages.BooleanField(1, required=False)
    status_code = messages.EnumField(StatusCode, 2, required=False)
    status_message = messages.StringField(3, required=False)

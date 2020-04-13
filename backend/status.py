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
    EMAIL_ALREADY_EXISTS = 10
    FAILED_PRECONDITION = 11
    NOT_FOUND = 12
    USER_MISMATCH_WTH_EMAIL = 13
    USER_MISMATCH_WTH_VERIFICATION_CODE = 14

class Status(messages.Message):
    is_ok = messages.BooleanField(1, required=False)
    status_code = messages.EnumField(StatusCode, 2, required=False)
    status_message = messages.StringField(3, required=False)

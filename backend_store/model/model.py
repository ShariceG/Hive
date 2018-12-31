from google.appengine.ext import ndb

class UserModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PhoneNumber = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class PostModel(ndb.Model):
    PostText = ndb.StringProperty(required=False)
    Username = ndb.StringProperty(required=False)
    # Raw Latitude/Longitude
    OriginalLongitude = ndb.StringProperty(required=False)
    OriginalLatitude = ndb.StringProperty(required=False)
    # Latitude/Longitude truncated
    LocationLongitude = ndb.StringProperty(required=False)
    LocationLatitude = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)
    PopularityIndex = ndb.IntegerProperty(required=False)

class CommentModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PostID = ndb.StringProperty(required=False)
    CommentText = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class ActionModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PostID = ndb.StringProperty(required=False)
    ActionType = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class LocationModel(ndb.Model):
    LocationLongitude = ndb.StringProperty(required=False)
    LocationLatitude = ndb.StringProperty(required=False)
    PopularityIndex = ndb.IntegerProperty(required=False)
    UpdateKey = ndb.StringProperty(required=False)

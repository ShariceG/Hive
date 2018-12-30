from google.appengine.ext import ndb

class UserModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PhoneNumber = ndb.StringProperty(required=False)
    Radius = ndb.IntegerProperty(required=False)
    CreationDT = ndb.DateTimeProperty(required=False, auto_now_add=True)

class PostModel(ndb.Model):
    PostText = ndb.StringProperty(required=False)
    Username = ndb.StringProperty(required=False)
    OriginalLongitude = ndb.StringProperty(required=False)
    OriginalLatitude = ndb.StringProperty(required=False)
    LocationLongitude = ndb.StringProperty(required=False)
    LocationLatitude = ndb.StringProperty(required=False)
    TrendingLongitude = ndb.StringProperty(required=False) # remove
    TrendingLatitude = ndb.StringProperty(required=False) # remove
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
    TrendingLongitude = ndb.StringProperty(required=False) # remove
    TrendingLatitude = ndb.StringProperty(required=False) # remove
    CreationDT = ndb.DateTimeProperty(required=False, auto_now_add=True)
    CreationTimeSec = ndb.FloatProperty(required=False)

class LocationModel(ndb.Model):
    LocationLongitude = ndb.StringProperty(required=False)
    LocationLatitude = ndb.StringProperty(required=False)
    PopularityIndex = ndb.IntegerProperty(required=False)
    UpdateKey = ndb.StringProperty(required=False)

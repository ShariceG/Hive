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
    TrendingLongitude = ndb.StringProperty(required=False)
    TrendingLatitude = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class CommentModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PostID = ndb.StringProperty(required=False)
    CommentText = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class ActionModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PostID = ndb.StringProperty(required=False)
    ActionType = ndb.StringProperty(required=False)
    TrendingLongitude = ndb.StringProperty(required=False)
    TrendingLatitude = ndb.StringProperty(required=False)
    CreationDT = ndb.DateTimeProperty(required=False, auto_now_add=True)
    CreationTimeSec = ndb.FloatProperty(required=False)

class TrendingModel(ndb.Model):
    RegionName = ndb.StringProperty(required=False)
    RegionLongitude = ndb.StringProperty(required=False)
    RegionLatitude = ndb.StringProperty(required=False)
    RegionInteractions = ndb.IntegerProperty(required=False)

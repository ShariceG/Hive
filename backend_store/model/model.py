from google.appengine.ext import ndb

class UserModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PhoneNumber = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class Location(ndb.Model):
    Latitude = ndb.StringProperty(required=False)
    Longitude = ndb.StringProperty(required=False)
    # Truncated lat and long
    AreaLatitude = ndb.StringProperty(required=False)
    AreaLongitude = ndb.StringProperty(required=False)
    # Area, based on reverse geo coordinates
    Area = ndb.StringProperty(required=False)

class PostModel(ndb.Model):
    PostText = ndb.StringProperty(required=False)
    Username = ndb.StringProperty(required=False)
    Location = ndb.StructuredProperty(Location, required=False)
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
    Location = ndb.StructuredProperty(Location, required=False)
    PopularityIndex = ndb.IntegerProperty(required=False)
    UpdateKey = ndb.StringProperty(required=False)

from google.appengine.ext import ndb

class UserModel(ndb.Model):
    Username = ndb.StringProperty(required=False)
    PhoneNumber = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class Area(ndb.Model):
    # Lat/Lon truncated to the precision of city, state, country.
    Latitude = ndb.StringProperty(required=False)
    Longitude = ndb.StringProperty(required=False)
    City = ndb.StringProperty(required=False)
    State = ndb.StringProperty(required=False)
    Country = ndb.StringProperty(required=False)

class Location(ndb.Model):
    # Full precision latitude and longitude
    Latitude = ndb.StringProperty(required=False)
    Longitude = ndb.StringProperty(required=False)
    Area = ndb.StructuredProperty(Area, required=False)

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
    # Either a post id or comment id should be provided but not both.
    PostID = ndb.StringProperty(required=False)
    CommentID = ndb.StringProperty(required=False)
    ActionType = ndb.StringProperty(required=False)
    CreationTimestampSec = ndb.FloatProperty(required=False)

class LocationModel(ndb.Model):
    Location = ndb.StructuredProperty(Location, required=False)
    PopularityIndex = ndb.IntegerProperty(required=False)
    UpdateKey = ndb.StringProperty(required=False)

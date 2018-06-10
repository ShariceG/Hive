import math

MILES_PER_DEGREE_LONG_EQUATOR = 68.703
MILES_PER_DEGREE_LAT = 69.000

class BoundingBox(object):
    def __init__(self, top, left, bottom, right):
        self.top = top
        self.left = left
        self.bottom = bottom
        self.right = right

    def to_string(self):
        return 'Top:%f Right:%f Bottom:%f Left:%f' % (self.top,
            self.right, self.bottom, self.left)

class GeoLocation(object):

    def get_bounding_box(self, deg_long, deg_lat, width):
        left = self.deg_long_minus_miles(deg_long, deg_lat, width/2)
        top = self.deg_lat_plus_miles(deg_lat, width/2)
        bottom = self.deg_lat_minus_miles(deg_lat, width/2)
        right = self.deg_long_plus_miles(deg_long, deg_lat, width/2)
        return BoundingBox(top, left, bottom, right)

    def valid_deg_long(self, deg_long):
        return deg_long >= -180 and deg_long < 180

    def valid_deg_lat(self, deg_lat):
        return deg_lat >= -90 and deg_lat < 90

    def miles_per_deg_long(self, lat_deg):
        lat_rad = math.radians(lat_deg)
        return math.cos(lat_rad) * MILES_PER_DEGREE_LONG_EQUATOR

    def miles_per_deg_lat(self):
        return MILES_PER_DEGREE_LAT

    def deg_lat_minus_miles(self, deg_lat, miles):
        diff = deg_lat - (miles / self.miles_per_deg_lat())
        return self.keep_within_range(diff, -90, 90);

    def deg_lat_plus_miles(self, deg_lat, miles):
        the_sum = deg_lat + (miles / self.miles_per_deg_lat())
        return self.keep_within_range(the_sum, -90, 90)

    def deg_long_minus_miles(self, deg_long, deg_lat, miles):
        diff = deg_long - (miles / self.miles_per_deg_long(deg_lat))
        return self.keep_within_range(diff, -180, 180)

    def deg_long_plus_miles(self, deg_long, deg_lat, miles):
        the_sum = deg_long + (miles / self.miles_per_deg_long(deg_lat))
        return self.keep_within_range(the_sum, -180, 180)

    def keep_within_range(self, value, inclusive_a, exclusive_b):
        length = exclusive_b - inclusive_a
        if value >= inclusive_a and value < exclusive_b:
            return value
        return value + length if value < inclusive_a else value - length

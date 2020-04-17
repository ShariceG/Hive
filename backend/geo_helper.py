import json
import urllib

from backend_store.main_proto import entity_proto
from google.appengine.api import urlfetch

# Yes, the earth is not flat but all of these values and calculations assume
# the earth is, for simplicity.
# How many units of latitude or longitude equate to a distance of 1 mile.
UNITS_PER_MILE = 0.0140

# Distance between post regions in some fraction of a mile.
MIN_UNITS_BETWEEN_POST_REGIONS = UNITS_PER_MILE / 4

MIN_MILES_BETWEEN_POST_REGIONS = MIN_UNITS_BETWEEN_POST_REGIONS / UNITS_PER_MILE

_RAPID_HOST = 'geocodeapi.p.rapidapi.com'
_RAPID_API_KEY = 'a54e0796e8msh2a21bd76558c31ap1c3e17jsn109dd24cb81f'

def _round_x_to_nearest_multiple_of_n(x, n):
    # How many decimal places to round to. This really just has to be reasonably
    # large enough since we're working with fractions.
    precision = 10
    return round(n * round(float(x)/n), precision)

def _round_x_to_nearest_post_region(x):
    n = MIN_UNITS_BETWEEN_POST_REGIONS
    return _round_x_to_nearest_multiple_of_n(x, n)

def get_rounded_lat_lon(lat, lon):
    lat = float(lat)
    lon = float(lon)
    return (_round_x_to_nearest_post_region(lat),
        _round_x_to_nearest_post_region(lon))

def get_regions_n_miles_from_rounded_lat_lon(lat, lon, n):
    lat = float(lat)
    lon = float(lon)
    miles_covered = 0
    regions = [(lat, lon)]
    centers = [(lat, lon)]
    while miles_covered < n:
        new_centers = []
        for center in centers:
            all_regions, new_regions = _generate_neighbors(center, regions)
            new_centers += new_regions
            regions = all_regions
        centers = new_centers
        miles_covered += MIN_MILES_BETWEEN_POST_REGIONS
    return regions

def _generate_neighbors(center, current_regions):
    directions = [
        (0, -1), (0, 1),
        (1, 0), (-1, 0),
        (-1, -1), (1, -1),
        (1, 1), (-1, 1)]
    offset = MIN_UNITS_BETWEEN_POST_REGIONS
    new_regions = []
    all_regions = [] + current_regions
    for d in directions:
        new_lat = center[0] + d[0] * offset
        new_lon = center[1] + d[1] * offset
        new_point = (new_lat, new_lon)
        if  not new_point in current_regions:
            new_regions.append(new_point)
            all_regions.append(new_point)
    return all_regions, new_regions

def geo_to_area(lat, lon):
    lat = str(lat)
    lon = str(lon)
    headers = {
	   "x-rapidapi-host": _RAPID_HOST,
       "x-rapidapi-key": _RAPID_API_KEY
    }
    data = {
        'latitude': str(lat),
        'longitude': str(lon),
        'range': '0'
    }
    data = urllib.urlencode(data)
    url = 'https://' + _RAPID_HOST + '/GetNearestCities?'
    result = urlfetch.fetch(
        url=url + data, headers=headers, method=urlfetch.GET)
    locations = json.loads(result.content)
    locations = sorted(locations, key=lambda k: k['Distance'])

    area = entity_proto.Area(
        latitude=lat,
        longitude=lon,
        city='',
        state='',
        country=''
    )

    if len(locations) > 0:
        area.city = locations[0]['City']
        area.country = locations[0]['Country']

    return area

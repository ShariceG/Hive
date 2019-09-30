import json
import urllib

from backend_store.main_proto import entity_proto
from google.appengine.api import urlfetch

_RAPID_HOST = 'geocodeapi.p.rapidapi.com'
_RAPID_API_KEY = 'a54e0796e8msh2a21bd76558c31ap1c3e17jsn109dd24cb81f'

def geo_to_area(lat, lon):
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

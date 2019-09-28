import json
import urllib
from google.appengine.api import urlfetch

_RAPID_HOST = 'geocodeapi.p.rapidapi.com'
_RAPID_API_KEY = 'a54e0796e8msh2a21bd76558c31ap1c3e17jsn109dd24cb81f'

def geo_to_area(lat, long):
    headers = {
	   "x-rapidapi-host": _RAPID_HOST,
       "x-rapidapi-key": _RAPID_API_KEY
    }
    data = {
        'latitude': str(lat),
        'longitude': str(long),
        'range': '0'
    }
    data = urllib.urlencode(data)
    url = 'https://' + _RAPID_HOST + '/GetNearestCities?'
    result = urlfetch.fetch(
        url=url + data, headers=headers, method=urlfetch.GET)
    locations = json.loads(result.content)
    locations = sorted(locations, key=lambda k: k['Distance'])

    return locations[0]['City']

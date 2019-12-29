package coloredcoded.hive.client;

import org.json.simple.JSONObject;

import java.io.Serializable;

public class Location implements Serializable {
	
	public static class Area implements Serializable {
		private String latStr;
		private String lonStr;
		private String city;
		private String state;
		private String country;
		
		public Area() {
			latStr = "";
			lonStr = "";
			city = "";
			state = "";
			country = "";
		}
		
		public Area(String lat, String lon, String city, String state, String country) {
			latStr = lat;
			lonStr = lon;
			this.city = city;
			this.state = state;
			this.country = country;
		}
		
		public String getLatitude() {
			return latStr;
		}
		
		public String getLongitude() {
			return lonStr;
		}
		
		public String getCity() {
			return city;
		}
		
		public String getState() {
			return state;
		}
		
		public String getCountry() {
			return country;
		}
		
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			json.put("latitude", latStr);
			json.put("longitude", lonStr);
			json.put("city", city);
			json.put("state", state);
			json.put("country", country);
			return json;
		}
	}
	
	/*
	 * This class represents a geographical location.
	 */

	private Area area;  // Always given to us by the server.
	private String latStr;
	private String lonStr;
	
	public Location(String lat, String lon) {
		area = new Area();
		latStr = lat;
		lonStr = lon;
	}

	public Location(String lat, String lon, Area area) {
		this.area = area;
		latStr = lat;
		lonStr = lon;
	}
	
	public Location() {
		area = new Area();
		latStr = "";
		lonStr = "";
	}
	
	public static Location jsonToLocation(JSONObject locationJson) {
		return new Location(locationJson);
	}
	
	private Location(JSONObject locationJson) {
		latStr = (String) locationJson.get("latitude");
		lonStr = (String) locationJson.get("longitude");
		if (locationJson.containsKey("area")) {
			JSONObject areaJson = (JSONObject) locationJson.get("area");
			area = new Area(
					(String) areaJson.get("latitude"),
					(String) areaJson.get("longitude"),
					(String) areaJson.get("city"),
					(String) areaJson.get("state"),
					(String) areaJson.get("country"));
		}
	}

	public Area getArea() {
		return area;
	}

	public String getLatitude() {
		return latStr;
	}

	public String getLongitude() {
		return lonStr;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("latitude", latStr);
		json.put("longitude", lonStr);
		json.put("area", area.toJSON());
		return json;
	}
	
}

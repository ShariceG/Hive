package coloredcoded.hive.client;

import android.location.Location;

import org.json.simple.JSONObject;

import java.io.Serializable;

// Had to name HiveLocation because android already has an object called Location.
public class HiveLocation implements Serializable {
	
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

		public String toString() {
			return city + ", " + country;
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
	 * This class represents a geographical location for the use of this app. latStr and lonStr
	 * are guaranteed to exist. Area does not exist unless this was created via JSONObject since
	 * that information comes from the server.
	 */

	private Area area;  // Always given to us by the server.
	private String latStr;
	private String lonStr;

	public HiveLocation(String lat, String lon) {
		area = new Area();
		latStr = lat;
		lonStr = lon;
	}

	public HiveLocation(String lat, String lon, Area area) {
		this.area = area;
		latStr = lat;
		lonStr = lon;
	}

	public HiveLocation(Location location) {
		this(location.getLatitude()+"", location.getLongitude()+"");
	}
	
	public HiveLocation() {
		area = new Area();
		latStr = "";
		lonStr = "";
	}
	
	public static HiveLocation jsonToLocation(JSONObject locationJson) {
		return new HiveLocation(locationJson);
	}
	
	private HiveLocation(JSONObject locationJson) {
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

	public String toString() {
		return toJSON().toJSONString();
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("latitude", latStr);
		json.put("longitude", lonStr);
		json.put("area", area.toJSON());
		return json;
	}
	
}

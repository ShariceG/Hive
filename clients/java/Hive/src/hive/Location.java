package hive;

import org.json.simple.JSONObject;

public class Location {
	
	/*
	 * This class represents a geographical location.
	 */

	private String area;  // Represents the human readable version of the geo coordinates. Ex: "Las Vegas, NV"
	private String latStr;
	private String lonStr;
	private JSONObject json;
	
	public Location(String lat, String lon) {
		area = "";
		json = new JSONObject();
		latStr = lat;
		lonStr = lon;
	}
	
	public Location() {
		area = "";
		json = new JSONObject();
		latStr = "";
		lonStr = "";
	}
	
	public static Location jsonToLocation(JSONObject locationJson) {
		return new Location(locationJson);
	}
	
	private Location(JSONObject locationJson) {
		json = locationJson;
		latStr = (String) locationJson.get("latitude");
		lonStr = (String) locationJson.get("longitude");
		area = (String) locationJson.get("area");
	}

	public String getArea() {
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
		json.put("area", area);
		return json;
	}
	
}

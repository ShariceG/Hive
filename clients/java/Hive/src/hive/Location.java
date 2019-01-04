package hive;

public class Location {
	
	/*
	 * This class represents a geographical location.
	 */

	private String label;  // Represents the human readable version of the geo coordinates. Ex: "Las Vegas, NV"
	private String locationStr;   // Represents the string format the server expects all geo locations to be in. "lat:lon"
	private String latStr;
	private String lonStr;
	
	public Location() {
		label = "";
		locationStr = "";
		latStr = "";
		lonStr = "";
	}
	
	public static Location jsonToLocation(String locationJsonStr) {
		return new Location(locationJsonStr);
	}
	
	private Location(String locationJsonStr) {
		if (!locationJsonStr.contains(":")) {
			throw new RuntimeException("Invalid location, no ':' -> " + locationJsonStr);
		}
		
		String[] split = locationJsonStr.split(":");
		if (split.length != 2) {
			throw new RuntimeException("Invalid location: " + locationStr);
		}
		
		this.locationStr = locationJsonStr;
		this.label = "";
		this.latStr = split[0];
		this.lonStr = split[1];
	}

	public String getLabel() {
		return label;
	}

	public String getLocationStr() {
		return locationStr;
	}

	public String getLatStr() {
		return latStr;
	}

	public String getLonStr() {
		return lonStr;
	}
	
	public String toString() {
		return locationStr;
	}
	
}

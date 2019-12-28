package coloredcoded.hive.client;

public final class User {
	
	private final String username;
	private final String phoneNumber;

	// Format is "longitude:latitude"
	// This should not be a real-time location. This should only be
	// set when needed (when user is posting and/or retrieving posts).
	// The best thing to do is to only change this field if the user has significantly
	// moved from their location. 
	private String location;
	
	public User(String username, String phoneNumber) {
		this(username, phoneNumber, null);
	}
	
	public User(String username, String phoneNumber, String location) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.location = location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	public String getLocation() {
		return this.location;
	}
}

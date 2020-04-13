package coloredcoded.hive.client;

import android.app.Activity;

import org.json.simple.JSONObject;

import coloredcoded.hive.AppHelper;

public final class User {

	private static String USER_DATA_KEY = "user";

	private final String username;
	private final String email;
	private final boolean isSignUpVerified;

	public User(String username, String email, boolean isSignUpVerified) {
		this.username = username;
		this.email = email;
		this.isSignUpVerified = isSignUpVerified;
	}

	public String getUsername() {
		return this.username;
	}

	public String getEmail() {
		return this.email;
	}

	public boolean isSignUpVerified() {
		return this.isSignUpVerified;
	}

	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("email", email);
		json.put("isSignUpVerified", isSignUpVerified);
		return json.toJSONString();
	}

	public void removeFromInternalStorage(Activity activity) {
		AppHelper.deleteFromInternalStorage(activity, USER_DATA_KEY);
	}

	public void writeToInternalStorage(Activity activity) {
		AppHelper.writeToInternalStorage(activity, USER_DATA_KEY, toJSONString());
	}

	public static boolean isInInternalStorage(Activity activity) {
		return AppHelper.internalStorageContainsKey(activity, USER_DATA_KEY);
	}

	public static User fromJSON(JSONObject json) {
		return new User((String) json.get("username"),
				(String) json.get("email"),
				(boolean) json.get("isSignUpVerified"));
	}

	public static User fromInternalStorage(Activity activity) {
		org.json.simple.JSONObject json = AppHelper.readFromInternalStorageToJSONObject(
				activity, USER_DATA_KEY);
		return User.fromJSON(json);
	}
}

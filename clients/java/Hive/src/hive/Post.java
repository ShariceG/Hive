package hive;

import org.json.simple.JSONObject;

/**
 * 
 * @author
 * This is a class representing a post by a user.
 *
 */
public final class Post {
	
	private static long POST_LIFESPAN_SEC = 24 * 60 * 60;
	
	// Username of whoever made the post
	private String username;
	// Provided by the server. Never supply.
	private String postId;
	private String postText;
	// Location of the user when post was made
	private Location location;
	private int likes;
	private int dislikes;
	private double creationTimestampSec;
	private JSONObject jsonPost;
	
	private Post(String username, String postId, String postText, Location location, int likes, int dislikes, 
			double creationTimestampSec, JSONObject jsonPost) {
		this.username = username;
		this.postText = postText;
		this.postId = postId;
		this.location = location;
		this.likes = likes;
		this.dislikes = dislikes;
		this.creationTimestampSec = creationTimestampSec;
		this.jsonPost = jsonPost;
	}
	
	public double getCreationTimestampSec() {
		return creationTimestampSec;
	}
	
	public long getCreationTimestampSecAsLong() {
		return Double.valueOf(creationTimestampSec).longValue();
	}

	public String getUsername() {
		return username;
	}

	public String getPostId() {
		return postId;
	}

	public String getPostText() {
		return postText;
	}

	public Location getLocation() {
		return location;
	}

	public int getLikes() {
		return likes;
	}

	public int getDislikes() {
		return dislikes;
	}
	
	public boolean isExpired() {
		double currentTimeSec = System.currentTimeMillis() / 1000;
		return creationTimestampSec + POST_LIFESPAN_SEC < currentTimeSec;
	}
	
	public String toString() {
		return jsonPost.toJSONString();
	}
	
	public static Post jsonToPost(JSONObject jsonPost) {
		int likes = jsonPost.get("likes") == null ? 0 : Integer.parseInt((String)jsonPost.get("likes"));
		int dislikes = jsonPost.get("dislikes") == null ? 0 : Integer.parseInt((String)jsonPost.get("dislikes"));
		double timestamp = Double.parseDouble(jsonPost.get("creation_timestamp_sec")+"");
		return new Post((String)jsonPost.get("username"), (String)jsonPost.get("post_id"), (String)jsonPost.get("post_text"), 
				Location.jsonToLocation((String)jsonPost.get("location")), 
				likes, dislikes, timestamp, jsonPost);
	}

}

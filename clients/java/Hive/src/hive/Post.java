package hive;

import org.json.simple.JSONObject;

/**
 * 
 * @author
 * This is a class representing a post by a user. This object may be
 * used as part of a request to the server or as part of a response from
 * the server.
 *
 */
public final class Post {
	
	// Username of whoever made the post
	private String username;
	// Provided by the server. Never supply.
	private String postId;
	private String postText;
	// Location of the user when post was made
	private String location;
	private int likes;
	private int dislikes;
	private JSONObject jsonPost;
	
	public Post(String username, String postId, String postText, String location, int likes, int dislikes) {
		this.username = username;
		this.postText = postText;
		this.postId = postId;
		this.location = location;
		this.likes = likes;
		this.dislikes = dislikes;
	}
	
	public Post(String username, String postId, String postText, String location, int likes, int dislikes, JSONObject jsonPost) {
		this(username, postId, postText, location, likes, dislikes);
		this.jsonPost = jsonPost;
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

	public String getLocation() {
		return location;
	}

	public int getLikes() {
		return likes;
	}

	public int getDislikes() {
		return dislikes;
	}
	
	public String toString() {
		return jsonPost.toJSONString();
	}
	
	public static Post jsonToPost(JSONObject jsonPost) {
		int likes = jsonPost.get("likes") == null ? 0 : Integer.parseInt((String)jsonPost.get("likes"));
		int dislikes = jsonPost.get("dislikes") == null ? 0 : Integer.parseInt((String)jsonPost.get("dislikes"));
		return new Post((String)jsonPost.get("username"), (String)jsonPost.get("post_id"), (String)jsonPost.get("post_text"), 
				(String)jsonPost.get("location"), 
				likes, dislikes, jsonPost);
	}

}

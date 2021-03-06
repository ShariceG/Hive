package coloredcoded.hive.client;

import androidx.annotation.Nullable;

import org.json.simple.JSONObject;

import java.io.Serializable;

/**
 * 
 * @author
 * This is a class representing a post by a user.
 *
 */
public final class Post implements Serializable {
	
	private static long POST_LIFESPAN_SEC = 24 * 60 * 60;
	
	// Username of whoever made the post
	private String username;
	// Provided by the server. Never supply.
	private String postId;
	private String postText;
	// HiveLocation of the user when post was made
	private HiveLocation hiveLocation;
	// Type of action currently on this post made by the user of the phone. Can be mutated.
	// Lets keep the amount of mutable things small.
	private ActionType userActionType;
	private int likes;
	private int dislikes;
	private double creationTimestampSec;
	private JSONObject jsonPost;
	private int numberOfComments;
	
	private Post(JSONObject jsonPost, String username, String postId, String postText,
				 HiveLocation hiveLocation, int likes, int dislikes, double creationTimestampSec,
				 ActionType userActionType, int numberOfComments) {
		this.username = username;
		this.postText = postText;
		this.postId = postId;
		this.hiveLocation = hiveLocation;
		this.likes = likes;
		this.dislikes = dislikes;
		this.creationTimestampSec = creationTimestampSec;
		this.jsonPost = jsonPost;
		this.userActionType = userActionType;
		this.numberOfComments = numberOfComments;
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

	public HiveLocation getHiveLocation() {
		return hiveLocation;
	}

	public int getLikes() {
		return likes;
	}
	public void addLikes(int delta) { likes += delta; }

	public int getDislikes() {
		return dislikes;
	}
	public void addDislikes(int delta) { dislikes += delta; }

	public ActionType getUserActionType() { return userActionType; }
	public void setUserActionType(ActionType actionType) { userActionType = actionType; }

	public int getNumberOfComments() {
		return numberOfComments;
	}
	public void incrementNumberOfComments() {
		numberOfComments++;
	}
	
	public boolean isExpired() {
		double currentTimeSec = System.currentTimeMillis() / 1000;
		return creationTimestampSec + POST_LIFESPAN_SEC < currentTimeSec;
	}
	
	public String toString() {
		return jsonPost.toJSONString();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof Post)) {
			return false;
		}
		Post p = (Post) obj;
		return getPostId().equals(p.getPostId());
	}

	@Override
	public int hashCode() {
		return postId.hashCode();
	}

	public static Post jsonToPost(JSONObject jsonPost) {
		int likes = jsonPost.get("likes") == null ? 0 : Integer.parseInt((String)jsonPost.get("likes"));
		int dislikes = jsonPost.get("dislikes") == null ? 0 : Integer.parseInt((String)jsonPost.get("dislikes"));
		double timestamp = Double.parseDouble(jsonPost.get("creation_timestamp_sec")+"");
		ActionType actionType = jsonPost.get("user_action_type") == null ? ActionType.NO_ACTION
				: ActionType.valueOf((String)jsonPost.get("user_action_type"));
		int numberOfComments = jsonPost.get("number_of_comments") == null ? 0 :
				Integer.parseInt((String) jsonPost.get("number_of_comments"));
		return new Post(jsonPost,
				(String)jsonPost.get("username"),
				(String)jsonPost.get("post_id"),
				(String)jsonPost.get("post_text"),
				HiveLocation.jsonToLocation((JSONObject) jsonPost.get("location")),
				likes, dislikes, timestamp,
				actionType, numberOfComments);
	}

}

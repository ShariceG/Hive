package coloredcoded.hive.client;

import androidx.annotation.Nullable;

import org.json.simple.JSONObject;

public final class Comment {
	
	private String commentId;
	// Username of comment author
	private String username;
	private String postId;
	private String commentText;
	private double creationTimestampSec;
	private JSONObject jsonComment;
	// Type of action currently on this post made by the user of the phone. Can be mutated.
	// Lets keep the amount of mutable things small.
	private ActionType userActionType;
	private int likes;
	private int dislikes;
	
	private Comment(String commentId, String username, String postId, String commentText, 
			double creationTimestampSec, JSONObject jsonComment,
					int likes, int dislikes, ActionType userActionType) {
		this.commentId = commentId;
		this.username = username;
		this.postId = postId;
		this.commentText = commentText;
		this.creationTimestampSec = creationTimestampSec;
		this.jsonComment = jsonComment;
		this.likes = likes;
		this.dislikes = dislikes;
		this.userActionType = userActionType;
	}
	
	public Double getCreationTimestampSec() {
		return creationTimestampSec;
	}
	
	public long getCreationTimestampSecAsLong() {
		return Double.valueOf(creationTimestampSec).longValue();
	}

	public String getCommentId() {
		return commentId;
	}

	public String getUsername() {
		return username;
	}

	public String getPostId() {
		return postId;
	}

	public String getCommentText() {
		return commentText;
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
	
	public String toString() {
		return jsonComment.toJSONString();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (!(obj instanceof Comment)) {
			return false;
		}
		Comment c = (Comment) obj;
		return getCommentId().equals(c.getCommentId());
	}

	@Override
	public int hashCode() {
		return commentId.hashCode();
	}
	
	public static Comment jsonToComment(JSONObject jsonComment) {
		int likes = jsonComment.get("likes") == null ? 0 : Integer.parseInt(
				(String)jsonComment.get("likes"));
		int dislikes = jsonComment.get("dislikes") == null ? 0 : Integer.parseInt(
				(String)jsonComment.get("dislikes"));
		ActionType actionType = jsonComment.get("user_action_type") == null ? ActionType.NO_ACTION
				: ActionType.valueOf((String)jsonComment.get("user_action_type"));
		// Converting to string using toString first then to double just in case for some
		// reason the timestamp doesn't come to us as a double already.
		double timestamp = Double.parseDouble(jsonComment.get("creation_timestamp_sec").toString());
		return new Comment((String)jsonComment.get("comment_id"),
				(String)jsonComment.get("username"),
				(String)jsonComment.get("post_id"),
				(String)jsonComment.get("comment_text"),
				timestamp, jsonComment, likes, dislikes, actionType);
	}
}
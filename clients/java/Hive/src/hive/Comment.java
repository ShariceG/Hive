package hive;

import org.json.simple.JSONObject;

public final class Comment {
	
	private String commentId;
	// Username of comment author
	private String username;
	private String postId;
	private String commentText;
	private JSONObject jsonComment;
	
	public Comment(String commentId, String username, String postId, String commentText) {
		this.commentId = commentId;
		this.username = username;
		this.postId = postId;
		this.commentText = commentText;
	}
	
	public Comment(String commentId, String username, String postId, String commentText, JSONObject jsonComment) {
		this(commentId, username, postId, commentText);
		this.jsonComment = jsonComment;
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
	
	public String toString() {
		return jsonComment.toJSONString();
	}
	
	public static Comment jsonToComment(JSONObject jsonComment) {
		return new Comment((String)jsonComment.get("comment_id"), (String)jsonComment.get("username"), 
				(String)jsonComment.get("post_id"), (String)jsonComment.get("comment_text"), jsonComment);
	}
}
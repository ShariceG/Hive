package hive;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Response {
	
	private final ArrayList<Post> posts;
	private final ArrayList<Comment> comments;
	private final ServerStatusCode serverStatusCode;
	
	public Response(JSONObject jsonResponse) {
		JSONArray jsonPosts = (JSONArray) jsonResponse.get("posts");
		JSONArray jsonComments = (JSONArray) jsonResponse.get("comments");
		
		this.posts = getPostList(jsonPosts);
		this.comments = getCommentList(jsonComments);
		this.serverStatusCode = getServerStatusCodeFromJson(jsonResponse);
	}
	
	public Response(ArrayList<Post> posts, ArrayList<Comment> comments, ServerStatusCode serverStatusCode) {	
		this.posts = posts;
		this.comments = comments;
		this.serverStatusCode = serverStatusCode;
	}
	
	public StatusOr<Post> getPost() {
		if (posts.isEmpty()) {
			return new StatusOr<Post>(StatusError.GENERIC_EMPTY_ERROR); 
		}
		if (posts.size() > 1) {
			// getPost() can only be called if there is only one element in the list.
			return new StatusOr<Post>(StatusError.GENERIC_INVARIANT_ERROR);
		}
		return new StatusOr<Post>(posts.get(0));
	}
	
	public ArrayList<Post> getPosts() {
		return posts;
	}
	
	public ArrayList<Comment> getComments() {
		return comments;
	}
	
	public boolean serverReturnedWithError() {
		return !getServerStatusCode().equals(ServerStatusCode.OK);
	}
	
	public ServerStatusCode getServerStatusCode() {
		return serverStatusCode;
	}
		
	private ServerStatusCode getServerStatusCodeFromJson(JSONObject jsonResponse) {
		JSONObject status = (JSONObject) jsonResponse.get("status");
		return ServerStatusCode.valueOf((String) status.get("status_code"));
	}
	
	private ArrayList<Post> getPostList(JSONArray jsonPosts) {
		if (jsonPosts == null) {
			return new ArrayList<Post>();
		}
		ArrayList<Post> posts = new ArrayList<Post>();
		for (Object jsonPost : jsonPosts) {
			posts.add(Post.jsonToPost((JSONObject) jsonPost));
		}
		return posts;
	}
	
	private ArrayList<Comment> getCommentList(JSONArray jsonComments) {
		if (jsonComments == null) {
			return new ArrayList<Comment>();
		}
		ArrayList<Comment> comments = new ArrayList<Comment>();
		for (Object jsonComment : jsonComments) {
			comments.add(Comment.jsonToComment((JSONObject) jsonComment));
		}
		return comments;
	}
}

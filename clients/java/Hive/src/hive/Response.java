package hive;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Response {
	
	private final ArrayList<Post> posts;
	private final ArrayList<Comment> comments;
	private final ServerStatusCode serverStatusCode;
	private final ArrayList<Location> locations;
	private final QueryMetadata queryMetadata;
	
	public Response(JSONObject jsonResponse) {	
		this.posts = getPostList(jsonResponse);
		this.comments = getCommentList(jsonResponse);
		this.serverStatusCode = getServerStatusCodeFromJson(jsonResponse);
		this.locations = getLocationList(jsonResponse);
		this.queryMetadata = getQueryMetadata(jsonResponse);
	}
	
	public QueryMetadata getQueryMetadata() {
		return queryMetadata;
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
	
	public ArrayList<Location> getLocations() {
		return locations;
	}
	
	public boolean serverReturnedWithError() {
		return !getServerStatusCode().equals(ServerStatusCode.OK);
	}
	
	public ServerStatusCode getServerStatusCode() {
		return serverStatusCode;
	}
	
	private QueryMetadata getQueryMetadata(JSONObject jsonResponse) {
		if (!jsonResponse.containsKey("query_metadata")) {
			return new QueryMetadata();
		}
		return QueryMetadata.jsonToQueryMetadata((JSONObject) jsonResponse.get("query_metadata"));
	}
		
	private ServerStatusCode getServerStatusCodeFromJson(JSONObject jsonResponse) {
		JSONObject status = (JSONObject) jsonResponse.get("status");
		return ServerStatusCode.valueOf((String) status.get("status_code"));
	}
	
	private ArrayList<Location> getLocationList(JSONObject jsonResponse) {
		if (!jsonResponse.containsKey("locations")) {
			return new ArrayList<Location>();
		}
		JSONArray jsonLocations = (JSONArray) jsonResponse.get("locations");
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Object locationJson : jsonLocations) {
			locations.add(Location.jsonToLocation((JSONObject)locationJson));
		}
		return locations;
	}
	
	private ArrayList<Post> getPostList(JSONObject jsonResponse) {
		if (!jsonResponse.containsKey("posts")) {
			return new ArrayList<Post>();
		}
		JSONArray jsonPosts = (JSONArray) jsonResponse.get("posts");
		ArrayList<Post> posts = new ArrayList<Post>();
		for (Object jsonPost : jsonPosts) {
			posts.add(Post.jsonToPost((JSONObject) jsonPost));
		}
		return posts;
	}
	
	private ArrayList<Comment> getCommentList(JSONObject jsonResponse) {
		if (!jsonResponse.containsKey("comments")) {
			return new ArrayList<Comment>();
		}
		JSONArray jsonComments = (JSONArray) jsonResponse.get("comments");
		ArrayList<Comment> comments = new ArrayList<Comment>();
		for (Object jsonComment : jsonComments) {
			comments.add(Comment.jsonToComment((JSONObject) jsonComment));
		}
		return comments;
	}
}

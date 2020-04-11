package coloredcoded.hive.client;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Response {
	
	private final ArrayList<Post> posts;
	private final ArrayList<Comment> comments;
	private final ServerStatusCode serverStatusCode;
	private final ArrayList<HiveLocation> hiveLocations;
	private final QueryMetadata queryMetadata;
	private String serverMessage = "";
	private String verificationCode = "";
	private String username = "";
	
	public Response(JSONObject jsonResponse) {	
		this.posts = getPostList(jsonResponse);
		this.comments = getCommentList(jsonResponse);
		this.serverStatusCode = getServerStatusCodeFromJson(jsonResponse);
		this.hiveLocations = getLocationList(jsonResponse);
		this.queryMetadata = getQueryMetadata(jsonResponse);

		if (jsonResponse.get("verification_code") != null) {
			this.verificationCode = (String) jsonResponse.get("verification_code");
		}
		if (jsonResponse.get("username") != null) {
			this.username = (String) jsonResponse.get("username");
		}
		JSONObject status = (JSONObject) jsonResponse.get("status");
		if (status.get("status_message") != null) {
			this.serverMessage = (String) status.get("status_message");
		}
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

	public String getUsername() {
		return username;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public String getServerMessage() {
		return serverMessage;
	}

	public ArrayList<Post> getPosts() {
		return posts;
	}
	
	public ArrayList<Comment> getComments() {
		return comments;
	}
	
	public ArrayList<HiveLocation> getHiveLocations() {
		return hiveLocations;
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
	
	private ArrayList<HiveLocation> getLocationList(JSONObject jsonResponse) {
		if (!jsonResponse.containsKey("hiveLocations")) {
			return new ArrayList<HiveLocation>();
		}
		JSONArray jsonLocations = (JSONArray) jsonResponse.get("hiveLocations");
		ArrayList<HiveLocation> hiveLocations = new ArrayList<HiveLocation>();
		for (Object locationJson : jsonLocations) {
			hiveLocations.add(HiveLocation.jsonToLocation((JSONObject)locationJson));
		}
		return hiveLocations;
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

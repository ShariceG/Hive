package hive;

import java.util.Map;

public interface ServerClient {
	
	/*
	 * This is an interface that handles requests to the server.
	 * 
	 * You will notice that some functions require a QueryParams object. The server paginates some results. That is,
	 * it will only serve back a certain number of the total results. It is up to the client to keep track of this object as it will
	 * be used to tell the server where the last batch of results ended and where the next batch should begin.
	 * 
	 */
	
	// Returns a new server client. Please only use this function to get a ServerClient.
	// Do not call new ServerClientImp() directly as implementation can change.
	public static ServerClient newServerClient() {
		return new ServerClientImp();
	}

	void createUser(String username, String phoneNumber, 
			Callback callback, Map<String, Object> notes);
	void insertComment(String username, String commentText, String postId, 
			Callback callback, Map<String, Object> notes);
	void insertPost(String username, String postText, Location location, 
			Callback callback, Map<String, Object> notes);
	void updatePost(String username, String postId, ActionType actionType, 
			Callback callback, Map<String, Object> notes);
	void getAllPostsAtLocation(Location location, QueryParams queryParams, 
			Callback callback, Map<String, Object> notes);
	void getAllPostsByUser(String username, QueryParams params, 
			Callback callback, Map<String, Object> notes);
	void getAllPostsCommentedOnByUser(String username, QueryParams params, 
			Callback callback, Map<String, Object> notes);
	void getAllPostComments(String postId, QueryParams params, 
			Callback callback, Map<String, Object> notes);
	void getAllPopularPostsAtLocation(Location location, QueryParams params, 
			Callback callback, Map<String, Object> notes);
	void getPopularLocations(Callback callback, Map<String, Object> notes);
	void getAllPostLocations(Callback callback, Map<String, Object> notes);
	
	// Please call shutdown when application is finished running to gracefully free the clients resources.
	void shutdown();
}
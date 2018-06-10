package hive;

public interface ServerClient {
	// Prefer to use this instead of passing in null.
	public static Callback NO_CALLBACK = null;
	// Returns a new server client. Please only use this function to get a ServerClient.
	// Do not call new ServerClientImp() directly as implementation can change.
	public static ServerClient newServerClient() {
		return new ServerClientImp();
	}

	// If a callback is provided, the operation is assumed to be asynchronous.
	StatusOr<Response> createUser(String username, String phoneNumber, Callback callback);
	StatusOr<Response> insertComment(String username, String commentText, String postId, Callback callback);
	StatusOr<Response> insertPost(String username, String postText, String location, Callback callback);
	StatusOr<Response> updatePost(String username, String postId, ActionType actionType, Callback callback);
	StatusOr<Response> getAllPostsAroundUser(String username, String location, Callback callback);
	StatusOr<Response> getAllPostsByUSer(String username, Callback callback);
	StatusOr<Response> getAllPostsCommentedOnByUser(String username, Callback callback);
	StatusOr<Response> getAllPostComments(String postId, Callback callback);
	
	// Please call shutdown when application is finished running to gracefully free the clients resources.
	void shutdown();
}
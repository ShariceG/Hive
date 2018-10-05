package hive;

public interface ServerClient {
	// Returns a new server client. Please only use this function to get a ServerClient.
	// Do not call new ServerClientImp() directly as implementation can change.
	public static ServerClient newServerClient() {
		return new ServerClientImp();
	}

	void createUser(String username, String phoneNumber, Callback callback);
	void insertComment(String username, String commentText, String postId, Callback callback);
	void insertPost(String username, String postText, String location, Callback callback);
	void updatePost(String username, String postId, ActionType actionType, Callback callback);
	void getAllPostsAroundUser(String username, String location, Callback callback);
	void getAllPostsByUser(String username, Callback callback);
	void getAllPostsCommentedOnByUser(String username, Callback callback);
	void getAllPostComments(String postId, Callback callback);
	
	// Please call shutdown when application is finished running to gracefully free the clients resources.
	void shutdown();
}
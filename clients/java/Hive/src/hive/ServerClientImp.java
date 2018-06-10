package hive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;

public class ServerClientImp implements ServerClient {
	
	private UtilityBelt utils = new UtilityBelt();
	private ExecutorService threadPool;	
	public ServerClientImp() {
		threadPool = Executors.newCachedThreadPool();
	}

	@Override
	public void shutdown() {
		threadPool.shutdownNow();
	}

	@Override
	public StatusOr<Response> createUser(String username, String phoneNumber, Callback callback) {
		utils.checkStringNotEmpty(username);
		utils.checkStringNotEmpty(phoneNumber);
		Request.RequestParams params = new Request.RequestParams();
		params.user = new User(username, phoneNumber);
		Request request = new Request(RequestType.CREATE_USER, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}

	@Override
	public StatusOr<Response> insertComment(String username, String commentText, String postId, Callback callback) {
		utils.checkStringNotEmpty(username);
		utils.checkStringNotEmpty(commentText);
		utils.checkStringNotEmpty(postId);
		Request.RequestParams params = new Request.RequestParams();
		params.comment = new Comment(/*commentId=*/null, username, postId, commentText);
		Request request = new Request(RequestType.INSERT_COMMENT, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}

	@Override
	public StatusOr<Response> insertPost(String username, String postText, String location, Callback callback) {
		utils.checkStringNotEmpty(username);
		utils.checkStringNotEmpty(postText);
//		checkValidLocation(location);
		Request.RequestParams params = new Request.RequestParams();
		params.post = new Post(username, /*postId=*/null, postText, location, /*likes=*/0, /*dislikes*/0);
		Request request = new Request(RequestType.INSERT_POST, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}
	
	@Override
	public StatusOr<Response> updatePost(String username, String postId, ActionType actionType, Callback callback) {
		utils.checkStringNotEmpty(username);
		utils.checkStringNotEmpty(postId);
		utils.checkNotNull(actionType);
		Request.RequestParams params = new Request.RequestParams();
		params.user = new User(username, /*phoneNumber=*/null);
		params.post = new Post(/*username=*/null, postId, /*postText=*/null, /*location=*/null, /*likes=*/0, /*dislikes*/0);
		params.actionType = actionType;
		Request request = new Request(RequestType.UPDATE_POST, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}

	@Override
	public StatusOr<Response> getAllPostsAroundUser(String username, String location, Callback callback) {
		utils.checkStringNotEmpty(username);
		checkValidLocation(location);
		Request.RequestParams params = new Request.RequestParams();
		params.user = new User(username, /*phoneNumber=*/null, location);
		Request request = new Request(RequestType.GET_ALL_POSTS_AROUND_USER, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}

	@Override
	public StatusOr<Response> getAllPostsByUSer(String username, Callback callback) {
		utils.checkStringNotEmpty(username);
		Request.RequestParams params = new Request.RequestParams();
		params.user = new User(username, /*phoneNumber=*/null, /*location=*/null);
		Request request = new Request(RequestType.GET_ALL_POSTS_BY_USER, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}

	@Override
	public StatusOr<Response> getAllPostsCommentedOnByUser(String username, Callback callback) {
		utils.checkStringNotEmpty(username);
		Request.RequestParams params = new Request.RequestParams();
		params.user = new User(username, /*phoneNumber=*/null, /*location=*/null);
		Request request = new Request(RequestType.GET_ALL_POSTS_COMMENTED_ON_BY_USER, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}

	@Override
	public StatusOr<Response> getAllPostComments(String postId, Callback callback) {
		utils.checkStringNotEmpty(postId);
		Request.RequestParams params = new Request.RequestParams();
		params.post = new Post(/*username=*/null, postId, /*postText=*/null, /*location=*/null, /*likes=*/0, /*dislikes*/0);
		Request request = new Request(RequestType.GET_ALL_POST_COMMENTS, params);
		if (callback == null) {
			return executeServerRequestSync(request);
		}
		executeServerRequestAsync(request, callback);
		return new StatusOr<Response>(StatusError.NO_ERROR);
	}
	
	private void checkValidLocation(String location) {
		utils.checkStringNotEmpty(location);
		
		// For better visuals, imagine all the double slashes are single slashes.
		// Matches any string that starts with any number of digits, followed by a 
		// period then exactly 2 digits followed by a colon, followed by any number
		// of digits again, a period and exactly 2 digits.
		utils.trueOrFail(location.matches("^\\d+\\.\\d{2}:\\d+\\.\\d{2}$"));
	}
	
	private StatusOr<Response> executeServerRequestSync(Request request) {
		int retries = request.getRetries();
		StatusOr<JSONObject> jsonResponse;
		do {
			jsonResponse = handleRequest(request);
		} while (jsonResponse.hasError() && retries-- + 1 > 0);
		
		if (jsonResponse.hasError()) {
			return new StatusOr<Response>(jsonResponse);
		}
		return new StatusOr<Response>(new Response(jsonResponse.get()));
	}

	private void executeServerRequestAsync(Request request, Callback callback) {
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				StatusOr<Response> response = executeServerRequestSync(request);
				callback.serverRequestCallback(response);
			}
		});
	}

	private StatusOr<JSONObject> handleRequest(Request request) {
		ServerMessenger messenger = new ServerMessenger();
		Request.RequestParams params = request.getRequestParams();
		User user = params.user;
		Post post = params.post;
		Comment comment = params.comment;
		switch (request.getRequestType()) {
		case CREATE_USER:
			return messenger.sendMessageToCreateUser(user.getUsername(), user.getPhoneNumber());
		case INSERT_COMMENT:
			return messenger.sendMessageToInsertComment(comment.getUsername(), comment.getCommentText(), comment.getPostId());
		case INSERT_POST:
			return messenger.sendMessageToInsertPost(post.getUsername(), post.getPostText(), post.getLocation());
		case GET_ALL_POSTS_AROUND_USER:
			return messenger.sendMessageToGetAllPostsAroundUser(user.getUsername(), user.getLocation());
		case GET_ALL_POSTS_BY_USER:
			return messenger.sendMessageToGetAllPostsByUser(user.getUsername());
		case GET_ALL_POSTS_COMMENTED_ON_BY_USER:
			return messenger.sendMessageToGetAllPostsCommentedOnByUser(user.getUsername());
		case GET_ALL_POST_COMMENTS:
			return messenger.sendMessageToGetAllCommentsForPost(post.getPostId());
		case UPDATE_POST:
			return messenger.sendMessageToUpdatePost(post.getPostId(), user.getUsername(), params.actionType.name());
		default:
			throw new IllegalArgumentException("Unknown request type: " + request.getRequestType().name());
		}
	}
	
}

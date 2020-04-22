package hiveSimulator;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import hive.Location;

import hive.ActionType;
import hive.Callback;
import hive.QueryParams;
import hive.Response;
import hive.ServerClient;
import hive.StatusOr;

public class SimulatedUser {

	private ServerClient client;
	
	private String username;
	private String email;
	private Location location;
	private QueryParams queryParams;
		
	public SimulatedUser(String username, String email, Location location) {
		this.client = ServerClient.newServerClient();
		this.username = username;
		this.email = email;
		this.location = location;
		this.queryParams = new QueryParams(true, "", "");
		createNewUser();
	}
	
	public String toString() {
		return username;
	}
	
	public void createNewUser() {
		client.createNewUser(username, email, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr, 
					Map<String, Object> notes) {
				try {
					if (responseOr.hasError()) {
						throw new RuntimeException(responseOr.toString());
					}
					if (responseOr.get().serverReturnedWithError()) {
						throw new RuntimeException(responseOr.get().getServerStatusCode().name());
					}
					client.checkVerificationCode(username, email, "123456", new Callback() {
						public void serverRequestCallback(StatusOr<Response> responseOr, 
								Map<String, Object> notes) {
							try {
								if (responseOr.hasError()) {
									throw new RuntimeException(responseOr.toString());
								}
								if (responseOr.get().serverReturnedWithError()) {
									throw new RuntimeException(responseOr.get().getServerStatusCode().name());
								}
								System.out.println(username + " has been created.");
							} catch (RuntimeException e) {
								if (!responseOr.get().serverReturnedWithError()) {
									e.printStackTrace();
								}
							}
						}
					}, /*notes=*/null);
				} catch (RuntimeException e) {
					if (!responseOr.get().serverReturnedWithError()) {
						e.printStackTrace();
					}
				}
			}
		}, /*notes=*/null);
	}
	
	public void writePost() {
		client.insertPost(username, randomPost(), location, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr, 
					Map<String, Object> notes) {
				if (reportError(responseOr, notes)) {
					return;
				}
			}
		}, /*notes=*/null);
	}
	
	public void writeComment() {
		client.getAllPostsAtLocation(username, location, queryParams, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr, 
					Map<String, Object> notes) {
				if (reportError(responseOr, notes)) {
					return;
				}
				
				if (responseOr.get().getPosts().size() == 0) {
					writePost();
					return;
				}
				
				int randomIndex = new Random().nextInt(responseOr.get().getPosts().size());
				String postId = responseOr.get().getPosts().get(randomIndex).getPostId();
				client.insertComment(username, randomComment(), postId, new Callback() {
					public void serverRequestCallback(StatusOr<Response> responseOr, 
							Map<String, Object> notes) {
						if (reportError(responseOr, notes)) {
							return;
						}
					}
				}, /*notes=*/null);
				
			}
		}, /*notes=*/null);
	}
	
	public void performActionOnAPost() {
		client.getAllPostsAtLocation(username, location, queryParams, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr, 
					Map<String, Object> notes) {
				if (reportError(responseOr, notes)) {
					return;
				}
				
				if (responseOr.get().getPosts().size() == 0) {
					writePost();
					return;
				}
				
				int randomIndex = new Random().nextInt(responseOr.get().getPosts().size());
				String postId = responseOr.get().getPosts().get(randomIndex).getPostId();
				ActionType type = new Random().nextInt(1000) < 500 ? ActionType.LIKE : ActionType.DISLIKE;
				client.updatePost(username, postId, type, new Callback() {
					public void serverRequestCallback(StatusOr<Response> responseOr, 
							Map<String, Object> notes) {
						if (reportError(responseOr, notes)) {
							return;
						}
					}
				}, /*notes=*/null);
				
			}
		}, /*notes=*/null);
	}
	
	public void changeLocation(Location location) {
		this.location = location;
	}
	
	private String randomPost() {
		return "A random post: " + new Random().nextFloat();
	}
	
	private String randomComment() {
		return "A random comment: " + new Random().nextFloat();
	}
	
	private boolean reportError(StatusOr<Response> responseOr, 
			Map<String, Object> notes) {
		String stackTrace = "";
		boolean error = false;
		if (responseOr.hasError()) {
			stackTrace += responseOr.getErrorMessage() + "\n";
			error = true;
		} else if (responseOr.get().serverReturnedWithError()) {
			stackTrace += responseOr.get().getServerStatusCode() + "\n";
			error = true;
		}
		if (error) {
			stackTrace += Arrays.toString(Thread.currentThread().getStackTrace());
			System.out.println(stackTrace);
		}
		return error;
	}
	
}

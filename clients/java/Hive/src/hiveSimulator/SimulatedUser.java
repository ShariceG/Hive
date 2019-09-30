package hiveSimulator;

import java.util.Arrays;
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
	private String phoneNumber;
	private Location location;
	private QueryParams queryParams;
		
	public SimulatedUser(String username, String phoneNumber, Location location) {
		this.client = ServerClient.newServerClient();
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.location = location;
		this.queryParams = new QueryParams(true, "", "");
		createNewUser();
	}
	
	public void createNewUser() {
		client.createUser(username, phoneNumber, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				try {
					if (responseOr.hasError()) {
						throw new RuntimeException(responseOr.toString());
					}
					if (responseOr.get().serverReturnedWithError()) {
						throw new RuntimeException(responseOr.get().getServerStatusCode().name());
					}
					System.out.println(username + " has been created.");
				} catch (RuntimeException e) {
					if (!e.getMessage().contains("USER_ALREADY_EXISTS")) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public void writePost() {
		client.insertPost(username, randomPost(), location, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				if (reportError(responseOr)) {
					return;
				}
			}
		});
	}
	
	public void writeComment() {
		client.getAllPostsAtLocation(location, queryParams, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				if (reportError(responseOr)) {
					return;
				}
				
				if (responseOr.get().getPosts().size() == 0) {
					writePost();
					return;
				}
				
				int randomIndex = new Random().nextInt(responseOr.get().getPosts().size());
				String postId = responseOr.get().getPosts().get(randomIndex).getPostId();
				client.insertComment(username, randomComment(), postId, new Callback() {
					public void serverRequestCallback(StatusOr<Response> responseOr) {
						if (reportError(responseOr)) {
							return;
						}
					}
				});
				
			}
		});
	}
	
	public void performActionOnAPost() {
		client.getAllPostsAtLocation(location, queryParams, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				if (reportError(responseOr)) {
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
					public void serverRequestCallback(StatusOr<Response> responseOr) {
						if (reportError(responseOr)) {
							return;
						}
					}
				});
				
			}
		});
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
	
	private boolean reportError(StatusOr<Response> responseOr) {
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

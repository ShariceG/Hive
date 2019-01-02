package hiveSimulator;

import java.util.Random;

import javax.management.RuntimeErrorException;

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
	private String location;
	private QueryParams queryParams;
		
	public SimulatedUser(String username, String phoneNumber, String location) {
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
				if (responseOr.hasError()) {
					throw new RuntimeErrorException(null, responseOr.toString());
				}
				if (responseOr.get().serverReturnedWithError()) {
					throw new RuntimeErrorException(null, responseOr.get().getServerStatusCode().name());
				}
				System.out.println(username + " has been created.");
			}
		});
	}
	
	public void writePost() {
		client.insertPost(username, randomPost(), location, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				// ignore response, assume it worked.
			}
		});
	}
	
	public void writeComment() {
		client.getAllPostsAroundUser(username, location, queryParams, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				
				if (responseOr.get().getPosts().size() == 0) {
					writePost();
					return;
				}
				
				int randomIndex = new Random().nextInt(responseOr.get().getPosts().size());
				String postId = responseOr.get().getPosts().get(randomIndex).getPostId();
				client.insertComment(username, randomComment(), postId, new Callback() {
					public void serverRequestCallback(StatusOr<Response> response) {
						// ignore response, assume it worked.
					}
				});
				
			}
		});
	}
	
	public void performActionOnAPost() {
		client.getAllPostsAroundUser(username, location, queryParams, new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr) {
				
				if (responseOr.get().getPosts().size() == 0) {
					writePost();
					return;
				}
				
				int randomIndex = new Random().nextInt(responseOr.get().getPosts().size());
				String postId = responseOr.get().getPosts().get(randomIndex).getPostId();
				ActionType type = new Random().nextInt(1000) < 500 ? ActionType.LIKE : ActionType.DISLIKE;
				client.updatePost(username, postId, type, new Callback() {
					public void serverRequestCallback(StatusOr<Response> responseOr) {
						// ignore response, assume it worked.
					}
				});
				
			}
		});
	}
	
	public void changeLocation(String location) {
		this.location = location;
	}
	
	private String randomPost() {
		return "A random post: " + new Random().nextFloat();
	}
	
	private String randomComment() {
		return "A random comment: " + new Random().nextFloat();
	}
	
}

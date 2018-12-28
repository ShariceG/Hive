package hiveSimulator;

import java.util.ArrayList;
import java.util.Random;

import javax.management.RuntimeErrorException;

import hive.Callback;
import hive.Response;
import hive.ServerClient;
import hive.StatusOr;

public class SimulatedUser {
	
	private ServerClient client;
	
	private String username;
	private String phoneNumber;
	private String location;
		
	public SimulatedUser(String username, String phoneNumber, String location) {
		this.client = ServerClient.newServerClient();
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.location = location;
		createNewUser();
	}
	
	public void createNewUser() {
		client.createUser(username, phoneNumber, new Callback() {
			public void serverRequestCallback(StatusOr<Response> response) {
				if (response.hasError()) {
					throw new RuntimeErrorException(null, response.toString());
				}
				if (response.get().serverReturnedWithError()) {
					throw new RuntimeErrorException(null, response.get().getServerStatusCode().name());
				}
				System.out.println(username + " has been created.");
			}
		});
	}
	
	public void writePost() {
		client.insertPost(username, randomPost(), location, new Callback() {
			public void serverRequestCallback(StatusOr<Response> response) {
				// ignore response, assume it worked.
			}
		});
	}
	
	public void writeComment() {
		client.getAllPostsAroundUser(username, location, new Callback() {
			public void serverRequestCallback(StatusOr<Response> response) {
				
				if (response.get().getPosts().size() == 0) {
					writePost();
					return;
				}
				
				int randomIndex = new Random().nextInt(response.get().getPosts().size());
				String postId = response.get().getPosts().get(randomIndex).getPostId();
				client.insertComment(username, randomComment(), postId, new Callback() {
					public void serverRequestCallback(StatusOr<Response> response) {
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

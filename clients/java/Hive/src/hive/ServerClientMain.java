package hive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerClientMain {
	
	private static ServerClient client = ServerClient.newServerClient();
	private final static int USER_COUNT = 1;
	private static ArrayList<Post> madePosts;

	public static void main(String[] args) {
		madePosts = new ArrayList<Post>();
		createUsers(USER_COUNT);
//		makePosts(1);
//		makeComments(100);
//		makePostUpdates(100);
//		StatusOr<Response> response = client.getAllPostComments("f7e29db07ff845e6a001583949d32b6a", ServerClient.NO_CALLBACK);
//		printArrayList(response.get().getComments());
		client.shutdown();
	}
	
	public static void makePostUpdates(int amount) {
		System.out.println("Making " + amount + " post updates.");
		for (int i = 0; i < amount; i++) {
			ActionType type = new Random().nextInt(1000) < 500 ? ActionType.LIKE : ActionType.DISLIKE;
			client.updatePost(getRandomUsername(), getRandomPostId(), type, new Callback() {
				public void serverRequestCallback(StatusOr<Response> response) {
					System.out.println(response.toString());
				}
			});
		}
	}
	
	public static void makeComments(int amount) {
		System.out.println("Making " + amount + " comments.");
		for (int i = 0; i < amount; i++) {
			String username = getRandomUsername();
			String comment = "Comment by " + username;
			client.insertComment(username, comment, getRandomPostId(), new Callback() {
				public void serverRequestCallback(StatusOr<Response> response) {
					System.out.println(response.toString());
				}
			});
		}
	}
	
	public static void makePosts(int amount) {
		System.out.println("Making " + amount + " posts.");
		for (int i = 0; i < amount; i++) {
			String username = getRandomUsername();
			String post = "Post by " + username;
			String location = getRandomLocation();
			client.insertPost(username, post, location, new Callback() {
				public void serverRequestCallback(StatusOr<Response> response) {
					madePosts.add(response.get().getPost().get());
				}
			});
		}
	}
	
	public static String getRandomPostId() {
		return madePosts.get(new Random().nextInt(madePosts.size())).getPostId();
	}
	
	public static String getRandomLocation() {
		Random random = new Random();	
		String longStr = Double.toString(42 + random.nextDouble() * 2);
		String latStr = Double.toString(98 + random.nextDouble() * 2);
		return longStr + ":" + latStr;
	}
	
	public static String getRandomUsername() {
		return "user" + new Random().nextInt(USER_COUNT);
	}
	
	public static void createUsers(int amount) {
		System.out.println("Creating " + amount + " users.");
		for (int i = 0; i < amount; i++) {
			client.createUser("user"+i, i+""+i+""+i, new Callback() {
				public void serverRequestCallback(StatusOr<Response> response) {
					if (response.hasError()) {
						System.err.println(response.getErrorMessage());
					}
					if (response.get().serverReturnedWithError()) {
						System.out.println(response.get().getServerStatusCode().name());
					}
				}
			});
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void printArrayList(List arrayList) {
		for (Object obj : arrayList) {
			System.out.println(obj);
		}
	}

}

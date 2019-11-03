package hive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServerClientMain {
	
	private static ServerClient client = ServerClient.newServerClient();
	private final static int USER_COUNT = 10;
	private static ArrayList<Post> madePosts;

	public static void main(String[] args) {
		madePosts = new ArrayList<Post>();
		client.getPopularLocations(new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr, 
					Map<String, Object> notes) {
				System.out.println(responseOr.toString());
			}
		}, /*notes=*/null);
		client.getAllPopularPostsAtLocation(makeLocation("33.75","-116.35"), new QueryParams(true, "", ""), new Callback() {
			public void serverRequestCallback(StatusOr<Response> responseOr, 
					Map<String, Object> notes) {
				System.out.println("BLAH BLAH BLAH ");
				Post p = responseOr.get().getPosts().get(0);
				System.out.println(p.getCreationTimestampSecAsLong() + " " + p.getCreationTimestampSec() + " " + p.isExpired());
			}
		}, /*notes=*/null);
		createUsers(USER_COUNT);
		makePosts(1);
		makeComments(100);
		makePostUpdates(1);
//		StatusOr<Response> response = client.getAllPostComments("f7e29db07ff845e6a001583949d32b6a", ServerClient.NO_CALLBACK);
//		printArrayList(response.get().getComments());
		client.shutdown();
	}
	
	public static Location makeLocation(String lat, String lon) {
		return new Location(lat, lon);
	}
	
	public static void makePostUpdates(int amount) {
		System.out.println("Making " + amount + " post updates.");
		for (int i = 0; i < amount; i++) {
			ActionType type = new Random().nextInt(1000) < 500 ? ActionType.LIKE : ActionType.DISLIKE;
			client.updatePost(getRandomUsername(), getRandomPostId(), type, new Callback() {
				public void serverRequestCallback(StatusOr<Response> responseOr, 
						Map<String, Object> notes) {
					System.out.println(responseOr.toString());
				}
			}, /*notes=*/null);
		}
	}
	
	public static void makeComments(int amount) {
		System.out.println("Making " + amount + " comments.");
		for (int i = 0; i < amount; i++) {
			String username = getRandomUsername();
			String comment = "Comment by " + username;
			client.insertComment(username, comment, getRandomPostId(), new Callback() {
				public void serverRequestCallback(StatusOr<Response> responseOr, 
						Map<String, Object> notes) {
					System.out.println(responseOr.toString());
				}
			}, /*notes=*/null);
		}
	}
	
	public static void makePosts(int amount) {
		System.out.println("Making " + amount + " posts.");
		for (int i = 0; i < amount; i++) {
			String username = getRandomUsername();
			String post = "Post by " + username;
			Location location = getRandomLocation();
			client.insertPost(username, post, location, new Callback() {
				public void serverRequestCallback(StatusOr<Response> responseOr, 
						Map<String, Object> notes) {
					madePosts.add(responseOr.get().getPost().get());
				}
			}, /*notes=*/null);
		}
	}
	
	public static String getRandomPostId() {
		return madePosts.get(new Random().nextInt(madePosts.size())).getPostId();
	}
	
	public static Location getRandomLocation() {
		Random random = new Random();	
		String longStr = Double.toString(42 + random.nextDouble() * 2);
		String latStr = Double.toString(98 + random.nextDouble() * 2);
		return new Location(longStr, latStr);
	}
	
	public static String getRandomUsername() {
		return "user" + new Random().nextInt(USER_COUNT);
	}
	
	public static void createUsers(int amount) {
		System.out.println("Creating " + amount + " users.");
		for (int i = 0; i < amount; i++) {
			client.createUser("user"+i, i+""+i+""+i, new Callback() {
				public void serverRequestCallback(StatusOr<Response> responseOr, 
						Map<String, Object> notes) {
					if (responseOr.hasError()) {
						System.err.println(responseOr.getErrorMessage());
					}
					if (responseOr.get().serverReturnedWithError()) {
						System.out.println(responseOr.get().getServerStatusCode().name());
					}
				}
			}, /*notes=*/null);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void printArrayList(List arrayList) {
		for (Object obj : arrayList) {
			System.out.println(obj);
		}
	}

}

package coloredcoded.hive.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerClientImp implements ServerClient {
	
	private static final int CONNECTION_TIMEOUT_MS = 5000;
	private static final int READ_TIMEOUT_MS = 55000;
	
//	private static final String SERVER_DOMAIN = "http://localhost:8080";
	private static final String SERVER_DOMAIN = "http://10.0.0.218:8080";
	private static final String COMMON_PATH = "/_ah/api/media_api/v1/";
	private static final String CREATE_USER_PATH = "app.create_user?";
	private static final String INSERT_COMMENT_PATH = "app.insert_comment?";
	private static final String INSERT_POST_PATH = "app.insert_post?";
	private static final String GET_ALL_POST_COMMENTS_PATH = "app.get_all_comments_for_post?";
	private static final String GET_ALL_POSTS_AT_LOCATION_PATH = "app.get_all_posts_at_location?";
	private static final String GET_ALL_POST_LOCATIONS_PATH = "app.get_all_post_locations?";
	private static final String GET_ALL_POSTS_BY_USER_PATH = "app.get_all_posts_by_user?";
	private static final String GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH =
			"app.get_all_posts_commented_on_by_user?";
	private static final String UPDATE_POST_PATH = "app.update_post?";
	private static final String UPDATE_COMMENT_PATH = "app.update_comment?";
	private static final String GET_ALL_POPULAR_POSTS_AT_LOCATION_PATH =
			"app.get_all_popular_posts_at_location?";
    private static final String GET_POPULAR_LOCATIONS_PATH = "app.get_popular_locations?";
	
	private ExecutorService threadPool;
	
	public ServerClientImp(){
		threadPool = Executors.newCachedThreadPool();
	}
	
	@Override
	public void shutdown() {
		threadPool.shutdownNow();
	}
	
	public void getPopularLocations(Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		String path = constructIncompleteUrlPath() + GET_POPULAR_LOCATIONS_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	public void getAllPostLocations(Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		String path = constructIncompleteUrlPath() + GET_ALL_POST_LOCATIONS_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllPopularPostsAtLocation(Location location, QueryParams params, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("location", location.toJSON());
		request.put("query_params", params.toJson());

		String path = constructIncompleteUrlPath() + GET_ALL_POPULAR_POSTS_AT_LOCATION_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllPostsCommentedOnByUser(String username, QueryParams params, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("query_params", params.toJson());
		
		String path = constructIncompleteUrlPath() + GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllPostsByUser(String username, QueryParams params, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("query_params", params.toJson());
		
		String path = constructIncompleteUrlPath() + GET_ALL_POSTS_BY_USER_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllPostsAtLocation(String username, Location location, QueryParams params, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("location", location.toJSON());
		request.put("query_params", params.toJson());

		String path = constructIncompleteUrlPath() + GET_ALL_POSTS_AT_LOCATION_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void getAllPostComments(String postId, QueryParams params, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("post_id", postId);
		request.put("query_params", params.toJson());
		
		String path = constructIncompleteUrlPath() + GET_ALL_POST_COMMENTS_PATH;
		executeHttpRequestAsync("GET", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void createUser(String username, String phoneNumber, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("phone_number", phoneNumber);

		String path = constructIncompleteUrlPath() + CREATE_USER_PATH;
		executeHttpRequestAsync("POST", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void updateComment(String username, String commentId, ActionType actionType,
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("comment_id", commentId);
		request.put("action_type", actionType.name() + "");

		String path = constructIncompleteUrlPath() + UPDATE_COMMENT_PATH;
		executeHttpRequestAsync("POST", path, request, callback, notes);
	}

	@SuppressWarnings("unchecked")
	public void updatePost(String username, String postId, ActionType actionType,
						   Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("post_id", postId);
		request.put("action_type", actionType.name() + "");

		String path = constructIncompleteUrlPath() + UPDATE_POST_PATH;
		executeHttpRequestAsync("POST", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void insertPost(String username, String postText, Location location, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("post_text", postText);
		request.put("location", location.toJSON());

		String path = constructIncompleteUrlPath() + INSERT_POST_PATH;
		executeHttpRequestAsync("POST", path, request, callback, notes);
	}
	
	@SuppressWarnings("unchecked")
	public void insertComment(String username, String commentText, String postId, 
			Callback callback, Map<String, Object> notes) {
		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("comment_text", commentText);
		request.put("post_id", postId);
		
		String path = constructIncompleteUrlPath() + INSERT_COMMENT_PATH;
		executeHttpRequestAsync("POST", path, request, callback, notes);
	}
	
	public String constructIncompleteUrlPath() {
		return SERVER_DOMAIN + COMMON_PATH;
	}
	
	private JSONObject jsonStringToObject(String jsonString) {
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String jsonObjectToUrlParameter(JSONObject jsonRequest) {
		String urlParams = jsonObjectToUrlParameterImp(jsonRequest, "");
		if (!urlParams.isEmpty()) {
			return urlParams.substring(0, urlParams.length()-1);
		}
		return "";
	}
		
	private String jsonObjectToUrlParameterImp(JSONObject jsonObj, String path) {
		String params = "";
		for (Object field : jsonObj.keySet()) {
			Object value = jsonObj.get(field);
			String newPath = path.isEmpty() ? (String)field : path + "." + field;
			if (value instanceof String) {
				params += newPath + "=" + value + "&";
			} else {
				params += jsonObjectToUrlParameterImp((JSONObject)value, newPath);
			}
		}
		return params;
	}
	
	private void executeHttpRequestAsync(
			final String httpMethod, final String path, final JSONObject request,
			final Callback callback, final Map<String, Object> notes) {
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				boolean isGetRequest = httpMethod.equalsIgnoreCase("GET");
				StatusOr<Response> responseOr =
						isGetRequest ? executeGet(path, request) : executePost(path, request);
				callback.serverRequestCallback(responseOr, notes);
			}
		});
	}
	
	private StatusOr<Response> executePost(String targetURL, JSONObject jsonParams) {
	  String urlParameters = jsonParams.toJSONString();
	  HttpURLConnection connection = null;
	  try {
	    //Create connection
	    URL url = new URL(targetURL);

	    connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type", "application/json");
	    
	    connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
	    connection.setReadTimeout(READ_TIMEOUT_MS);

	    connection.setRequestProperty("Content-Length", 
	        Integer.toString(urlParameters.getBytes().length));
	    connection.setRequestProperty("Content-Language", "en-US");  

	    connection.setUseCaches(false);
	    connection.setDoOutput(true);

	    //Send request
	    DataOutputStream wr = new DataOutputStream (
	        connection.getOutputStream());
	    wr.writeBytes(urlParameters);
	    wr.close();

	    //Get Response 
	    InputStream is = connection.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
	    String line;
	    while ((line = rd.readLine()) != null) {
	      response.append(line);
	      response.append('\r');
	    }
	    rd.close();
	    JSONObject jsonResponse = jsonStringToObject(response.toString());
		return new StatusOr<Response>(new Response(jsonResponse));
	  } catch (FileNotFoundException fnfe) {
		  fnfe.printStackTrace();
		  return new StatusOr<Response>(StatusError.SERVER_NOT_FOUND, 
					  fnfe.getMessage()); 
	  } catch (SocketTimeoutException timeoutExcep) {
		  timeoutExcep.printStackTrace();
		  return new StatusOr<Response>(StatusError.CONNECTION_TIMEOUT_ERROR, 
				  timeoutExcep.getMessage()); 
	  } catch (IOException e) {
		  e.printStackTrace();
		  return new StatusOr<Response>(StatusError.GENERIC_CONNECTION_ERROR, 
				  e.getMessage());
	  } catch (Exception exc) {
		  exc.printStackTrace();
		  return new StatusOr<Response>(StatusError.GENERIC_SERVER_ERROR, 
					 exc.getMessage());
	  } finally {
		  if (connection != null) {
			  connection.disconnect();
		  }
	  }
	}
	
	private StatusOr<Response> executeGet(String targetURL, JSONObject jsonParams) {
		targetURL += jsonObjectToUrlParameter(jsonParams);
		HttpURLConnection connection = null;
		BufferedReader in = null;
		StringBuffer response = null;
		try {
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
		    connection.setReadTimeout(READ_TIMEOUT_MS);
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject jsonResponse = jsonStringToObject(response.toString());
			return new StatusOr<Response>(new Response(jsonResponse));
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return new StatusOr<Response>(StatusError.SERVER_NOT_FOUND, 
					  fnfe.getMessage()); 
		} catch (SocketTimeoutException timeoutExcep) {
			timeoutExcep.printStackTrace();
			return new StatusOr<Response>(StatusError.CONNECTION_TIMEOUT_ERROR, 
				  timeoutExcep.getMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
			return new StatusOr<Response>(StatusError.GENERIC_CONNECTION_ERROR, 
					  e.getMessage());
		} catch (Exception exc) {
			exc.printStackTrace();
			return new StatusOr<Response>(StatusError.GENERIC_SERVER_ERROR, 
					 exc.getMessage());
		} finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		}
	}
}

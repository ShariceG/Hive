package hive;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerMessenger {
	private static final int CONNECTION_TIMEOUT_MS = 5000;
	private static final int READ_TIMEOUT_MS = 55000;
	
	private static final String SERVER_DOMAIN = "http://localhost:8080";
	private static final String COMMON_PATH = "/_ah/api/media_api/v1/";
	private static final String CREATE_USER_PATH = "app.create_user?";
	private static final String INSERT_COMMENT_PATH = "app.insert_comment?";
	private static final String INSERT_POST_PATH = "app.insert_post?";
	private static final String GET_ALL_POST_COMMENTS_PATH = "app.get_all_comments_for_post?";
	private static final String GET_ALL_POSTS_AROUND_USER_PATH = "app.get_all_posts_around_user?";
	private static final String GET_ALL_POSTS_BY_USER_PATH = "app.get_all_posts_by_user?";
	private static final String GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH = "app.get_all_posts_commented_on_by_user?";
	private static final String UPDATE_POST_PATH = "app.update_post?";
	
	public ServerMessenger(){
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToGetAllPostsCommentedOnByUser(String username) {
		JSONObject user = new JSONObject();
		user.put("username", username);
		JSONObject request = new JSONObject();
		request.put("user", user);
		
		String path = constructIncompleteUrlPath() + GET_ALL_POSTS_COMMENTED_ON_BY_USER_PATH;
		
		StatusOr<String> responseOr = executeGet(path, request);
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToGetAllPostsByUser(String username) {
		JSONObject user = new JSONObject();
		user.put("username", username);
		JSONObject request = new JSONObject();
		request.put("user", user);
		
		String path = constructIncompleteUrlPath() + GET_ALL_POSTS_BY_USER_PATH;
		StatusOr<String> responseOr = executeGet(path, request);
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToGetAllPostsAroundUser(String username, String location) {
		JSONObject user = new JSONObject();
		user.put("username", username);
		user.put("location", location);
		JSONObject request = new JSONObject();
		request.put("user", user);
		
		String path = constructIncompleteUrlPath() + GET_ALL_POSTS_AROUND_USER_PATH;
		StatusOr<String> responseOr = executeGet(path, request);
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToGetAllCommentsForPost(String postId) {
		JSONObject post = new JSONObject();
		post.put("post_id", postId);
		JSONObject request = new JSONObject();
		request.put("post", post);
		
		String path = constructIncompleteUrlPath() + GET_ALL_POST_COMMENTS_PATH;
		StatusOr<String> responseOr = executeGet(path, request);
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToCreateUser(String username, String phoneNumber) {
		JSONObject user = new JSONObject();
		user.put("username", username);
		user.put("phone_number", phoneNumber);
		JSONObject request = new JSONObject();
		request.put("user", user);

		String path = constructIncompleteUrlPath() + CREATE_USER_PATH;
		StatusOr<String> responseOr = executePost(path, request.toJSONString());
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToUpdatePost(String postId, String username, String actionType) {
		JSONObject user = new JSONObject();
		user.put("username", username);
		JSONObject post = new JSONObject();
		post.put("post_id", postId);
		JSONObject request = new JSONObject();
		request.put("user", user);
		request.put("post", post); 
		request.put("action_type", actionType);
		
		String path = constructIncompleteUrlPath() + UPDATE_POST_PATH;
		StatusOr<String> responseOr = executePost(path, request.toJSONString());
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToInsertPost(String username, String postText, String location) {
		JSONObject post = new JSONObject();
		post.put("username", username);
		post.put("post_text", postText);
		post.put("location", location);
		JSONObject request = new JSONObject();
		request.put("post", post);

		String path = constructIncompleteUrlPath() + INSERT_POST_PATH;
		StatusOr<String> responseOr = executePost(path, request.toJSONString());
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
	}
	
	@SuppressWarnings("unchecked")
	public StatusOr<JSONObject> sendMessageToInsertComment(String username, String commentText, String postId) {
		JSONObject comment = new JSONObject();
		comment.put("username", username);
		comment.put("comment_text", commentText);
		comment.put("post_id", postId);
		JSONObject request = new JSONObject();
		request.put("comment", comment);

		String path = constructIncompleteUrlPath() + INSERT_COMMENT_PATH;
		StatusOr<String> responseOr = executePost(path, request.toJSONString());
		if (responseOr.hasError()) {
			return new StatusOr<JSONObject>(responseOr);
		}
		return new StatusOr<JSONObject>(jsonStringToObject(responseOr.get()));
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
		return urlParams.substring(0, urlParams.length()-1);
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
	
	private StatusOr<String> executePost(String targetURL, String urlParameters) {
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
	    return new StatusOr<String>(response.toString());
	  } catch (FileNotFoundException fnfe) {
			return new StatusOr<String>(StatusError.SERVER_NOT_FOUND, 
					  fnfe.getMessage()); 
	  } catch (SocketTimeoutException timeoutExcep) {
		  return new StatusOr<String>(StatusError.CONNECTION_TIMEOUT_ERROR, 
				  timeoutExcep.getMessage()); 
	  } catch (IOException e) {
		  return new StatusOr<String>(StatusError.GENERIC_CONNECTION_ERROR, 
				  e.getMessage());
	  } catch (Exception exc) {
		  return new StatusOr<String>(StatusError.GENERIC_SERVER_ERROR, 
					 exc.getMessage());
	  } finally {
		  if (connection != null) {
			  connection.disconnect();
		  }
	  }
	}
	
	private StatusOr<String> executeGet(String targetURL, JSONObject jsonParams) {	
		targetURL += jsonObjectToUrlParameter(jsonParams);
		HttpURLConnection connection = null;
		BufferedReader in = null;
		StringBuffer response = null;
		try {
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			
			connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
		    connection.setReadTimeout(READ_TIMEOUT_MS);

			in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return new StatusOr<String>(response.toString());
		} catch (FileNotFoundException fnfe) {
			return new StatusOr<String>(StatusError.SERVER_NOT_FOUND, 
					  fnfe.getMessage()); 
		} catch (SocketTimeoutException timeoutExcep) {
			return new StatusOr<String>(StatusError.CONNECTION_TIMEOUT_ERROR, 
				  timeoutExcep.getMessage()); 
		} catch (IOException e) {
			return new StatusOr<String>(StatusError.GENERIC_CONNECTION_ERROR, 
					  e.getMessage());
		} catch (Exception exc) {
			 return new StatusOr<String>(StatusError.GENERIC_SERVER_ERROR, 
					 exc.getMessage());
		} finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		}
	}
}

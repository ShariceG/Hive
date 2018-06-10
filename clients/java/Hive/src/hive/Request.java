package hive;

public class Request {
	static class RequestParams {
		public User user = null;
		public Post post = null;
		public Comment comment = null;
		public ActionType actionType = null;
	}
	
	private RequestParams params;
	private RequestType type;
	private int retries;
	
	public Request(RequestType type, RequestParams params) {
		this(type, params, 0);
	}

	public Request(RequestType type, RequestParams params, int retries) {
		this.params = params;
		this.type = type;
	}
	
	public RequestType getRequestType() {
		return type;
	}
	
	public RequestParams getRequestParams() {
		return params;
	}
	
	public int getRetries() {
		return retries;
	}
}

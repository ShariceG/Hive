package hive;

import org.json.simple.JSONObject;

public class QueryParams {
	/*
	 * This class represents query parameters that are sometimes needed by the server to know which batch of results to serve. 
	 */
	
	private boolean getNewer;  // True if you want the server to serve new results, false for older results.
	private String currTopCursorStr;
	private String currBottomCursorStr;
	
	public QueryParams() {
        getNewer = true;
        currTopCursorStr = "";
        currBottomCursorStr = "";
    }
    
    public QueryParams(boolean getNewer, String currTopCursorStr, String currBottomCursorStr) {
        this.getNewer = getNewer;
        this.currTopCursorStr = currTopCursorStr;
        this.currBottomCursorStr = currBottomCursorStr;
    }
    
    @SuppressWarnings("unchecked")
	public JSONObject toJson() {
    	JSONObject queryParams = new JSONObject();
    	queryParams.put("get_newer", getNewer ? "true" : "false");
    	if (currTopCursorStr != null) {
	    	queryParams.put("curr_top_cursor_str", currTopCursorStr);
    	}
    	if (currBottomCursorStr != null) {
	    	queryParams.put("curr_bottom_cursor_str", currBottomCursorStr);
    	}
        return queryParams;
    }
}
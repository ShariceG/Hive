package hive;

import java.util.Optional;

import org.json.simple.JSONObject;

public class QueryInfo {
	
	private class QueryParams {
		private boolean getNewer;
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
	
	private class QueryMetadata {
		
	}

}

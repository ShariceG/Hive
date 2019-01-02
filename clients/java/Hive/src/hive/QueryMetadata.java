package hive;

import org.json.simple.JSONObject;

public class QueryMetadata {
	private String newTopCursorStr;
	private String newBottomCursorStr;
	private String hasMoreOlderData;
    
    public QueryMetadata() {
        newTopCursorStr = "";
        newBottomCursorStr = "";
        hasMoreOlderData = "";
    }
   
    private QueryMetadata(JSONObject jsonMetadata) {
        if (jsonMetadata.containsKey("new_top_cursor_str")) {
            newTopCursorStr = (String) jsonMetadata.get("new_top_cursor_str");
        }
        if (jsonMetadata.containsKey("new_bottom_cursor_str")) {
            newTopCursorStr = (String) jsonMetadata.get("new_bottom_cursor_str");
        }
        if (jsonMetadata.containsKey("has_more_older_data")) {
            newTopCursorStr = (String) jsonMetadata.get("has_more_older_data");
        }
    }
    
    public static QueryMetadata jsonToQueryMetadata(JSONObject jsonMetadata) {
    	return new QueryMetadata(jsonMetadata);
    }
    
    public void updateMetadata(QueryMetadata newMetadata) {
        if (newMetadata.newTopCursorStr != null) {
            newTopCursorStr = newMetadata.newTopCursorStr;
        }
        if (newMetadata.newBottomCursorStr != null) {
            newBottomCursorStr = newMetadata.newBottomCursorStr;
        }
        if (newMetadata.hasMoreOlderData != null) {
            hasMoreOlderData = newMetadata.hasMoreOlderData;
        }
    }
}

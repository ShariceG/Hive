package coloredcoded.hive.client;

import java.util.Map;

public abstract class Callback {
	public void serverRequestCallback(StatusOr<Response> responseOr, 
			Map<String, Object> notes) {
		new NoSuchMethodError().printStackTrace();
	}
}
package hive;

public abstract class Callback {
	public void serverRequestCallback(StatusOr<Response> responseOr) {
		new NoSuchMethodError().printStackTrace();
	}
}
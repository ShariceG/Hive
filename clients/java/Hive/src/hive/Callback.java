package hive;

public abstract class Callback {
	public void serverRequestCallback(StatusOr<Response> response) {
		new NoSuchMethodError().printStackTrace();
	}
}
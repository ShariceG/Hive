package hive;

public class StatusOr<T> {
	
	private String errorMessage;
	private StatusError error;
	private T value;
	
	protected StatusOr(T v, StatusError err, String msg) {
		errorMessage = msg;
		error = err;
		value = v;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StatusOr(StatusOr statusOr) {
		if (statusOr.hasError()) {
			errorMessage = statusOr.errorMessage;
			error = statusOr.error;
		} else {
			value = (T) statusOr.get();
		}
	}
	
	public StatusOr(T v) {
		this(v, StatusError.NO_ERROR, "");
	}
	
	public StatusOr(StatusError err, String msg) {
		this(null, err, msg);
	}
	
	public StatusOr(StatusError err) {
		this(null, err, "");
	}
	
	public T get() {
		if (hasError()) {
			RuntimeException e = new RuntimeException("Unhandled error: \n" + getErrorMessage() + "\n");
			e.printStackTrace();
		}
		return value;
	}
	
	public boolean hasError() {
		return !error.equals(StatusError.NO_ERROR);
	}
	
	public StatusError getStatusError() {
		return error;
	}
	
	public String getErrorMessage() {
		return error.name() + ": " + errorMessage;
	}
	
	public String toString() {
		if (hasError()) {
			return getErrorMessage();
		}
		return value.toString();
	}
}

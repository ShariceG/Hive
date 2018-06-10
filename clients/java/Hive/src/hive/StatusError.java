package hive;

public enum StatusError {
	NO_ERROR,
	CONNECTION_TIMEOUT_ERROR,
	GENERIC_CONNECTION_ERROR,
	// Use in a context where you expect something to not be empty is empty
	GENERIC_EMPTY_ERROR,
	GENERIC_SERVER_ERROR,
	// Use in a context where you expect something to be 100% true but it isn't
	GENERIC_INVARIANT_ERROR,
	SERVER_NOT_FOUND
}

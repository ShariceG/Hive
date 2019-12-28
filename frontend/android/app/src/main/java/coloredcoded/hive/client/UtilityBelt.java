package coloredcoded.hive.client;

public class UtilityBelt {
	
	public void checkStringNotEmpty(String str) {
		checkNotNull(str);
		trueOrFail(!str.isEmpty());
	}
	
	public void checkNotNull(Object obj) {
		trueOrFail(obj != null);
	}
	
	public void trueOrFail(boolean truth) {
		if (!truth) {
			throw new IllegalArgumentException();
		}
	}

}

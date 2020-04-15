package coloredcoded.hive;

import android.util.Log;

import coloredcoded.hive.client.HiveEnvironment;

public class HiveGlobal {

    private static HiveEnvironment environment;

    public static void instantiateNewEnvironment() {
        if (environment != null) {
            throw new RuntimeException("Please call destroyEnvironment() first.");
        }
        environment = HiveEnvironment.newEnvironment();
    }

    public static HiveEnvironment getEnvironment() {
        return environment;
    }

    public static void destroyEnvironment() {
        System.out.println("Destroying HiveEnvironment.");
        environment.cleanUp();
        environment = null;
    }
}

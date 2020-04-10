package coloredcoded.hive;

import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;

public class AppHelper {

    static ServerClient serverClient() {
        return new ServerClientImp();
    }

    static Location getTestUserLocation() {
        return new Location("47.608013", "-122.335167",
                new Location.Area("47.60", "-122.33",
                        "Seattle", "WA", "United States"));
    }

    static String getTestUser() {
        return "user1";
    }

}

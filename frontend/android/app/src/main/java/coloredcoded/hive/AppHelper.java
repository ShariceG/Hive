package coloredcoded.hive;

import coloredcoded.hive.client.Location;

public class AppHelper {

    static Location getTestUserLocation() {
        return new Location("47.608013", "-122.335167",
                new Location.Area("47.60", "-122.33",
                        "Seattle", "WA", "United States"));
    }

    static String getTestUser() {
        return "user1";
    }

}

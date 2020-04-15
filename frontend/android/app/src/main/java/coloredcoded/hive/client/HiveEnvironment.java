package coloredcoded.hive.client;

import android.app.Activity;

import coloredcoded.hive.LocationHandler;

public class HiveEnvironment {

    private User user;
    private LocationHandler locationHandler;

    public HiveEnvironment() {
        user = null;
        locationHandler = null;
    }

    public static HiveEnvironment newEnvironment() {
        return new HiveEnvironment();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public LocationHandler getLocationHandler() {
        return locationHandler;
    }

    public void initLocationHandler(Activity activity, LocationHandler.Delegate delegate) {
        if (locationHandler != null) {
            return;
        }
        locationHandler = new LocationHandler(activity, delegate);
    }

    public void setLocationHandlerDelegate(LocationHandler.Delegate delegate) {
        locationHandler.setDelegate(delegate);
    }

    public void cleanUp() {
        getLocationHandler().stopLocationUpdates();
        setLocationHandlerDelegate(null);
    }
}

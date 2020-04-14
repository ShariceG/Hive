package coloredcoded.hive;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationHandler implements LocationListener {

    public interface Delegate {
        // Useful if the user changes permissions outside the app while the app is running. I think.
        void userTentativelyDeniedPermission();
        void userPermanentlyDeniedPermission();
        void providePermissionJustificationToUser();
        void userApprovedLocationPermission();
        void locationUpdate(Location location);
    }

    private static int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static int MIN_TIME_BEFORE_LOCATION_UPDATE_MS = 1000 * 60 * 2;

    private LocationManager locationManager;
    private Activity activity;
    private Delegate delegate;

    public LocationHandler(Activity activity, Delegate delegate) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.activity = activity;
        setDelegate(delegate);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BEFORE_LOCATION_UPDATE_MS,
                    0, this);
        } catch (SecurityException e) {
            System.err.println("No permissions!");
            throw e;
        }
    }

    public void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    public Location getLatestLocation() {
        if (delegate == null) {
            throw new RuntimeException("Delegate must not be null.");
        }
        try {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            delegate.userTentativelyDeniedPermission();
        }
        return null;
    }

    public boolean hasLocationPermissions() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public void checkLocationPermission() {
        if (delegate == null) {
            throw new RuntimeException("Delegate must not be null.");
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            delegate.userTentativelyDeniedPermission();
            return;
        }
        final String permissionType = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(activity, permissionType)
            == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{permissionType},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    // The activity gets the response to the requestPermissions call and routes it back to us
    // through this function to decipher. And then we delegate it back to the activity to decide
    // what to do.
    public void checkLocationPermissionRequest(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResult) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (delegate == null) {
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            delegate.userPermanentlyDeniedPermission();
            return;
        }
        if (permissions.length > 0 &&
                !permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return;
        }
        if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            delegate.userApprovedLocationPermission();
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            delegate.providePermissionJustificationToUser();
        } else {
            delegate.userPermanentlyDeniedPermission();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (delegate == null) {
            return;
        }
        delegate.locationUpdate(location);
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println(provider + " services are enabled");
        checkLocationPermission();
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println(provider + " services are disabled");
        checkLocationPermission();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println(provider + " status changed.");
        if (delegate == null) {
            return;
        }
        if (status == PackageManager.PERMISSION_GRANTED) {
            delegate.userApprovedLocationPermission();
        } else {
            delegate.userTentativelyDeniedPermission();
        }
    }
}

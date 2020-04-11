package coloredcoded.hive;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationHandler implements LocationListener {

    public interface Delegate {
        void userDeniedLocationPermission();
        void userApprovedLocationPermission();
        void locationUpdate(Location location);
    }

    private static int LOCATION_PERMISSION_REQUEST_CODE = 123;

    private LocationManager locationManager;
    private Activity activity;
    private Delegate delegate;

    public LocationHandler(Activity activity) {
        this(activity, null);
    }

    public LocationHandler(Activity activity, Delegate delegate) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60,
                    400, this);
        } catch (SecurityException e) {
            System.err.println("No permissions!");
            e.printStackTrace();
        }
        setDelegate(delegate);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public boolean askLocationPermissionIfNeeded() {
        String permissionType = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ContextCompat.checkSelfPermission(activity, permissionType)
            == PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionType)) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionType},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
//        if (delegate != null) {
//
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (delegate != null) {
            delegate.locationUpdate(location);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (delegate != null) {
            delegate.userApprovedLocationPermission();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (delegate != null) {
            delegate.userDeniedLocationPermission();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Deprecated. Allegedly.
    }
}

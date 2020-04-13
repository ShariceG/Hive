package coloredcoded.hive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private LocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // It's possible that for some reason the app hiccups and restarts on *this* activity
        // after SignInActivity. If that happens, the global environment will be null so we need to
        // make sure we recreate it.
        AppHelper.setupUserInEnvironmentIfNeeded(this);
        setupLocationServices();
    }

    public void setupLocationServices() {
        final AlertDialog alert = AppHelper.getPermanentAlert(this,
                "Trying to Find Your Location...",
                "If this takes a while, restart the app");
        final AlertDialog permanentDenialAlert = AppHelper.getPermanentAlert(this,
                "Ok...",
                "No GPS Location permissions, no app. *shrug*");

        final MainActivity that = this;
        HiveGlobal.environment.initLocationHandler(this, new LocationHandler.Delegate() {
            @Override
            public void userApprovedLocationPermission() {
                System.out.println("APPROVED for location services.");
                permanentDenialAlert.dismiss();
                locationHandler.requestLocationUpdates();
                AppHelper.presentAlert(that, alert);
            }

            @Override
            public void locationUpdate(Location location) {
                System.out.println("Got a location update, it is safe to init main activity.");
                alert.dismiss();
                initActivity();
            }
            @Override
            public void userTentativelyDeniedPermission() {
                System.out.println("TENTATIVELY DENIED for location services.");
                providePermissionJustificationToUser();
            }

            @Override
            public void userPermanentlyDeniedPermission() {
                System.out.println("PERMANENTLY DENIED for location services.");
                AppHelper.presentAlert(that, permanentDenialAlert);
            }


            @Override
            public void providePermissionJustificationToUser() {
                System.out.println("Showing justification for location services.");
                AppHelper.showAlert(that, "",
                        "Hive is a location-based social media. Please enable GPS and" +
                                " permit use of Location services!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                locationHandler.checkLocationPermission();
                            }
                        });
            }
        });
        locationHandler = HiveGlobal.environment.getLocationHandler();
        if (locationHandler.hasLocationPermissions()) {
            locationHandler.requestLocationUpdates();
        } else {
            locationHandler.checkLocationPermission();
        }
    }

    public void initActivity() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(viewPagerAdapter.getDefaultFragmentIndex());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHandler.checkLocationPermissionRequest(requestCode, permissions, grantResults);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private static final int DEFAULT_FRAGMENT_INDEX = 1;
        private ArrayList<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new ArrayList<>();
            fragments.add(MenuFragment.newInstance());
            fragments.add(HomeFragment.newInstance());
            fragments.add(PopularPostsFragment.newInstance());
        }

        public int getDefaultFragmentIndex() {
            return DEFAULT_FRAGMENT_INDEX;
        }

        public int getCount() {
            return fragments.size();
        }

        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}

/*
No indication that you can swipe left and right
Write Something -> change to icon with pencil and circle
Dynamic counter for character limit in post view
 */
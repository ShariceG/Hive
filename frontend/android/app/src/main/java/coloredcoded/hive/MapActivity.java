package coloredcoded.hive;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;
import coloredcoded.hive.client.StatusOr;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ServerClient client;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new ServerClientImp();
        setContentView(R.layout.map_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchAllPostLocations() {
        final MapActivity that = this;
        client.getAllPostLocations(new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr, Map<String, Object> notes) {
                Response response = responseOr.get();
                locations = response.getLocations();
                that.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Location location : locations) {
                            int lat = (int) Double.parseDouble(location.getLatitude());
                            int lon = (int) Double.parseDouble(location.getLongitude());
                            LatLng latLng = new LatLng(lat, lon);
                            Marker marker = map.addMarker(
                                    new MarkerOptions()
                                            .position(latLng)
                                            .title(location.getArea().getCity()));
                            marker.showInfoWindow();
                        }
                    }
                });

            }
        }, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        fetchAllPostLocations();
    }
}

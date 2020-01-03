package coloredcoded.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

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

        Button backButton = findViewById(R.id.mapBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchAllPostLocations() {
        final MapActivity that = this;
        client.getAllPostLocations(new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
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
                            marker.setTag(location);

                            // Have to generate an icon because showInfoWindow() doesn't work when
                            // there are multiple markers. Uncomment the code below if we actually
                            // want to show these icons. It looks kinda messy.
                            /**
                            IconGenerator iconFactory = new IconGenerator(
                                    getApplicationContext());
                            marker.setIcon(BitmapDescriptorFactory
                                            .fromBitmap(iconFactory.makeIcon(
                                                    location.getArea().getCity())));
                             **/
                        }
                    }
                });

            }
        }, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapActivity.this,
                        PostFeedFromMapActivity.class);
                intent.putExtra("location", (Location) marker.getTag());
                startActivity(intent);
                return false;
            }
        });
        fetchAllPostLocations();
    }
}

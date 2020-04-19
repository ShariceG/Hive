package coloredcoded.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.HiveLocation;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.StatusOr;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private ServerClient client;
    private ArrayList<HiveLocation> hiveLocations;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = AppHelper.serverClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_layout, container, false);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    private void fetchAllPostLocations() {
        client.getAllPostLocations(new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                Response response = responseOr.get();
                hiveLocations = response.getHiveLocations();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (HiveLocation hiveLocation : hiveLocations) {
                            int lat = (int) Double.parseDouble(hiveLocation.getLatitude());
                            int lon = (int) Double.parseDouble(hiveLocation.getLongitude());
                            LatLng latLng = new LatLng(lat, lon);
                            Marker marker = map.addMarker(
                                    new MarkerOptions()
                                            .position(latLng)
                                            .title(hiveLocation.getArea().getCity()));
                            marker.setTag(hiveLocation);

                            // Have to generate an icon because showInfoWindow() doesn't work when
                            // there are multiple markers. Uncomment the code below if we actually
                            // want to show these icons. It looks kinda messy.
                            /**
                            IconGenerator iconFactory = new IconGenerator(
                                    getApplicationContext());
                            marker.setIcon(BitmapDescriptorFactory
                                            .fromBitmap(iconFactory.makeIcon(
                                                    hiveLocation.getArea().getCity())));
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
                Intent intent = new Intent(getActivity(),
                        PostFeedFromMapActivity.class);
                intent.putExtra("location", (HiveLocation) marker.getTag());
                startActivity(intent);
                return false;
            }
        });
        fetchAllPostLocations();
    }
}

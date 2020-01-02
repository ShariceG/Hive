package coloredcoded.hive;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;
import coloredcoded.hive.client.StatusOr;

public class PopularPostsFragment extends Fragment {

    public class PopularRecyclerViewAdapter
            extends RecyclerView.Adapter<PopularRecyclerViewAdapter.ViewHolder> {

        // stores and recycles views as they are scrolled off screen
        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }

        private ArrayList<Location> popularLocations;

        // data is passed into the constructor
        PopularRecyclerViewAdapter(ArrayList<Location> locations) {
            popularLocations = locations;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setText("TEXT");
            return new ViewHolder(textView);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Location location = popularLocations.get(position);
            holder.textView.setText(location.getArea().toString());
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return popularLocations.size();
        }
    }

    public static PopularPostsFragment newInstance() {
        return new PopularPostsFragment();
    }

    private ServerClient client;
    private ArrayList<Location> popularLocations;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new ServerClientImp();
        popularLocations = new ArrayList<>();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.popular_posts_layout, container,
                false);
        RecyclerView rView = v.findViewById(R.id.popularRecyclerView);
        rView.setAdapter(new PopularRecyclerViewAdapter(popularLocations));
        rView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchPopularLocations();
        return rView;
    }

    public void fetchPopularLocations() {
        client.getPopularLocations(getPopularLocationsCallback(), null);
    }

    public Callback getPopularLocationsCallback() {
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                Response response = responseOr.get();
                popularLocations.clear();
                popularLocations.addAll(response.getLocations());
                Log.d("POPULAR", popularLocations.toString());
            }
        };
    }
}

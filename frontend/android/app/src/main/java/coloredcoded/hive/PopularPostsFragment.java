package coloredcoded.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Map;

import coloredcoded.hive.client.ActionType;
import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.HiveLocation;
import coloredcoded.hive.client.Post;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;
import coloredcoded.hive.client.StatusOr;

public class PopularPostsFragment extends Fragment implements PostFeedManager.Delegate,
        PopularRecyclerViewAdapter.Delegate {

    public static PopularPostsFragment newInstance() {
        return new PopularPostsFragment();
    }

    private ServerClient client;
    private ArrayList<HiveLocation> popularHiveLocations;
    private RecyclerView popularRecyclerView;
    private PopularRecyclerViewAdapter popularAdapter;
    private PostFeedManager postFeedManager;
    // Current location that was tapped by the user.
    private HiveLocation currentHiveLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new ServerClientImp();
        popularHiveLocations = new ArrayList<>();
        popularAdapter = new PopularRecyclerViewAdapter(getContext(), popularHiveLocations, this);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.popular_posts_layout, container,
                false);
        // Set up post feed manger.
        postFeedManager = new PostFeedManager(getContext());
        SwipeRefreshLayout refreshLayout = v.findViewById(R.id.popularPostSwipeRefresh);
        postFeedManager = new PostFeedManager(getContext());
        postFeedManager.configure((ListView) v.findViewById(R.id.popularPostListView),
                refreshLayout, this);
        postFeedManager.setDisableLikeAndDislikeButtons(true);

        // Set up recycler for popular locations.
        popularRecyclerView = v.findViewById(R.id.popularRecyclerView);
        popularRecyclerView.setAdapter(popularAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        popularRecyclerView.setLayoutManager(layoutManager);
        fetchPopularLocations();
        return v;
    }

    public void fetchPopularLocations() {
        client.getPopularLocations(getPopularLocationsCallback(), null);
    }

    public Callback getPopularLocationsCallback() {
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                final Response response = responseOr.get();
                if (response.getHiveLocations().isEmpty()) {
                    System.out.println("No popular locations!");
                }
                popularHiveLocations.clear();
                popularHiveLocations.addAll(response.getHiveLocations());
                popularRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        popularAdapter.notifyDataSetChanged();
                        boolean firstTime = currentHiveLocation == null;
                        if (!response.getHiveLocations().isEmpty()) {
                            currentHiveLocation = popularHiveLocations.get(0);
                            if (firstTime) {
                                // This is the first time we're doing this so lets have the posts load
                                // without having a user needing to click on it.
                                postFeedManager.resetDataAndPokeNew();
                            }
                        } else {
                            postFeedManager.reloadUI();
                        }
                    }
                });
            }
        };
    }

    // PopularRecyclerViewAdapter.Delegate overrides
    @Override
    public void onItemClick(View v) {
        int index = popularRecyclerView.getChildLayoutPosition(v);
        HiveLocation hiveLocation = popularHiveLocations.get(index);
        // No need to do any work if user is clicking on the same hiveLocation.
        if (hiveLocation.equals(currentHiveLocation)) {
            return;
        }
        currentHiveLocation = hiveLocation;
        postFeedManager.resetDataAndPokeNew();
    }

    // PostFeedManager overrides.
    @Override
    public void showComments(PostView postView) {
        Intent intent = new Intent(getActivity(), SeeCommentsActivity.class);
        intent.putExtra("post", postView.getPost());
        intent.putExtra("disallowCommentInteraction", true);
        getActivity().startActivity(intent);
    }

    @Override
    public void fetchMorePosts(QueryParams queryParams) {
        if (currentHiveLocation == null) {
            System.out.println("No currentHiveLocation set yet.");
            return;
        }
        client.getAllPopularPostsAtLocation(AppHelper.getLoggedInUsername(), currentHiveLocation,
                queryParams, getAllPopularPostsAtLocationCallback(), null);
    }

    public Callback getAllPopularPostsAtLocationCallback() {
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                Response response = responseOr.get();
                QueryMetadata newMetadata = response.getQueryMetadata();
                postFeedManager.addMorePosts(response.getPosts(), newMetadata);
            }
        };
    }

    @Override
    public void performAction(Post post, ActionType actionType) {
    }
}

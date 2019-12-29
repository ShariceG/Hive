package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.Map;

import coloredcoded.hive.client.ActionType;
import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.Post;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;
import coloredcoded.hive.client.StatusOr;

public class HomeFragment extends Fragment implements PostFeedManager.Delegate{

    private ServerClient client;
    private PostFeedManager postFeedManager;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new ServerClientImp();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_layout, container, false);
        ListView postFeedListView = v.findViewById(R.id.postFeedListView);
        postFeedManager = new PostFeedManager(getContext());
        postFeedManager.configure(postFeedListView, this);
        return v;
    }

    @Override
    public void fetchMorePosts(QueryParams queryParams) {
        Location location = new Location("-13.71000000", "-76.22000000",
                new Location.Area("-13.71", "-76.22",
                        "Seattle", "WA", "United States"));
        client.getAllPostsAtLocation("user1", location, queryParams,
                fetchMorePostsCallback(), null);
    }

    public Callback fetchMorePostsCallback() {
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
    public void showComments(PostView postView) {

    }

    @Override
    public void performAction(Post post, ActionType actionType) {

    }
}
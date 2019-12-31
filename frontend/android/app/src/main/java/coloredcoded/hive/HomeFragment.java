package coloredcoded.hive;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.HashMap;
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
        SwipeRefreshLayout refreshLayout = v.findViewById(R.id.postFeedSwipeRefresh);
        postFeedManager = new PostFeedManager(getContext());
        postFeedManager.configure(postFeedListView, refreshLayout, this);


        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Make a Post!");
        alert.setMessage("now!");
        alert.setView(inflater.inflate(R.layout.make_post_layout, container, false));

        alert.show();

        return v;
    }

    @Override
    public void fetchMorePosts(QueryParams queryParams) {
        client.getAllPostsAtLocation(testUser(), testLocation(), queryParams,
                getAllPostsAtLocationCallback(), null);
    }

    public Callback getAllPostsAtLocationCallback() {
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
        Intent intent = new Intent(getActivity(), SeeCommentsActivity.class);
        intent.putExtra("post", postView.getPost());
        getActivity().startActivity(intent);
    }

    @Override
    public void performAction(Post post, ActionType actionType) {
        Map<String, Object> notes = new HashMap<>();
        notes.put("postId", post.getPostId());
        notes.put("actionType", actionType);
        client.updatePost(testUser(), post.getPostId(), actionType,
                updatePostCallback(), notes);
    }

    public Callback updatePostCallback() {
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                Response response = responseOr.get();
                postFeedManager.updateActionType(
                        (String)notes.get("postId"), (ActionType)notes.get("actionType"));
            }
        };
    }

    private Location testLocation() {
        return new Location("47.608013", "-122.335167",
                new Location.Area("47.60", "-122.33",
                        "Seattle", "WA", "United States"));
    }

    private String testUser() {
        return "user1";
    }
}
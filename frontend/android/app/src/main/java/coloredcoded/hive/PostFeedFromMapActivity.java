package coloredcoded.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
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

public class PostFeedFromMapActivity extends AppCompatActivity implements PostFeedManager.Delegate {

    private PostFeedManager postFeedManager;
    private Location location;
    private ServerClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_feed_from_map_layout);
        client = new ServerClientImp();
        location = (Location) getIntent().getSerializableExtra("location");

        Button backButton = findViewById(R.id.mapPostFeedBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTextView = findViewById(R.id.locationTextView);
        titleTextView.setText(location.getArea().toString());

        postFeedManager = new PostFeedManager(getApplicationContext());
        postFeedManager.configure(
                (ListView) findViewById(R.id.mapPostFeedListView),
                (SwipeRefreshLayout) findViewById(R.id.mapPostFeedSwipeRefresh),
                this);
        postFeedManager.setDisableLikeAndDislikeButtons(true);
    }

    @Override
    public void showComments(PostView postView) {
        Intent intent = new Intent(PostFeedFromMapActivity.this,
                SeeCommentsActivity.class);
        intent.putExtra("post", postView.getPost());
        intent.putExtra("disallowMakingComments", true);
        startActivity(intent);
    }

    @Override
    public void fetchMorePosts(QueryParams queryParams) {
        client.getAllPostsAtLocation(testUser(), location, queryParams,
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
    public void performAction(Post post, ActionType actionType) {
    }

    private String testUser() {
        return "user1";
    }
}

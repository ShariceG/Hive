package coloredcoded.hive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class PostFeedFromMapActivity extends AppCompatActivity implements PostFeedManager.Delegate {

    private PostFeedManager postFeedManager;
    private HiveLocation hiveLocation;
    private ServerClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_feed_from_map_layout);
        client = new ServerClientImp();
        hiveLocation = (HiveLocation) getIntent().getSerializableExtra("location");

        Button backButton = findViewById(R.id.mapPostFeedBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTextView = findViewById(R.id.locationTextView);
        titleTextView.setText(hiveLocation.getArea().toString());

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
        intent.putExtra("disallowCommentInteraction", true);
        startActivity(intent);
    }

    @Override
    public void fetchMorePosts(QueryParams queryParams) {
        client.getAllPopularPostsAtLocation(testUser(), hiveLocation, queryParams,
                getAllPopularPostsAtLocationCallback(), null);
    }

    public Callback getAllPopularPostsAtLocationCallback() {
        final Activity that = this;
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                if (responseOr.hasError() || responseOr.get().serverReturnedWithError()) {
                    if (!responseOr.hasError()) {
                        System.out.println("ERROR_FROM_SERVER: " +
                                responseOr.get().getServerErrorStr());
                    }
                    postFeedManager.reloadUI();
                    AppHelper.showInternalServerErrorAlert(that);
                    return;
                }
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

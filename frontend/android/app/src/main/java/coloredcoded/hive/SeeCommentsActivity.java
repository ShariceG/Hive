package coloredcoded.hive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.Post;
import coloredcoded.hive.client.QueryParams;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;
import coloredcoded.hive.client.StatusOr;

public class SeeCommentsActivity extends AppCompatActivity implements CommentFeedManager.Delegate {

    private ServerClient client;
    private CommentFeedManager commentFeedManager;
    private PostView postView;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The only way we get here is from another activity transitioning to us. So we expect some
        // extra information regarding the post whose comments we are displaying.
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra("post");

        setContentView(R.layout.see_comments_layout);
        client = new ServerClientImp();
        commentFeedManager = new CommentFeedManager(getApplicationContext());
        commentFeedManager.configure((ListView) findViewById(R.id.commentFeedListView), this);
    }

    @Override
    public void fetchMoreComments(QueryParams queryParams) {
        client.getAllPostComments(post.getPostId(), queryParams,
                getAllPostCommentsCallback(), null);
    }

    public Callback getAllPostCommentsCallback() {
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                Response response = responseOr.get();
                commentFeedManager.addMoreComments(response.getComments(),
                        response.getQueryMetadata());
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
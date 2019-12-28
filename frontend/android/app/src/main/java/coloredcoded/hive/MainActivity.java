package coloredcoded.hive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.Post;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;
import coloredcoded.hive.client.StatusOr;

public class MainActivity extends AppCompatActivity {

    public static class FirstFragment extends Fragment {

        private ArrayList<Post> posts;
        private ServerClient client;
        private QueryMetadata prevQueryMetadata;

        public static FirstFragment newInstance() {
            return new FirstFragment();
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            posts = new ArrayList<>();
            client = new ServerClientImp();
            prevQueryMetadata = new QueryMetadata();
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.home_layout, container);
            ListView postFeedListView = v.findViewById(R.id.postFeedListView);

            getMorePosts(true);
            postFeedListView.setAdapter(new PostFeedAdapter(getContext(), posts));
            return v;
        }

        public void getMorePosts(boolean newerPosts) {
            // TODO: SANITIZE URL so that spaces are encoded properly
            Location location = new Location("47.608013", "-122.335167",
                    new Location.Area("47.60", "-122.33",
                            "Seattle", "WA", "United States"));
            QueryParams params = new QueryParams(newerPosts,
                    prevQueryMetadata.getNewTopCursorStr(),
                    prevQueryMetadata.getNewBottomCursorStr());
            client.getAllPostsAtLocation("user1", location, params,
                    getMorePostsCallback(), null);
        }

        public Callback getMorePostsCallback() {
            return new Callback() {
                @Override
                public void serverRequestCallback(StatusOr<Response> responseOr,
                                                  Map<String, Object> notes) {
                    Response response = responseOr.get();
                    QueryMetadata newMetadata = response.getQueryMetadata();
                    prevQueryMetadata.updateMetadata(newMetadata);
                    ArrayList<Post> newPosts = response.getPosts();
                    posts.addAll(newPosts);
                    Collections.sort(posts, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return (int) (o2.getCreationTimestampSec() -
                                    o1.getCreationTimestampSec());
                        }
                    });
                }
            };
        }
    }

    private FragmentPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapater(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
    }
}

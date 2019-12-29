package coloredcoded.hive;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import coloredcoded.hive.client.ActionType;
import coloredcoded.hive.client.Post;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;

// Manages all the things having to do with interacting with a post feed. Essentially, is managing
// a list view.
public class PostFeedManager implements PostView.Delegate {

    // Will delegate tasks to the fragment that created it.
    public interface Delegate {
        void showComments(PostView postView);
        void fetchMorePosts(QueryParams queryParams);
        void performAction(Post post, ActionType actionType);
    }

    // Adapter in charge of making sure that the post feed displays correctly. PostFeedManager could
    // subclass ArrayAdapter directly. Perhaps we should consider doing that.
    private class PostFeedAdapter extends ArrayAdapter<Post> {

        private PostView.Delegate postViewDelegate;

        public PostFeedAdapter(Context context, PostView.Delegate delegate, ArrayList<Post> thePosts) {
            super(context, -1, thePosts);
            posts = thePosts;
            postViewDelegate = delegate;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return PostView.newInstance(getItem(position), postViewDelegate, parent);
        }
    }

    private ArrayList<Post> posts;
    private QueryMetadata prevQueryMetadata;
    private ArrayAdapter<Post> postFeedAdapter;
    private ListView postFeedListView;
    private Delegate delegate;

    public PostFeedManager(Context context) {
        posts = new ArrayList<>();
        prevQueryMetadata = new QueryMetadata();
        postFeedAdapter = new PostFeedAdapter(context, this, posts);
    }

    public void configure(ListView listView, Delegate d) {
        postFeedListView = listView;
        delegate = d;
        postFeedListView.setAdapter(postFeedAdapter);

        // Fetch an initial set of posts.
        fetchMorePosts(true);
    }

    private void fetchMorePosts(boolean getNewer) {
        if (!getNewer && !prevQueryMetadata.hasMoreOlderData()) {
            Log.d("PostFeedManager",
                    "Server already told us there are no more old posts. Returning.");
            return;
        }
        QueryParams params = new QueryParams(getNewer,
                prevQueryMetadata.getNewTopCursorStr(),
                prevQueryMetadata.getNewBottomCursorStr());
        delegate.fetchMorePosts(params);
    }

    // Updates the list view. Must be called on the UI thread.
    public void reload() {
        postFeedListView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        postFeedAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    public void addMorePosts(ArrayList<Post> morePosts, QueryMetadata newMetadata) {
        prevQueryMetadata.updateMetadata(newMetadata);
        posts.addAll(morePosts);

        // Remove expired hosts.
        Iterator<Post> itr = posts.iterator();
        while (itr.hasNext()) {
            if (itr.next().isExpired()) {
                itr.remove();
            }
        }

        // Sort posts.
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return (int) (o2.getCreationTimestampSec() -
                        o1.getCreationTimestampSec());
            }
        });

        reload();
    }

    @Override
    public void commentButtonClick(PostView postView) {
        Log.d("commentButtonClick", "commentButtonClick: ");
    }

    @Override
    public void performAction(PostView postView, ActionType actionType) {
        Log.d("performAction", "performAction: ");
    }
}

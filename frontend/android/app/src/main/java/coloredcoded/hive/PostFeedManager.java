package coloredcoded.hive;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

        public PostFeedAdapter(Context context, PostView.Delegate delegate,
                               ArrayList<Post> thePosts) {
            super(context, -1, thePosts);
            postViewDelegate = delegate;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            PostView pv = PostView.newInstance(getItem(position), postViewDelegate, parent);
            if (disableLikeAndDislikeButtons) {
                pv.disableLikeAndDislikeButtons();
            }
            return pv.getPostView();
        }
    }

    private ArrayList<Post> posts;
    private QueryMetadata prevQueryMetadata;
    private ArrayAdapter<Post> postFeedAdapter;
    private ListView postFeedListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Delegate delegate;
    private boolean disableLikeAndDislikeButtons;

    public PostFeedManager(Context context) {
        posts = new ArrayList<>();
        prevQueryMetadata = new QueryMetadata();
        postFeedAdapter = new PostFeedAdapter(context, this, posts);
        disableLikeAndDislikeButtons = false;
    }

    public void configure(ListView listView, SwipeRefreshLayout refreshLayout, Delegate delegate) {
        postFeedListView = listView;
        this.delegate = delegate;
        postFeedListView.setAdapter(postFeedAdapter);
        swipeRefreshLayout = refreshLayout;

        setupListeners();
        fetchMorePosts(true);
    }

    public Post findPostById(String postId) {
        Post post = null;
        Iterator<Post> itr = posts.iterator();
        while (itr.hasNext()) {
            post = itr.next();
            if (post.getPostId().equals(postId)) {
                break;
            }
        }
        return post;
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("Refreshing..");
                fetchMorePosts(true);
            }
        });
        postFeedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (swipeRefreshLayout.isRefreshing()) {
                    return;
                }
                if (posts.isEmpty()) {
                    return;
                }
                // If we are displaying the last set of items in the list view.
                if (firstVisibleItem + visibleItemCount >= totalItemCount -1) {
                    fetchMorePosts(false);
                }
            }
        });
    }

    public void setRefreshAnimation(final boolean set) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(set);
            }
        });
    }

    private void fetchMorePosts(boolean getNewer) {
        System.out.println("Fetching " + (getNewer ? "newer" : "older") + " posts");
        if (!getNewer && !prevQueryMetadata.hasMoreOlderData()) {
            System.out.println("Server already told us there are no more old posts. Returning.");
            return;
        }
        if (getNewer) {
            setRefreshAnimation(true);
        }
        QueryParams params = new QueryParams(getNewer,
                prevQueryMetadata.getNewTopCursorStr(),
                prevQueryMetadata.getNewBottomCursorStr());
        delegate.fetchMorePosts(params);
    }

    // Updates the list view. Must be called on the UI thread.
    public void reloadUI() {
        postFeedListView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        postFeedAdapter.notifyDataSetChanged();
                        setRefreshAnimation(false);
                    }
                }
        );
    }

    public void addMorePosts(ArrayList<Post> morePosts, QueryMetadata newMetadata) {
        prevQueryMetadata.updateMetadata(newMetadata);

        HashSet<Post> set = new HashSet<>(morePosts);

        // Merge old posts and new (morePosts) posts. If a post exists in old and in new, take the
        // new post.
        for (Post p : posts) {
            if (!set.contains(p)) {
                set.add(p);
            }
        }

        // Don't include expired hosts while transferring from set back to posts.
        Iterator<Post> itr = set.iterator();
        posts.clear();
        while (itr.hasNext()) {
            Post p = itr.next();
            if (!p.isExpired()) {
                posts.add(p);
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

        reloadUI();
    }

    public void incrementNumberOfComments(String postId) {
        findPostById(postId).incrementNumberOfComments();
    }

    public void updateActionType(String postId, ActionType actionType) {
        // Find post with id.
        Post post = findPostById(postId);
        Iterator<Post> itr = posts.iterator();

        // Now change the # of likes and dislikes by 1 depending on the old and
        // new action type.
        ActionType newActionType = actionType;
        ActionType oldActionType = post.getUserActionType();
        if (oldActionType == ActionType.LIKE) {
            post.addLikes(-1);
            if (newActionType == ActionType.DISLIKE) {
                post.addDislikes(1);
            }
        }
        if (oldActionType == ActionType.DISLIKE) {
            post.addDislikes(-1);
            if (newActionType == ActionType.LIKE) {
                post.addLikes(1);
            }
        }
        if (oldActionType == ActionType.NO_ACTION) {
            switch (newActionType) {
                case LIKE:
                    post.addLikes(1);
                    break;
                case DISLIKE:
                    post.addDislikes(1);
                    break;
                case NO_ACTION:
                    Log.e("updateActionType",
                            "WARNING: This shouldn't happen but its not really an error.");
                    break;
            }
        }
        post.setUserActionType(newActionType);
        reloadUI();
    }

    public void resetData() {
        posts.clear();
        prevQueryMetadata = new QueryMetadata();
    }

    public void pokeNew() {
        fetchMorePosts(true);
    }

    public void resetDataAndPokeNew() {
        System.out.println("Attempting to reset data");
        resetData();
        pokeNew();
    }

    public void setDisableLikeAndDislikeButtons(boolean set) {
        disableLikeAndDislikeButtons = set;
    }

    @Override
    public void commentButtonClick(PostView postView) {
        delegate.showComments(postView);
    }

    @Override
    public void performAction(PostView postView, ActionType actionType) {
        delegate.performAction(postView.getPost(), actionType);
    }
}

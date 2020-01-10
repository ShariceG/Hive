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
import coloredcoded.hive.client.Comment;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;

public class CommentFeedManager implements CommentView.Delegate {

    // Will delegate tasks to the activity that created it.
    public interface Delegate {
        void fetchMoreComments(QueryParams queryParams);
        void performAction(Comment comment, ActionType actionType);
    }

    private class CommentFeedAdapter extends ArrayAdapter<Comment> {

        private CommentView.Delegate commentViewDelegate;

        public CommentFeedAdapter(Context context, CommentView.Delegate delegate,
                                  ArrayList<Comment> theComments) {
            super(context, -1, theComments);
            commentViewDelegate = delegate;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return CommentView.newInstance(getItem(position), commentViewDelegate, parent);
        }
    }

    private ArrayList<Comment> comments;
    private QueryMetadata prevQueryMetadata;
    private ArrayAdapter<Comment> commentFeedAdapter;
    private ListView commentFeedListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Delegate delegate;

    public CommentFeedManager(Context context) {
        comments = new ArrayList<>();
        prevQueryMetadata = new QueryMetadata();
        commentFeedAdapter = new CommentFeedAdapter(context, this, comments);
    }

    public void configure(ListView listView, SwipeRefreshLayout refreshLayout, Delegate d) {
        commentFeedListView = listView;
        swipeRefreshLayout = refreshLayout;
        delegate = d;
        commentFeedListView.setAdapter(commentFeedAdapter);
        fetchMoreComments(true);
        setupListeners();
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMoreComments(true);
            }
        });
        commentFeedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                // If we are displaying the last set of items in the list view.
                if (firstVisibleItem + visibleItemCount >= totalItemCount -1) {
                    fetchMoreComments(false);
                }
            }
        });
    }

    private void fetchMoreComments(boolean getNewer) {
        if (!getNewer && !prevQueryMetadata.hasMoreOlderData()) {
            Log.d("CommentFeedManager",
                    "Server already told us there are no more old comments. Returning.");
            return;
        }
        QueryParams params = new QueryParams(getNewer,
                prevQueryMetadata.getNewTopCursorStr(),
                prevQueryMetadata.getNewBottomCursorStr());
        delegate.fetchMoreComments(params);
    }

    // Updates the list view. Must be called on the UI thread.
    public void reload() {
        commentFeedListView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        commentFeedAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    public void pokeNew() {
        fetchMoreComments(true);
    }

    public void addMoreComments(ArrayList<Comment> moreComments, QueryMetadata newMetadata) {
        prevQueryMetadata.updateMetadata(newMetadata);

        HashSet<Comment> set = new HashSet<>(comments);
        for (Comment comment : moreComments) {
            set.add(comment);
        }

        // Don't include expired hosts while transferring from set to post.
        Iterator<Comment> itr = set.iterator();
        comments.clear();
        while (itr.hasNext()) {
            Comment p = itr.next();
            comments.add(p);
        }

        // Sort posts.
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return (int) (o2.getCreationTimestampSec() -
                        o1.getCreationTimestampSec());
            }
        });

        reload();
    }

    public void updateActionType(String commentId, ActionType actionType) {
        // Find comment with id.
        Comment comment = null;
        Iterator<Comment> itr = comments.iterator();
        while (itr.hasNext()) {
            comment = itr.next();
            if (comment.getCommentId().equals(commentId)) {
                break;
            }
        }

        // Now change the # of likes and dislikes by 1 depending on the old and
        // new action type.
        ActionType newActionType = actionType;
        ActionType oldActionType = comment.getUserActionType();
        if (oldActionType == ActionType.LIKE) {
            comment.addLikes(-1);
            if (newActionType == ActionType.DISLIKE) {
                comment.addDislikes(1);
            }
        }
        if (oldActionType == ActionType.DISLIKE) {
            comment.addDislikes(-1);
            if (newActionType == ActionType.LIKE) {
                comment.addLikes(1);
            }
        }
        if (oldActionType == ActionType.NO_ACTION) {
            switch (newActionType) {
                case LIKE:
                    comment.addLikes(1);
                    break;
                case DISLIKE:
                    comment.addDislikes(1);
                    break;
                case NO_ACTION:
                    Log.e("updateActionType",
                            "WARNING: This shouldn't happen but its not really an error.");
                    break;
            }
        }
        comment.setUserActionType(newActionType);
        reload();
    }

    @Override
    public void performAction(CommentView commentView, ActionType actionType) {
        delegate.performAction(commentView.getComment(), actionType);
    }

}

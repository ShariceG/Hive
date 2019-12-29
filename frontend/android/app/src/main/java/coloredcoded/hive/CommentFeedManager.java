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

import coloredcoded.hive.client.Comment;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;

public class CommentFeedManager implements CommentView.Delegate {

    // Will delegate tasks to the activity that created it.
    public interface Delegate {
        void fetchMoreComments(QueryParams queryParams);
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
    private Delegate delegate;

    public CommentFeedManager(Context context) {
        comments = new ArrayList<>();
        prevQueryMetadata = new QueryMetadata();
        commentFeedAdapter = new CommentFeedAdapter(context, this, comments);
    }

    public void configure(ListView listView, Delegate d) {
        commentFeedListView = listView;
        delegate = d;
        commentFeedListView.setAdapter(commentFeedAdapter);
        fetchMoreComments(true);
        setupListeners();
    }

    private void setupListeners() {
//        commentFeedListView.setOnScrollListener();
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
                    }
                }
        );
    }

    public void addMoreComments(ArrayList<Comment> moreComments, QueryMetadata newMetadata) {
        prevQueryMetadata.updateMetadata(newMetadata);
        comments.addAll(moreComments);
        // Sort comments.
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return (int) (o2.getCreationTimestampSec() -
                        o1.getCreationTimestampSec());
            }
        });
        reload();
    }
}

package coloredcoded.hive;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import coloredcoded.hive.client.ActionType;
import coloredcoded.hive.client.Post;

// This class represents a View. It, itself, is not a view but, through, composition, holds a post
// view. Using getPostView() you may get the real view.
public class PostView {

    public interface Delegate {
        void commentButtonClick(PostView postView);
        void performAction(PostView postView, ActionType actionType);
    }

    private TextView userTextView;
    private TextView dateTextView;
    private TextView postTextView;
    private Button commentButton;
    private Button likeButton;
    private Button dislikeButton;
    // The entire inflated view of a post.
    private View postView;

    private Delegate delegate;
    private Post post;

    public static View newViewInstance(Post post, Delegate delegate, ViewGroup parent) {
        return newInstance(post, delegate, parent).getPostView();
    }

    public static PostView newInstance(Post post, Delegate delegate, ViewGroup parent) {
        PostView view = new PostView();
        view.configure(post, delegate, parent);
        return view;
    }

    // Overload of newInstance but this time, we take an existing post view and fill it with the
    // post information. Yes this is confusing. This function so that we can reconstruct a post view
    // when we see comments.
    public static PostView newInstance(Post post, View view, Delegate delegate, ViewGroup parent) {
        PostView pView = new PostView();
        pView.configure(post, view, delegate, parent);
        return pView;
    }

    public PostView() {
        postView = null;
    }

    public void instantiate(ViewGroup parent) {
        if (postView == null) {
            postView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_layout, parent, false);
        }
        userTextView = postView.findViewById(R.id.userTextView);
        dateTextView = postView.findViewById(R.id.dateTextView);
        postTextView = postView.findViewById(R.id.postTextView);
        commentButton = postView.findViewById(R.id.commentButton);
        likeButton = postView.findViewById(R.id.likeButton);
        dislikeButton = postView.findViewById(R.id.dislikeButton);
    }

    public void configure(Post p, View view, Delegate d, ViewGroup parent) {
        postView = view;
        configure(p, d, parent);
    }

    public void configure(Post p, Delegate d, ViewGroup parent) {
        instantiate(parent);
        post = p;
        delegate = d;
        userTextView.setText(post.getUsername());
        postTextView.setText(post.getPostText());
        likeButton.setText("Like: " + post.getLikes());
        dislikeButton.setText("Dislike: " + post.getDislikes());
        DateFormat f = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        f.setTimeZone(TimeZone.getDefault());
        dateTextView.setText(f.format(new Date(post.getCreationTimestampSecAsLong() * 1000)));
        setupListeners();
    }

    private void setupListeners() {
        final PostView postView = this;
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.commentButtonClick(postView);
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                switch (post.getUserActionType()) {
                    case NO_ACTION:
                    case DISLIKE:
                        delegate.performAction(postView, ActionType.LIKE);
                        break;
                    case LIKE:
                        delegate.performAction(postView, ActionType.NO_ACTION);
                        break;
                }
            }
        });
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                switch (post.getUserActionType()) {
                    case NO_ACTION:
                    case LIKE:
                        delegate.performAction(postView, ActionType.DISLIKE);
                        break;
                    case DISLIKE:
                        delegate.performAction(postView, ActionType.NO_ACTION);
                        break;
                }
            }
        });
    }

    public void disableAllButtons() {
        likeButton.setEnabled(false);
        dislikeButton.setEnabled(false);
        commentButton.setEnabled(false);
    }

    public void disableLikeAndDislikeButtons() {
        likeButton.setEnabled(false);
        dislikeButton.setEnabled(false);
    }

    public View getPostView() {
        return postView;
    }

    public Post getPost() {
        return post;
    }

}

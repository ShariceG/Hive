package coloredcoded.hive;

import android.content.Context;
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
    private View postView;

    private Delegate delegate;
    private Post post;

    public static View newInstance(Post post, Delegate delegate, ViewGroup parent) {
        PostView view = new PostView();
        view.configure(post, delegate, parent);
        return view.getPostView();
    }

    public void instantiate(ViewGroup parent) {
        postView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout, parent, false);
        userTextView = postView.findViewById(R.id.userTextView);
        dateTextView = postView.findViewById(R.id.dateTextView);
        postTextView = postView.findViewById(R.id.postTextView);
        commentButton = postView.findViewById(R.id.commentButton);
        likeButton = postView.findViewById(R.id.likeButton);
        dislikeButton = postView.findViewById(R.id.dislikeButton);
    }

    public void configure(Post p, Delegate d, ViewGroup parent) {
        instantiate(parent);
        post = p;
        delegate = d;
        userTextView.setText(post.getUsername());
        postTextView.setText(post.getPostText());
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
                delegate.performAction(postView, ActionType.LIKE);
            }
        });
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.performAction(postView, ActionType.DISLIKE);
            }
        });
    }

    public View getPostView() {
        return postView;
    }

}

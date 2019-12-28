package coloredcoded.hive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import coloredcoded.hive.client.Post;

// This class represents a View. It, itself, is not a view but, through, composition, holds a post
// view. Using getPostView() you may get the real view.
public class PostView {

    private TextView userTextView;
    private TextView dateTextView;
    private TextView postTextView;
    private Button commentButton;
    private Button likeButton;
    private Button dislikeButton;
    private View postView;

    public static View newInstance(Context context, Post post, ViewGroup parent) {
        PostView view = new PostView();
        view.configure(post, parent);
        return view.getPostView();
    }

    public void instantiate(ViewGroup parent) {
        postView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout, parent);
        userTextView = postView.findViewById(R.id.userTextView);
        dateTextView = postView.findViewById(R.id.dateTextView);
        postTextView = postView.findViewById(R.id.postTextView);
        commentButton = postView.findViewById(R.id.commentButton);
        likeButton = postView.findViewById(R.id.likeButton);
        dislikeButton = postView.findViewById(R.id.dislikeButton);
    }

    public void configure(Post post, ViewGroup parent) {
        instantiate(parent);
        userTextView.setText(post.getUsername());
        postTextView.setText(post.getPostText());
    }

    public View getPostView() {
        return postView;
    }

}

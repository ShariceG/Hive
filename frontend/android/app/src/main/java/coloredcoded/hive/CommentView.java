package coloredcoded.hive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import coloredcoded.hive.client.ActionType;
import coloredcoded.hive.client.Comment;

public class CommentView {

    public interface Delegate {
        void performAction(CommentView commentView, ActionType actionType);
    }

    // The entire inflated view of a comment.
    private View commentView;
    private TextView userTextView;
    private TextView commentTextView;
    private Button likeButton;
    private Button dislikeButton;

    private Delegate delegate;
    private Comment comment;

    public static View newInstance(Comment comment, Delegate delegate, ViewGroup parent) {
        CommentView view = new CommentView();
        view.configure(comment, delegate, parent);
        return view.getCommentView();
    }

    public void instantiate(ViewGroup parent) {
        commentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_layout, parent, false);
        userTextView = commentView.findViewById(R.id.userTextView);
        commentTextView = commentView.findViewById(R.id.commentTextView);
        likeButton = commentView.findViewById(R.id.commentLikeButton);
        dislikeButton = commentView.findViewById(R.id.commentDislikeButton);
        setupListeners();
    }

    public void configure(Comment c, Delegate d, ViewGroup parent) {
        instantiate(parent);
        comment = c;
        delegate = d;
        userTextView.setText(c.getUsername());
        commentTextView.setText(c.getCommentText());
        likeButton.setText("Like: " + c.getLikes());
        dislikeButton.setText("Dislike: " + c.getDislikes());

    }

    private void setupListeners() {
        final CommentView that = this;
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLikeAndDislikeButtons();
                v.setEnabled(false);
                switch (comment.getUserActionType()) {
                    case NO_ACTION:
                    case DISLIKE:
                        delegate.performAction(that, ActionType.LIKE);
                        break;
                    case LIKE:
                        delegate.performAction(that, ActionType.NO_ACTION);
                        break;
                }
            }
        });
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLikeAndDislikeButtons();
                v.setEnabled(false);
                switch (comment.getUserActionType()) {
                    case NO_ACTION:
                    case LIKE:
                        delegate.performAction(that, ActionType.DISLIKE);
                        break;
                    case DISLIKE:
                        delegate.performAction(that, ActionType.NO_ACTION);
                        break;
                }
            }
        });
    }

    public void disableAllButtons() {
        likeButton.setEnabled(false);
        dislikeButton.setEnabled(false);
    }

    public void disableLikeAndDislikeButtons() {
        likeButton.setEnabled(false);
        dislikeButton.setEnabled(false);
    }

    public View getCommentView() { return commentView; }

    public Comment getComment() { return comment; }

}

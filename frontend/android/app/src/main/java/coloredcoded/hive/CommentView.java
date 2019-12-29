package coloredcoded.hive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import coloredcoded.hive.client.Comment;

public class CommentView {

    public interface Delegate {
    }

    // The entire inflated view of a comment.
    private View commentView;
    private TextView userTextView;
    private TextView commentTextView;

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
    }

    public void configure(Comment c, Delegate d, ViewGroup parent) {
        instantiate(parent);
        comment = c;
        delegate = d;
        userTextView.setText(c.getUsername());
        commentTextView.setText(c.getCommentText());
    }

    public View getCommentView() { return commentView; }

    public Comment getComment() { return comment; }

}

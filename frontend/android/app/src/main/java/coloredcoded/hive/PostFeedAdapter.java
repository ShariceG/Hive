package coloredcoded.hive;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import coloredcoded.hive.client.Post;

public class PostFeedAdapter extends ArrayAdapter<Post> {

    public PostFeedAdapter(Context context, ArrayList<Post> posts) {
        super(context, -1, posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return PostView.newInstance(getContext(), getItem(position), parent);
    }
}

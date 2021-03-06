package coloredcoded.hive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.HashMap;
import java.util.Map;

import coloredcoded.hive.client.ActionType;
import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Post;
import coloredcoded.hive.client.QueryMetadata;
import coloredcoded.hive.client.QueryParams;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.StatusOr;

public class HomeFragment extends Fragment implements PostFeedManager.Delegate {

    private ServerClient client;
    private PostFeedManager postFeedManager;
    private View makePostView;
    private AlertDialog makePostAlert;
    private Button writeSomethingButton;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSystemService(Context.LOCATION_SERVICE);
        client = AppHelper.serverClient();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_layout, container, false);
        ListView postFeedListView = v.findViewById(R.id.postFeedListView);
        SwipeRefreshLayout refreshLayout = v.findViewById(R.id.postFeedSwipeRefresh);
        postFeedManager = new PostFeedManager(getContext());
        postFeedManager.configure(postFeedListView, refreshLayout, this);

        // Setup alert dialog box for making a post.
        makePostView = inflater.inflate(R.layout.make_post_layout, container, false);
        makePostAlert = new AlertDialog.Builder(getContext()).create();
        makePostAlert.setView(makePostView);
        makePostAlert.setCancelable(false);
        makePostAlert.setCanceledOnTouchOutside(false);
        makePostAlert.getWindow().getAttributes().gravity = Gravity.TOP;
        makePostView.findViewById(R.id.makePostButton).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = makePostView.findViewById(R.id.postEditText);
                String text = et.getText().toString().trim();
                if (text.isEmpty()) {
                    return;
                }
                makePostAlert.dismiss();
                insertPost(text);
            }
        });
        makePostView.findViewById(R.id.cancelMakePostButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        writeSomethingButton.post(new Runnable() {
                            @Override
                            public void run() {
                                // We assume this won't be null when this Runnable function fires...
                                writeSomethingButton.setEnabled(true);
                            }
                        });
                        makePostAlert.dismiss();
                    }
                });
        writeSomethingButton =  v.findViewById(R.id.writeSomethingButton);
        writeSomethingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeSomethingButton.setEnabled(false);
                makePostAlert.show();
                makePostAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        makePostAlert.getWindow().getAttributes().height);
            }
        });
        return v;
    }

    private void insertPost(String text) {
        postFeedManager.setRefreshAnimation(true);
        client.insertPost(AppHelper.getLoggedInUsername(), text,
                AppHelper.getCurrentUserLocation(),
                getInsertPostCallback(), null);
    }

    private Callback getInsertPostCallback() {
        final Fragment that = this;
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                if (responseOr.hasError() || responseOr.get().serverReturnedWithError()) {
                    if (!responseOr.hasError()) {
                        System.out.println("ERROR_FROM_SERVER: " +
                                responseOr.get().getServerErrorStr());
                    }
                    postFeedManager.reloadUI();
                    if (AppHelper.isFragmentVisibleToUser(that)) {
                        AppHelper.showInternalServerErrorAlert(getActivity());
                    }
                    return;
                }
                postFeedManager.addMorePosts(responseOr.get().getPosts(),
                        new QueryMetadata());
                EditText et = makePostView.findViewById(R.id.postEditText);
                et.getText().clear();
                writeSomethingButton.post(new Runnable() {
                    @Override
                    public void run() {
                        writeSomethingButton.setEnabled(true);
                    }
                });
            }
        };
    }

    @Override
    public void fetchMorePosts(QueryParams queryParams) {
        client.getAllPostsAtLocation(AppHelper.getLoggedInUsername(),
                AppHelper.getCurrentUserLocation(), queryParams,
                getAllPostsAtLocationCallback(), null);
    }

    public Callback getAllPostsAtLocationCallback() {
        final Fragment that = this;
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                if (responseOr.hasError() || responseOr.get().serverReturnedWithError()) {
                    if (!responseOr.hasError()) {
                        System.out.println("ERROR_FROM_SERVER: " +
                                responseOr.get().getServerErrorStr());
                    }
                    postFeedManager.reloadUI();
                    if (AppHelper.isFragmentVisibleToUser(that)) {
                        AppHelper.showInternalServerErrorAlert(getActivity());
                    }
                    return;
                }
                Response response = responseOr.get();
                QueryMetadata newMetadata = response.getQueryMetadata();
                postFeedManager.addMorePosts(response.getPosts(), newMetadata);
            }
        };
    }

    @Override
    public void showComments(PostView postView) {
        Intent intent = new Intent(getActivity(), SeeCommentsActivity.class);
        intent.putExtra("post", postView.getPost());
        getActivity().startActivity(intent);
    }

    @Override
    public void performAction(Post post, ActionType actionType) {
        Map<String, Object> notes = new HashMap<>();
        notes.put("postId", post.getPostId());
        notes.put("actionType", actionType);
        client.updatePost(AppHelper.getLoggedInUsername(), post.getPostId(), actionType,
                updatePostCallback(), notes);
    }

    public Callback updatePostCallback() {
        final Fragment that = this;
        return new Callback() {
            @Override
            public void serverRequestCallback(StatusOr<Response> responseOr,
                                              Map<String, Object> notes) {
                if (responseOr.hasError() || responseOr.get().serverReturnedWithError()) {
                    if (!responseOr.hasError()) {
                        System.out.println("ERROR_FROM_SERVER: " +
                                responseOr.get().getServerErrorStr());
                    }
                    postFeedManager.reloadUI();
                    if (AppHelper.isFragmentVisibleToUser(that)) {
                        AppHelper.showInternalServerErrorAlert(getActivity());
                    }
                    return;
                }
                postFeedManager.updateActionType(
                        (String)notes.get("postId"), (ActionType)notes.get("actionType"));
            }
        };
    }
}
package coloredcoded.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import coloredcoded.hive.client.User;

public class MenuFragment extends Fragment {

    public static MenuFragment newInstance() { return new MenuFragment(); }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_layout, container, false);
        final User user = HiveGlobal.getEnvironment().getUser();
        Button logOutButton = v.findViewById(R.id.logOutButton);
        TextView usernameTextView = v.findViewById(R.id.usernameTextView);
        usernameTextView.setText("User: " + user.getUsername());
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.removeFromInternalStorage(getActivity());
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        return v;
    }
}

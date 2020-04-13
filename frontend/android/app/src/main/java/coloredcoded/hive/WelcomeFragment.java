package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.Map;

public class WelcomeFragment extends Fragment implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_layout, container,
                false);
        Button beginButton = v.findViewById(R.id.beginButton);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.goToMainApp();
            }
        });
        return v;
    }

    @Override
    public void setSignInPageDelegate(SignInActivity.SignInDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setArgs(Map<String, Object> notes) {
    }
}

package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.Map;

public class LogInOrSignUpFragment extends Fragment implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view for the fragment based on layout XML
        View v = inflater.inflate(R.layout.log_in_or_sign_up_layout, container, false);
        Button logInButton = v.findViewById(R.id.logInButton);
        Button signUpButton = v.findViewById(R.id.signUpButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.goEnterEmailAddress(null);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.goEnterEmailAddressAndUsername(null);
            }
        });
        return v;
    }

    @Override
    public void setSignInPageDelegate(SignInActivity.SignInDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setNotes(Map<String, Object> args) {
    }
}

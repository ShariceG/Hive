package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class EnterEmailAndUsernameFragment extends Fragment
        implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view for the fragment based on layout XML
        View v = inflater.inflate(R.layout.enter_email_and_username_layout, container,
                false);
        final EditText emailEditText = v.findViewById(R.id.enterEmailEditText);
        final EditText usernameEditText = v.findViewById(R.id.enterUsernameEditText);
        Button nextButton = v.findViewById(R.id.enterEmailUsernameNextButton);
        Button goBackButton = v.findViewById(R.id.enterEmailUsernameGoBackButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                if (email.isEmpty() || username.isEmpty()) {
                    return;
                }
                Map<String, Object> notes = new HashMap<>();
                notes.put("username", username);
                notes.put("email", email);
                delegate.goEnterPinCode(notes);
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.goLogInOrSignUp();
            }
        });
        return v;
    }

    @Override
    public void setSignInPageDelegate(SignInActivity.SignInDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setNotes(Map<String, Object> notes) {
    }
}


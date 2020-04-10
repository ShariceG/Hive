package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.Map;

public class EnterEmailFragment extends Fragment implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view for the fragment based on layout XML
        View v = inflater.inflate(R.layout.enter_email_layout, container, false);
        final EditText emailEditText = v.findViewById(R.id.enterEmailEditText);
        Button nextButton = v.findViewById(R.id.enterEmailNextButton);
        Button goBackButton = v.findViewById(R.id.enterEmailGoBackButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                if (email.isEmpty()) {
                    return;
                }
                delegate.goEnterPinCode(Collections.singletonMap("email", (Object) email));
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

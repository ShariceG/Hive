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

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.StatusOr;

public class EnterEmailAndUsernameFragment extends Fragment
        implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;
    private ServerClient client;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = AppHelper.serverClient();
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
                final String email = emailEditText.getText().toString();
                final String username = usernameEditText.getText().toString();
                if (email.isEmpty() || username.isEmpty()) {
                    AppHelper.showAlert(getActivity(),
                            "Username and/or Email cannot be empty");
                    return;
                }
                client.createNewUser(username, email, new Callback() {
                    @Override
                    public void serverRequestCallback(StatusOr<Response> responseOr,
                                                      Map<String, Object> notes) {
                        if (responseOr.hasError()) {
                            AppHelper.showInternalServerErrorAlert(getActivity());
                            return;
                        }
                        Response response = responseOr.get();
                        if (response.serverReturnedWithError()) {
                            AppHelper.showAlert(getActivity(), "Oh...",
                                    response.getServerMessage());
                            return;
                        }
                        Map<String, Object> args = new HashMap<>();
                        args.put("username", username);
                        args.put("email", email);
                        delegate.saveLogInData(username, email, false);
                        delegate.goEnterPinCode(args);
                    }
                }, null);
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
    public void setArgs(Map<String, Object> args) {
    }
}


package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.StatusOr;
import coloredcoded.hive.client.User;
import coloredcoded.hive.client.UtilityBelt;

public class EnterEmailFragment extends Fragment implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;
    private ServerClient client;
    private View view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = AppHelper.serverClient();
        // Inflate the view for the fragment based on layout XML
        view = inflater.inflate(R.layout.enter_email_layout, container, false);
        final EditText emailEditText = view.findViewById(R.id.enterEmailEditText);
        Button nextButton = view.findViewById(R.id.enterEmailNextButton);
        Button goBackButton = view.findViewById(R.id.enterEmailGoBackButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString().trim();
                if (!UtilityBelt.isValidEmail(email)) {
                    AppHelper.showAlert(getActivity(),
                            "Please enter a valid email address");
                    return;
                }
                client.verifyExistingUser(email, new Callback() {
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
                        String username = response.getUsername();
                        Map<String, Object> args = new HashMap<>();
                        args.put("username", username);
                        args.put("email", email);
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
        return view;
    }

    @Override
    public void setSignInPageDelegate(SignInActivity.SignInDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setArgs(Map<String, Object> args) {
        if (!args.containsKey("discoveredUnverifiedUser") ||
                !(boolean) args.get("discoveredUnverifiedUser")) {
            return;
        }
        User user = (User) args.get("user");
        TextView unverifiedUserEditText = view.findViewById(R.id.unverifiedUserEditText);
        unverifiedUserEditText.setText(String.format(
                "Hey %s, please verify your email. Not %s? No biggie, tap Go Back!",
                user.getUsername(), user.getUsername()));
    }
}

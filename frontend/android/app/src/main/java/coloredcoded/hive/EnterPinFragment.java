package coloredcoded.hive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import coloredcoded.hive.client.Callback;
import coloredcoded.hive.client.Response;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.StatusOr;

public class EnterPinFragment extends Fragment implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;
    private Map<String, Object> args;
    private ServerClient client;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = AppHelper.serverClient();
        // Inflate the view for the fragment based on layout XML
        View v = inflater.inflate(R.layout.enter_pin_code_layout, container, false);
        final EditText pinEditText = v.findViewById(R.id.enterPinCodeEditText);
        Button hiveInButton = v.findViewById(R.id.hiveInButton);
        Button sendAnotherEmailButton = v.findViewById(R.id.anotherEmailButton);
        hiveInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEditText.getText().toString();
                if (pin.isEmpty()) {
                    return;
                }
                final String email = (String) args.get("email");
                final String username = (String) args.get("username");
                client.checkVerificationCode(username, email, pin, new Callback() {
                    @Override
                    public void serverRequestCallback(StatusOr<Response> responseOr,
                                                      Map<String, Object> notes) {
                        if (responseOr.hasError()) {
                            return;
                        }
                        Response response = responseOr.get();
                        if (response.serverReturnedWithError()) {
                            return;
                        }
                        Map<String, Object> args = new HashMap<>();
                        args.put("username", username);
                        args.put("email", email);
                        delegate.goToMainAppOrWelcomeIfLogIn(Collections.EMPTY_MAP);
                    }
                }, null);
            }
        });
        sendAnotherEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        this.args = args;
    }
}
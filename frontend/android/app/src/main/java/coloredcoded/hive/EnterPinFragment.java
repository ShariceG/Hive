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

public class EnterPinFragment extends Fragment implements SignInActivity.SignInFragment {

    private SignInActivity.SignInDelegate delegate;
    private Map<String, Object> notes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                delegate.goWelcome(Collections.EMPTY_MAP);
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
    public void setNotes(Map<String, Object> notes) {
        this.notes = notes;
    }
}
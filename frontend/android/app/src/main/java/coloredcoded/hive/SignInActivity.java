package coloredcoded.hive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    public interface SignInDelegate {
        void goLogInOrSignUp();
        void goEnterEmailAddress(Map<String, Object> args);
        void goEnterEmailAddressAndUsername(Map<String, Object> args);
        void goEnterPinCode(Map<String, Object> args);
        void goWelcome(Map<String, Object> args);
        void goToMainAppOrWelcomeIfLogIn(Map<String, Object> args);
        void goToMainApp();
        void saveLogInData(String username, String email, boolean isSignUpVerified);
    }

    public interface SignInFragment {
        void setSignInPageDelegate(SignInDelegate delegate);
        void setNotes(Map<String, Object> notes);
    }

    private enum FragmentPosition {
        LOG_IN_OR_SIGN_UP,
        ENTER_EMAIL,
        ENTER_EMAIL_AND_USERNAME,
        ENTER_PIN,
        WELCOME
    }

    private ViewPagerAdapter viewPagerAdapter;
    private CustomViewPager viewPager;
    private boolean isSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_layout);
        viewPager = findViewById(R.id.signInViewPager);
        viewPager.setPagingEnabled(false);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(viewPagerAdapter.getDefaultFragmentIndex());
        isSignUp = false;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter implements
            SignInActivity.SignInDelegate {

        private ArrayList<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new ArrayList<>();
            addFragment(new LogInOrSignUpFragment());
            addFragment(new EnterEmailFragment());
            addFragment(new EnterEmailAndUsernameFragment());
            addFragment(new EnterPinFragment());
            addFragment(new WelcomeFragment());
        }

        public Fragment setupFragment(Fragment fragment) {
            SignInFragment frag = (SignInFragment) (fragment);
            frag.setSignInPageDelegate(this);
            return fragment;
        }

        public void addFragment(Fragment fragment) {
            fragments.add(setupFragment(fragment));
        }

        public void setCurrentItem(final FragmentPosition position,
                                   final Map<String, Object> notes) {
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    ((SignInFragment)getItem(position.ordinal())).setNotes(notes);
                    viewPager.setCurrentItem(position.ordinal(), false);
                }
            });
        }

        public int getDefaultFragmentIndex() {
            return 0;
        }

        public int getCount() {
            return fragments.size();
        }

        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public void goLogInOrSignUp() {
            setCurrentItem(FragmentPosition.LOG_IN_OR_SIGN_UP, Collections.EMPTY_MAP);
        }

        @Override
        public void goEnterEmailAddress(Map<String, Object> args) {
            setCurrentItem(FragmentPosition.ENTER_EMAIL, args);
        }

        @Override
        public void goEnterEmailAddressAndUsername(Map<String, Object> args) {
            isSignUp = true;
            setCurrentItem(FragmentPosition.ENTER_EMAIL_AND_USERNAME, args);
        }

        @Override
        public void goEnterPinCode(Map<String, Object> args) {
            setCurrentItem(FragmentPosition.ENTER_PIN, args);
        }

        @Override
        public void goWelcome(Map<String, Object> args) {
            setCurrentItem(FragmentPosition.WELCOME, args);
        }

        @Override
        public void goToMainAppOrWelcomeIfLogIn(Map<String, Object> args) {
            if (isSignUp) {
                goWelcome(args);
            } else {
                goToMainApp();
            }
        }

        @Override
        public void goToMainApp() {
            goToMainActivity();
        }

        @Override
        public void saveLogInData(String username, String email, boolean isSignUpVerified) {
        }
    }
}
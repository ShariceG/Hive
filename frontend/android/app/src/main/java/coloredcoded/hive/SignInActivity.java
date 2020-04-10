package coloredcoded.hive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    public interface SignInDelegate {
        void goLogInOrSignUp();
        void goEnterEmailAddress(Map<String, Object> notes);
        void goEnterEmailAddressAndUsername(Map<String, Object> notes);
        void goEnterPinCode(Map<String, Object> notes);
        void goWelcome(Map<String, Object> notes);
        void goToMainAppOrWelcomeIfLogIn(Map<String, Object> notes);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_layout);
        viewPager = findViewById(R.id.signInViewPager);
        viewPager.setPagingEnabled(false);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(viewPagerAdapter.getDefaultFragmentIndex());
    }

    // FragmentStatePagerAdapter will create a new fragment before viewing instead of caching.
    public class ViewPagerAdapter extends FragmentStatePagerAdapter implements
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

        public void setCurrentItem(FragmentPosition position, Map<String, Object> notes) {
            ((SignInFragment)getItem(position.ordinal())).setNotes(notes);
            viewPager.setCurrentItem(position.ordinal(), false);
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
        public void goEnterEmailAddress(Map<String, Object> notes) {
            setCurrentItem(FragmentPosition.ENTER_EMAIL, notes);
        }

        @Override
        public void goEnterEmailAddressAndUsername(Map<String, Object> notes) {
            setCurrentItem(FragmentPosition.ENTER_EMAIL_AND_USERNAME, notes);
        }

        @Override
        public void goEnterPinCode(Map<String, Object> notes) {
            setCurrentItem(FragmentPosition.ENTER_PIN, notes);
        }

        @Override
        public void goWelcome(Map<String, Object> notes) {
            setCurrentItem(FragmentPosition.WELCOME, notes);
        }

        @Override
        public void goToMainAppOrWelcomeIfLogIn(Map<String, Object> notes) {

        }

        @Override
        public void goToMainApp() {

        }

        @Override
        public void saveLogInData(String username, String email, boolean isSignUpVerified) {

        }
    }
}
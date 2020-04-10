package coloredcoded.hive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(viewPagerAdapter.getDefaultFragmentIndex());
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private static final int DEFAULT_FRAGMENT_INDEX = 1;
        private ArrayList<Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new ArrayList<>();
            fragments.add(MenuFragment.newInstance());
            fragments.add(HomeFragment.newInstance());
            fragments.add(PopularPostsFragment.newInstance());
        }

        public int getDefaultFragmentIndex() {
            return DEFAULT_FRAGMENT_INDEX;
        }

        public int getCount() {
            return fragments.size();
        }

        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}

/*
No indication that you can swipe left and right
Write Something -> change to icon with pencil and circle
Dynamic counter for character limit in post view
 */
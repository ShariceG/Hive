package coloredcoded.hive;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

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

package coloredcoded.hive;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.Fragment;

public class ViewPagerAdapater extends FragmentPagerAdapter {

    public ViewPagerAdapater(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public int getCount() {
        return 3;
    }

    public Fragment getItem(int position) {
        return MainActivity.FirstFragment.newInstance();
    }

    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}

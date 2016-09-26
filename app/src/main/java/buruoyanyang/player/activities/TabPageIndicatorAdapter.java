package buruoyanyang.player.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * buruoyanyang.player.activities
 */
public class TabPageIndicatorAdapter extends FragmentPagerAdapter {
    String[] channelName;
    public TabPageIndicatorAdapter(FragmentManager supportFragmentManager,String[] titleStrings) {
        super(supportFragmentManager);
        channelName = titleStrings;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return channelName[position % channelName.length];
    }

    @Override
    public int getCount() {
        return channelName.length;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }
}

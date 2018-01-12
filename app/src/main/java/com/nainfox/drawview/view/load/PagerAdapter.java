package com.nainfox.drawview.view.load;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by yjk on 2018. 1. 12..
 */

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TutorialFragment.create(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}

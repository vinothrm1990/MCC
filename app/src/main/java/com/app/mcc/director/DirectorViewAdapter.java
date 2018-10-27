package com.app.mcc.director;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DirectorViewAdapter extends FragmentPagerAdapter {

    Context mContext;

    public DirectorViewAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new DirectorCategoryFragment();
        } else {
            return new DirectorMemberFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "CATEGORY LIST";
            case 1:
                return "MEMBER LIST";
            default:
                return null;
        }
    }
}

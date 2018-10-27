package com.app.mcc.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.app.mcc.fragment.DirectorLoginFragment;
import com.app.mcc.fragment.DirectorRegisterFragment;

public class DirectorAdapter extends FragmentPagerAdapter {

    Context mContext;

    public DirectorAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new DirectorLoginFragment();
        } else {
            return new DirectorRegisterFragment();
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
                return "LOGIN";
            case 1:
                return "REGISTER";
            default:
                return null;
        }
    }
}

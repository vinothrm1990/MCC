package com.app.mcc.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.app.mcc.fragment.DirectorLoginFragment;
import com.app.mcc.fragment.DirectorRegisterFragment;
import com.app.mcc.fragment.MemberLoginFragment;
import com.app.mcc.fragment.MemberRegisterFragment;

public class MemberAdapter extends FragmentPagerAdapter {

    Context mContext;

    public MemberAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MemberLoginFragment();
        } else {
            return new MemberRegisterFragment();
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

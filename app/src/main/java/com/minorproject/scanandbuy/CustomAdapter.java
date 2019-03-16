package com.minorproject.scanandbuy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class CustomAdapter extends FragmentStatePagerAdapter {

    private final int ITEMS = 1;

    public CustomAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return ITEMS;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new loginfragment1();
            default: return new loginfragment1() ;
        }
    }
}

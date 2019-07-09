package com.example.tripbyphoto.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tripbyphoto.fragment.FragmentOne;
import com.example.tripbyphoto.fragment.FragmentTwo;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    static final int NUMBER_OF_PAGES = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentOne.newInstance(0);
            case 1:
                return FragmentTwo.newInstance(1);
            default:
                return null;
        }
    }

    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
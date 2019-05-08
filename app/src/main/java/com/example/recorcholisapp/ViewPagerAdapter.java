package com.example.recorcholisapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;


import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<Fragment> arrayFragments;
    private ArrayList<String> arrayTitles;


    public ViewPagerAdapter(FragmentManager supportFragmentManager, ArrayList<Fragment> arrayFragments, ArrayList<String> arrayTitulos) {
        super(supportFragmentManager);
        this.arrayFragments = arrayFragments;
        this.arrayTitles = arrayTitulos;

    }

    @Override
    public Fragment getItem(int i) {
        return arrayFragments.get(i);
    }

    @Override
    public int getCount() {
        return arrayFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return arrayTitles.get(position);
    }
}

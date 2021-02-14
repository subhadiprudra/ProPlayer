package com.easylife.proplayer;


import android.content.Context;

import com.easylife.proplayer.Fragments.Joined;
import com.easylife.proplayer.Fragments.Matches;
import com.easylife.proplayer.Fragments.Result;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Matches matches= new Matches();
                return matches;

            case 1:
                Joined joined= new Joined();
                return joined;
            case 2:
                Result result = new Result();
                return result;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

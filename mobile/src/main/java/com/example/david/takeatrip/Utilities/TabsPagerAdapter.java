package com.example.david.takeatrip.Utilities;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Fragments.HomeFragment;
import com.example.david.takeatrip.Fragments.TopRatedFragment;
import com.example.david.takeatrip.Fragments.SearchUsersFragment;
import com.example.david.takeatrip.Fragments.FollowersFragment;
import com.example.david.takeatrip.Fragments.FollowingFragment;

import java.util.ArrayList;
import java.util.List;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Profilo> seguaci;
    private Context context;

    public TabsPagerAdapter(FragmentManager fm, Context context, ArrayList<Profilo> followers) {
        super(fm);
        seguaci = followers;
        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new HomeFragment();
            case 1:
                // Movies fragment activity
                return new FollowingFragment();
            case 2:
                // Movies fragment activity
                Log.i("TEST", "seguaci in Adapter: " + seguaci);
                Log.i("TEST", "context in Adapter: " + context);

                    return new FollowersFragment(context, seguaci);
            case 3:
                // Games fragment activity
                return new TopRatedFragment();
            case 4:
                // Movies fragment activity
                return new SearchUsersFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }

}
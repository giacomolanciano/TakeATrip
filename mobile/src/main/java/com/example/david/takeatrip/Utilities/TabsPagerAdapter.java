package com.example.david.takeatrip.Utilities;

/**
 * Created by lucagiacomelli on 21/02/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Fragments.FollowersFragment;
import com.example.david.takeatrip.Fragments.FollowingFragment;
import com.example.david.takeatrip.Fragments.HomeFragment;
import com.example.david.takeatrip.Fragments.SearchUsersFragment;
import com.example.david.takeatrip.Fragments.TopRatedFragment;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Profilo> follower;
    private ArrayList<Profilo> follow;
    private ArrayList<Profilo> homePage;
    private Context context;

    public TabsPagerAdapter(FragmentManager fm, Context context, ArrayList<Profilo> followers) {
        super(fm);
        this.follower = followers;

        //TODO modificare
        this.follow = followers;
        this.homePage = followers;

        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:

                HomeFragment newFragment = new HomeFragment();

                Bundle args = new Bundle();

                newFragment.setArguments(args);

                return newFragment;

            case 1:
                // Movies fragment activity
                Log.i("TEST", "seguaci in Adapter: " + follower);
                Log.i("TEST", "context in Adapter: " + context);

                // return new FollowingFragment(context, follow);
                return new FollowingFragment(context, follower);
            case 2:
                // Movies fragment activity
                Log.i("TEST", "seguiti in Adapter: " + follow);
                Log.i("TEST", "context in Adapter: " + context);

                    return new FollowersFragment(context, follow);
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
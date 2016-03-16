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
import com.example.david.takeatrip.Fragments.FollowersFragment;
import com.example.david.takeatrip.Fragments.FollowingFragment;

import java.util.ArrayList;

public class TabsPagerAdapterVisualizzazioneFollow extends FragmentPagerAdapter {

    private ArrayList<Profilo> follower;
    private ArrayList<Profilo> follow;
    private Context context;

    public TabsPagerAdapterVisualizzazioneFollow(FragmentManager fm, Context context, ArrayList<Profilo> followers) {
        super(fm);
        this.follower = followers;
        this.follow = followers;

        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                Log.i("TEST", "seguaci in Adapter: " + follower);
                Log.i("TEST", "context in Adapter: " + context);

                // return new FollowingFragment(context, follow);
                return new FollowingFragment(context, follower);
            case 1:
                // Movies fragment activity
                Log.i("TEST", "seguiti in Adapter: " + follow);
                Log.i("TEST", "context in Adapter: " + context);

                return new FollowersFragment(context, follow);

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
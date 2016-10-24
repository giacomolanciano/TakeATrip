package com.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 21/02/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.takeatrip.Classes.Profilo;
import com.takeatrip.Fragments.FollowersFragment;
import com.takeatrip.Fragments.FollowingFragment;
import com.takeatrip.Fragments.SearchUsersFragment;

import java.util.ArrayList;
import java.util.HashSet;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static String TAG = "TEST TabsPagerAdapt";

    private ArrayList<Profilo> followers;
    private ArrayList<Profilo> following;
    private HashSet<Profilo> profiles;
    private Context context;

    public TabsPagerAdapter(FragmentManager fm, Context context, ArrayList<Profilo> followers, ArrayList<Profilo> following, HashSet<Profilo> profiles) {
        super(fm);

        if(followers == null){
            this.followers = new ArrayList<Profilo>();
        }
        else{
            this.followers = followers;

        }

        if(following == null){
            this.following = new ArrayList<Profilo>();
        }
        else{
            this.following = following;
        }

        if(profiles != null)
            this.profiles = profiles;

        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {
        Bundle b;

        switch (index) {

            /*
            case 0:

                HomeFragment newFragment = new HomeFragment(context, follower);

                Bundle args = new Bundle();

                newFragment.setArguments(args);

                return newFragment;
                */

            case 0:
                // Movies fragment activity

                b = new Bundle();
                b.putSerializable("following", following);
                FollowingFragment followingFragment = new FollowingFragment();
                followingFragment.setArguments(b);

                return followingFragment;


            case 1:
                // Movies fragment activity
                b = new Bundle();
                b.putSerializable("followers", followers);
                FollowersFragment followersFragment = new FollowersFragment();
                followersFragment.setArguments(b);

                Log.i(TAG, "args: "+followersFragment.getArguments());
                Log.i(TAG, "args followers: "+followersFragment.getArguments().getSerializable("followers"));

                return followersFragment;
            case 2:
                // Games fragment activity
                b = new Bundle();
                b.putSerializable("profiles", profiles);
                SearchUsersFragment searchFragment = new SearchUsersFragment();
                searchFragment.setArguments(b);


                return searchFragment;

            /*
            case 4:
                // Movies fragment activity
                return new TopRatedFragment();
                */

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
package com.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 21/02/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.takeatrip.Classes.Profilo;
import com.takeatrip.Fragments.FollowersFragment;
import com.takeatrip.Fragments.FollowingFragment;
import com.takeatrip.Fragments.SearchUsersFragment;

import java.util.ArrayList;
import java.util.HashSet;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static String TAG = "TEST TabsPagerAdapt";
    private static int NUM_ITEMS = 3;
    private String[] titles = {"FOLLOWING", "FOLLOWERS","SEARCH"};

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

            case 0:
                b = new Bundle();
                b.putSerializable("following", following);
                FollowingFragment followingFragment =  FollowingFragment.newInstance(0,titles[0]);
                followingFragment.setArguments(b);

                return followingFragment;


            case 1:
                b = new Bundle();
                b.putSerializable("followers", followers);
                FollowersFragment followersFragment =  FollowersFragment.newInstance(1,titles[1]);
                followersFragment.setArguments(b);
                return followersFragment;

            case 2:
                b = new Bundle();
                b.putSerializable("profiles", profiles);
                SearchUsersFragment searchFragment = SearchUsersFragment.newInstance(2,titles[2]);
                searchFragment.setArguments(b);
                return searchFragment;

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return NUM_ITEMS;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
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

import java.util.ArrayList;

public class TabsPagerAdapterVisualizzazioneFollow extends FragmentPagerAdapter {

    private static String TAG = "TEST TabsPagerAdaptVF";

    private String[] titles = {"FOLLOWING", "FOLLOWERS"};
    private ArrayList<Profilo> followers;
    private ArrayList<Profilo> following;
    private ArrayList<Profilo> homePage;
    private Context context;

    public TabsPagerAdapterVisualizzazioneFollow(FragmentManager fm, Context context, ArrayList<Profilo> followers, ArrayList<Profilo> following) {
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

        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {

        Bundle b;

        switch (index) {
            case 0:
                b = new Bundle();
                b.putSerializable("following", following);
                FollowingFragment followingFragment = new FollowingFragment();
                followingFragment.setArguments(b);
                return followingFragment;
            case 1:
                b = new Bundle();
                b.putSerializable("followers", followers);
                FollowersFragment followersFragment = new FollowersFragment();
                followersFragment.setArguments(b);
                return followersFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
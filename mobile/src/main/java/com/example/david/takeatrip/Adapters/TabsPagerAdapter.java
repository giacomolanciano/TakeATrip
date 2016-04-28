package com.example.david.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 21/02/16.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Fragments.FollowersFragment;
import com.example.david.takeatrip.Fragments.FollowingFragment;
import com.example.david.takeatrip.Fragments.SearchUsersFragment;

import java.util.ArrayList;
import java.util.Set;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static String TAG = "TEST TabsPagerAdapt";

    private ArrayList<Profilo> followers;
    private ArrayList<Profilo> following;
    private Set<Profilo> profiles;
    private Context context;

    public TabsPagerAdapter(FragmentManager fm, Context context, ArrayList<Profilo> followers, ArrayList<Profilo> following, Set<Profilo> profiles) {
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
                return new FollowingFragment(context, following);

            case 1:
                // Movies fragment activity
                return new FollowersFragment(context, followers);
            case 2:
                // Games fragment activity
                return new SearchUsersFragment(context, profiles);

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
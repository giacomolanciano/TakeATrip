package com.example.david.takeatrip.Utilities;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.david.takeatrip.Fragments.HomeFragment;
import com.example.david.takeatrip.Fragments.TopRatedFragment;
import com.example.david.takeatrip.Fragments.SearchUsersFragment;
import com.example.david.takeatrip.Fragments.FollowersFragment;
import com.example.david.takeatrip.Fragments.FollowingFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
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
                return new FollowersFragment();
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
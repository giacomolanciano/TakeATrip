package com.example.david.takeatrip.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.takeatrip.R;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class FollowingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_following, container, false);

        return rootView;
    }
}


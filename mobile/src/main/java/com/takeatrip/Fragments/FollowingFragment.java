package com.takeatrip.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.takeatrip.Adapters.RecyclerViewAdapterFollowing;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.R;
import com.takeatrip.Utilities.DataObject;

import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class FollowingFragment extends Fragment {

    private static final String TAG = "TEST FollowingFragment";

    private Context context;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<DataObject> dataFollowing;

    private ViewGroup group;
    private ImageView image_default;
    private ArrayList<Profilo> following;

    // newInstance constructor for creating fragment with arguments
    public static FollowingFragment newInstance(int page, String title) {
        FollowingFragment fragmentFirst = new FollowingFragment();
        return fragmentFirst;
    }

    public FollowingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();
        following = (ArrayList<Profilo>) getArguments().getSerializable("following");

        View v = inflater.inflate(R.layout.activity_recyclerview_lista_viaggi, container, false);
        group = new ViewGroup(getContext()) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        ImageView image_default = new ImageView(getContext());
        image_default.setImageDrawable(getContext().getDrawable(R.drawable.default_male));
        group.addView(image_default);

        dataFollowing = new ArrayList<DataObject>();

        Log.i(TAG, "seguiti: "+ following);
        if(following.size() == 0){
            TextView viewNotFound = (TextView) v.findViewById(R.id.viewNotFount);
            viewNotFound.setText(R.string.no_following);
            viewNotFound.setTextSize(20);
            viewNotFound.setTextColor(getResources().getColor(R.color.blu_scuro));
        }
        else{

            for(Profilo p : following){
                dataFollowing.add(new DataObject(p));
            }

            mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);

            Log.i(TAG, "data set: " + getDataSet());
            RecyclerViewAdapterFollowing adapter = new RecyclerViewAdapterFollowing(getDataSet(), getContext());
            adapter.onCreateViewHolder(group, 0);
            mRecyclerView.setAdapter(adapter);
        }






        return v;
    }


    private ArrayList<DataObject> getDataSet() {
        return dataFollowing;
    }


}


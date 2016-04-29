package com.example.david.takeatrip.Fragments;

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

import com.example.david.takeatrip.Adapters.MyRecyclerViewAdapterFollowers;
import com.example.david.takeatrip.Adapters.RecyclerViewViaggiAdapter;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;

import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class FollowersFragment extends Fragment {

    private static final String TAG = "TEST FollowersFragment";

    TextView nome;
    TextView cognome;

    private Context context;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<DataObject> dataFollowers;


    private ViewGroup group;
    private ImageView image_default;



    private ArrayList<Profilo> followers;

    public FollowersFragment(Context context, ArrayList<Profilo> listaSeguaci) {
        this.context = context;
        followers = listaSeguaci;

        dataFollowers = new ArrayList<DataObject>();


        for(Profilo p : followers){
            dataFollowers.add(new DataObject(p));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_recyclerview_lista_viaggi, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewViaggiAdapter(getDataSet(), this.getContext());
        mRecyclerView.setAdapter(mAdapter);


        image_default = new ImageView(context);
        image_default.setImageResource((R.drawable.default_male));
        group = new ViewGroup(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        group.addView(image_default);


        Log.i(TAG, "data set: " + getDataSet());
        MyRecyclerViewAdapterFollowers adapter = new MyRecyclerViewAdapterFollowers(getDataSet(), getContext());
        adapter.onCreateViewHolder(group, 0);
        mRecyclerView.setAdapter(adapter);



        return v;
    }



    private ArrayList<DataObject> getDataSet() {
        return dataFollowers;
    }







}


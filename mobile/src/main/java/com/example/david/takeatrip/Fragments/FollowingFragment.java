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

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;
import com.example.david.takeatrip.Utilities.MyRecyclerViewAdapterFollowing;
import com.example.david.takeatrip.Utilities.RecyclerViewViaggiAdapter;

import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class FollowingFragment extends Fragment {
    TextView nome;
    TextView cognome;

    private Context context;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<DataObject> dataFollowing;


    private ViewGroup group;
    private ImageView image_default;



    private ArrayList<Profilo> following;

    public FollowingFragment(Context context, ArrayList<Profilo> listaSeguiti) {
        this.context = context;
        following = listaSeguiti;

        dataFollowing = new ArrayList<DataObject>();

        Log.i("TEST", "seguiti: "+ following);


        for(Profilo p : following){
            dataFollowing.add(new DataObject(p));
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



        Log.i("TEST", "context of the Followers Fragment: " + context);


        image_default = new ImageView(context);
        image_default.setImageResource((R.drawable.default_male));
        group = new ViewGroup(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        group.addView(image_default);


        Log.i("TEST", "data set: " + getDataSet());
        MyRecyclerViewAdapterFollowing adapter = new MyRecyclerViewAdapterFollowing(getDataSet());
        adapter.onCreateViewHolder(group, 0);
        mRecyclerView.setAdapter(adapter);



        return v;
    }



    private ArrayList<DataObject> getDataSet() {
        return dataFollowing;
    }





}


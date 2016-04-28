package com.example.david.takeatrip.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class SearchUsersFragment extends Fragment {

    private static final String TAG = "TEST SearchUsersFragment";

    private Context context;
    private Set<Profilo> profiles;
    List<String> userNames;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<DataObject> dataFollowers;


    /*TODO: ci sono due modi: caricare tutti gli utenti in locale e poi visualizzare velocemente quelli che servono
    oppure caricare solo quelli che servono con query ripetute*/

    public SearchUsersFragment(Context context, Set<Profilo> profiles){
        this.context = context;
        this.profiles = profiles;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listUsers);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mAdapter = new RecyclerViewViaggiAdapter(getDataSet(), context);
        //mRecyclerView.setAdapter(mAdapter);




        return rootView;
    }

}


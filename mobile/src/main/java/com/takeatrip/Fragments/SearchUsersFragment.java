package com.takeatrip.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.takeatrip.Adapters.RecyclerViewAdapterUsers;
import com.takeatrip.AsyncTasks.RicercaUtenteTask;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.R;
import com.takeatrip.Utilities.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class SearchUsersFragment extends Fragment {

    private static final String TAG = "TEST SearchUsersFr";

    private Context context;

    private Set<Profilo> profiles;
    List<String> userNames;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText editTextUser;
    private LinearLayout searchUser;
    private ArrayList<DataObject> dataUsers;
    private ViewGroup group;
    private ImageView image_default;


    ProgressDialog progressDialog;




    public SearchUsersFragment() {}


    // newInstance constructor for creating fragment with arguments
    public static SearchUsersFragment newInstance(int page, String title) {
        SearchUsersFragment fragmentFirst = new SearchUsersFragment();
        return fragmentFirst;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();
        this.profiles = (Set<Profilo>) getArguments().getSerializable("profiles");
        dataUsers = new ArrayList<DataObject>();

        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        progressDialog = new ProgressDialog(context);

        editTextUser = (EditText) rootView.findViewById(R.id.editTextUserSearch);

        searchUser = (LinearLayout) rootView.findViewById(R.id.searchUser);
        searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataUsers = new ArrayList<DataObject>();
                try {

                    String testo_cercato = editTextUser.getText().toString().trim().replace(" ", "");
                    profiles = new RicercaUtenteTask(context,testo_cercato).execute().get();

                    Log.i(TAG, "testo cercato: " +  testo_cercato);

                    for(Profilo p : profiles){
                        dataUsers.add(new DataObject(p));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listUsers);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(context);
                mRecyclerView.setLayoutManager(mLayoutManager);

                image_default = new ImageView(context);
                image_default.setImageResource((R.drawable.default_male));
                group = new ViewGroup(context) {
                    @Override
                    protected void onLayout(boolean changed, int l, int t, int r, int b) {

                    }
                };
                group.addView(image_default);

                Log.i(TAG, "data set: " + getDataSet());
                RecyclerViewAdapterUsers adapter = new RecyclerViewAdapterUsers(getDataSet(), getContext());
                adapter.onCreateViewHolder(group, 0);
                mRecyclerView.setAdapter(adapter);

            }
        });

        return rootView;
    }


    private ArrayList<DataObject> getDataSet() {
        return dataUsers;
    }


    private void showDialog(ProgressDialog dialog){
        dialog.show();
    }

    private void dismissDialog(ProgressDialog dialog){
        dialog.dismiss();
    }

}


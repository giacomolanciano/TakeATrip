package com.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.takeatrip.Adapters.TabsPagerAdapterVisualizzazioneFollow;
import com.takeatrip.Classes.Following;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Interfaces.AsyncResponseFollowers;
import com.takeatrip.Interfaces.AsyncResponseFollowing;
import com.takeatrip.R;
import com.takeatrip.Utilities.DataObject;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VisualizzazioneFollowActivity extends FragmentActivity implements AsyncResponseFollowers, AsyncResponseFollowing {

    private static final String TAG = "TEST VisualFollowAct";

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;

    private TabsPagerAdapterVisualizzazioneFollow mAdapter;
    private ArrayList<Following> follow;
    private ArrayList<Following> following;
    private ArrayList<DataObject> dataFollowers;

    ArrayList<Profilo> seguiti = new ArrayList<Profilo>();
    ArrayList<Profilo> seguaci = new ArrayList<Profilo>();

    private ProgressDialog mProgressDialog;

    private String email;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ViewGroup group;


    private static String LOG_TAG = "CardViewActivity";

    private Profilo corrente;

    private String[] tabs = {"FOLLOWERS","FOLLOWING"};

    private int[] icons = {
            R.drawable.ic_account_box_black_36dp,
            R.drawable.ic_account_circle_black_36dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizzazione_follow);


        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");

        }
        viewPager = (ViewPager) findViewById(R.id.pagerVisualizzazione);
        pagerTabStrip =  (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.blue));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.white));
        pagerTabStrip.setTextSize(0,20);
        pagerTabStrip.setTextSize(1,20);
        pagerTabStrip.setTextSize(2,20);

        corrente = new Profilo(email);

        follow = new ArrayList<Following>();
        following = new ArrayList<Following>();
        dataFollowers = new ArrayList<DataObject>();

        /*Email Utente*/
        com.takeatrip.AsyncTasks.MyTaskFollowers mT = new com.takeatrip.AsyncTasks.MyTaskFollowers(this, email, corrente);
        mT.delegate = this;
        mT.execute();

        showProgressDialog();
    }



    @Override
    public void processFinishForFollowers(ArrayList<Following> follow) {
        PopolaListaFollowers(follow);

        //Una volta prelevati i followers, prelevo i following
        com.takeatrip.AsyncTasks.MyTaskFollowing mTF = new com.takeatrip.AsyncTasks.MyTaskFollowing(this, email, corrente);
        mTF.delegate = this;
        mTF.execute();
    }


    //Finish also the following
    @Override
    public void processFinishForFollowing(ArrayList<Following> follow) {
        PopolaListaFollowing(follow);
        settaAdapter();
    }


    private void PopolaListaFollowers(List<Following> following) {
        seguaci.clear();
        for (Following f : following) {
            seguaci.add(f.getSegue());
        }
    }


    //era un popolalista
    private void PopolaListaFollowing(ArrayList<Following> follow) {
        seguiti.clear();
        seguiti = new ArrayList<Profilo>();
        for (Following f : follow) {
            seguiti.add(f.getSeguito());
        }
    }


    private void settaAdapter() {
        mAdapter = new TabsPagerAdapterVisualizzazioneFollow(getSupportFragmentManager(), getBaseContext(),seguaci,seguiti);
        viewPager.setAdapter(mAdapter);
        hideProgressDialog();
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
        }
    }


}

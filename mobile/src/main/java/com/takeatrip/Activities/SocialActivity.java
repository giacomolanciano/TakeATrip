package com.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.takeatrip.Adapters.TabsPagerAdapter;
import com.takeatrip.AsyncTasks.MyTaskFollowers;
import com.takeatrip.AsyncTasks.MyTaskFollowing;
import com.takeatrip.Classes.Following;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Interfaces.AsyncResponseFollowers;
import com.takeatrip.Interfaces.AsyncResponseFollowing;
import com.takeatrip.R;
import com.takeatrip.Utilities.DataObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SocialActivity extends FragmentActivity implements AsyncResponseFollowers, AsyncResponseFollowing {

    private static final String TAG = "TEST SocialActivity";

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    private final String ADDRESS_PRELIEVO = "PrendiFollower.php";
    private final String ADDRESS_PRELIEVO_FOLLOWING = "PrendiFollowing.php";

    private ArrayList<Following> follow;
    private ArrayList<Following> following;
    private ArrayList<DataObject> dataFollowers;

    ArrayList<Profilo> seguiti = new ArrayList<Profilo>();
    ArrayList<Profilo> seguaci = new ArrayList<Profilo>();

    private String email;
    String fromMain;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ViewGroup group;

    private static String LOG_TAG = "CardViewActivity";

    private ImageView image_default;
    private TextView nome;

    private Fragment fragment;

    private Profilo corrente;
    private HashSet<Profilo> profiles;

    private String[] tabs = {"FOLLOWERS","FOLLOWING", "SEARCH"};
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            fromMain = intent.getStringExtra("fromMain");
        }
        viewPager = (ViewPager) findViewById(R.id.pager);
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

        group = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };


        image_default = new ImageView(this);
        nome = new TextView(this);
        image_default.setImageDrawable(getDrawable(R.drawable.default_male));
        group.addView(image_default);

        /*Email Utente*/
        MyTaskFollowers mT = new MyTaskFollowers(this, email, corrente);
        mT.delegate = this;
        mT.execute();
        showProgressDialog();
    }


    @Override
    public void processFinishForFollowers(ArrayList<Following> follow) {
        PopolaListaFollowers(follow);

        //Una volta prelevati i followers, prelevo i following
        MyTaskFollowing mTF = new MyTaskFollowing(this, email, corrente);
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
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(),seguaci,seguiti, profiles);
        Log.i(TAG, "adapter di viePager settato con seguaci: " + seguaci + " seguiti: " +seguiti);


        viewPager.setAdapter(mAdapter);
        if(fromMain!= null){
            viewPager.setCurrentItem(2);
        }


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

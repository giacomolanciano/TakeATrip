package com.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
public class SocialActivity extends FragmentActivity implements ActionBar.TabListener, AsyncResponseFollowers, AsyncResponseFollowing {

    private static final String TAG = "TEST SocialActivity";

    private ViewPager viewPager;
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
    private static String LOG_TAG = "CardViewActivity";

    private ViewGroup group;
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

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        corrente = new Profilo(email);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "on Page selected: " + position);
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        Log.i(TAG, "fromMain:"+ fromMain);
        if(fromMain!= null){
            viewPager.setCurrentItem(2);
            actionBar.setSelectedNavigationItem(2);
        }


        follow = new ArrayList<Following>();
        following = new ArrayList<Following>();
        dataFollowers = new ArrayList<DataObject>();

        image_default = new ImageView(this);
        nome = new TextView(this);
        image_default.setImageDrawable(getDrawable(R.drawable.default_male));


        /*Email Utente*/
        MyTaskFollowers mT = new MyTaskFollowers(this, email, corrente);
        mT.delegate = this;
        mT.execute();

        //TODO: introdurre delegate per download followers and following
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        showProgressDialog();

    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());


        if(tab.getPosition()==0) {
            tab.setText(" FOLLOWERS");
        }
        if(tab.getPosition()==1) {
            tab.setText(" FOLLOWING");
        }
        if(tab.getPosition()==2) {
            tab.setText(" SEARCH");
        }

        Log.i(TAG, "TAB SELEZIONATO: "+ tab.getPosition() );
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

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

        //settaAdapter();
    }


    private void PopolaListaFollowers(List<Following> following) {
        seguaci.clear();
        for (Following f : following) {
            seguaci.add(f.getSegue());
            Log.i(TAG, "email seguaci: " + f.getSegue().getEmail());
        }
        Log.i(TAG, "seguaci di: "+ corrente + ": " + seguaci);
    }


    //era un popolalista
    private void PopolaListaFollowing(ArrayList<Following> follow) {
        seguiti.clear();
        seguiti = new ArrayList<Profilo>();
        for (Following f : follow) {
            Log.i(TAG, "seguito: " + f.getSeguito());
            seguiti.add(f.getSeguito());
        }

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(),seguaci,seguiti, profiles);
        viewPager.setAdapter(mAdapter);
        Log.i(TAG, "seguiti da: "+ corrente + ": " + seguiti);
    }

    private void settaAdapter() {
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(),seguaci,seguiti, profiles);
        Log.i(TAG, "adapter di viePager settato con seguaci: " + seguaci + " seguiti: " +seguiti);
        if(fromMain!= null){
            viewPager.setCurrentItem(2);
        }
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

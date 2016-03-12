package com.example.david.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;
import com.example.david.takeatrip.Utilities.MyRecyclerViewAdapter;
import com.example.david.takeatrip.Utilities.TabsPagerAdapter;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SocialActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/Follower.php";


    private ArrayList<Profilo> profili;
    private ArrayList<DataObject> dataTravels;
    private String email;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    private ViewGroup group;
    private ImageView image_default;


    // TODO: Tab titles in other languages
    private String[] tabs = {"Home","Following","Followers", "Top Rated", "Search"};

    private int[] icons = {R.drawable.ic_people_black_36dp,
           R.drawable.ic_add_a_photo_black_36dp,R.drawable.ic_add_black_24dp,
            R.drawable.ic_add_white_24dp,
            R.drawable.ic_cast_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (int tab_name : icons) {
            actionBar.addTab(actionBar.newTab().setIcon(tab_name)
                    .setTabListener(this));
        }


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        /*Email Utente*/


        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            Log.i("TEST", "email utente in Social: " + email);

        }


    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());


        if(tab.getPosition()==2){
            Log.e("TEST", "TAB SELEZIONATO NUMERO 2 ");

        }

        Log.e("TEST", "TAB SELEZIONATO: "+ tab.getPosition() );

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}

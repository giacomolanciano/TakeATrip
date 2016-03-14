package com.example.david.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Following;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Fragments.FollowersFragment;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;
import com.example.david.takeatrip.Utilities.TabsPagerAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SocialActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/Follower.php";


    private ArrayList<Following> follow;
    private ArrayList<DataObject> dataFollowers;
    private String email;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    private ViewGroup group;
    private ImageView image_default;
    private TextView nome;

    private Fragment fragment;

    private Profilo seguito;

    // TODO: Tab titles in other languages
    private String[] tabs = {"Home","Following","Followers", "Top Rated", "Search"};

    private int[] icons = {R.drawable.ic_people_black_36dp,
           R.drawable.ic_add_a_photo_black_36dp,
            R.drawable.ic_add_black_24dp,
            R.drawable.ic_add_white_24dp,
            R.drawable.ic_cast_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);


        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            Log.i("TEST", "email utente in Social: " + email);

        }
        viewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (int tab_name : icons) {
            actionBar.addTab(actionBar.newTab().setIcon(tab_name)
                    .setTabListener(this));
        }


        //TODO: cambiare in caso di visualizzazione esterna

        seguito = new Profilo(email);


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

        follow = new ArrayList<Following>();
        dataFollowers = new ArrayList<DataObject>();

        image_default = new ImageView(this);
        nome = new TextView(this);
        nome.setText("CIAO");
        image_default.setImageDrawable(getDrawable(R.drawable.default_male));


        group = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };

       // group.addView(image_default);
        group.addView(nome);


        /*Email Utente*/




       MyTaskFollowers mT = new MyTaskFollowers();
        mT.execute();



    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());


        //TODO una volta impostate le icone giuste queste scritte vanno levate
        if(tab.getPosition()==0) {
            tab.setText("HOME");
        }
        if(tab.getPosition()==1) {
            tab.setText("FOLLOWING");
        }
        if(tab.getPosition()==2) {
            tab.setText("FOLLOWERS");
        }
        if(tab.getPosition()==3) {
            tab.setText("SEARCH");
        }
        if(tab.getPosition()==4) {
            tab.setText("TOP RATED");
        }

        Log.i("TEST", "TAB SELEZIONATO: "+ tab.getPosition() );


    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private class MyTaskFollowers extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ADDRESS_PRELIEVO);
                httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                if (is != null) {
                    //converto la risposta in stringa
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();

                        String result = sb.toString();

                        if (result.equals("null\n")) {
                            stringaFinale = "Non sono presenti followers";
                            Log.i("TEST", "result da Followers: " + stringaFinale);

                        } else {
                            JSONArray jArray = new JSONArray(result);
                            Log.i("TEST", "jArray" + jArray.toString());

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String emailSeguace = json_data.getString("email").toString();
                                    String nomeUtente = json_data.getString("nome");
                                    String cognomeUtente = json_data.getString("cognome");
                                    String username = json_data.getString("username");
                                    String sesso = json_data.getString("sesso");
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                    Profilo seguace = new Profilo(emailSeguace, nomeUtente,cognomeUtente, null, null,sesso,username,null,null,null,urlImmagineProfilo,urlImmagineCopertina);
                                    follow.add(new Following(seguace, seguito));
                                }
                            }

                            Log.i("TEST", "lista followers di " + email + ": " + follow);
                            for (int i = 0; i < follow.size(); i++) {
                                Log.i("TEST", "followers : " + follow.get(i).toString());


                            }
                        }


                    } catch (Exception e) {
                        Log.e("TEST", "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e("TEST", "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (stringaFinale.equals("")) {
                PopolaListaFollowers(follow);
            } else {
                Toast.makeText(getBaseContext(), stringaFinale, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }

    //era un popolalista
    private void PopolaListaFollowers( ArrayList<Following> follow) {
        ArrayList<Profilo> seguaci = new ArrayList<Profilo>();

        for (Following f : follow) {
            Log.i("TEST", "seguaci: " + f.getSegue());
            seguaci.add(f.getSegue());

        }

        // Initilization
        Log.i("TEST", "contesto SocialActivity: " + getBaseContext());

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(),seguaci);
        viewPager.setAdapter(mAdapter);



        Log.i("TEST", "seguaci di: "+ seguito + ": " + seguaci);

    }
}

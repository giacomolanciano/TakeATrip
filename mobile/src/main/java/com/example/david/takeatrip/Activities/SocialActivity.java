package com.example.david.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Adapters.TabsPagerAdapter;
import com.example.david.takeatrip.Classes.Following;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DataObject;

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
import java.util.HashSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SocialActivity extends FragmentActivity implements ActionBar.TabListener {

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

    private int[] icons = {
            //R.drawable.ic_people_black_36dp,
           R.drawable.ic_account_box_black_36dp,
            R.drawable.ic_account_circle_black_36dp,
            R.drawable.ic_search_white_36dp,
            //R.drawable.ic_cast_light
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            Log.i(TAG, "email utente in Social: " + email);

        }
        viewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



        /*
        non ci servono tutti gli utenti
        try {
            profiles = new PrelievoTotaleUtentiTask(this).execute().get();
            Log.i(TAG, "all profiles: " + profiles.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        */

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }


        corrente = new Profilo(email);


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
        following = new ArrayList<Following>();
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


        //TODO: introdurre delegate per download followers and following
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        MyTaskFollowing mTF = new MyTaskFollowing();
        mTF.execute();



    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());


        //TODO una volta impostate le icone giuste queste scritte vanno levate

        /*
        if(tab.getPosition()==0) {
            tab.setText("HOME");
        }
        */


        if(tab.getPosition()==0) {
            tab.setText(" FOLLOWERS");
        }
        if(tab.getPosition()==1) {
            tab.setText(" FOLLOWING");
        }
        if(tab.getPosition()==2) {
            tab.setText(" SEARCH");
        }

        /*
        if(tab.getPosition()==4) {
            tab.setText("TOP RATED");
        }
        */

        Log.i(TAG, "TAB SELEZIONATO: "+ tab.getPosition() );


    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private class MyTaskFollowing extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_PRELIEVO);
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
                            stringaFinale = "Non sono presenti following";
                            Log.i(TAG, "result da Followers: " + stringaFinale);
                            Log.i(TAG, "NO FOLLOWING " + seguaci);
                            //mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(), seguaci,null);
                            //viewPager.setAdapter(mAdapter);  //LASCIARE ASSOLUTAMENTE COSI!!!!!

                        } else {
                            JSONArray jArray = new JSONArray(result);

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
                                    String dataNascita = json_data.getString("dataNascita");
                                    String lavoro = json_data.getString("lavoro");
                                    String nazionalita = json_data.getString("nazionalita");
                                    String descrizione = json_data.getString("descrizione");
                                    String tipo = json_data.getString("tipo");

                                    Profilo seguito = new Profilo(emailSeguace, nomeUtente, cognomeUtente, dataNascita, nazionalita, sesso, username, lavoro, descrizione, tipo,urlImmagineProfilo,urlImmagineCopertina);
                                    Log.i(TAG, "seguito : " + seguito.getEmail());
                                    follow.add(new Following(corrente, seguito));
                                    //Corrente è il seguito
                                }
                            }

                        }


                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e(TAG, "Errore nella connessione http " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (stringaFinale.equals("")) {
                PopolaListaFollowing(follow);
            } else {
                Toast.makeText(getBaseContext(), stringaFinale, Toast.LENGTH_LONG).show();
                settaAdapter();
            }
            
            
            super.onPostExecute(aVoid);
        }
    }

    private void settaAdapter() {
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(),seguaci,seguiti, profiles);
        viewPager.setAdapter(mAdapter);
    }

    //era un popolalista
    private void PopolaListaFollowing( ArrayList<Following> follow) {
        seguiti.clear();
        seguiti = new ArrayList<Profilo>();
        for (Following f : follow) {
            Log.i(TAG, "seguito: " + f.getSeguito());
            seguiti.add(f.getSeguito());
        }

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(),seguaci,seguiti, profiles);
        viewPager.setAdapter(mAdapter);

        Log.i(TAG, "seguiti di: "+ corrente + ": " + seguiti);

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
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_PRELIEVO_FOLLOWING);
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
                            Log.i(TAG, "result da Followers: " + stringaFinale);
                            //mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(), seguiti);
                            //viewPager.setAdapter(mAdapter); //LASCIARE ASSOLUTAMENTE COSI!!!!!


                        } else {
                            JSONArray jArray = new JSONArray(result);

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String emailSeguito = json_data.getString("email").toString();
                                    String nomeUtente = json_data.getString("nome");
                                    String cognomeUtente = json_data.getString("cognome");
                                    String username = json_data.getString("username");
                                    String sesso = json_data.getString("sesso");
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                    String dataNascita = json_data.getString("dataNascita");
                                    String lavoro = json_data.getString("lavoro");
                                    String nazionalita = json_data.getString("nazionalita");
                                    String descrizione = json_data.getString("descrizione");
                                    String tipo = json_data.getString("tipo");
                                    Profilo seguace = new Profilo(emailSeguito, nomeUtente, cognomeUtente, dataNascita, nazionalita, sesso, username, lavoro, descrizione, tipo,urlImmagineProfilo,urlImmagineCopertina);
                                    following.add(new Following(seguace,corrente));
                                   }
                            }

                            Log.i(TAG, "lista followers di " + email + ": " + follow);
                            for (int i = 0; i < following.size(); i++) {
                                Log.i(TAG, "followers : " + following.get(i).toString());
                            }
                        }


                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e(TAG, "Errore nella connessione http " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (stringaFinale.equals("")) {
                PopolaListaFollowers(following);
            } else {
                Toast.makeText(getBaseContext(), stringaFinale, Toast.LENGTH_LONG).show();
                settaAdapter();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void PopolaListaFollowers( ArrayList<Following> following) {
        seguaci.clear();
        for (Following f : following) {
            seguaci.add(f.getSegue());
            Log.i(TAG, "email seguaci: " + f.getSegue().getEmail());
        }
/*
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getBaseContext(), seguaci,seguiti);
        viewPager.setAdapter(mAdapter);
*/

        Log.i(TAG, "seguaci di: "+ corrente + ": " + seguaci);

    }



}

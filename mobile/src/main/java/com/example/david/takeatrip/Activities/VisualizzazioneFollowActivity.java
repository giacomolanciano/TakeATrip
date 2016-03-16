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

import com.example.david.takeatrip.Classes.Following;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;
import com.example.david.takeatrip.Utilities.TabsPagerAdapterVisualizzazioneFollow;

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

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VisualizzazioneFollowActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapterVisualizzazioneFollow mAdapter;
    private ActionBar actionBar;

    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/Follower.php";
    private final String ADDRESS_PRELIEVO_FOLLOWING = "http://www.musichangman.com/TakeATrip/InserimentoDati/Following.php";


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

    // TODO: Tab titles in other languages
    private String[] tabs = {"Home","Following","Followers", "Top Rated", "Search"};

    private int[] icons = {R.drawable.ic_people_black_36dp,
            R.drawable.ic_add_a_photo_black_36dp,
           };

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

        MyTaskFollowing mTF = new MyTaskFollowing();
        mTF.execute();



    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());


        //TODO una volta impostate le icone giuste queste scritte vanno levate
        if(tab.getPosition()==0) {
            tab.setText("FOLLOWERS");
        }
        if(tab.getPosition()==1) {
            tab.setText("FOLLOWING");
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
                            stringaFinale = "Non sono presenti following";
                            Log.i("TEST", "result da Followers: " + stringaFinale);
                            Log.i("TEST", "NO FOLLOWING " + seguaci);
                            mAdapter = new TabsPagerAdapterVisualizzazioneFollow(getSupportFragmentManager(), getBaseContext(), seguaci);
                            viewPager.setAdapter(mAdapter);  //LASCIARE ASSOLUTAMENTE COSI!!!!!

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
                                    Log.i("TEST", "seguace : " + seguace.getEmail());
                                    follow.add(new Following(seguace, corrente));
                                    //Corrente è il seguito
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
        ArrayList<Profilo> vuoto = new ArrayList<Profilo>();
        Log.i("TEST", "LISTA SEGUACI VUOTA " );
        Log.i("TEST", "INVIO VUOTO");


        for (Following f : follow) {
            Log.i("TEST", "seguaci: " + f.getSegue());
            seguaci.add(f.getSegue());
        }

        // Initilization
        Log.i("TEST", "contesto SocialActivity: " + getBaseContext());

        Log.i("TEST", "INVIO SEGUACI" );
        mAdapter = new TabsPagerAdapterVisualizzazioneFollow(getSupportFragmentManager(), getBaseContext(), seguaci);
        viewPager.setAdapter(mAdapter);



        Log.i("TEST", "seguaci di: "+ corrente + ": " + seguaci);

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
                HttpPost httppost = new HttpPost(ADDRESS_PRELIEVO_FOLLOWING);
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
                            Log.i("TEST", "result da Following: " + stringaFinale);
                            mAdapter = new TabsPagerAdapterVisualizzazioneFollow(getSupportFragmentManager(), getBaseContext(), seguiti);
                            viewPager.setAdapter(mAdapter); //LASCIARE ASSOLUTAMENTE COSI!!!!!


                        } else {
                            JSONArray jArray = new JSONArray(result);
                            Log.i("TEST", "jArray" + jArray.toString());

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

                                    Profilo seguito = new Profilo(emailSeguito, nomeUtente,cognomeUtente, null, null,sesso,username,null,null,null,urlImmagineProfilo,urlImmagineCopertina);
                                    Log.i("TEST", "seguito : " + seguito.getEmail());
                                    following.add(new Following(corrente,seguito));
                                    //Corrente è il seguace
                                }
                            }

                            Log.i("TEST", "lista following di " + email + ": " + following);
                            for (int i = 0; i < following.size(); i++) {
                                Log.i("TEST", "following : " + following.get(i).toString());


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
                PopolaListaFollowing(following);
            } else {
                Toast.makeText(getBaseContext(), stringaFinale, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(aVoid);
        }
    }

    private void PopolaListaFollowing( ArrayList<Following> following) {
        ArrayList<Profilo> vuoto = new ArrayList<Profilo>();

        Log.i("TEST", "LISTA SEGUITI VUOTA " );
        Log.i("TEST", "INVIO VUOTO");

        for (Following f : following) {
            Log.i("TEST", "seguiti: " + f.getSeguito());
            seguiti.add(f.getSeguito());

        }

        // Initilization
        Log.i("TEST", "contesto SocialActivity: " + getBaseContext());

        Log.i("TEST", "INVIO SEGUITI" );

        mAdapter = new TabsPagerAdapterVisualizzazioneFollow(getSupportFragmentManager(), getBaseContext(), seguiti);
        viewPager.setAdapter(mAdapter);



        Log.i("TEST", "seguiti di: "+ corrente + ": " + seguiti);

    }
}

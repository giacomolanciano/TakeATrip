package com.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.takeatrip.Adapters.RecyclerViewViaggiAdapter;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DataObject;
import com.takeatrip.Utilities.GoogleTranslate;
import com.takeatrip.Utilities.InternetConnection;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "TEST SearchActivity";

    private final String ADDRESS = "QueryNomiUtenti.php";
    private final String ADDRESS_PER_VIAGGI_DA_UTENTE = "QueryViaggiDiUtente.php";
    private final String ADDRESS_PER_VIAGGI_DA_DESTINAZIONE = "QueryViaggiDaDestinazione.php";

    private final String API_KEY = "AIzaSyA1YAGqN4CRpchUly-R5MkllvnM99I872A";

    private String nomeScelto, cognomeScelto, destination, searchBy;
    private String emailUtente, emailEserno;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    private AutoCompleteTextView editTextUser;
    List<String> users, destinations;
    Map<Profilo, List<Viaggio>> viaggi_profilo;
    private ListView lista;
    private ArrayList<DataObject> dataTravels;
    private ArrayList<Viaggio> viaggi;

    private ViewGroup group;
    private ImageView image_default;


    private PlaceAutocompleteFragment autocompleteFragment;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private ProgressDialog mProgressDialog;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent;
        if ((intent = getIntent()) != null) {
            emailUtente = intent.getStringExtra("email");
        }

        editTextUser = (AutoCompleteTextView) findViewById(R.id.editTextUser);

        float density = getResources().getDisplayMetrics().density;
        editTextUser.setTextSize(20);

        if(density == 3.0 || density == 4.0){
            editTextUser.setTextSize(25);
        }

        // lista = (ListView)findViewById(R.id.listTravelsBySearch);
        mRecyclerView = (RecyclerView) findViewById(R.id.listTravelsBySearch);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewViaggiAdapter(getDataSet(), SearchActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        image_default = new ImageView(this);
        image_default.setImageDrawable(getDrawable(R.drawable.default_male));

        group = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };

        group.addView(image_default);

        users = new ArrayList<String>();
        destinations = new ArrayList<String>();
        viaggi_profilo = new HashMap<Profilo, List<Viaggio>>();
        dataTravels = new ArrayList<DataObject>();
        viaggi = new ArrayList<Viaggio>();


        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint(getString(R.string.SearchByDestination));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());

                if (viaggi_profilo.size() != 0 && editTextUser.getText().toString().equals("")) {
                    viaggi_profilo.clear();
                }

                String s = place.getName().toString();

                new MyTaskTranslate(s).execute();

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }


        });
        new MyTask().execute();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void PopolaLista(Map<Profilo, List<Viaggio>> p_v) {
        ArrayList<DataObject> result = new ArrayList<DataObject>();

        List<String> codiciViaggi = new ArrayList<String>();
        for (Profilo p : p_v.keySet()) {
            for (Viaggio v : p_v.get(p)) {
                if (!codiciViaggi.contains(v.getCodice())) {
                    ImageView image = new ImageView(SearchActivity.this);
                    result.add(new DataObject(v, p, image));
                    codiciViaggi.add(v.getCodice());
                }
            }
        }


        RecyclerViewViaggiAdapter adapter = new RecyclerViewViaggiAdapter(result, SearchActivity.this);
        adapter.onCreateViewHolder(group, 0);
        mRecyclerView.setAdapter(adapter);


    }


    //viaggi[i] is associated to profili[i]
    private void PopolaListaDaDestinazione(Viaggio[] viaggi, Profilo[] profili) {
        ArrayList<DataObject> result = new ArrayList<DataObject>();

        List<String> codiciViaggi = new ArrayList<String>();
        for (int i=0; i<viaggi.length; i++) {
            if (!codiciViaggi.contains(viaggi[i].getCodice())) {
                ImageView image = new ImageView(SearchActivity.this);
                result.add(new DataObject(viaggi[i], profili[i], image));
                codiciViaggi.add(viaggi[i].getCodice());}
        }

        RecyclerViewViaggiAdapter adapter = new RecyclerViewViaggiAdapter(result, SearchActivity.this);
        adapter.onCreateViewHolder(group, 0);
        mRecyclerView.setAdapter(adapter);
    }





    public ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();

        return results;
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }



    private class MyTask extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String result, stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(SearchActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS);
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

                            result = sb.toString();


                            JSONArray jArray = new JSONArray(result);

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    String emailUtente = json_data.getString("email").toString();
                                    String usernameUtente = json_data.getString("username").toString();

                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "(" + usernameUtente + ")";
                                    users.add(stringaFinale);
                                }
                            }


                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this, android.R.layout.simple_list_item_1, users);

            editTextUser.setAdapter(adapter);
            editTextUser.setThreshold(1);
            editTextUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    onClickSearchUser(view, adapter.getItem(position).toString());
                }
            });

            Log.i(TAG, "search by user: focus the editText..." + searchBy);


        }
    }





    private class MyTaskTranslate extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String toTranslate;
        String translated;

        public MyTaskTranslate(String word) {
            toTranslate = word;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(SearchActivity.this)) {

                    Log.i(TAG, "placeNuovo: " + toTranslate);

                    String languageDevice = Locale.getDefault().getLanguage();
                    if (!languageDevice.equals("it")) {
                        GoogleTranslate translate = new GoogleTranslate(API_KEY);
                        translated = translate.translate(toTranslate, languageDevice, "it");

                        String finalTranslated = translated.toLowerCase();
                        translated = finalTranslated.replace(" ", "_");

                        Log.i(TAG, "placeNuovo tradotto: " + translated);

                        destination = translated;


                    } else {
                        String finalTranslated = toTranslate.toLowerCase();
                        toTranslate = finalTranslated.replace(" ", "_");
                        destination = toTranslate;


                    }

                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showProgressDialog();
            new myTaskSearchByDestination().execute();

        }
    }




    public void onClickSearchUser(View v, String utenteSelezionato) {
        if (viaggi_profilo.size() != 0 && editTextUser.getText().toString().equals("")) {
            viaggi_profilo.clear();
        }
        String usernameUtenteSelezionato = utenteSelezionato.substring(utenteSelezionato.indexOf('(') + 1, utenteSelezionato.indexOf(')'));
        showProgressDialog();
        new myTaskSearchByUser(usernameUtenteSelezionato).execute();
    }


    public void onClickSearchUser(View v) {

        /*
        if(viaggi_profilo.size() != 0 && editTextUser.getText().toString().equals("")){
            viaggi_profilo.clear();
        }
        String utenteSelezionato = editTextUser.getText().toString();
        String usernameUtenteSelezionato = utenteSelezionato.substring(utenteSelezionato.indexOf('(') + 1, utenteSelezionato.indexOf(')'));

        new myTaskSearchByUser(usernameUtenteSelezionato).execute();

        */

    }


    private class myTaskSearchByUser extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result;
        Map<Profilo, List<Viaggio>> mappaProvvisoria = new HashMap<Profilo, List<Viaggio>>();
        String username;

        public myTaskSearchByUser(String username) {
            this.username = username;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("username", username));

            try {
                if (InternetConnection.haveInternetConnection(SearchActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_PER_VIAGGI_DA_UTENTE);
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

                            result = sb.toString();


                            Log.i(TAG, "risultato dalla search: " + result);


                            List<Viaggio> viaggi = new ArrayList<Viaggio>();
                            Profilo p = null;

                            JSONArray jArray = new JSONArray(result);
                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codice = json_data.getString("codice").toString();
                                    String nomeViaggio = json_data.getString("nomeViaggio").toString();
                                    String emailUtente = json_data.getString("email").toString();
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    String urlImmagineViaggio = json_data.getString("idFotoViaggio").toString();
                                    String condivisioneDefault = json_data.getString("livelloCondivisione").toString();

                                    if(i==0)
                                        p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, null, null, null, null, null);

                                    viaggi.add(new Viaggio(codice, nomeViaggio, urlImmagineViaggio, condivisioneDefault));
                                    mappaProvvisoria.put(p, viaggi);
                                }
                            }


                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "mappaProvvisoria:" + mappaProvvisoria);

            PopolaLista(mappaProvvisoria);
            autocompleteFragment.setText("");

            hideProgressDialog();
            super.onPostExecute(aVoid);

        }
    }

    private class myTaskSearchByDestination extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";
        Map<Profilo, List<Viaggio>> mappaProvvisoria = new HashMap<Profilo, List<Viaggio>>();

        Viaggio[] viaggi;
        Profilo[] profili;

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("destinazione", destination));

            Log.i(TAG, "destinazione: " + destination);

            try {
                if (InternetConnection.haveInternetConnection(SearchActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_PER_VIAGGI_DA_DESTINAZIONE);
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

                            result = sb.toString();


                            JSONArray jArray = new JSONArray(result);

                            if (jArray != null && result != null) {

                                viaggi = new Viaggio[jArray.length()];
                                profili = new Profilo[jArray.length()];

                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codice = json_data.getString("codice").toString();
                                    String nomeViaggio = json_data.getString("nomeViaggio").toString();
                                    String emailUtente = json_data.getString("email").toString();
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    String urlImmagineViaggio = json_data.getString("idFotoViaggio").toString();
                                    String condivisioneDefault = json_data.getString("livelloCondivisione").toString();


                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, null, null, null, null, null);
                                    Viaggio nuovoViaggio = new Viaggio(codice, nomeViaggio, urlImmagineViaggio, condivisioneDefault);

                                    viaggi[i] = nuovoViaggio;
                                    profili[i] = p;

                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                        }
                    } else {
                        Log.e(TAG, "Input Stream uguale a null");
                    }

                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "viaggi:" + Arrays.toString(viaggi));
            Log.i(TAG, "profili:" + Arrays.toString(profili));

            PopolaListaDaDestinazione(viaggi, profili);
            editTextUser.setText("");

            hideProgressDialog();
            super.onPostExecute(aVoid);

        }
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


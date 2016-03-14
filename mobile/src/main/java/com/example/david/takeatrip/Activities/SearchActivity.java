package com.example.david.takeatrip.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;
import com.example.david.takeatrip.Utilities.GoogleTranslate;
import com.example.david.takeatrip.Utilities.MyRecyclerViewAdapter;
import com.example.david.takeatrip.Utilities.ViaggioAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.widget.AdapterView.*;

public class SearchActivity extends AppCompatActivity {

    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private final String ADDRESS_PER_VIAGGI_DA_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryViaggiDiUtente.php";
    private final String ADDRESS_PER_VIAGGI_DA_DESTINAZIONE = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryViaggiDaDestinazione.php";

    private final String API_KEY = "AIzaSyAj4Z5rzBBBXq7jUxUYuXI5pQ6_RR1OUkQ";

    private String nomeScelto, cognomeScelto, destination;
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent;
        if((intent = getIntent()) != null){
            emailUtente = intent.getStringExtra("email");
        }

        editTextUser = (AutoCompleteTextView) findViewById(R.id.editTextUser);


       // lista = (ListView)findViewById(R.id.listTravelsBySearch);
        mRecyclerView = (RecyclerView) findViewById(R.id.listTravelsBySearch);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
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
        viaggi_profilo = new HashMap<Profilo,List<Viaggio>>();
        dataTravels = new ArrayList<DataObject>();
        viaggi = new ArrayList<Viaggio>();



        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //TODO: fare stringa
        autocompleteFragment.setHint("Search by destination");


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TEST", "Place: " + place.getName());

                if(viaggi_profilo.size() != 0 && editTextUser.getText().toString().equals("")){
                    viaggi_profilo.clear();
                }

                String s = place.getName().toString();

                new MyTaskTranslate(s).execute();

            }

            @Override
            public void onError(Status status) {
                Log.i("TEST", "An error occurred: " + status);
            }


        });

        new MyTask().execute();
    }






    private void PopolaLista(Map<Profilo, List<Viaggio>> p_v){
        ArrayList<DataObject> result = new ArrayList<DataObject>();

        List<String> codiciViaggi= new ArrayList<String>();
        for(Profilo p : p_v.keySet()){
            for(Viaggio v: p_v.get(p)){
                if(!codiciViaggi.contains(v.getCodice())){
                    result.add(new DataObject(v, p));
                    codiciViaggi.add(v.getCodice());
                }
            }
        }


        Log.i("TEST", "result:" + result);



        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(result);
        adapter.onCreateViewHolder(group, 0);
        mRecyclerView.setAdapter(adapter);

      //  final ViaggioAdapter adapter = new ViaggioAdapter(this, R.layout.entry_travels_listview, result);
       // lista.setAdapter(adapter);

       /* lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                final Viaggio viaggio = (Viaggio) adattatore.getItemAtPosition(pos);

                Intent intent = new Intent(SearchActivity.this, ViaggioActivity.class);
                intent.putExtra("email", emailUtente);
                intent.putExtra("codiceViaggio", viaggio.getCodice());
                intent.putExtra("nomeViaggio", viaggio.getNome());

                startActivity(intent);

            }
        });*/

    }

    public ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        //for (int index = 0; index < 20; index++) {
            //DataObject obj = new DataObject("Some Primary Text " + index,
            //        "Secondary " + index);
            //results.add(index, obj);
       // }
        return results;
    }


    private class MyTaskTranslate extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String toTranslate;
        String translated;

        public MyTaskTranslate(String word){
            toTranslate = word;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(SearchActivity.this)) {

                    Log.i("TEST", "placeNuovo: " + toTranslate);

                    String languageDevice = Locale.getDefault().getLanguage();
                    if(!languageDevice.equals("it")){
                        GoogleTranslate translate = new GoogleTranslate(API_KEY);
                        translated = translate.translate(toTranslate, languageDevice, "it");

                        String finalTranslated = translated.toLowerCase();
                        translated = finalTranslated.replace(" ", "_");

                        Log.i("TEST", "placeNuovo tradotto: " + translated);

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

            new myTaskSearchByDestination().execute();

        }
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
                    HttpPost httppost = new HttpPost(ADDRESS);
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

                            if(jArray != null && result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    String emailUtente = json_data.getString("email").toString();
                                    String usernameUtente = json_data.getString("username").toString();

                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "("+usernameUtente+")";
                                    users.add(stringaFinale);
                                }
                            }



                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                }
                else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(),e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this,android.R.layout.simple_list_item_1,users);

            editTextUser.setAdapter(adapter);
            editTextUser.setThreshold(1);
            editTextUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    onClickSearchUser(view, adapter.getItem(position).toString());
                }
            });



        }
    }

    public void onClickSearchUser(View v, String utenteSelezionato) {
        if(viaggi_profilo.size() != 0 && editTextUser.getText().toString().equals("")){
            viaggi_profilo.clear();
        }
        String usernameUtenteSelezionato = utenteSelezionato.substring(utenteSelezionato.indexOf('(') + 1, utenteSelezionato.indexOf(')'));
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
        String result, stringaFinale = "";
        Map<Profilo, List<Viaggio>> mappaProvvisoria = new HashMap<Profilo, List<Viaggio>>();
        String username;

        public myTaskSearchByUser(String username){
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
                    HttpPost httppost = new HttpPost(ADDRESS_PER_VIAGGI_DA_UTENTE);
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

                            if(jArray != null && result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codice = json_data.getString("codice").toString();
                                    String nomeViaggio = json_data.getString("nomeViaggio").toString();
                                    String emailUtente = json_data.getString("email").toString();
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    String urlImmagineViaggio = json_data.getString("urlImmagineViaggio").toString();
                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente,null, null, null, null, null, null, null);

                                    List<Viaggio> viaggi = new ArrayList<Viaggio>();
                                    viaggi.add(new Viaggio(codice, nomeViaggio, urlImmagineViaggio));

                                    Log.i("TEST", "viaggi: " + viaggi);
                                    mappaProvvisoria.put(p,viaggi);
                                }
                            }



                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                }
                else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(),e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("TEST", "mappaProvvisoria:" +mappaProvvisoria);

            PopolaLista(mappaProvvisoria);
            autocompleteFragment.setText("");

            super.onPostExecute(aVoid);

        }
    }

    private class myTaskSearchByDestination extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";
        Map<Profilo, List<Viaggio>> mappaProvvisoria = new HashMap<Profilo, List<Viaggio>>();


        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("destinazione", destination));

            Log.i("TEST", "destinazione: " + destination);

            try {
                if (InternetConnection.haveInternetConnection(SearchActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(ADDRESS_PER_VIAGGI_DA_DESTINAZIONE);
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

                            if(jArray != null && result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codice = json_data.getString("codice").toString();
                                    String nomeViaggio = json_data.getString("nomeViaggio").toString();
                                    String emailUtente = json_data.getString("email").toString();
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    String urlImmagineViaggio = json_data.getString("urlImmagineViaggio").toString();

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente,null, null, null, null, null, null, null);

                                    List<Viaggio> viaggi = new ArrayList<Viaggio>();
                                    viaggi.add(new Viaggio(codice, nomeViaggio,urlImmagineViaggio));

                                    mappaProvvisoria.put(p,viaggi);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.e("TEST", "Input Stream uguale a null");
                    }

                }
                else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(),e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("TEST", "Mappa mappaProvvisoria:" +mappaProvvisoria);

            PopolaLista(mappaProvvisoria);
            editTextUser.setText("");
            //PopolaLista();


            super.onPostExecute(aVoid);

        }
    }
}


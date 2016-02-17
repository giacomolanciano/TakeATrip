package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.Classes.ViaggioAdapter;
import com.example.david.takeatrip.R;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {


    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private final String ADDRESS_PER_VIAGGI_DA_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryViaggiDiUtente.php";
    private final String ADDRESS_PER_VIAGGI_DA_DESTINAZIONE = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryViaggiDaDestinazione.php";



    private String nomeScelto, cognomeScelto, destination;

    private AutoCompleteTextView editTextUser;
    List<String> users, destinations;
    Map<Profilo, List<Viaggio>> viaggi_profilo;
    private ListView lista;


    private PlaceAutocompleteFragment autocompleteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        editTextUser = (AutoCompleteTextView) findViewById(R.id.editTextUser);


        lista = (ListView)findViewById(R.id.listTravelsBySearch);


        users = new ArrayList<String>();
        destinations = new ArrayList<String>();
        viaggi_profilo = new HashMap<Profilo,List<Viaggio>>();


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
                s = s.toLowerCase();
                String placeNuovo = s.replace(" ", "_");


                Log.i("TEST", "placeNuovo: " + placeNuovo);

                destination = placeNuovo;

                new myTaskSearchByDestination().execute();
            }

            @Override
            public void onError(Status status) {
                Log.i("TEST", "An error occurred: " + status);
            }


        });

        new MyTask().execute();


    }


    public void onClickSearchUser(View v) {
        if(viaggi_profilo.size() != 0 && editTextUser.getText().toString().equals("")){
            viaggi_profilo.clear();
        }
        String[] nomeSplittato = editTextUser.getText().toString().split(" ");
        if(nomeSplittato.length == 2){
            nomeScelto = nomeSplittato[0];
            cognomeScelto = nomeSplittato[1];
        }

        //Toast.makeText(getBaseContext(), "nome:" + nomeScelto, Toast.LENGTH_LONG).show();
        //Toast.makeText(getBaseContext(), "cognome:" + cognomeScelto, Toast.LENGTH_LONG).show();

        new myTaskSearchByUser().execute();

    }



    private void PopolaLista(Map<Profilo, List<Viaggio>> p_v){
        List<Viaggio> result = new ArrayList<Viaggio>();
        for(Profilo p : p_v.keySet()){
            result.addAll(p_v.get(p));
        }

        Log.i("TEST", "result:" + result);

        final ViaggioAdapter adapter = new ViaggioAdapter(this,R.layout.entry_travels_listview, result);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                final Viaggio viaggio = (Viaggio) adattatore.getItemAtPosition(pos);
                // Toast.makeText(getBaseContext(), "hai cliccato il viaggio: " + viaggio.getNome(), Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(SearchActivity.this, ViaggioActivity.class);
                //intent.putExtra("email", email);
                intent.putExtra("codiceViaggio", viaggio.getCodice());
                intent.putExtra("nomeViaggio", viaggio.getNome());

                startActivity(intent);

            }
        });

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
                                    stringaFinale = nomeUtente + " " + cognomeUtente;
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

            ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this,android.R.layout.simple_list_item_1,users);

            editTextUser.setAdapter(adapter);
            editTextUser.setThreshold(1);

            super.onPostExecute(aVoid);

        }
    }





    private class myTaskSearchByUser extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";
        Map<Profilo, List<Viaggio>> mappaProvvisoria = new HashMap<Profilo, List<Viaggio>>();


        @Override
        protected Void doInBackground(Void... params) {



            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("nome", nomeScelto));
            dataToSend.add(new BasicNameValuePair("cognome", cognomeScelto));

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
                                    Profilo p = new Profilo(nomeScelto, cognomeScelto,null,null);

                                    List<Viaggio> viaggi = new ArrayList<Viaggio>();
                                    viaggi.add(new Viaggio(codice, nomeViaggio));

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
            Log.i("TEST", "Mappa mappaProvvisoria:" +mappaProvvisoria);

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
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    Profilo p = new Profilo(nomeUtente, cognomeUtente,null,null);

                                    List<Viaggio> viaggi = new ArrayList<Viaggio>();
                                    viaggi.add(new Viaggio(codice, nomeViaggio));

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


            Log.i("TEST", "Mappa mappaProvvisoria:" +mappaProvvisoria);

            PopolaLista(mappaProvvisoria);
            editTextUser.setText("");
            //PopolaLista();


            super.onPostExecute(aVoid);

        }
    }
}

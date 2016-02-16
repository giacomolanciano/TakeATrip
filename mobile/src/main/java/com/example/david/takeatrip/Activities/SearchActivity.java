package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.Classes.ViaggioAdapter;
import com.example.david.takeatrip.R;

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

public class SearchActivity extends AppCompatActivity {


    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private final String ADDRESS_PER_VIAGGI_DA_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryViaggiDiUtente.php";


    private String nomeScelto, cognomeScelto, destination;

    private AutoCompleteTextView editTextUser, editTextDestination;
    List<String> users, destinations;
    List<Viaggio> viaggi;
    private ListView lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        editTextUser = (AutoCompleteTextView) findViewById(R.id.editTextUser);
        editTextDestination = (AutoCompleteTextView) findViewById(R.id.editTextDestination);

        lista = (ListView)findViewById(R.id.listTravelsBySearch);


        users = new ArrayList<String>();
        destinations = new ArrayList<String>();
        viaggi = new ArrayList<Viaggio>();


        new MyTask().execute();


    }


    public void onClickSearchUser(View v) {
        viaggi.clear();
        String[] nomeSplittato = editTextUser.getText().toString().split(" ");
        if(nomeSplittato.length == 2){
            nomeScelto = nomeSplittato[0];
            cognomeScelto = nomeSplittato[1];
        }

        Toast.makeText(getBaseContext(), "nome:" + nomeScelto, Toast.LENGTH_LONG).show();
        Toast.makeText(getBaseContext(), "cognome:" + cognomeScelto, Toast.LENGTH_LONG).show();

        new myTaskSearchByUser().execute();

    }



    private void PopolaLista(){

        final ViaggioAdapter adapter = new ViaggioAdapter(this,R.layout.entry_travels_listview, viaggi);
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
                                    viaggi.add(new Viaggio(codice, nomeViaggio));
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


            PopolaLista();


            super.onPostExecute(aVoid);

        }
    }
}


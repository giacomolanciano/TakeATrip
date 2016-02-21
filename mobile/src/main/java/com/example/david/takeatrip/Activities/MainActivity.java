package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.RoundedImageView;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private final String ADDRESS_INSERIMENTO_VIAGGIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoViaggio.php";
    private final String ADDRESS_INSERIMENTO_ITINERARIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoItinerario.php";

    private String name, surname, email;
    private String date, password;

    private ImageView imageViewProfileRound;
    private FrameLayout layoutNewTravel;
    TableLayout table_layout;
    private LinearLayout layoutNewPartecipants;


    String nomeViaggio, UUIDViaggio;
    AutoCompleteTextView text;
    List<String> names, namesPartecipants;
    Set<Profilo> profiles, partecipants;
    Profilo myProfile;

    EditText editTextNameTravel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");
        }
        else{
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView)findViewById(R.id.imageView_round);
        layoutNewTravel = (FrameLayout)findViewById(R.id.FrameNewTravel);
        //table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        layoutNewPartecipants = (LinearLayout)findViewById(R.id.layoutPartecipants);


        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        editTextNameTravel=(EditText)findViewById(R.id.editTextNameTravel);





        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();

        myProfile = new Profilo(email, name, surname,null);

        new MyTask().execute();

    }




    private void AddToLayout(String partecipant){
        TextView tv = new TextView(this);
        tv.setText(partecipant);


        layoutNewPartecipants.addView(tv);
    }

    /*

    private void BuildTable(int rows, int cols, String partecipant) {

        // outer for loop
        for (int i = 1; i <= rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int j = 1; j <= cols; j++) {

                TextView tv = new TextView(this);
                tv.setText("R " + i + ", C" + j);


                row.addView(tv);

            }

            table_layout.addView(row);

        }
    }

*/

    public void ClickImageProfile(View v){
        Intent openProfilo = new Intent(MainActivity.this, ProfiloActivity.class);
        openProfilo.putExtra("name", name);
        openProfilo.putExtra("surname", surname);
        openProfilo.putExtra("email", email);
        openProfilo.putExtra("dateOfBirth", date);
        openProfilo.putExtra("pwd", password);

        // passo all'attivazione dell'activity
        startActivity(openProfilo);
    }


    public void onClickSearchTravels(View v){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }


    public void ClickTravels(View v){
        Intent openListaViaggi = new Intent(MainActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);

        // passo all'attivazione dell'activity
        startActivity(openListaViaggi);
    }


    public void ClickNewTravel(View v){
        nomeViaggio = "";

        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        layoutNewTravel.setVisibility(View.VISIBLE);

    }

    public void ClickNewPartecipant(View v){
        if(!text.getText().toString().equals("")){
            AddToLayout(text.getText().toString());

            //BuildTable(3, 3);
            namesPartecipants.add(text.getText().toString());

            text.setText("");
        }

    }

    public void onClickCancelButton(View v){
        //TODO: eliminare layout esistente
        layoutNewTravel.setVisibility(View.INVISIBLE);

        namesPartecipants.clear();
        partecipants.clear();
        layoutNewPartecipants.removeAllViews();
        editTextNameTravel.setText("");

        Log.i("TEST", "lista nomi partecipanti:" + namesPartecipants);
        Log.i("TEST", "lista partecipanti:" + partecipants);

    }


    public void onClickCreateTravel(View v){
        if(!partecipants.contains(myProfile)){
            partecipants.add(myProfile);
        }

        nomeViaggio = editTextNameTravel.getText().toString();

        for(String s : namesPartecipants){
            String name = s.split(" ")[0];
            String surname = s.split(" ")[1];
            for(Profilo p : profiles){
                if(p.getName().equals(name) && p.getSurname().equals(surname)){
                    if(!partecipants.contains(p)){
                        partecipants.add(p);
                    }
                }
            }
        }
        Log.i("TEST", "lista partecipanti:" + partecipants);
        new TaskForUUID().execute();


    }


    public void onClickSocialButton(View v){
        Intent intent = new Intent(MainActivity.this, SocialActivity.class);
        startActivity(intent);
    }




    private class MyTask extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(MainActivity.this)) {
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

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null);
                                    profiles.add(p);
                                    stringaFinale = nomeUtente + " " + cognomeUtente;
                                    names.add(stringaFinale);
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

            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,names);

            text.setAdapter(adapter);
            text.setThreshold(1);

            super.onPostExecute(aVoid);

        }
    }



    private class TaskForUUID extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("viaggio", nomeViaggio));

            Log.i("TEST", "nomeViaggio: " + nomeViaggio);

                try {
                    if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                        Log.i("CONNESSIONE Internet", "Presente!");
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(ADDRESS_INSERIMENTO_VIAGGIO);
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
                                //Log.i("TEST", "result" +result);

                                UUIDViaggio = result;

                                Log.i("TEST", "UUID viaggio " +UUIDViaggio);

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

            if(result.contains("Duplicate")){
                Log.e("TEST", "devo generare un nuovo UUID");
                new TaskForUUID().execute();
            }
            else{
                Log.i("TEST", "UUID corretto, ora aggiungo gli itinerari");
                new TaskForItineraries().execute();
            }
            super.onPostExecute(aVoid);

        }
    }


    private class TaskForItineraries extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {

            for(Profilo p : partecipants){
                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("codice", UUIDViaggio));
                dataToSend.add(new BasicNameValuePair("email", p.getEmail()));


                try {
                    if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                        Log.i("CONNESSIONE Internet", "Presente!");
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(ADDRESS_INSERIMENTO_ITINERARIO);
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            layoutNewTravel.setVisibility(View.INVISIBLE);

            //TODO: stringa in inglese
            Toast.makeText(getBaseContext(), "Viaggio creato con successo", Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);

        }
    }
}

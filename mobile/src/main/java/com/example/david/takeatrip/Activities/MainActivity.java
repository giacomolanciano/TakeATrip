package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DatabaseHandler;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

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
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private final String ADDRESS_INSERIMENTO_VIAGGIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoViaggio.php";
    private final String ADDRESS_INSERIMENTO_ITINERARIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoItinerario.php";
    private final String ADDRESS_INSERIMENTO_FILTRO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoFiltro.php";



    private final int LIMIT_IMAGES_VIEWS = 6;
    private final String TAG = "MainActivity";

    private String name, surname, email, nazionalità, sesso, username, lavoro, descrizione, tipo;
    private String date, password;

    private ImageView imageViewProfileRound;
    private FrameLayout layoutNewTravel;
    TableLayout table_layout;
    private LinearLayout layoutNewPartecipants;


    String nomeViaggio, UUIDViaggio, filtro;
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
            nazionalità = intent.getStringExtra("nazionalita");
            sesso = intent.getStringExtra("sesso");
            username = intent.getStringExtra("username");
            lavoro = intent.getStringExtra("lavoro");
            descrizione = intent.getStringExtra("descrizione");
            tipo = intent.getStringExtra("tipo");
        }
        else{
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView)findViewById(R.id.imageView_round);
        //layoutNewTravel = (FrameLayout)findViewById(R.id.FrameNewTravel);
        //table_layout = (TableLayout) findViewById(R.id.tableLayout1);



        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();

        myProfile = new Profilo(email, name, surname,date, password, nazionalità, sesso, username, lavoro, descrizione);

        new MyTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();


        AppEventsLogger.activateApp(this);
    }

    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }




    public void ClickImageProfile(View v){
        Intent openProfilo = new Intent(MainActivity.this, ProfiloActivity.class);
        openProfilo.putExtra("name", name);
        openProfilo.putExtra("surname", surname);
        openProfilo.putExtra("email", email);
        openProfilo.putExtra("dateOfBirth", date);
        openProfilo.putExtra("pwd", password);
        openProfilo.putExtra("nazionalita", nazionalità);
        openProfilo.putExtra("sesso", sesso);
        openProfilo.putExtra("username", username);
        openProfilo.putExtra("lavoro", lavoro);
        openProfilo.putExtra("descrizione", descrizione);
        openProfilo.putExtra("tipo", tipo);

        // passo all'attivazione dell'activity
        startActivity(openProfilo);
    }


    public void onClickSearchTravels(View v){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }


    public void ClickTravels(View v){
        Intent openListaViaggi = new Intent(MainActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);

        // passo all'attivazione dell'activity
        startActivity(openListaViaggi);
    }


    LinearLayout rowHorizontal;
    public void ClickNewTravel(View v){
        nomeViaggio = "";

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_viaggio2);
        dialog.setTitle("Create new travel");

        final AutoCompleteTextView text=(AutoCompleteTextView)dialog.findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,names);
        text.setHint("Add partecipant");
        text.setAdapter(adapter);
        text.setThreshold(1);


        //layoutNewPartecipants = (TableLayout)dialog.findViewById(R.id.layoutPartecipants);
        layoutNewPartecipants = (LinearLayout)dialog.findViewById(R.id.layoutPartecipants);
        rowHorizontal = (LinearLayout)dialog.findViewById(R.id.layout_horizontal);



        TextView travel = (TextView) dialog.findViewById(R.id.titoloViaggio);

        editTextNameTravel = (EditText) dialog.findViewById(R.id.editTextNameTravel);


        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreateTravel);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextNameTravel.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Name Travel omitted", Toast.LENGTH_LONG).show();
                }
                else {

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
                    Log.i("TEST", "nome Viaggio:" + nomeViaggio);


                    //new TaskForUUID().execute();

                    namesPartecipants.clear();
                    partecipants.clear();

                    dialog.dismiss();
                }
            }
        });


        Button buttonCancella = (Button) dialog.findViewById(R.id.buttonCancellaDialog);
        buttonCancella.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                namesPartecipants.clear();
                partecipants.clear();
                layoutNewPartecipants.removeAllViews();
                editTextNameTravel.setText("");

                Log.i("TEST", "lista nomi partecipanti:" + namesPartecipants);
                Log.i("TEST", "lista partecipanti:" + partecipants);

                dialog.dismiss();
            }
        });




        final FloatingActionButton buttonAdd = (FloatingActionButton) dialog.findViewById(R.id.floatingButtonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals("")) {
                    TextView tv = new TextView(MainActivity.this);
                    tv.setText(text.getText().toString());

                    String s = text.getText().toString();
                    String name = s.split(" ")[0];
                    String surname = s.split(" ")[1];
                    for(Profilo p : profiles){


                        //TODO: se si mette l'username è univoca la ricerca per utente
                        if(p.getName().equals(name) && p.getSurname().equals(surname)){
                            if(!partecipants.contains(p)){

                                if(partecipants.size()%LIMIT_IMAGES_VIEWS == 0){
                                    rowHorizontal = new LinearLayout(MainActivity.this);
                                    rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                                    //Log.i(TAG, "creato nuovo layout");
                                    layoutNewPartecipants.addView(rowHorizontal);
                                    layoutNewPartecipants.addView(new TextView(MainActivity.this), 20, 20);
                                    //Log.i(TAG, "aggiunto row e view al layout verticale");
                                }

                                final ImageView image = new RoundedImageView(MainActivity.this, null);
                                image.setContentDescription(p.getEmail());

                                //TODO: mettere le immagini dei partecipanti
                                image.setImageResource(R.drawable.logodef);
                                rowHorizontal.addView(image, 40, 40);
                                rowHorizontal.addView(new TextView(MainActivity.this), 20, 40);
                                Log.i(TAG, "aggiungo la view nel layout orizzonale");

                                partecipants.add(p);

                            }
                            else{
                                Toast.makeText(getBaseContext(), "User already present in travel", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    text.setText("");

                }

            }
        });


        dialog.show();
    }



    public void onClickSocialButton(View v){
        Intent intent = new Intent(MainActivity.this, SocialActivity.class);
        startActivity(intent);
    }

    public void onClickSettings(View v){

        try{
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setItems(R.array.CommandsSettingsMain, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //logout profile
                            if(email.contains("@")){
                                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                                // Inserting Users
                                Log.d("TEST", "Drop the user...");
                                db.deleteContact(myProfile);
                            }
                            else{
                                if(LoginManager.getInstance() != null){
                                    Log.d("TEST", "Log out from facebook: ..");

                                    LoginManager.getInstance().logOut();
                                }
                            }
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            finish();
                            break;
                    }
                }
            });

            // Create the AlertDialog object and return it
            builder.create().show();
        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }
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

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, null, null, null, null, null);
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
            /*
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,names);

            text.setAdapter(adapter);
            text.setThreshold(1);

*/
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

            Log.i("TEST", "lista partecipanti:" + partecipants);


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
            String stringaFiltro = nomeViaggio.split(" ")[0];
            filtro = stringaFiltro.toLowerCase();

            Log.i("TEST", "filtro: " + filtro);

            new TaskForFilter().execute();

            super.onPostExecute(aVoid);

        }
    }


    private class TaskForFilter extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {

            for(Profilo p : partecipants){
                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("codiceViaggio", UUIDViaggio));
                dataToSend.add(new BasicNameValuePair("filtro", filtro));


                try {
                    if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                        Log.i("CONNESSIONE Internet", "Presente!");
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(ADDRESS_INSERIMENTO_FILTRO);
                        httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                        HttpResponse response = httpclient.execute(httppost);

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

            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //TODO: stringa in inglese
            Toast.makeText(getBaseContext(), "Viaggio creato con successo", Toast.LENGTH_LONG).show();


            /*
            //TODO: fa partire l'activity del Viaggio
            Intent intent = new Intent(MainActivity.this, ViaggioActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("codiceViaggio", UUIDViaggio);
            intent.putExtra("nomeViaggio", nomeViaggio);

            startActivity(intent);

            */
            super.onPostExecute(aVoid);

        }
    }
}

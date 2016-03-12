package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Utilities.RetrieveImageTask;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatabaseHandler;
import com.example.david.takeatrip.Utilities.DownloadImageTask;
import com.example.david.takeatrip.Utilities.RetrieveImageTask;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveId;

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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private final String ADDRESS_INSERIMENTO_VIAGGIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoViaggio.php";
    private final String ADDRESS_INSERIMENTO_ITINERARIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoItinerario.php";
    private final String ADDRESS_INSERIMENTO_FILTRO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoFiltro.php";

    private static final int REQUEST_FOLDER = 123;
    private static final int REQUEST_IMAGE_PROFILE = 124;
    private static final int REQUEST_COVER_IMAGE = 125;


    private final int LIMIT_IMAGES_VIEWS = 6;
    private final String TAG = "MainActivity";

    private String name, surname, email, nazionalità, sesso, username, lavoro, descrizione, tipo;
    private String date, password;
    private String emailEsterno;

    private ImageView imageViewProfileRound;

    private LinearLayout layoutNewPartecipants;

    String nomeViaggio, UUIDViaggio, filtro;
    AutoCompleteTextView text;
    List<String> names, namesPartecipants;
    Set<Profilo> profiles, partecipants;
    Profilo myProfile;

    EditText editTextNameTravel;
    Bitmap imageProfile = null;
    Bitmap coverImage = null;
    Profile profile;
    private DriveId idFolderTAT, idImmagineCopertina, idImageProfile;


    private ProgressDialog mProgressDialog;


    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent() != null) {
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
            profile = intent.getParcelableExtra("profile");
        }
        else{
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView)findViewById(R.id.imageView_round);


        new MyTask().execute();
        new MyTaskIDFolder(this,email).execute();
        new MyTaskIDProfileImage(this,email).execute();
        new MyTaskIDCoverImage(this,email).execute();



        if(profile != null){
            Log.i("TEST", profile.getProfilePictureUri(150, 150).toString());
            final Uri image_uri = profile.getProfilePictureUri(150, 150);

            try {
                final URI image_URI = new URI(image_uri.toString());

                Log.i("TEST", "url_image: " + image_URI.toURL().toString());

                DownloadImageTask task = new DownloadImageTask(imageViewProfileRound);
                task.execute(image_URI.toURL().toString());

                imageProfile = ((BitmapDrawable)imageViewProfileRound.getDrawable()).getBitmap();
                Log.i("TEST", "bitmap image profile: " + imageProfile);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            if(sesso != null && sesso.equals("M")){
                imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_male));
            }
            else if (sesso != null && sesso.equals("F")){
                imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_female));
            }
        }

        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();

        myProfile = new Profilo(email, name, surname,date, password, nazionalità, sesso, username, lavoro, descrizione);

    }


    @Override
    protected void onResume() {
        super.onResume();

        TakeATrip TAT = ((TakeATrip) getApplicationContext());
        googleApiClient = TAT.getGoogleApiClient();
        googleApiClient.connect();
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
        openProfilo.putExtra("emailEsterno", emailEsterno);
        openProfilo.putExtra("dateOfBirth", date);
        openProfilo.putExtra("pwd", password);
        openProfilo.putExtra("nazionalita", nazionalità);
        openProfilo.putExtra("sesso", sesso);
        openProfilo.putExtra("username", username);
        openProfilo.putExtra("lavoro", lavoro);
        openProfilo.putExtra("descrizione", descrizione);
        openProfilo.putExtra("tipo", tipo);
        openProfilo.putExtra("profile", profile);
        openProfilo.putExtra("idFolder", idFolderTAT);
        openProfilo.putExtra("idImageProfile",idImageProfile);
        openProfilo.putExtra("coverImage",idImmagineCopertina);

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
        namesPartecipants.clear();
        partecipants.clear();

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


                    new TaskForUUID().execute();

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
                    Log.i("TEST", "partecipante selezionato: " + s);


                    String usernameUtenteSelezionato = s.substring(s.indexOf('(')+1, s.indexOf(')'));
                    Log.i("TEST", "username selezionato: " + usernameUtenteSelezionato);


                    for(Profilo p : profiles){

                        if(p.getUsername().equals(usernameUtenteSelezionato)){
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
        Intent openSocial = new Intent(MainActivity.this, SocialActivity.class);
        openSocial.putExtra("email", email);
        startActivity(openSocial);
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

                                /*
                                if(LoginManager.getInstance() != null){
                                    Log.d("TEST", "Log out from facebook: ..");

                                    LoginManager.getInstance().logOut();
                                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                    finish();
                                }else{
                                    LoginActivity loginActivity = new LoginActivity();
                                    loginActivity.GoogleLogOut(MainActivity.this);
                                }*/


                                Log.d("TEST", "Log out from google con apiClient: " + googleApiClient);

                                if(!googleApiClient.isConnected()) {
                                    googleApiClient.connect();
                                }
                                else{
                                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                    Log.d("TEST", "Status: " + status);
                                                    if(status.isSuccess()){
                                                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                                    }

                                                }
                                            });
                                }
                            }

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

    @Override
    public void onClick(View v) {

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
                                    String nomeUtente = json_data.getString("nome");
                                    String cognomeUtente = json_data.getString("cognome");
                                    String emailUtente = json_data.getString("email");
                                    String username = json_data.getString("username");


                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, null, username, null, null, null);
                                    profiles.add(p);
                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "("+username+")";
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

            //  layoutNewTravel.setVisibility(View.INVISIBLE);

            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //é stato creato tutto il viaggio, ora associo al viaggio la cartella sul drive
            //TODO: creare la folder nel drive per ospitare i contenuti del viaggio
            Intent intent = new Intent(getBaseContext(), CreateFolderActivity.class);
            intent.putExtra("nameFolder", nomeViaggio);


            //TODO: deve essere modificata con gli id delle cartelle di tutti i partecipanti al viaggio
            intent.putExtra("idFolder", idFolderTAT);

            //TODO: far in modo che l'activity non si veda
            startActivityForResult(intent, REQUEST_FOLDER);



            Toast.makeText(getBaseContext(), R.string.created_travel, Toast.LENGTH_LONG).show();

            super.onPostExecute(aVoid);

        }
    }






    private class MyTaskFolder extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_INSERT_FOLDER = "InserimentoCartella.php";

        InputStream is = null;
        String emailUser, idTravel,result;
        String nomeCartella;
        DriveId idFolder;
        Context context;

        public MyTaskFolder(Context c, String emailUtente, String idT, DriveId id, String n){
            context  = c;
            emailUser = emailUtente;
            idTravel = idT;
            idFolder = id;
            nomeCartella = n;
        }


        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("emailUtente", emailUser));
            dataToSend.add(new BasicNameValuePair("codiceViaggio", idTravel));
            dataToSend.add(new BasicNameValuePair("codiceCartella", idFolder+""));
            dataToSend.add(new BasicNameValuePair("nomeCartella", nomeCartella));

            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_FOLDER);
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
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
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
            Log.i("TEST", "risultato operazione di inserimento cartella nel DB:" + result);
            if(!result.equals("OK")){
                //Non è stata creata una cartella per ospitare il viaggio
                //new MyTaskFolder().execute();
            }

            Intent intent = new Intent(MainActivity.this, ViaggioActivity.class);
            intent.putExtra("email",emailUser);
            intent.putExtra("codiceViaggio", idTravel);
            intent.putExtra("nomeViaggio", nomeViaggio);
            intent.putExtra("idFolder", idFolder);



            startActivity(intent);
            finish();

            super.onPostExecute(aVoid);

        }
    }


    private class MyTaskIDFolder extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_FOLDER = "QueryDriveIDCartella.php";

        InputStream is = null;
        String emailUser, idTravel,result;
        String nomeCartella;
        DriveId idFolder;
        Context context;

        public MyTaskIDFolder(Context c, String emailUtente){
            context  = c;
            emailUser = emailUtente;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));

            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_QUERY_FOLDER);
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
                                    idFolderTAT = DriveId.decodeFromString(json_data.getString("codiceCartella"));
                                }
                            }
                        } catch (Exception e) {
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
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

            Log.i("TEST", "Prelevato id cartella di default: " +idFolderTAT);

            super.onPostExecute(aVoid);

        }
    }



    private class MyTaskIDProfileImage extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_PROFILE_IMAGE = "QueryDriveIDImmagineProfilo.php";

        InputStream is = null;
        String emailUser, idTravel,result;
        String nomeCartella;
        DriveId idFolder;
        Context context;

        public MyTaskIDProfileImage(Context c, String emailUtente){
            context  = c;
            emailUser = emailUtente;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));

            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_QUERY_PROFILE_IMAGE);
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
                                    idImageProfile = DriveId.decodeFromString(json_data.getString("idImmagineProfilo"));
                                }
                            }
                        } catch (Exception e) {
                            result = "ERRORE";
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
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
            Log.i("TEST", "risultato dal prelievo dell'id imm profilo: " + result);
            if(!result.equals("ERRORE")){
                new RetrieveImageTask(MainActivity.this,imageViewProfileRound,idImageProfile).execute();
            }
        }
    }


    private class MyTaskIDCoverImage extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_COVER_IMAGE = "QueryDriveIDImmagineCopertina.php";

        InputStream is = null;
        String emailUser, idTravel,result;
        String nomeCartella;
        DriveId idFolder;
        Context context;

        public MyTaskIDCoverImage(Context c, String emailUtente){
            context  = c;
            emailUser = emailUtente;
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));
            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_QUERY_COVER_IMAGE);
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
                                    idImmagineCopertina = DriveId.decodeFromString(json_data.getString("idImmagine"));
                                }
                            }
                        } catch (Exception e) {
                            result = "ERRORE";
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
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
            Log.i("TEST", "risultato dal prelievo dell'id imm copertina: " + idImmagineCopertina);
            super.onPostExecute(aVoid);
        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_FOLDER){
            if(resultCode == RESULT_OK) {
                String idTravel = UUIDViaggio;
                DriveId idFolder = data.getParcelableExtra("idFolder");
                String nameFolder = data.getStringExtra("nameFolder");

                Log.i("TEST", "Ricevuto l'id della cartella: " + idFolder);
                Log.i("TEST", "Ricevuto il nome della cartella: " + nameFolder);


                MyTaskFolder myTaskFolder = new MyTaskFolder(this, email, idTravel, idFolder, nameFolder);
                myTaskFolder.execute();
            }

        }
        else if (requestCode == REQUEST_IMAGE_PROFILE){
            Bitmap image = null;
            byte[] bytes = data.getByteArrayExtra("bitmapImage");
            if(bytes != null){
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            imageProfile = image;
            imageViewProfileRound.setImageBitmap(image);

        }
        else if (requestCode == REQUEST_COVER_IMAGE){
            Bitmap image = null;
            byte[] bytes = data.getByteArrayExtra("bitmapImage");
            if(bytes != null){
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            coverImage = image;
        }
    }


}

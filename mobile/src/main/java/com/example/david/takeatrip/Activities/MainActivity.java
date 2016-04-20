package com.example.david.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatabaseHandler;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveId;
import com.squareup.picasso.Picasso;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private final String ADDRESS = "QueryNomiUtenti.php";
    private final String ADDRESS_INSERIMENTO_VIAGGIO = "InserimentoViaggio.php";
    private final String ADDRESS_INSERIMENTO_ITINERARIO = "InserimentoItinerario.php";
    private final String ADDRESS_INSERIMENTO_FILTRO = "InserimentoFiltro.php";



    private static final int REQUEST_FOLDER = 123;
    private static final int REQUEST_IMAGE_PROFILE = 124;
    private static final int REQUEST_COVER_IMAGE = 125;


    private final int LIMIT_IMAGES_VIEWS = 5;
    private final String TAG = "MainActivity";

    private String name, surname, email, nazionalità, sesso, username, lavoro, descrizione, tipo;
    private String date, password, urlImmagineProfilo, urlImmagineCopertina;
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
    private String urlFolderTAT;

    private ProgressDialog mProgressDialog;
    private GoogleApiClient googleApiClient;

    private TakeATrip TAT;


    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;


    // The S3 client
    private AmazonS3Client s3;


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

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(MainActivity.this);


        new MyTask().execute();

        new MyTaskIDProfileImage(this,email).execute();
        new MyTaskIDCoverImage(this,email).execute();


        if(sesso != null && sesso.equals("M")){
            imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_male));
        }
        else if (sesso != null && sesso.equals("F")){
            imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_female));
        }

        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();
        myProfile = new Profilo(email, name, surname,date, password, nazionalità, sesso, username, lavoro, descrizione);
        TakeATrip TAT = (TakeATrip)getApplicationContext();
        TAT.setProfiloCorrente(myProfile);
    }


    @Override
    protected void onResume() {
        super.onResume();

        TakeATrip TAT = ((TakeATrip) getApplicationContext());
        googleApiClient = TAT.getGoogleApiClient();
        if(googleApiClient != null){
            googleApiClient.connect();
        }
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
        openProfilo.putExtra("urlImmagineProfilo",urlImmagineProfilo);
        openProfilo.putExtra("urlImmagineCopertina",urlImmagineCopertina);

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


        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(wrapper);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.activity_viaggio2, null);
        builder.setView(dialog);
        builder.setTitle(getString(R.string.NewTravel));


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




        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        namesPartecipants.clear();
                        partecipants.clear();
                        layoutNewPartecipants.removeAllViews();
                        editTextNameTravel.setText("");

                        Log.i("TEST", "lista nomi partecipanti:" + namesPartecipants);
                        Log.i("TEST", "lista partecipanti:" + partecipants);

                    }
                });

        builder.setPositiveButton(getString(R.string.Create),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editTextNameTravel.getText().toString().equals("")){
                            Toast.makeText(getBaseContext(), "Name Travel omitted", Toast.LENGTH_LONG).show();
                        }
                        else {

                            if(!partecipants.contains(myProfile)){
                                partecipants.add(myProfile);
                            }
                            nomeViaggio = editTextNameTravel.getText().toString();
                            for(String s : namesPartecipants){
                                for(Profilo p : profiles){
                                    if(p.getUsername().equals(s)){
                                        if(!partecipants.contains(p)){
                                            partecipants.add(p);
                                        }
                                    }
                                }
                            }
                            Log.i("TEST", "lista partecipanti:" + partecipants);
                            Log.i("TEST", "nome Viaggio:" + nomeViaggio);

                            new TaskForUUID().execute();

                        }
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

                                    layoutNewPartecipants.addView(rowHorizontal);
                                    layoutNewPartecipants.addView(new TextView(MainActivity.this), 20, 20);
                                }

                                final ImageView image = new RoundedImageView(MainActivity.this, null);
                                image.setContentDescription(p.getEmail());

                                if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
//                                    new BitmapWorkerTask(image).execute(Constants.ADDRESS_TAT + p.getIdImageProfile());
                                    String signedUrl = beginDownloadProfilePicture(p.getIdImageProfile());
                                    Picasso.with(MainActivity.this).load(signedUrl).into(image);

                                }else {
                                    if(p.getSesso().equals("M")){
                                        image.setImageResource(R.drawable.default_male);
                                    }
                                    else{
                                        image.setImageResource(R.drawable.default_female);
                                    }
                                }

                                rowHorizontal.addView(image, 60, 60);
                                rowHorizontal.addView(new TextView(MainActivity.this), 20, 60);
                                Log.i(TAG, "aggiungo la view nel layout orizzonale");
                                partecipants.add(p);
                                namesPartecipants.add(p.getUsername());

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


        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    public void onClickSocialButton(View v){
        Intent openSocial = new Intent(MainActivity.this, SocialActivity.class);
        openSocial.putExtra("email", myProfile.getEmail());
        Log.i("TEST: ", "EMAIL PER SOCIAL "+ myProfile.getEmail() );
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

                                if(profile!= null && LoginManager.getInstance() != null){
                                    Log.d("TEST", "Log out from facebook: ..");

                                    LoginManager.getInstance().logOut();
                                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                    finish();
                                }else{
                                    Log.d("TEST", "Log out from google con apiClient: " + googleApiClient);

                                    if(!googleApiClient.isConnected()) {
                                        googleApiClient.connect();
                                        Toast.makeText(getBaseContext(), getString(R.string.LogOutFailed), Toast.LENGTH_LONG).show();

                                    }
                                    else {
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
        String idProfiles, idCovers;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS);
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
                                    String sesso = json_data.getString("sesso");
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                    if(urlImmagineProfilo.equals("null")){
                                        idProfiles = null;
                                    }
                                    else {
                                        idProfiles = urlImmagineProfilo;
                                    }

                                    if (urlImmagineCopertina.equals("null")){
                                        idCovers = null;
                                    }
                                    else{
                                        idCovers = urlImmagineCopertina;
                                    }

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, sesso, username, null, null, null,idProfiles,idCovers);
                                    profiles.add(p);
                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "("+username+")";
                                    names.add(stringaFinale);
                                }
                            }


                        } catch (Exception e) {
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
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
        }
    }



/*
    private class MyTaskIDProfileImage extends AsyncTask<Void, Void, Void> {

        // The list of objects we find in the S3 bucket
        private List<S3ObjectSummary> s3ObjList;
        // A dialog to let the user know we are retrieving the files
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, getString(R.string.CaricamentoInCorso), "");
        }

        @Override
        protected Void doInBackground(Void... inputs) {

            try{
                // Queries files in the bucket from S3.
                s3ObjList = s3.listObjects(Constants.BUCKET_NAME).getObjectSummaries();
                transferRecordMaps.clear();

                HashMap<String, List<Object>> map = new HashMap<String, List<Object>>();
                List<Object> immaginiProfilo = new ArrayList<Object>();
                for (S3ObjectSummary summary : s3ObjList) {
                    Log.i("TEST", "keys of object in the bucket " + Constants.BUCKET_NAME + ": " + summary.getKey());
                    if(summary.getKey().contains(Constants.PROFILE_PICTURES_LOCATION)){
                        immaginiProfilo.add(summary.getKey());
                    }
                }





                map.put("profileImages", immaginiProfilo);
                transferRecordMaps.add(map);

            }
            catch(Exception e){
                Log.e("TEST", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            //se sono presenti immagini del profilo, prendo la prima
            if(transferRecordMaps.size()>0){
                if(transferRecordMaps.get(0) != null){
                    List<Object> immaginiProfilo = transferRecordMaps.get(0).get("profileImages");


                    //TODO: qui prelevare le immagini del profilo degli utenti
                    if(immaginiProfilo != null){
                        for(Object keyImage : immaginiProfilo){
                            String image = (String) keyImage;
                            if(image.contains(email)){

                                Log.i("TEST", "profile image: " + image);

                                beginDownloadProfilePicture(image);

                            }
                        }


                        //beginDownloadProfilePicture((String) transferRecordMaps.get(0).get("profileImages"));
                    }
                }

                transferRecordMaps.clear();
            }
        }
    }

*/


    private String beginDownloadProfilePicture(String key) {
        // Location to download files from S3 to. You can choose any accessible
        // file.
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        URL url = null;
        try {

            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += 1000 * 60 * 60; // 1 hour.
            expiration.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(Constants.BUCKET_NAME,key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);

            Log.i("TEST", "expiration date image: " + generatePresignedUrlRequest.getExpiration());
            Log.i("TEST", "amazon client: " + s3);



            url = s3.generatePresignedUrl(generatePresignedUrlRequest);

            Log.i("TEST", "url file: " + url);


            // Initiate the download
            //TransferObserver observer = transferUtility.download(Constants.BUCKET_NAME, key, file);
            //Log.i("TEST", "downloaded file: " + file);


            //observer.setTransferListener(new DownloadListener());
        }
        catch(Exception exception){
            exception.printStackTrace();
        }


        return url.toString();

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
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_INSERIMENTO_VIAGGIO);
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
                        HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_INSERIMENTO_ITINERARIO);
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
            String stringaFiltro = nomeViaggio.replace(" ","_");
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
                        HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_INSERIMENTO_FILTRO);
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
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            /*

            Intent intent = new Intent(getBaseContext(), CreateDriveFolderActivity.class);
            intent.putExtra("nameFolder", nomeViaggio);
            intent.putExtra("idFolder", idFolderTAT);

            //TODO: far in modo che l'activity non si veda
            startActivityForResult(intent, REQUEST_FOLDER);

            */
            Toast.makeText(getBaseContext(), R.string.created_travel, Toast.LENGTH_LONG).show();

            super.onPostExecute(aVoid);

        }
    }




    private class MyTaskIDProfileImage extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_PROFILE_IMAGE = "QueryImmagineProfilo.php";

        InputStream is = null;
        String emailUser, idTravel,result;
        String nomeCartella;
        DriveId idFolder;
        Context context;
        String signedUrl;

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

                                    //idImageProfile = DriveId.decodeFromString(json_data.getString("idImmagineProfilo"));
                                    urlImmagineProfilo = json_data.getString("urlImmagineProfilo");

                                }
                            }


                            if(!result.equals("NULL") && !urlImmagineProfilo.equals("null")){
                                signedUrl = beginDownloadProfilePicture(urlImmagineProfilo);
                            }


                        } catch (Exception e) {
                            result = "NULL";
                            //Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
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
            if(signedUrl != null ){
                Picasso.with(MainActivity.this).load(signedUrl.toString()).into(imageViewProfileRound);
            }
            else{
                //L'utente è loggato con facebook
                if(profile != null){
                    Log.i("TEST", profile.getProfilePictureUri(150, 150).toString());
                    final Uri image_uri = profile.getProfilePictureUri(150, 150);

                    try {
                        final URI image_URI = new URI(image_uri.toString());

                        Log.i("TEST", "url_image: " + image_URI.toURL().toString());


                        Picasso.with(MainActivity.this).load(image_URI.toURL().toString()).into(imageViewProfileRound);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            hideProgressDialog();

        }
    }


    private class MyTaskIDCoverImage extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_COVER_IMAGE = "QueryImmagineCopertina.php";

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
                                    //idImmagineCopertina = DriveId.decodeFromString(json_data.getString("idImmagine"));
                                    urlImmagineCopertina = json_data.getString("urlImmagineCopertina");
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
            Log.i("TEST", "risultato dal prelievo dell'id imm copertina: " + urlImmagineCopertina);

            super.onPostExecute(aVoid);
        }
    }



    /*
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
    }
    */


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
        }
    }

}

package com.example.david.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatabaseHandler;
import com.example.david.takeatrip.Utilities.InternetConnection;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TEST MainActivity";


    private final String ADDRESS = "QueryNomiUtenti.php";
    private final String ADDRESS_INSERIMENTO_VIAGGIO = "InserimentoViaggio.php";
    private final String ADDRESS_INSERIMENTO_ITINERARIO = "InserimentoItinerario.php";
    private final String ADDRESS_INSERIMENTO_FILTRO = "InserimentoFiltro.php";

    private static final int SIZE_IMAGE_PARTECIPANT = Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT - 40;


    private static final int REQUEST_FOLDER = 123;
    private static final int REQUEST_IMAGE_PROFILE = 124;
    private static final int REQUEST_COVER_IMAGE = 125;


    private final int LIMIT_IMAGES_VIEWS = 5;

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

    //per allert
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null) {
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
        } else {
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView) findViewById(R.id.imageView_round);

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(MainActivity.this);


        new MyTask().execute();

        new MyTaskIDProfileImage(this, email).execute();
        new MyTaskIDCoverImage(this, email).execute();


        if (sesso != null && sesso.equals("M")) {
            imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_male));
        } else if (sesso != null && sesso.equals("F")) {
            imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_female));
        }

        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();
        myProfile = new Profilo(email, name, surname, date, password, nazionalità, sesso, username, lavoro, descrizione);
        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setProfiloCorrente(myProfile);
    }


    @Override
    protected void onResume() {
        super.onResume();

        TakeATrip TAT = ((TakeATrip) getApplicationContext());
        googleApiClient = TAT.getGoogleApiClient();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        AppEventsLogger.activateApp(this);
    }

    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void ClickImageProfile(View v) {
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
        openProfilo.putExtra("urlImmagineProfilo", urlImmagineProfilo);
        openProfilo.putExtra("urlImmagineCopertina", urlImmagineCopertina);

        // passo all'attivazione dell'activity
        startActivity(openProfilo);
    }


    public void onClickSearchTravels(View v) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }


    public void ClickTravels(View v) {
        Intent openListaViaggi = new Intent(MainActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);
        // passo all'attivazione dell'activity
        startActivity(openListaViaggi);
    }


    LinearLayout rowHorizontal;

    public void ClickNewTravel(final View v) {
        nomeViaggio = "";
        namesPartecipants.clear();
        partecipants.clear();


        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(wrapper);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.dialog_insert_viaggio, null);
        builder.setView(dialog);
        builder.setTitle(getString(R.string.NewTravel));


        final AutoCompleteTextView text = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
        text.setHint("Add partecipant");
        text.setAdapter(adapter);
        text.setThreshold(1);


        //layoutNewPartecipants = (TableLayout)dialog.findViewById(R.id.layoutPartecipants);
        layoutNewPartecipants = (LinearLayout) dialog.findViewById(R.id.layoutPartecipants);
        rowHorizontal = (LinearLayout) dialog.findViewById(R.id.layout_horizontal);


        TextView travel = (TextView) dialog.findViewById(R.id.titoloViaggio);
        editTextNameTravel = (EditText) dialog.findViewById(R.id.editTextNameTravel);

        //  builder.setCancelable(false);//potrebbe essere una soluzione per evitare il backPressed

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allertDialog(v);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allertDialog(v);
                    }
                });

        builder.setPositiveButton(getString(R.string.Create),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editTextNameTravel.getText().toString().equals("")) {
                            Toast.makeText(getBaseContext(), "Name Travel omitted", Toast.LENGTH_LONG).show();
                            ClickNewTravel(v);
                        } else {

                            if (!partecipants.contains(myProfile)) {
                                partecipants.add(myProfile);
                            }
                            nomeViaggio = editTextNameTravel.getText().toString();
                            for (String s : namesPartecipants) {
                                for (Profilo p : profiles) {
                                    if (p.getUsername().equals(s)) {
                                        if (!partecipants.contains(p)) {
                                            partecipants.add(p);
                                        }
                                    }
                                }
                            }
                            Log.i(TAG, "lista partecipanti:" + partecipants);
                            Log.i(TAG, "nome Viaggio:" + nomeViaggio);

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
                    Log.i(TAG, "partecipante selezionato: " + s);


                    String usernameUtenteSelezionato = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
                    Log.i(TAG, "username selezionato: " + usernameUtenteSelezionato);
                    for (Profilo p : profiles) {

                        if (p.getUsername().equals(usernameUtenteSelezionato)) {
                            if (!partecipants.contains(p)) {

                                if (partecipants.size() % LIMIT_IMAGES_VIEWS == 0) {
                                    rowHorizontal = new LinearLayout(MainActivity.this);
                                    rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                                    layoutNewPartecipants.addView(rowHorizontal);
                                    layoutNewPartecipants.addView(new TextView(MainActivity.this), Constants.BASE_DIMENSION_OF_SPACE, Constants.BASE_DIMENSION_OF_SPACE);
                                }

                                final ImageView image = new RoundedImageView(MainActivity.this, null);
                                image.setContentDescription(p.getEmail());

                                if (p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")) {
                                    String signedUrl = beginDownloadProfilePicture(p.getIdImageProfile());
                                    Picasso.with(MainActivity.this).
                                            load(signedUrl).
                                            resize(SIZE_IMAGE_PARTECIPANT, SIZE_IMAGE_PARTECIPANT).
                                            into(image);

                                } else {
                                    if (p.getSesso().equals("M")) {
                                        image.setImageResource(R.drawable.default_male);
                                    } else {
                                        image.setImageResource(R.drawable.default_female);
                                    }
                                }

                                rowHorizontal.addView(image, SIZE_IMAGE_PARTECIPANT, SIZE_IMAGE_PARTECIPANT);
                                rowHorizontal.addView(new TextView(MainActivity.this), Constants.BASE_DIMENSION_OF_SPACE, SIZE_IMAGE_PARTECIPANT);
                                Log.i(TAG, "aggiungo la view nel layout orizzonale");
                                partecipants.add(p);
                                namesPartecipants.add(p.getUsername());

                            } else {
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


    public void onClickSocialButton(View v) {
        Intent openSocial = new Intent(MainActivity.this, SocialActivity.class);
        openSocial.putExtra("email", myProfile.getEmail());
        Log.i("TEST: ", "EMAIL PER SOCIAL " + myProfile.getEmail());
        startActivity(openSocial);
    }

    public void onClickTutorial(View v) {
        Intent openTutorial = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(openTutorial);
    }

    public void onClickSettings(View v) {

        try {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setItems(R.array.CommandsSettingsMain, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //logout profile
                            if (email.contains("@")) {
                                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                                // Inserting Users
                                Log.d(TAG, "Drop the user...");
                                db.deleteContact(myProfile);
                            } else {

                                if (profile != null && LoginManager.getInstance() != null) {
                                    Log.d(TAG, "Log out from facebook: ..");

                                    LoginManager.getInstance().logOut();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Log.d(TAG, "Log out from google con apiClient: " + googleApiClient);

                                    if (!googleApiClient.isConnected()) {
                                        googleApiClient.connect();
                                        Toast.makeText(getBaseContext(), getString(R.string.LogOutFailed), Toast.LENGTH_LONG).show();

                                    } else {
                                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                                                new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(Status status) {
                                                        Log.d(TAG, "Status: " + status);
                                                        if (status.isSuccess()) {
                                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
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
                                    String nomeUtente = json_data.getString("nome");
                                    String cognomeUtente = json_data.getString("cognome");
                                    String emailUtente = json_data.getString("email");
                                    String username = json_data.getString("username");
                                    String sesso = json_data.getString("sesso");
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                    if (urlImmagineProfilo.equals("null")) {
                                        idProfiles = null;
                                    } else {
                                        idProfiles = urlImmagineProfilo;
                                    }

                                    if (urlImmagineCopertina.equals("null")) {
                                        idCovers = null;
                                    } else {
                                        idCovers = urlImmagineCopertina;
                                    }

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, sesso, username, null, null, null, idProfiles, idCovers);
                                    profiles.add(p);
                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "(" + username + ")";
                                    names.add(stringaFinale);
                                }
                            }


                        } catch (Exception e) {
                            Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                } else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


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
                    new GeneratePresignedUrlRequest(Constants.BUCKET_NAME, key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);

            Log.i(TAG, "expiration date image: " + generatePresignedUrlRequest.getExpiration());
            Log.i(TAG, "amazon client: " + s3);


            url = s3.generatePresignedUrl(generatePresignedUrlRequest);

            Log.i(TAG, "url file: " + url);


            // Initiate the download
            //TransferObserver observer = transferUtility.download(Constants.BUCKET_NAME, key, file);
            //Log.i(TAG, "downloaded file: " + file);


            //observer.setTransferListener(new DownloadListener());
        } catch (Exception exception) {
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

            Log.i(TAG, "nomeViaggio: " + nomeViaggio);

            try {
                if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_VIAGGIO);
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
                            //Log.i(TAG, "result" +result);

                            UUIDViaggio = result;

                            Log.i(TAG, "UUID viaggio " + UUIDViaggio);

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                } else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (result.contains("Duplicate")) {
                Log.e(TAG, "devo generare un nuovo UUID");
                new TaskForUUID().execute();
            } else {
                Log.i(TAG, "UUID corretto, ora aggiungo gli itinerari");
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

            Log.i(TAG, "lista partecipanti:" + partecipants);

            for (Profilo p : partecipants) {

                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("codice", UUIDViaggio));
                dataToSend.add(new BasicNameValuePair("email", p.getEmail()));


                try {
                    if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                        Log.i(TAG, "CONNESSIONE Internet Presente!");
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_ITINERARIO);
                        httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                        HttpResponse response = httpclient.execute(httppost);

                        HttpEntity entity = response.getEntity();

                        is = entity.getContent();

                    } else
                        Log.e(TAG, "CONNESSIONE Internet Assente!");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(e.toString(), e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String stringaFiltro = nomeViaggio.replace(" ", "_");
            filtro = stringaFiltro.toLowerCase();

            Log.i(TAG, "filtro: " + filtro);

            new TaskForFilter().execute();

            super.onPostExecute(aVoid);

        }
    }


    private class TaskForFilter extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {

            for (Profilo p : partecipants) {
                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("codiceViaggio", UUIDViaggio));
                dataToSend.add(new BasicNameValuePair("filtro", filtro));


                try {
                    if (InternetConnection.haveInternetConnection(MainActivity.this)) {
                        Log.i(TAG, "CONNESSIONE Internet Presente!");
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_FILTRO);
                        httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                        HttpResponse response = httpclient.execute(httppost);

                    } else
                        Log.e(TAG, "CONNESSIONE Internet Assente!");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(e.toString(), e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), R.string.created_travel, Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);

        }
    }


    private class MyTaskIDProfileImage extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_PROFILE_IMAGE = "QueryImmagineProfilo.php";

        InputStream is = null;
        String emailUser, idTravel, result;
        String nomeCartella;
        DriveId idFolder;
        Context context;
        String signedUrl;

        public MyTaskIDProfileImage(Context c, String emailUtente) {
            context = c;
            emailUser = emailUtente;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));

            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
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

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);

                                    //idImageProfile = DriveId.decodeFromString(json_data.getString("idImmagineProfilo"));
                                    urlImmagineProfilo = json_data.getString("urlImmagineProfilo");

                                }
                            }


                            if (!result.equals("NULL") && !urlImmagineProfilo.equals("null")) {
                                signedUrl = beginDownloadProfilePicture(urlImmagineProfilo);
                            }


                        } catch (Exception e) {
                            result = "NULL";
                            //Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                        }
                    } else {
                        Log.i(TAG, "Input Stream uguale a null");
                    }
                } else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "risultato dal prelievo dell'id imm profilo: " + result);
            if (signedUrl != null) {
                Picasso.with(MainActivity.this).
                        load(signedUrl.toString()).
                        resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT * 2, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT * 2).
                        into(imageViewProfileRound);
            } else {
                //L'utente è loggato con facebook
                if (profile != null) {
                    Log.i(TAG, profile.getProfilePictureUri(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT + 50, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT + 50).toString());
                    final Uri image_uri = profile.getProfilePictureUri(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT + 50, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT + 50);

                    try {
                        final URI image_URI = new URI(image_uri.toString());

                        Log.i(TAG, "url_image: " + image_URI.toURL().toString());


                        Picasso.with(MainActivity.this).load(image_URI.toURL().toString()).into(imageViewProfileRound);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private class MyTaskIDCoverImage extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_COVER_IMAGE = "QueryImmagineCopertina.php";

        InputStream is = null;
        String emailUser, idTravel, result;
        String nomeCartella;
        DriveId idFolder;
        Context context;

        public MyTaskIDCoverImage(Context c, String emailUtente) {
            context = c;
            emailUser = emailUtente;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));
            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
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

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    //idImmagineCopertina = DriveId.decodeFromString(json_data.getString("idImmagine"));
                                    urlImmagineCopertina = json_data.getString("urlImmagineCopertina");
                                }
                            }
                        } catch (Exception e) {
                            result = "ERRORE";
                            Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                        }
                    } else {
                        Log.i(TAG, "Input Stream uguale a null");
                    }
                } else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "risultato dal prelievo dell'id imm copertina: " + urlImmagineCopertina);

            super.onPostExecute(aVoid);
        }
    }

    //Dialog per backPressed in home
    private void prepareSignOut() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.allert_message))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doubleBackToExitPressedOnce = true;
                        onBackPressed();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.logodefbordo))
                .show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        prepareSignOut();
        deleteIdOnShared(MainActivity.this);

    }

    public static void deleteIdOnShared(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("com.example.david.takeatrip", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }


    //Dialog per cancel o backPressed su creazione viaggio
    private void allertDialog(final View v) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.back))
                .setMessage(getString(R.string.allert_message))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        namesPartecipants.clear();
                        partecipants.clear();
                        layoutNewPartecipants.removeAllViews();
                        editTextNameTravel.setText("");

                        Log.i(TAG, "lista nomi partecipanti:" + namesPartecipants);
                        Log.i(TAG, "lista partecipanti:" + partecipants);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClickNewTravel(v);  //senza questo ritorna alla home
                        //TODO cercare di far mantenere le info già inserite

                    }
                })
                .setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.logodefbordo))
                .show();

    }
}

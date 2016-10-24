package com.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveId;
import com.squareup.picasso.Picasso;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatabaseHandler;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.UtilS3Amazon;

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

    //per salvare i dati
    private static final String EMAIL = "email";

    private String name, surname, email, nazionalità, sesso, username, lavoro, descrizione, tipo;
    private String date, password, urlImmagineProfilo, urlImmagineCopertina;
    private String emailEsterno;
    private ImageView imageViewProfileRound;
    private List<String> names, namesPartecipants;
    private Set<Profilo> profiles, partecipants;
    private Profilo profilo;
    private Profile fbProfile;
    private ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient;

    //per alert
    private boolean doubleBackToExitPressedOnce = false;

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

        // Declare a new thread to do a preference check before starting APP INTRO
        Thread appIntroThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                // Forces flag equals to true, only for debugging
                // TODO comment this line for release
                //isFirstStart = true;

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the app intro thread
        appIntroThread.start();

        setContentView(R.layout.activity_main);

/*      usato solo con onSavedInstanceState
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            email = savedInstanceState.getString(email);
        }
*/
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
            fbProfile = intent.getParcelableExtra("fbProfile");
        } else {
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView) findViewById(R.id.imageView_round);

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(MainActivity.this);


        new MyTask().execute();
        showProgressDialog();
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
        profilo = new Profilo(email, name, surname, date, password, nazionalità, sesso, username, lavoro, descrizione);



        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setProfiloCorrente(profilo);
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

    /* usato solo quando viene distrutta l'activity
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "SALVO BUNDLE: " + savedInstanceState);
        // Save the user's current game state
        savedInstanceState.putString(EMAIL, email);
        Log.i(TAG, "SALVO EMAIL: " + email);

    }
    */
/*
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        email = savedInstanceState.getString(email);
        Log.i(TAG, "EMAIL SALVATA: " + email);
    }
    */

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
        openProfilo.putExtra("profile", fbProfile);
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
        Intent openNewTravel = new Intent(MainActivity.this, NuovoViaggioActivity.class);
        openNewTravel.putExtra("name", name);
        openNewTravel.putExtra("surname", surname);
        openNewTravel.putExtra("email", email);
        openNewTravel.putExtra("emailEsterno", emailEsterno);
        openNewTravel.putExtra("dateOfBirth", date);
        openNewTravel.putExtra("pwd", password);
        openNewTravel.putExtra("nazionalita", nazionalità);
        openNewTravel.putExtra("sesso", sesso);
        openNewTravel.putExtra("username", username);
        openNewTravel.putExtra("lavoro", lavoro);
        openNewTravel.putExtra("descrizione", descrizione);
        openNewTravel.putExtra("tipo", tipo);
        openNewTravel.putExtra("urlImmagineProfilo", urlImmagineProfilo);
        openNewTravel.putExtra("urlImmagineCopertina", urlImmagineCopertina);
        // passo all'attivazione dell'activity
        startActivity(openNewTravel);

    }


    public void onClickSocialButton(View v) {
        Intent openSocial = new Intent(MainActivity.this, SocialActivity.class);
        openSocial.putExtra("email", profilo.getEmail());
        Log.i("TEST: ", "EMAIL PER SOCIAL " + profilo.getEmail());
        startActivity(openSocial);
    }

    public void onClickTutorial(View v) {
        Intent openTutorial = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(openTutorial);
    }

    public void onClickLogout(View v) {

        try {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.logout_alert))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (email.contains("@")) {
                                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                                // Inserting Users
                                Log.d(TAG, "Drop the user...");
                                db.deleteContact(profilo);
                            } else {

                                if (fbProfile != null && LoginManager.getInstance() != null) {
                                    Log.d(TAG, "Log out from facebook: ..");

                                    LoginManager.getInstance().logOut();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Log.d(TAG, "Log out from google con apiClient: " + googleApiClient);

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
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (googleApiClient.isConnected()) {
                                googleApiClient.disconnect();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.logodefbordo))
                    .show();

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
                        resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT * 2, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT * 2).
                        into(imageViewProfileRound);
            } else {
                //L'utente è loggato con facebook
                if (fbProfile != null) {
                    Log.i(TAG, fbProfile.getProfilePictureUri(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50).toString());
                    final Uri image_uri = fbProfile.getProfilePictureUri(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50);

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
            hideProgressDialog();
        }
    }

    //Dialog per backPressed in home
    private void prepareSignOut() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.alert_message))
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

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }


}

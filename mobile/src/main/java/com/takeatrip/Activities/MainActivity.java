package com.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.takeatrip.AsyncTasks.GetStopsTask;
import com.takeatrip.AsyncTasks.GetViaggiTask;
import com.takeatrip.AsyncTasks.InserimentoImmagineProfiloTask;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.Classes.Tappa;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.Interfaces.AsyncResponseStops;
import com.takeatrip.Interfaces.AsyncResponseTravels;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatabaseHandler;
import com.takeatrip.Utilities.DatesUtils;
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponseTravels, AsyncResponseStops {

    private static final String TAG = "TEST MainActivity";
    private final String ADDRESS = "QueryNomiUtenti.php";

    //per salvare i dati
    private static final String EMAIL = "email";

    private String name, surname, email, nazionalità, sesso, username, lavoro, descrizione, tipo;
    private String date, password, urlImmagineProfilo, urlImmagineCopertina;
    private String emailEsterno;
    private ImageView imageViewProfileRound;
    private Profilo profilo;
    private Profile fbProfile;
    private Viaggio ultimoViaggio;

    private ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient;

    private FloatingActionsMenu fabMenu;
    private FloatingActionsMenu fabMenu2;
    private FloatingActionButton buttonAddTravel;
    private FloatingActionButton buttonAddToStop;
    private FloatingActionButton buttonAddStop;
    private FloatingActionButton buttonSearchUser;
    private FloatingActionButton buttonSearchTravels;
    private FloatingActionButton buttonSocial;

    private boolean newStop = false;



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
    private String emailProfilo;




    private int xMenu1, yMenu1, xMenu2, yMenu2, xSocialButton, ySocialButton;






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
        if (getIntent() != null) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            emailProfilo = intent.getStringExtra("emailProfilo");
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

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayoutMain);

        //action buttons
        fabMenu = (FloatingActionsMenu) findViewById(R.id.menu);
        fabMenu2 = (FloatingActionsMenu) findViewById(R.id.menu2);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                fabMenu2.collapse();
            }

            @Override
            public void onMenuCollapsed() {
            }
        });

        fabMenu2.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                fabMenu.collapse();
            }

            @Override
            public void onMenuCollapsed() {
            }
        });


        buttonAddTravel = (FloatingActionButton) findViewById(R.id.addTravel);
        buttonAddToStop = (FloatingActionButton) findViewById(R.id.addToLastStop);
        buttonAddStop = (FloatingActionButton) findViewById(R.id.addStop);
        buttonSearchUser = (FloatingActionButton) findViewById(R.id.searchUser);
        buttonSearchTravels = (FloatingActionButton) findViewById(R.id.buttonSearchTravels);
        buttonSocial = (FloatingActionButton) findViewById(R.id.buttonSocial);



        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                frameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                int[] locations2 = new int[2];
                int[] locations3 = new int[2];
                fabMenu.getLocationInWindow(locations);
                fabMenu2.getLocationInWindow(locations2);
                buttonSocial.getLocationOnScreen(locations3);

                xMenu1 = locations[0];
                yMenu1 = locations[1];
                xMenu2 = locations2[0];
                yMenu2 = locations2[1];
                xSocialButton = locations3[0];
                ySocialButton = locations3[1];

                Log.i(TAG, "location on screen of fabMenu: " + xMenu1 +" "+ yMenu1);
                Log.i(TAG, "location on screen of fabMenu2: " + xMenu2 +" "+ yMenu2);
                Log.i(TAG, "location on screen of socialButton: " + xSocialButton +" "+ ySocialButton);
            }
        });


        inizializzaButton();

        imageViewProfileRound = (ImageView) findViewById(R.id.imageView_round);

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();


        //Task for retrieving the profile and cover image of the user
        String urlImageProfile = null;
        String urlCoverImage = null;
        try {
            urlImageProfile = new MyTaskIDProfileImage(this, email).execute().get();
            urlCoverImage = new MyTaskIDCoverImage(this, email).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        if (sesso != null && sesso.equals("M")) {
            imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_male));
        } else if (sesso != null && sesso.equals("F")) {
            imageViewProfileRound.setImageDrawable(getResources().getDrawable(R.drawable.default_female));
        }


        profilo = new Profilo(email, emailProfilo, name, surname, date, nazionalità, sesso, username, lavoro, descrizione,tipo,urlImageProfile,urlCoverImage);
        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setProfiloCorrente(profilo);


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






    private void inizializzaButton(){
        buttonAddTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickNewTravel(v);
                fabMenu.collapse();
            }
        });

        buttonAddToStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLastStop(v);
                fabMenu.collapse();
            }
        });

        buttonAddStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickNewStop(v);
                fabMenu.collapse();
            }
        });

        buttonSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onClickSocialButton(v);
            }
        });

        buttonSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ClickSearchUser(v);
                fabMenu2.collapse();
            }
        });

        buttonSearchTravels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onClickSearchTravels(v);
                fabMenu2.collapse();
            }
        });


    }


    private void ClickNewStop(View v) {
        newStop = true;

        GetViaggiTask GVT = new GetViaggiTask(MainActivity.this, email);
        GVT.delegate = this;
        GVT.execute();
    }

    private void ClickLastStop(View v) {
        GetViaggiTask GVT = new GetViaggiTask(MainActivity.this, email);
        GVT.delegate = this;
        GVT.execute();
    }


    @Override
    public void processFinishForTravels(List<Viaggio> travels) {

        if(travels.size() > 0){
            ultimoViaggio = travels.get(0);

            List<Profilo> partecipant = new ArrayList<Profilo>();
            partecipant.add(profilo);

            if(newStop){

                /*
                CharSequence[] namesPartecipants = { profilo.getName() };
                CharSequence[] listPartecipants = { profilo.getId() };
                CharSequence[] urlImagePartecipants = { profilo.getIdImageProfile() };
                CharSequence[] sessoPartecipants = { profilo.getSesso() };

                Intent intent = new Intent(MainActivity.this, ListaTappeActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("codiceViaggio", ultimoViaggio.getCodice());
                intent.putExtra("nomeViaggio", ultimoViaggio.getNome());
                intent.putExtra("urlImmagineViaggio", ultimoViaggio.getUrlImmagine());
                intent.putExtra("livelloCondivisione", ultimoViaggio.getCondivisioneDefault());
                intent.putExtra("urlImmagineViaggio", ultimoViaggio.getUrlImmagine());
                intent.putExtra("namesPartecipants", namesPartecipants);
                intent.putExtra("partecipanti", listPartecipants);
                intent.putExtra("urlImagePartecipants", urlImagePartecipants);
                intent.putExtra("sessoPartecipants", sessoPartecipants);
                startActivity(intent);
                */


                Intent openNewTravel = new Intent(MainActivity.this, ViaggioActivityConFragment.class);

                openNewTravel.putExtra("email", email);
                openNewTravel.putExtra("codiceViaggio", ultimoViaggio.getCodice());
                openNewTravel.putExtra("nomeViaggio", ultimoViaggio.getNome());
                openNewTravel.putExtra("urlImmagineViaggio", ultimoViaggio.getUrlImmagine());
                openNewTravel.putExtra("livelloCondivisione", ultimoViaggio.getCondivisioneDefault());
                startActivity(openNewTravel);



                newStop = false;
            }
            else{
                GetStopsTask GST = new GetStopsTask(MainActivity.this, partecipant, ultimoViaggio.getCodice());
                GST.delegate = this;
                GST.execute();
            }

        }
        else{
            Toast.makeText(this, this.getResources().getString(R.string.no_travels), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void processFinishForStops(Map<Profilo, List<Tappa>> profilo_tappe) {

        List<Tappa> tappe = profilo_tappe.get(profilo);
        if(tappe.size() == 0 ){
            new AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.adviseNoStops))
                    .setMessage(this.getString(R.string.adviseNewStop))
                    .setPositiveButton(this.getString(R.string.si), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent openNewTravel = new Intent(MainActivity.this, ViaggioActivityConFragment.class);

                            openNewTravel.putExtra("email", email);
                            openNewTravel.putExtra("codiceViaggio", ultimoViaggio.getCodice());
                            openNewTravel.putExtra("nomeViaggio", ultimoViaggio.getNome());
                            openNewTravel.putExtra("urlImmagineViaggio", ultimoViaggio.getUrlImmagine());
                            openNewTravel.putExtra("livelloCondivisione", ultimoViaggio.getCondivisioneDefault());

                            startActivity(openNewTravel);
                        }
                    })

                    .setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //onBackPressed();
                            return;
                        }
                    })
                    .setIcon(ContextCompat.getDrawable(this,R.drawable.logodefbordo))
                    .show();


        }else{
            final ArrayList<Tappa> tappe2 = (ArrayList<Tappa>) profilo_tappe.get(profilo);;

            Tappa ultimaTappa = tappe.get(tappe.size()-1);

            Intent intent = new Intent(MainActivity.this, TappaActivity.class);
            intent.putExtra("email",profilo.getId());
            intent.putExtra("emailProprietarioTappa", profilo.getId());
            intent.putExtra("codiceViaggio", ultimoViaggio.getCodice());
            intent.putExtra("ordine", tappe.size());
            intent.putExtra("ordineDB", ultimaTappa.getOrdine());
            intent.putExtra("nome", tappe.size() + ". "+ ultimaTappa.getName());
            intent.putExtra("data", DatesUtils.getStringFromDate(ultimaTappa.getData(), Constants.DISPLAYED_DATE_FORMAT));
            intent.putExtra("livelloCondivisioneTappa", ultimaTappa.getLivelloCondivisione());
            intent.putExtra("nomeViaggio", ultimoViaggio.getNome());
            intent.putParcelableArrayListExtra("tappeViaggio", tappe2);


            startActivity(intent);
        }


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
        openProfilo.putExtra("profile", fbProfile);
        openProfilo.putExtra("urlImmagineProfilo", urlImmagineProfilo);
        openProfilo.putExtra("urlImmagineCopertina", urlImmagineCopertina);

        // passo all'attivazione dell'activity
        startActivity(openProfilo);
    }


    private void ClickSearchUser(View v) {
        Intent intent = new Intent(MainActivity.this, SocialActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("fromMain", "yes");
        startActivity(intent);
    }


    public void onClickSearchTravels(View v) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    public void ClickTravels(View v) {
        Intent openListaViaggi = new Intent(MainActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);
        startActivity(openListaViaggi);
    }


    public void ClickNewTravel(final View v) {
        Intent openNewTravel = new Intent(MainActivity.this, NuovoViaggioActivity.class);
        openNewTravel.putExtra("email", email);
        openNewTravel.putExtra("emailEsterno", emailEsterno);
        startActivity(openNewTravel);
    }




    public void onClickSocialButton(View v) {
        Intent openSocial = new Intent(MainActivity.this, SocialActivity.class);
        openSocial.putExtra("email", profilo.getId());
        Log.i("TEST: ", "EMAIL PER SOCIAL " + profilo.getId());
        startActivity(openSocial);
    }

    public void onClickTutorial(View v) {
        Intent openTutorial = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(openTutorial);
    }

    public void onClickLogout(View v) {
        if(InternetConnection.haveInternetConnection(getApplicationContext())){
            try {
                if (googleApiClient != null && !googleApiClient.isConnected()) {
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

                                    if (LoginManager.getInstance() != null) {
                                        Log.d(TAG, "Log out from facebook: ..");

                                        LoginManager.getInstance().logOut();
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Log.d(TAG, "Log out from google con apiClient: " + googleApiClient);

                                        if(googleApiClient != null){
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
        else{
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_LONG).show();
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

            url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return url.toString();

    }

    @Override
    public void onClick(View v) {

    }




    private class MyTaskIDProfileImage extends AsyncTask<Void, Void, String> {

        private final String ADDRESS_QUERY_PROFILE_IMAGE = "QueryImmagineProfilo.php";

        InputStream is = null;
        String emailUser, result;
        Context context;
        String signedUrl;

        public MyTaskIDProfileImage(Context c, String emailUtente) {
            context = c;
            emailUser = emailUtente;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));

            try {
                if (InternetConnection.haveInternetConnection(context)) {
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
                } else{
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
                    Toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return signedUrl;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if (signedUrl != null) {
                Picasso.with(MainActivity.this)
                        .load(signedUrl.toString())
                        .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT * 2, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT * 2)
                        .into(target);


            }
            //L'utente è loggato con facebook
            else if (fbProfile != null) {
                    Log.i(TAG, fbProfile.getProfilePictureUri(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50).toString());
                    final Uri image_uri = fbProfile.getProfilePictureUri(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT + 50);

                    try {
                        final URI image_URI = new URI(image_uri.toString());

                        //Picasso.with(MainActivity.this).load(image_URI.toURL().toString()).into(imageViewProfileRound);
                        Picasso.with(MainActivity.this).load(image_URI.toURL().toString()).into(target);



                        Bitmap bitmap = new BitmapWorkerTask(null).execute(image_URI.toURL().toString()).get();
                        File filesDir = getApplicationContext().getFilesDir();

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        File imageFile = new File(filesDir,"profileImageTATfromFB" + timeStamp+ Constants.IMAGE_EXT);
                        OutputStream os;
                        try {
                            os = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                            os.flush();
                            os.close();
                        } catch (Exception e) {
                            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                        }

                        beginUploadProfilePicture(imageFile.getAbsolutePath());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
            else{
                hideProgressDialog();
            }

        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // loading of the bitmap was a success
            imageViewProfileRound.setImageBitmap(bitmap);
            hideProgressDialog();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private class MyTaskIDCoverImage extends AsyncTask<Void, Void, String> {

        private final String ADDRESS_QUERY_COVER_IMAGE = "QueryImmagineCopertina.php";

        InputStream is = null;
        String emailUser, result;
        Context context;

        public MyTaskIDCoverImage(Context c, String emailUtente) {
            context = c;
            emailUser = emailUtente;
        }

        @Override
        protected String doInBackground(Void... params) {
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
                } else{
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
                    Toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
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




    private void beginUploadProfilePicture(String filePath) {
        if(InternetConnection.haveInternetConnection(getApplicationContext())) {
            if (filePath == null) {
                Log.i(TAG, "Could not find the filepath of the selected file");
                return;
            }
            File file = new File(filePath);

            ObjectMetadata myObjectMetadata = new ObjectMetadata();
            TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, email + "/" + Constants.PROFILE_PICTURES_LOCATION + "/" + file.getName(), file);
            new InserimentoImmagineProfiloTask(this, email, null, email + "/" + Constants.PROFILE_PICTURES_LOCATION + "/" + file.getName()).execute();
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }

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
            progressDialog.setCancelable(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }


}

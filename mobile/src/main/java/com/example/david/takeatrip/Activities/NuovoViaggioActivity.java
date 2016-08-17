package com.example.david.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
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
import com.example.david.takeatrip.Utilities.InternetConnection;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;
import com.facebook.Profile;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveId;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NuovoViaggioActivity extends AppCompatActivity {
    private static final String TAG = "TEST MainActivity";


    private final String ADDRESS = "QueryNomiUtenti.php";
    private final String ADDRESS_INSERIMENTO_VIAGGIO = "InserimentoViaggio.php";


    private static final int SIZE_IMAGE_PARTECIPANT = Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT;


    private final int LIMIT_IMAGES_VIEWS = 30;


    private LinearLayout layoutNewPartecipants;

    String nomeViaggio, UUIDViaggio, filtro;
    AutoCompleteTextView text;
    List<String> names, namesPartecipants;
    Set<Profilo> profiles, partecipants;
    Profilo myProfile;
    LinearLayout rowHorizontal;

    EditText editTextNameTravel;

    private String name, surname, email, emailEsterno, emailProfiloVisitato, emailFollowing;
    private String date, password, nazionalita, sesso, username, lavoro, descrizione, tipo;

    //per allert
    private boolean doubleBackToExitPressedOnce = false;

    // The S3 client
    private AmazonS3Client s3;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_viaggio);


        if (getIntent() != null) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");
            nazionalita = intent.getStringExtra("nazionalita");
            sesso = intent.getStringExtra("sesso");
            username = intent.getStringExtra("username");
            lavoro = intent.getStringExtra("lavoro");
            descrizione = intent.getStringExtra("descrizione");
            tipo = intent.getStringExtra("tipo");
        }

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(NuovoViaggioActivity.this);


        new MyTask().execute();

        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();
        myProfile = new Profilo(email, name, surname, date, password, nazionalita, sesso, username, lavoro, descrizione);
        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setProfiloCorrente(myProfile);
        NuovoViaggio();

    }

    public void NuovoViaggio(){
    nomeViaggio="";
    namesPartecipants.clear();
    partecipants.clear();

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_dropdown_item_1line, names);
    text = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
    text.setAdapter(adapter);
    text.setThreshold(1);


    //layoutNewPartecipants = (TableLayout)dialog.findViewById(R.id.layoutPartecipants);
    layoutNewPartecipants=(LinearLayout)

    findViewById(R.id.layoutPartecipants);

    rowHorizontal=(LinearLayout)

    findViewById(R.id.layout_horizontal);


    TextView travel = (TextView) findViewById(R.id.titoloViaggio);
    editTextNameTravel=(EditText)

    findViewById(R.id.editTextNameTravel);


/*
        builder.setPositiveButton(getString(R.string.Create),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editTextNameTravel.getText().toString().equals("")) {
                            Toast.makeText(getBaseContext(), "Name Travel omitted", Toast.LENGTH_LONG).show();
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

                            showProgressDialog();
                            new TaskForUUID().execute();

                        }
                    }
                });

*/


    final FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.floatingButtonAdd);
        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                if (!NuovoViaggioActivity.this.text.getText().toString().equals("")) {
                    TextView tv = new TextView(NuovoViaggioActivity.this);
                    tv.setText(NuovoViaggioActivity.this.text.getText().toString());

                    String s = NuovoViaggioActivity.this.text.getText().toString();
                    Log.i(TAG, "partecipante selezionato: " + s);


                    String usernameUtenteSelezionato = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
                    Log.i(TAG, "username selezionato: " + usernameUtenteSelezionato);
                    for (Profilo p : profiles) {

                        if (p.getUsername().equals(usernameUtenteSelezionato)) {
                            if (!partecipants.contains(p)) {

                                if (partecipants.size() % LIMIT_IMAGES_VIEWS == 0) {
                                    rowHorizontal = new LinearLayout(NuovoViaggioActivity.this);
                                    rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                                    layoutNewPartecipants.addView(rowHorizontal);
                                    layoutNewPartecipants.addView(new TextView(NuovoViaggioActivity.this), Constants.BASE_DIMENSION_OF_SPACE, Constants.BASE_DIMENSION_OF_SPACE);
                                }

                                final ImageView image = new RoundedImageView(NuovoViaggioActivity.this, null);
                                image.setContentDescription(p.getEmail());

                                if (p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")) {
                                    String signedUrl = beginDownloadProfilePicture(p.getIdImageProfile());
                                    Picasso.with(NuovoViaggioActivity.this).
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
                                rowHorizontal.addView(new TextView(NuovoViaggioActivity.this), Constants.BASE_DIMENSION_OF_SPACE, SIZE_IMAGE_PARTECIPANT);
                                Log.i(TAG, "aggiungo la view nel layout orizzonale");
                                partecipants.add(p);
                                namesPartecipants.add(p.getUsername());

                            } else {
                                Toast.makeText(getBaseContext(), "User already present in travel", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    NuovoViaggioActivity.this.text.setText("");

                }

            }
            }

            );
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


    private class MyTask extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";
        String idProfiles, idCovers;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(NuovoViaggioActivity.this)) {
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
                                    Log.i(TAG, "nome "+nomeUtente);
                                    String cognomeUtente = json_data.getString("cognome");
                                    Log.i(TAG, "cognome" + cognomeUtente);

                                    String emailUtente = json_data.getString("email");
                                    Log.i(TAG, "email" + emailUtente);

                                    String username = json_data.getString("username");
                                    Log.i(TAG, "username " + username);

                                    String sesso = json_data.getString("sesso");
                                    Log.i(TAG, "sesso" + sesso);

                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    Log.i(TAG, urlImmagineProfilo);

                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");
                                    Log.i(TAG, urlImmagineCopertina);


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

}

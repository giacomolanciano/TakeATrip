package com.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.squareup.picasso.Picasso;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.RoundedImageView;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NuovoViaggioActivity extends AppCompatActivity {

    private static final String TAG = "TEST NuovoViaggioActiv";
    private static final int SIZE_IMAGE_PARTECIPANT = Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT;
    private static final String ADDRESS = "QueryNomiUtenti.php";
    private static final String ADDRESS_INSERIMENTO_VIAGGIO = "InserimentoViaggio.php";
    private static final String ADDRESS_INSERIMENTO_ITINERARIO = "InserimentoItinerario.php";
    private static final String ADDRESS_INSERIMENTO_FILTRO = "InserimentoFiltro.php";
    private static final int LIMIT_IMAGES_VIEWS = 4;

    private LinearLayout layoutNewPartecipants;
    private String nomeViaggio, UUIDViaggio, filtro;
    private AutoCompleteTextView autoCompleteTextView;
    private List<String> names, namesPartecipants;
    private Set<Profilo> profiles, partecipants;
    private Profilo profilo;
    private LinearLayout rowHorizontal, layoutFAB;
    private ProgressDialog progressDialog;
    private EditText editTextNameTravel;
    private String name, surname, email, tipo;
    private String date, password, nazionalita, sesso, username, lavoro, descrizione;

    //per alert
    private boolean doubleBackToExitPressedOnce = false;

    // The S3 client
    private AmazonS3Client s3;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_viaggio);

        if (getIntent() != null) {
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
        }

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(NuovoViaggioActivity.this);


        new GetNomiUtentiTask().execute();

        names = new ArrayList<String>();
        namesPartecipants = new ArrayList<String>();
        partecipants = new HashSet<Profilo>();
        profiles = new HashSet<Profilo>();

        profilo = new Profilo(email);
        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setProfiloCorrente(profilo);

        nomeViaggio="";
        namesPartecipants.clear();
        partecipants.clear();

        layoutFAB = (LinearLayout) findViewById(R.id.layoutFloatingButtonAdd);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, names);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    layoutFAB.setVisibility(View.GONE);
                else if (layoutFAB.getVisibility() != View.VISIBLE) {
                    layoutFAB.setVisibility(View.VISIBLE);
                    autoCompleteTextView.showDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //no op
            }
        });


        //layoutNewPartecipants = (TableLayout)dialog.findViewById(R.id.layoutPartecipants);
        layoutNewPartecipants = (LinearLayout) findViewById(R.id.layoutPartecipants);
        rowHorizontal = (LinearLayout) findViewById(R.id.layout_horizontal);
        TextView travel = (TextView) findViewById(R.id.titoloViaggio);
        editTextNameTravel = (EditText) findViewById(R.id.editTextNameTravel);


        final FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.floatingButtonAdd);
        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    if (!NuovoViaggioActivity.this.autoCompleteTextView.getText().toString().equals("")) {
                        TextView tv = new TextView(NuovoViaggioActivity.this);
                        tv.setText(NuovoViaggioActivity.this.autoCompleteTextView.getText().toString());

                        String s = NuovoViaggioActivity.this.autoCompleteTextView.getText().toString();

                        String usernameUtenteSelezionato = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
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
                                    image.setContentDescription(p.getId());

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
                                    partecipants.add(p);
                                    namesPartecipants.add(p.getUsername());

                                } else {
                                    Toast.makeText(getBaseContext(), "User already present in travel", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        NuovoViaggioActivity.this.autoCompleteTextView.setText("");

                    }

                }
            });
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
            msec += Constants.ONE_HOUR_IN_MILLISEC; // 1 hour.
            expiration.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(Constants.BUCKET_NAME, key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);

            url = s3.generatePresignedUrl(generatePresignedUrlRequest);

            // Initiate the download
            //TransferObserver observer = transferUtility.download(Constants.BUCKET_NAME, key, file);
            //Log.i(TAG, "downloaded file: " + file);


            //observer.setTransferListener(new DownloadListener());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return url.toString();

    }

    public void ClickCreate(View v) {
        if (editTextNameTravel.getText().toString().equals("")) {
            Toast.makeText(getBaseContext(), "Name Travel omitted", Toast.LENGTH_LONG).show();
        } else {

            if (!partecipants.contains(profilo)) {
                partecipants.add(profilo);
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

            showProgressDialog();
            new UUIDTask().execute();

        }
    }


    //per ora non utilizzato
    public void openViaggio() {
        Intent openViaggio = new Intent(NuovoViaggioActivity.this, ViaggioActivityConFragment.class);
        openViaggio.putExtra("email", email);
        openViaggio.putExtra("emailEsterno", email);
        openViaggio.putExtra("codiceViaggio",UUIDViaggio);
        openViaggio.putExtra("nomeViaggio", nomeViaggio);
        openViaggio.putExtra("idFolder", "");
        openViaggio.putExtra("urlImmagineViaggio", "https://takeatriptravels.s3.amazonaws.com/c71d8f39-2918-4bfb-f26c-fdf9de072479/coverTravelImages/facebo10207495202451064_20160817_165913_109.jpg?x-amz-security-token=AgoGb3JpZ2luEIT%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCWV1LXdlc3QtMSKAAqiNDAYFP0XW%2FwU3oDxWLE76PyHjvNYO2cPaNptfoZbu6g2cdRWLu8Ia0%2BFQLjjj%2FyVouQtPbZKJRg5WBPMybboZbxTNcdwPbOd99OWsyp58o80wdy1tkIkugh2hfQNdDHqgSD52UQ6BZfWjdUjZC6TxQH3xBbZTtttnkroJGfh9qMdRcwf8FeaTHsCMXT6QuWW0rCNIZrkMVYCVpww42Ip%2BmnNkf4Z8YwSSWLUY2NvM%2BhDGp1YhMsTRyeQVQ%2BRz51psrcjqnHrItsynx%2BoBBu5%2B0xHnWlP%2BXNnOpAx3%2BtKoxUPlZOp9U6TWsKzSHz71DlgC9RuGoGK49cGxfDQ0%2FwgqpgUI6f%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARAAGgw0NjE0ODAxMTY0ODgiDL3p0xgCyg9q0BYHVCr6BNd14MpYZrrpXA3HaZMngV%2FcUUjHZ4te7vKPBezHgix%2BzMzJtJYdjJxJC6Z4oRrTSifi37YXoV%2BIz37erx3YPkskh30thvE0h3aBiA6x%2FBARa%2FfB7rR09gVw8Xo2FNK7PPM%2BXO%2FtzFV%2BEEimFwfoNzQObfYAS%2BLxD6UuuPwdhlOPjGZ3awRPvzxALixOq4KmBL3qSPd3opCsD1NfBJvdSCFaWYcRPCtWD4jaVU1T2EXjZlr9RF7vw9Z%2BKrXZLvig9IjXZN44Kzsof%2FoBGETkNDZk3wUEOrYGs5Z6aJ4D9nsM9hAwNEYs2sJjieFxLiEr45Z7pWO%2FOaHS80Wp%2BSjYv2LcM5n4dqtjUlYa%2F%2Fc42HAE3e0n5RN04QhGx5kPCvWQuY6gYmqYHJgFX72J%2FGNwtsp7QgAiPdQIuLyhNZtjpwfVMl1lOMpI%2F1M%2FiLHKWDV7Kj5ggpwBouJQil%2BVc5Y3l2Gm6ewXmnEFTuPdTy9tAShylebYJCqWDcEwAlYxPFxZZ5vnfUWQ9Mx0ctgFUAbuurR%2FTb6JvCwjGpNOyzFU%2FxG590HCtLioAbdMktUgcgdtl3ZeynFkFtPUHG8CaNocUPBkjnfcTN3iYLqTiwn4xHATTVlJ1hhLFwVBertsQsqZhBCLkvogGli2aU3tghEkpRNvgxlG2WipP4Qu6aDZ4VHi6fC0QG7Cgs4POmG7hnIeJQrczgecw8wflCk%2FIJA6GOpXURcq%2BzGh%2BfbilXPLAtoQC%2FvELq5fKCx3neinC%2FGQJkhdbkTHxIWjvyz0lh6y6dZtbDpUP6VOsSAaGB9UWy5P79JGGzoesfRyz%2FHdYMrbVoZIhp0gH9KrbgIwi9TVvQU%3D&AWSAccessKeyId=ASIAI5EUI7DOKMEYNDBQ&Expires=1471511679&Signature=%2BfPrFJzexSHl7TkhPRxYSTwwy2U%3D");
        openViaggio.putExtra("livelloCondivisione", "public");
        startActivity(openViaggio);
        finish();
    }


    public void openListaViaggi() {
        Intent openListaViaggi = new Intent(NuovoViaggioActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);
        //passo all'attivazione dell'activity
        startActivity(openListaViaggi);
        finish();
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
            progressDialog.dismiss();
        }
    }


    //Dialog per backPressed in home
    private void prepareSignOut() {

        new AlertDialog.Builder(NuovoViaggioActivity.this)
                .setTitle(getString(R.string.back))
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
                .setIcon(ContextCompat.getDrawable(NuovoViaggioActivity.this, R.drawable.logodefbordo))
                .show();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        prepareSignOut();
        deleteIdOnShared(NuovoViaggioActivity.this);

    }


    public static void deleteIdOnShared(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("com.example.david.takeatrip", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }


    public void onClickHelp(View v) {
        new android.support.v7.app.AlertDialog.Builder(NuovoViaggioActivity.this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.newTravel))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(ContextCompat.getDrawable(NuovoViaggioActivity.this, R.drawable.logodefbordo))
                .show();
    }


    private class ItinerariesTask extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {

            for (Profilo p : partecipants) {

                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("codice", UUIDViaggio));
                dataToSend.add(new BasicNameValuePair("email", p.getId()));


                try {
                    if (InternetConnection.haveInternetConnection(NuovoViaggioActivity.this)) {
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

            new FilterTask().execute();

            super.onPostExecute(aVoid);

        }
    }

    private class FilterTask extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {

            for (Profilo p : partecipants) {
                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("codiceViaggio", UUIDViaggio));
                dataToSend.add(new BasicNameValuePair("filtro", filtro));


                try {
                    if (InternetConnection.haveInternetConnection(NuovoViaggioActivity.this)) {
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
            hideProgressDialog();
            //apro lista viaggi
            openListaViaggi();

            //apro viaggio appena creato
            //openViaggio();
        }
    }

    private class UUIDTask extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("viaggio", nomeViaggio));

            try {
                if (InternetConnection.haveInternetConnection(NuovoViaggioActivity.this)) {
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

                            UUIDViaggio = result;
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
                new UUIDTask().execute();
            } else {

                new ItinerariesTask().execute();
            }
            super.onPostExecute(aVoid);

        }
    }

    private class GetNomiUtentiTask extends AsyncTask<Void, Void, Void> {

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

}

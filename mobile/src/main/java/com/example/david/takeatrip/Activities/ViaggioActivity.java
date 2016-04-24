package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.david.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.example.david.takeatrip.AsyncTasks.GetPartecipantiViaggioTask;
import com.example.david.takeatrip.AsyncTasks.InsertCoverImageTravelTask;
import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UploadFilePHP;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;
import com.example.david.takeatrip.Utilities.UtilS3AmazonCustom;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ViaggioActivity extends FragmentActivity {

    private static final String TAG = "TEST ViaggioActivity";

    private static final String ADDRESS = "QueryNomiUtenti.php";
    private static final String ADDRESS_INSERIMENTO_ITINERARIO = "InserimentoItinerario.php";
    private static final String ADDRESS_QUERY_FOLDER = "QueryCartellaGenerica.php";
    private static final String ADDRESS_INSERT_FOLDER = "CreazioneCartellaViaggio.php";


    private static final int DIMENSION_OF_IMAGE_PARTECIPANT = Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT;
    private static final int DIMENSION_OF_SPACE = Constants.BASE_DIMENSION_OF_SPACE;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private final int LIMIT_IMAGES_VIEWS = 4;


    private String email, emailEsterno, codiceViaggio, nomeViaggio;

    private boolean proprioViaggio = false;
    private List<Profilo> listPartecipants, profiles;
    private List<String> names;

    private TextView viewTitoloViaggio;
    private ImageView coverImageDialog;
    private LinearLayout layoutCopertinaViaggio;
    private LinearLayout layoutPartecipants;
    private LinearLayout rowHorizontal;

    private ImageView imageTravel;

    private DriveId idFolder;
    private Bitmap bitmapImageTravel;
    private String nameImageTravel, urlImageTravel;

    private String NameForUrl;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The S3 client
    private AmazonS3Client s3;

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



    private GridView gridView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaggio);

        //retreive the content view of the activity for GetPartecipantiViaggioTask to work
        View contentView = findViewById(android.R.id.content);


        imageTravel = (ImageView) findViewById(R.id.imageTravel);

        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            emailEsterno = intent.getStringExtra("emailEsterno");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
            idFolder = intent.getParcelableExtra("idFolder");
            urlImageTravel = intent.getStringExtra("urlImmagineViaggio");

            Log.i(TAG, "urlImageTravel: " + urlImageTravel);
            Log.i(TAG, "prova");


        }


        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(ViaggioActivity.this);

        listPartecipants = new ArrayList<Profilo>();
        names = new ArrayList<String>();
        profiles = new ArrayList<Profilo>();


        NameForUrl = codiceViaggio.trim().replace(" ", "");


        if(email == null){
            TakeATrip TAT = (TakeATrip)getApplicationContext();
            email = TAT.getProfiloCorrente().getEmail();
        }
        if(email != null){
            String url = email+"/"+NameForUrl;
        }

        Log.i(TAG, "email utente: " + email + " codiceViaggio: " + codiceViaggio + " nomeVaggio: " + nomeViaggio);

        gridView = (GridView) findViewById(R.id.grid_view);

        layoutCopertinaViaggio = (LinearLayout) findViewById(R.id.layoutCoverImageTravel);

        layoutPartecipants = (LinearLayout)findViewById(R.id.Partecipants);
        rowHorizontal = (LinearLayout) findViewById(R.id.layout_horizontal2);


        try {
            proprioViaggio = new GetPartecipantiViaggioTask(ViaggioActivity.this, contentView, s3,
                    codiceViaggio, listPartecipants, nomeViaggio, email, gridView, urlImageTravel,
                    layoutPartecipants, rowHorizontal).execute().get();

            popolaPartecipanti();

        } catch (InterruptedException e) {
            Log.e(TAG, "GetPartecipantiViaggioTask interrupted!");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e(TAG, "GetPartecipantiViaggioTask not executed!");
            e.printStackTrace();
        }


        new MyTaskPerUtenti().execute();



        //new MyTaskIDFolder(this,email,url,NameForUrl).execute();

    }

    public void onClickSettingsIcon(View v){

    }


    public void onClickImageTappa(View v){
        CharSequence[] emailPartecipants = new CharSequence[listPartecipants.size()];
        CharSequence[] urlImagePartecipants = new CharSequence[listPartecipants.size()];
        CharSequence[] sessoPartecipants = new CharSequence[listPartecipants.size()];

        int i= 0;
        for(Profilo p: listPartecipants){
            emailPartecipants[i] = p.getEmail();
            urlImagePartecipants[i] = p.getIdImageProfile();
            sessoPartecipants[i] = p.getSesso();
            i++;
        }

        Log.i(TAG, "email partecipants: " + Arrays.toString(emailPartecipants));

        Intent intent = new Intent(ViaggioActivity.this, ListaTappeActivity.class);
        if(email != null){
            intent.putExtra("email", email);
        }
        else{
            TakeATrip TAT = (TakeATrip)getApplicationContext();
            email = TAT.getProfiloCorrente().getEmail();
            intent.putExtra("email", email);
        }

        intent.putExtra("codiceViaggio", codiceViaggio);
        intent.putExtra("nomeViaggio", nomeViaggio);
        intent.putExtra("urlImmagineViaggio", urlImageTravel);
        intent.putExtra("partecipanti", emailPartecipants);
        intent.putExtra("urlImagePartecipants", urlImagePartecipants);
        intent.putExtra("sessoPartecipants", sessoPartecipants);



        startActivity(intent);
    }


    public void onClickImageTravel(View v) {
        if(proprioViaggio){
            try {
                ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

                AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setItems(R.array.CommandsTravelImage, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //view image profile

                                break;
                            case 1: //change image profile
                                Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intentPick, REQUEST_IMAGE_PICK);
                                break;

                            case 3: //exit
                                break;
                        }
                    }
                });

                builder.create().show();

            } catch (Exception e) {
                Log.e(e.toString().toUpperCase(), e.getMessage());
            }
        }


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmapImageTravel = (BitmapFactory.decodeFile(picturePath));
                Log.i(TAG, "image from gallery: " + picturePath);


                UtilS3AmazonCustom.uploadTravelCoverPicture(ViaggioActivity.this, picturePath,
                        codiceViaggio, email, bitmapImageTravel, layoutCopertinaViaggio);


/*
                if(email!= null && codiceViaggio != null){

                    String NameForUrl = codiceViaggio.trim().replace(" ", "");

                    String url = email+"/"+NameForUrl;
                    new MyTaskIDFolder(this,email,url,NameForUrl).execute();

                }
                else{
                    Toast.makeText(getBaseContext(), R.string.update_failed, Toast.LENGTH_LONG);
                }

                */
            }
        }
    }


    private void popolaPartecipanti(){
        int i=0;
        for(Profilo p : listPartecipants){
            if(i%LIMIT_IMAGES_VIEWS == 0){
                rowHorizontal = new LinearLayout(ViaggioActivity.this);
                rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                //Log.i(TAG, "creato nuovo layout");
                layoutPartecipants.addView(rowHorizontal);
                layoutPartecipants.addView(new TextView(ViaggioActivity.this), DIMENSION_OF_SPACE, DIMENSION_OF_SPACE);
                //Log.i(TAG, "aggiunto row e view al layout verticale");
            }



            final ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getEmail());

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickImagePartecipant(v);
                }
            });


            if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){

                String signedUrl = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_NAME, p.getIdImageProfile());



                Picasso.with(ViaggioActivity.this).
                        load(signedUrl).
                        resize(DIMENSION_OF_IMAGE_PARTECIPANT, DIMENSION_OF_IMAGE_PARTECIPANT).
                        into(image);


            }else {
                if(p.getSesso().equals("M")){
                    image.setImageResource(R.drawable.default_male);
                }
                else{
                    image.setImageResource(R.drawable.default_female);
                }
            }


            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //image.setImageResource(R.drawable.logodef);
            rowHorizontal.addView(image, DIMENSION_OF_IMAGE_PARTECIPANT, DIMENSION_OF_IMAGE_PARTECIPANT);
            rowHorizontal.addView(new TextView(this), DIMENSION_OF_SPACE, DIMENSION_OF_IMAGE_PARTECIPANT);


            i++;

        }

        if(proprioViaggio){

            FloatingActionButton buttonAddPartecipant = new FloatingActionButton(this);

            //TODO capire perchè il bottone non viene modificato (no colore)

            buttonAddPartecipant.setRippleColor(getResources().getColor(R.color.blue));
            buttonAddPartecipant.setImageResource(R.drawable.ic_add_white_24dp);
            buttonAddPartecipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAddPartecipant(v);
                }
            });

            rowHorizontal.addView(buttonAddPartecipant, DIMENSION_OF_IMAGE_PARTECIPANT, DIMENSION_OF_IMAGE_PARTECIPANT);

        }


    }

    public void onClickImagePartecipant(final View v){
        try {
            final Dialog dialog = new Dialog(this, R.style.CustomDialog);
            dialog.setContentView(R.layout.layout_dialog_profiles);

            TextView viewName = (TextView) dialog.findViewById(R.id.viewNameProfileDialog);
            LinearLayout layoutCopertina = (LinearLayout)dialog.findViewById(R.id.layoutCopertinaNelDialog);
            ImageView imageProfile = (ImageView) dialog.findViewById(R.id.imageView_round_Dialog);
            ImageView coverImageProfile = (ImageView) dialog.findViewById(R.id.CoverImageDialog);

            for(Profilo p : listPartecipants){
                if(p.getEmail().equals(v.getContentDescription())){
                    viewName.setText(p.getName() + " " + p.getSurname());

                    if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
                        String signedUrl = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_NAME, p.getIdImageProfile());

                        Picasso.with(ViaggioActivity.this).
                                load(signedUrl).
                                resize(DIMENSION_OF_IMAGE_PARTECIPANT*2, DIMENSION_OF_IMAGE_PARTECIPANT*2).
                                into(imageProfile);


                    }
                    else{
                        if(p.getSesso().equals("M")){
                            imageProfile.setImageResource(R.drawable.default_male);
                        }
                        else{
                            imageProfile.setImageResource(R.drawable.default_female);
                        }
                    }

                    if(p.getGetIdImageCover() != null && !p.getGetIdImageCover().equals("null")){

                        //String urlCoverPartecipant =  Constants.ADDRESS_TAT+ p.getGetIdImageCover();

                        String signedUrl = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_NAME, p.getGetIdImageCover());
                        new BitmapWorkerTask(null, layoutCopertina).execute(signedUrl);


                    }

                    break;
                }
            }


            Button viewProfileButton = (Button) dialog.findViewById(R.id.buttonViewProfile);
            viewProfileButton.setBackground(getDrawable(R.drawable.button_style));
            viewProfileButton.setTextSize(20);
            Button cancelDialogButton = (Button) dialog.findViewById(R.id.buttonCancelDialog);
            cancelDialogButton.setBackground(getDrawable(R.drawable.button_style));
            cancelDialogButton.setTextSize(20);



            viewProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG,"email profilo selezionato: "+ v.getContentDescription().toString());
                    for(Profilo p : listPartecipants){
                        if(p.getEmail().equals(v.getContentDescription())){

                            Intent openProfilo = new Intent(ViaggioActivity.this, ProfiloActivity.class);
                            openProfilo.putExtra("name", p.getName());
                            openProfilo.putExtra("surname", p.getSurname());
                            if(emailEsterno != null){
                                openProfilo.putExtra("emailEsterno", p.getEmail());
                            }
                            else{
                                openProfilo.putExtra("email", email);
                            }
                            openProfilo.putExtra("dateOfBirth", p.getDataNascita());
                            openProfilo.putExtra("nazionalita", p.getNazionalita());
                            openProfilo.putExtra("sesso", p.getSesso());
                            openProfilo.putExtra("username", p.getUsername());
                            openProfilo.putExtra("lavoro", p.getLavoro());
                            openProfilo.putExtra("descrizione", p.getDescrizione());
                            openProfilo.putExtra("tipo", p.getTipo());
                            openProfilo.putExtra("urlImmagineProfilo", p.getIdImageProfile());
                            openProfilo.putExtra("urlImmagineCopertina", p.getGetIdImageCover());

                            // passo all'attivazione dell'activity
                            startActivity(openProfilo);

                            break;
                        }
                    }
                }
            });

            cancelDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, e.toString().toUpperCase() + ": " + e.getMessage());
        }
    }


    public void onClickAddPartecipant(View v){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_viaggio2);
        dialog.setTitle("Add a Partecipant");

        final AutoCompleteTextView text=(AutoCompleteTextView)dialog.findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        text.setHint("Add partecipant");

        text.setAdapter(adapter);
        text.setThreshold(1);

        TextView travel = (TextView) dialog.findViewById(R.id.titoloViaggio);
        travel.setText(nomeViaggio);


        EditText nameTravel = (EditText) dialog.findViewById(R.id.editTextNameTravel);
        nameTravel.setText(nomeViaggio);
        nameTravel.setEnabled(false);


        /*

        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreateTravel);
        buttonCreate.setVisibility(View.INVISIBLE);

        Button buttonCancella = (Button) dialog.findViewById(R.id.buttonCancellaDialog);
        buttonCancella.setVisibility(View.INVISIBLE);
        */

        final FloatingActionButton buttonAdd = (FloatingActionButton) dialog.findViewById(R.id.floatingButtonAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals("")) {

                    String newPartecipant = text.getText().toString();
                    Log.i(TAG, "lista Partecipanti al viaggio: " + listPartecipants);
                    Log.i(TAG, "nuovo Partecipante: " + newPartecipant);

                    String usernameUtenteSelezionato = newPartecipant.substring(newPartecipant.indexOf('(')+1, newPartecipant.indexOf(')'));
                    for(Profilo p : profiles){
                        if(p.getUsername().equals(usernameUtenteSelezionato)){

                            //TODO: migliorare con override di equals in Profilo
                            boolean giaPresente = false;
                            for(Profilo partecipant : listPartecipants){
                                if(partecipant.getEmail().equals(p.getEmail())){
                                    giaPresente = true;
                                    break;
                                }
                            }
                            //if(!listPartecipants.contains(p)) {
                            if(!giaPresente) {
                                listPartecipants.add(p);
                                new TaskForItineraries(p).execute();

                                final ImageView image = new RoundedImageView(ViaggioActivity.this, null);
                                image.setContentDescription(p.getEmail());

                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onClickImagePartecipant(v);
                                    }
                                });

                                layoutPartecipants.removeAllViews();
                                popolaPartecipanti();
                            }
                            else{
                                Toast.makeText(getBaseContext(),"Already partecipant", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }

                    Log.i(TAG, "lista Partecipanti al viaggio: " + listPartecipants);

                    dialog.dismiss();

                }

            }
        });

        dialog.show();
    }





    private class MyTaskPerUtenti extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "TEST UtentiTask";

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(ViaggioActivity.this)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
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
                                sb.append(line).append("\n");
                            }
                            is.close();
                            result = sb.toString();
                            JSONArray jArray = new JSONArray(result);

                            if(result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String nomeUtente = json_data.getString("nome");
                                    String cognomeUtente = json_data.getString("cognome");
                                    String emailUtente = json_data.getString("email");
                                    String usernameUtente = json_data.getString("username");
                                    String sesso = json_data.getString("sesso");
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, sesso, usernameUtente,
                                                null, null, null, urlImmagineProfilo, urlImmagineCopertina);
                                    profiles.add(p);
                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "("+usernameUtente+")";
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
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString()+ ": " + e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }





    private class MyTaskIDFolder extends AsyncTask<Void, Void, Void> {


        InputStream is = null;
        String emailUser, nameFolder,result;
        String urlFolder;
        DriveId idFolder;
        Context context;

        public MyTaskIDFolder(Context c, String emailUtente, String urlFolder, String name){
            context  = c;
            emailUser = emailUtente;
            this.urlFolder = urlFolder;
            nameFolder = name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));
            dataToSend.add(new BasicNameValuePair("path", urlFolder));

            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
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
                        } catch (Exception e) {
                            Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i(TAG, "Input Stream uguale a null");
                    }
                }
                else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString()+ ": " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "risultato della query: " + result);
            if(result != null && !result.equals("null\n")){
                Log.i(TAG, "Presente cartella di viaggio");

                if(bitmapImageTravel != null){
                    Log.i(TAG, "upload dell'immagine del viaggio");

                    String pathImage = urlFolder+"/";
                    nameImageTravel = String.valueOf(System.currentTimeMillis()) + ".jpg";

                    Log.i(TAG, "nome immagine: " + nameImageTravel);
                    new UploadFilePHP(ViaggioActivity.this,bitmapImageTravel,pathImage,Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
                    new InsertCoverImageTravelTask(ViaggioActivity.this,email,codiceViaggio, null,
                            pathImage + Constants.NAME_IMAGES_TRAVEL_DEFAULT, bitmapImageTravel, layoutCopertinaViaggio).execute();
                }
                else{
                    Log.i(TAG, "solo prelievo immagine copertina viaggio gia presente");
                    new BitmapWorkerTask(null,layoutCopertinaViaggio).execute(Constants.ADDRESS_TAT + urlFolder + "/" + Constants.NAME_IMAGES_TRAVEL_DEFAULT);

                }
            }
            else{
                //se Non è presente la cartella in fase di upload la creo per poi aggiungere l'immagine del viaggio
                if(bitmapImageTravel != null){
                    new MyTaskFolder(ViaggioActivity.this, emailUser,codiceViaggio,null,urlFolder, nameFolder).execute();
                }
            }
            super.onPostExecute(aVoid);

        }
    }



    private class MyTaskFolder extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String emailUser, idTravel,result, urlCartella;
        String nomeCartella;
        DriveId idFolder;
        Context context;

        public MyTaskFolder(Context c, String emailUtente, String idTravel, DriveId id, String url,String n){
            context  = c;
            emailUser = emailUtente;
            this.idTravel = idTravel;
            idFolder = id;
            nomeCartella = n;
            urlCartella = url;
        }


        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("emailUtente", emailUser));
            dataToSend.add(new BasicNameValuePair("codiceViaggio", idTravel));
            dataToSend.add(new BasicNameValuePair("codiceCartella", idFolder+""));
            dataToSend.add(new BasicNameValuePair("nomeCartella", nomeCartella));
            dataToSend.add(new BasicNameValuePair("urlCartella", urlCartella));

            String urlImmagineViaggio = urlCartella + "/" + Constants.NAME_IMAGES_TRAVEL_DEFAULT;
            dataToSend.add(new BasicNameValuePair("urlImmagine", urlImmagineViaggio));


            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.ADDRESS_TAT+ADDRESS_INSERT_FOLDER);
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
                                sb.append(line).append("\n");
                            }
                            is.close();
                            result = sb.toString();
                        } catch (Exception e) {
                            Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i(TAG, "Input Stream uguale a null");
                    }
                }
                else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString()+ ": " + e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            String pathImage = urlCartella+"/";
            new UploadFilePHP(ViaggioActivity.this,bitmapImageTravel,pathImage,Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
            new InsertCoverImageTravelTask(ViaggioActivity.this,email,codiceViaggio, null,
                    pathImage + Constants.NAME_IMAGES_TRAVEL_DEFAULT, bitmapImageTravel, layoutCopertinaViaggio).execute();

            super.onPostExecute(aVoid);

        }
    }



    private class TaskForItineraries extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "TEST ItinerariesTask";

        private Profilo profilo;
        public TaskForItineraries(Profilo p){
            profilo = p;
        }

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
            dataToSend.add(new BasicNameValuePair("email", profilo.getEmail()));

            String url = profilo.getEmail()+"/"+NameForUrl;

            Log.i(TAG, "url della cartella del nuovo partecipante: " + url);
            dataToSend.add(new BasicNameValuePair("urlCartella",url));
            dataToSend.add(new BasicNameValuePair("nomeCartella",NameForUrl));

            try {
                if (InternetConnection.haveInternetConnection(ViaggioActivity.this)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_INSERIMENTO_ITINERARIO);
                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                    HttpResponse response = httpclient.execute(httppost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                }
                else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString()+ ": " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }




}

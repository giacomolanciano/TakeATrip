package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DownloadImageTask;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UploadFilePHP;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;



/*
import com.google.gcloud.storage.BlobId;
import com.google.gcloud.storage.Storage;
import com.google.gcloud.storage.StorageOptions;
import static java.nio.charset.StandardCharsets.UTF_8;
import com.google.gcloud.storage.Blob;
import com.google.gcloud.storage.Bucket;
import com.google.gcloud.storage.BucketInfo;
*/


public class ViaggioActivity extends AppCompatActivity {

    private static final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";
    private static final String ADDRESS_PARTECIPANTS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryPartecipantiViaggio.php";
    private static final String ADDRESS_INSERIMENTO_ITINERARIO = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoItinerario.php";

    private static final String TAG = "ViaggioActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private final int LIMIT_IMAGES_VIEWS = 4;


    private String email, codiceViaggio, nomeViaggio;

    private boolean proprioViaggio = false;
    private List<Profilo> listPartecipants, profiles;
    private List<String> names;

    private TextView viewTitoloViaggio;
    private LinearLayout layoutCopertinaViaggio;
    private LinearLayout layoutPartecipants;
    private LinearLayout rowHorizontal;

    private DriveId idFolder;
    private Bitmap bitmapImageTravel;
    private String nameImageTravel, urlImageTravel;

    private String NameForUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
            idFolder = intent.getParcelableExtra("idFolder");
            urlImageTravel = intent.getStringExtra("urlImmagineViaggio");

            Log.i("TEST"+ TAG+": ", "email: " +email +"codiceViaggio: "+ codiceViaggio + "nomeViaggio: " + nomeViaggio +"id Cartella Viaggio: " + idFolder);

        }

        listPartecipants = new ArrayList<Profilo>();
        names = new ArrayList<String>();
        profiles = new ArrayList<Profilo>();


        NameForUrl = codiceViaggio.trim().replace(" ", "");
        String url = email+"/"+NameForUrl;

        Log.i("TEST", "email utente: " + email + " codiceViaggio: " + codiceViaggio + " nomeVaggio: " + nomeViaggio);


        new MyTask().execute();
        new MyTaskPerUtenti().execute();


        //new MyTaskIDFolder(this,email,url,NameForUrl).execute();

    }

    public void onClickSettingsIcon(View v){

    }


    public void onClickImageTappa(View v){
        ArrayList<CharSequence> emailPartecipants = new ArrayList<CharSequence>();
        for(Profilo p: listPartecipants){
            emailPartecipants.add(p.getEmail());
        }

        Log.i(TAG, "email partecipants: " + emailPartecipants);

        Intent intent = new Intent(ViaggioActivity.this, ListaTappeActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("codiceViaggio", codiceViaggio);
        intent.putExtra("nomeViaggio", nomeViaggio);

        intent.putExtra("partecipanti", emailPartecipants);

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
        else{
            //TODO: si visualizza solo l'immagine del viaggio
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
                Log.i("image from gallery:", picturePath + "");

                String NameForUrl = codiceViaggio.trim().replace(" ", "");
                String url = email+"/"+NameForUrl;
                new MyTaskIDFolder(this,email,url,NameForUrl).execute();

            }
        }
    }


    private void PopolaPartecipanti(){
        int i=0;
        for(Profilo p : listPartecipants){
            if(i%LIMIT_IMAGES_VIEWS == 0){
                rowHorizontal = new LinearLayout(ViaggioActivity.this);
                rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                //Log.i(TAG, "creato nuovo layout");
                layoutPartecipants.addView(rowHorizontal);
                layoutPartecipants.addView(new TextView(ViaggioActivity.this), 20, 20);
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
                new DownloadImageTask(image).execute(Constants.ADDRESS_TAT + p.getIdImageProfile());
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
            rowHorizontal.addView(image, 100, 100);
            rowHorizontal.addView(new TextView(this), 20, 100);


            i++;

        }

        if(proprioViaggio){
            FloatingActionButton buttonAddPartecipant = new FloatingActionButton(this);
            buttonAddPartecipant.setRippleColor(getResources().getColor(R.color.blue));
            buttonAddPartecipant.setImageResource(R.drawable.ic_add_white_24dp);
            buttonAddPartecipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAddPartecipant(v);
                }
            });

            rowHorizontal.addView(buttonAddPartecipant, 100, 100);

        }


    }

    public void onClickImagePartecipant(final View v){
        try {
            final Dialog dialog = new Dialog(this, R.style.CustomDialog);
            dialog.setContentView(R.layout.layout_dialog_profiles);

            TextView viewName = (TextView) dialog.findViewById(R.id.viewNameProfileDialog);
            LinearLayout layoutCopertina = (LinearLayout)dialog.findViewById(R.id.layoutCopertinaNelDialog);
            ImageView imageProfile = (ImageView) dialog.findViewById(R.id.imageView_round_Dialog);

            for(Profilo p : listPartecipants){
                if(p.getEmail().equals(v.getContentDescription())){
                    viewName.setText(p.getName() + " " + p.getSurname());

                    if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
                        String urlProfilePartecipant =  Constants.ADDRESS_TAT + p.getIdImageProfile();
                        new DownloadImageTask(imageProfile).execute(urlProfilePartecipant);

                    }
                    if(p.getGetIdImageCover() != null && !p.getGetIdImageCover().equals("null")){
                        String urlCoverPartecipant =  Constants.ADDRESS_TAT+ p.getGetIdImageCover();
                        new DownloadImageTask(null, layoutCopertina).execute(urlCoverPartecipant);
                    }

                    break;
                }
            }


            Button viewProfileButton = (Button) dialog.findViewById(R.id.buttonViewProfile);
            Button cancelDialogButton = (Button) dialog.findViewById(R.id.buttonCancelDialog);

            viewProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TEST","email profilo selezionato: "+ v.getContentDescription().toString());
                    for(Profilo p : listPartecipants){
                        if(p.getEmail().equals(v.getContentDescription())){

                            Intent openProfilo = new Intent(ViaggioActivity.this, ProfiloActivity.class);
                            openProfilo.putExtra("name", p.getName());
                            openProfilo.putExtra("surname", p.getSurname());
                            openProfilo.putExtra("emailEsterno", p.getEmail());
                            openProfilo.putExtra("email", email);
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
            Log.e(e.toString().toUpperCase(), e.getMessage());
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



        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreateTravel);
        buttonCreate.setVisibility(View.INVISIBLE);

        Button buttonCancella = (Button) dialog.findViewById(R.id.buttonCancellaDialog);
        buttonCancella.setVisibility(View.INVISIBLE);

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
                                PopolaPartecipanti();
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



    private class MyTask extends AsyncTask<Void, Void, Void> {

        //query partecipanti viaggio

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));


            try {
                if (InternetConnection.haveInternetConnection(ViaggioActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(ADDRESS_PARTECIPANTS);

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
                                    String emailProfilo = json_data.getString("emailProfilo").toString();
                                    String nomePartecipante = json_data.getString("nome").toString();
                                    String cognomePartecipante = json_data.getString("cognome").toString();
                                    String data = json_data.getString("dataNascita").toString();
                                    String nazionalita = json_data.getString("nazionalita").toString();
                                    String sesso = json_data.getString("sesso").toString();
                                    String username = json_data.getString("username").toString();
                                    String lavoro = json_data.getString("lavoro").toString();
                                    String descrizione = json_data.getString("descrizione").toString();
                                    String tipo = json_data.getString("tipo").toString();
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo").toString();
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina").toString();

                                    listPartecipants.add(new Profilo(emailProfilo, nomePartecipante,cognomePartecipante,
                                            data, nazionalita, sesso, username, lavoro, descrizione, tipo, urlImmagineProfilo, urlImmagineCopertina));
                                }
                            }

                        } catch (Exception e) {
                            Log.e("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.e("TEST", "Input Stream uguale a null");
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
            Log.i("TEST", "lista partecipanti al viaggio " + nomeViaggio + ": " + listPartecipants.toString());
            //controllo se l'email dell'utente è tra quelle dei partecipanti al viaggio
            for(Profilo p : listPartecipants){
                if(email.equals(p.getEmail())){
                    proprioViaggio = true;
                    Log.i("TEST", "sei compreso nel viaggio");
                }
            }
            if(proprioViaggio){
                setContentView(R.layout.activity_viaggio);
            }
            else{
                Log.i("TEST", "non sei compreso nel viaggio");
                setContentView(R.layout.activity_viaggio3);
            }

            viewTitoloViaggio = (TextView)findViewById(R.id.titoloViaggio);
            layoutCopertinaViaggio = (LinearLayout)findViewById(R.id.layoutCoverImageTravel);

            if(urlImageTravel != null && !urlImageTravel.equals("null")){
                new DownloadImageTask(null,layoutCopertinaViaggio).execute(Constants.ADDRESS_TAT + urlImageTravel);
            }


            viewTitoloViaggio.setText(nomeViaggio);

            layoutPartecipants = (LinearLayout)findViewById(R.id.Partecipants);
            rowHorizontal = (LinearLayout) findViewById(R.id.layout_horizontal2);

            PopolaPartecipanti();

            super.onPostExecute(aVoid);

        }
    }




    private class MyTaskPerUtenti extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(ViaggioActivity.this)) {
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
                                    String usernameUtente = json_data.getString("username").toString();
                                    String sesso = json_data.getString("sesso").toString();
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo").toString();
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina").toString();

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





    private class MyTaskIDFolder extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_QUERY_FOLDER = "QueryCartellaGenerica.php";

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
            Log.i("TEST", "risultato della query: " + result);
            if(result != null && !result.equals("null\n")){
                Log.i("TEST", "Presente cartella di viaggio");

                if(bitmapImageTravel != null){
                    Log.i("TEST", "upload dell'immagine del viaggio");

                    String pathImage = urlFolder+"/";
                    nameImageTravel = String.valueOf(System.currentTimeMillis());

                    Log.i("TEST", "nome immagine: " + nameImageTravel);
                    new UploadFilePHP(ViaggioActivity.this,bitmapImageTravel,pathImage,Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
                    new MyTaskInsertImageTravel(ViaggioActivity.this,email,codiceViaggio, null,pathImage + Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
                }
                else{
                    Log.i("TEST", "solo prelievo immagine copertina viaggio gia presente");
                    new DownloadImageTask(null,layoutCopertinaViaggio).execute(Constants.ADDRESS_TAT + urlFolder + "/" + Constants.NAME_IMAGES_TRAVEL_DEFAULT);
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

        private final String ADDRESS_INSERT_FOLDER = "http://www.musichangman.com/TakeATrip/CreazioneCartellaViaggio.php";
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
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(ADDRESS_INSERT_FOLDER);
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
                //upload dell'immagine

                String pathImage = urlCartella+"/";
                new UploadFilePHP(ViaggioActivity.this,bitmapImageTravel,pathImage,Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
                new MyTaskInsertImageTravel(ViaggioActivity.this,email,codiceViaggio, null,pathImage + Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
            }

            super.onPostExecute(aVoid);

        }
    }


    private class MyTaskInsertImageTravel extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_INSERT_IMAGE_TRAVEL = "InserimentoImmagineViaggio.php";
        InputStream is = null;
        String emailUser,codiceViaggio, result, urlImmagine;
        DriveId idFile;
        Context context;


        public MyTaskInsertImageTravel(Context c, String emailUtente, DriveId id){
            context  = c;
            emailUser = emailUtente;
            idFile = id;
        }

        public MyTaskInsertImageTravel(Context c, String emailUtente, String codiceViaggio, DriveId id, String url){
            context  = c;
            emailUser = emailUtente;
            this.codiceViaggio = codiceViaggio;
            idFile = id;
            urlImmagine = url;
        }


        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));
            dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
            dataToSend.add(new BasicNameValuePair("id", idFile+""));
            dataToSend.add(new BasicNameValuePair("url", urlImmagine));


            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_IMAGE_TRAVEL);
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
            Log.i("TEST", "risultato operazione di inserimento immagine viaggio nel DB:" + result);
            if(!result.equals("OK")){
                //upload dell'immagine
                Drawable d = new BitmapDrawable(getResources(), bitmapImageTravel);
                layoutCopertinaViaggio.setBackground(d);
            }

            super.onPostExecute(aVoid);

        }

    }





    private class TaskForItineraries extends AsyncTask<Void, Void, Void> {

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

            Log.i("TEST", "url della cartella del nuovo partecipante: " + url);
            dataToSend.add(new BasicNameValuePair("urlCartella",url));
            dataToSend.add(new BasicNameValuePair("nomeCartella",NameForUrl));

            try {
                if (InternetConnection.haveInternetConnection(ViaggioActivity.this)) {
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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}

package com.example.david.takeatrip.Activities;

import android.app.Activity;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.RoundedImageView;

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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class ViaggioActivity extends AppCompatActivity {

    private static final String ADDRESS_PARTECIPANTS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryPartecipantiViaggio.php";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private String email, codiceViaggio, nomeViaggio;

    private boolean proprioViaggio = false;
    private List<Profilo> listPartecipants;

    private TextView viewTitoloViaggio;
    private LinearLayout layoutCopertinaViaggio;
    private LinearLayout layoutPartecipants;

    private Profilo currentProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
        }

        listPartecipants = new ArrayList<Profilo>();

        Log.i("TEST", "email utente: " + email + " codiceViaggio: " +codiceViaggio +" nomeVaggio: "+ nomeViaggio);

        //TODO: fare la query per individuare i partecipanti al viaggio

        new MyTask().execute();

    }



    public void onClickSettingsIcon(View v){

    }





    public void onClickImageTappa(View v){

        Intent intent = new Intent(ViaggioActivity.this, ListaTappeActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("codiceViaggio", codiceViaggio);
        intent.putExtra("nomeViaggio", nomeViaggio);

        startActivity(intent);
    }


    public void onClickImageTravel(View v) {
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


            // Create the AlertDialog object and return it
            builder.create().show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }
    }


    private void PopolaPartecipanti(){
        for(Profilo p : listPartecipants){
            final ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getEmail());
            currentProfile = p;

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickImagePartecipant(v);
                }
            });
            //TODO: mettere le immagini dei partecipanti
            image.setImageResource(R.drawable.logodef);
            layoutPartecipants.addView(image, 100, 100);
            layoutPartecipants.addView(new TextView(this), 20, 100);


        }

    }

    public void onClickImagePartecipant(final View v){
        try {
            final Dialog dialog = new Dialog(this, R.style.CustomDialog);
            //final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.layout_dialog_profiles);



            TextView viewName = (TextView) dialog.findViewById(R.id.viewNameProfileDialog);
            for(Profilo p : listPartecipants){
                if(p.getEmail().equals(v.getContentDescription())){
                    viewName.setText(p.getName() + " " + p.getSurname());
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
                            openProfilo.putExtra("email", p.getEmail());
                            openProfilo.putExtra("dateOfBirth", p.getDataNascita());

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
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.i("image from gallery:", picturePath + "");


                //TODO: update the db with new profile image
                Drawable d = new BitmapDrawable(getResources(), thumbnail);
                layoutCopertinaViaggio.setBackground(d);

            }
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

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
                                    listPartecipants.add(new Profilo(emailProfilo, nomePartecipante,cognomePartecipante, data));
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

            viewTitoloViaggio.setText(nomeViaggio);

            layoutPartecipants = (LinearLayout)findViewById(R.id.Partecipants);

            PopolaPartecipanti();

            super.onPostExecute(aVoid);

        }
    }

}

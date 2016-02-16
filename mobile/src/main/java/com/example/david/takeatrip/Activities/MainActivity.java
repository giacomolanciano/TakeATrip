package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private final String ADDRESS = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryNomiUtenti.php";

    private String name, surname, email;
    private String date, password;

    private ImageView imageViewProfileRound;
    private FrameLayout layoutNewTravel;
    private LinearLayout layoutPartecipants;

    AutoCompleteTextView text;
    List<String> names,partecipants;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");
        }
        else{
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView)findViewById(R.id.imageView_round);
        layoutNewTravel = (FrameLayout)findViewById(R.id.FrameNewTravel);
        layoutPartecipants = (LinearLayout)findViewById(R.id.LayoutPartecipantsAdded);


        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);




        names = new ArrayList<String>();
        partecipants = new ArrayList<String>();



        new MyTask().execute();

    }


    public void ClickImageProfile(View v){
        Intent openProfilo = new Intent(MainActivity.this, ProfiloActivity.class);
        openProfilo.putExtra("name", name);
        openProfilo.putExtra("surname", surname);
        openProfilo.putExtra("email", email);
        openProfilo.putExtra("dateOfBirth", date);
        openProfilo.putExtra("pwd", password);



        // passo all'attivazione dell'activity
        startActivity(openProfilo);
    }


    public void ClickTravels(View v){
        Intent openListaViaggi = new Intent(MainActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);

        // passo all'attivazione dell'activity
        startActivity(openListaViaggi);
    }


    public void ClickNewTravel(View v){

        text=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        layoutNewTravel.setVisibility(View.VISIBLE);

    }

    public void ClickNewPartecipant(View v){

        RoundedImageView view = new RoundedImageView(this,null);
        view.setImageResource(R.drawable.defaultprofile_picture);
        view.setMaxWidth(4);
        view.setMaxHeight(5);
        layoutPartecipants.addView(view);
        layoutPartecipants.setDividerPadding(5);

        partecipants.add(text.getText().toString());
        text.setText("");

    }

    public void onClickCancelButton(View v){
        layoutNewTravel.setVisibility(View.INVISIBLE);
    }


    public void onClickCreateTravel(View v){
        //TODO: immagazzina il viaggio nel DB

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
                                    String nomeUtente = json_data.getString("nome").toString();
                                    String cognomeUtente = json_data.getString("cognome").toString();
                                    stringaFinale = nomeUtente + " " + cognomeUtente;
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

            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,names);

            text.setAdapter(adapter);
            text.setThreshold(1);

            super.onPostExecute(aVoid);

        }
    }
}

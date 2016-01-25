package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.beust.jcommander.JCommander;
import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.YelpAPI;
import com.example.david.takeatrip.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class MetaActivity extends AppCompatActivity {
    private static final String CONSUMER_KEY = "GG8CCBcWfVT0H7oL_rzLLg";
    private static final String CONSUMER_SECRET = "sr64WWYax8rlTBleICHbTfBciJ8";
    private static final String TOKEN = "XDSYVTTi-IMkd6C4RJuKER1NOr_OtXmV";
    private static final String TOKEN_SECRET = "7XvrPJUelL6exTOCJDG-TktXQiM";



    private String nome = "";
    private TextView viewNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta);


        viewNome = (TextView)findViewById(R.id.ViewNomeMeta);

        if(getIntent() != null){
            Intent intent = getIntent();
            nome = intent.getStringExtra("nomeMeta");
        }



        new MyTask().execute();


        viewNome.setText(nome);


    }


    private class MyTask extends AsyncTask<Void, Void, Void> {


        String response = "";
        @Override
        protected Void doInBackground(Void... params) {


            YelpAPI.YelpAPICLI yelpApiCli = new YelpAPI.YelpAPICLI();
            yelpApiCli.setLocation(nome);

            new JCommander(yelpApiCli);

            YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
            response = yelpApi.queryAPI(yelpApi, yelpApiCli);



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(response.equals("API unavailable in this location")){
                Toast.makeText(getBaseContext(), "API YELP unavailable in this location", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(getBaseContext(), "caricati i dati sul DB " + nome + " " + cognome + " " + data + " " + email + " " + password , Toast.LENGTH_SHORT).show();

            super.onPostExecute(aVoid);

        }
    }
}

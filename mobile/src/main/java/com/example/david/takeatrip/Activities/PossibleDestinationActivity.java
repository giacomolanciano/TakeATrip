package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.david.takeatrip.R;

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
import java.util.List;

public class PossibleDestinationActivity extends AppCompatActivity {


    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/ScegliMeta.php";
    private String ADDRESS_PRELIEVO2 = "http://www.musichangman.com/TakeATrip/InserimentoDati/";


    private String temperature, pressure, humidity, speedWind;
    private String email;
    private String TextFile = "";

    private List<String> nomiCitta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_possible_destination);

        nomiCitta = new ArrayList<String>();


        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");

            temperature = intent.getStringExtra("temperatura");
            pressure = intent.getStringExtra("pressione");
            humidity = intent.getStringExtra("umidita");
            speedWind = intent.getStringExtra("velocitaVento");
        }

        if(humidity.toLowerCase().equals("low level")){
            humidity = "15";
        }
        else if(humidity.toLowerCase().equals("medium level")){
            humidity = "45";
        }
        else if(humidity.toLowerCase().equals("high level")){
            humidity = "85";
        }


        if(speedWind.toLowerCase().equals("low level")){
            speedWind = "1";

        }
        else if(speedWind.toLowerCase().equals("medium level")){
            speedWind = "3";

        }
        else if(speedWind.toLowerCase().equals("high level")){
            speedWind = "5";

        }

        double temperature2 = Double.parseDouble(temperature);
        temperature2 = temperature2 + 273.15;
        temperature = String.valueOf(temperature2);




        MyTask mT = new MyTask();
        mT.execute();






    }




    /*
    private void PopolaLista(){

        final ViaggioAdapter adapter = new ViaggioAdapter(this,R.layout.entry_travels_listview, viaggi);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                final Viaggio viaggio = (Viaggio) adattatore.getItemAtPosition(pos);
                Toast.makeText(getBaseContext(), "hai cliccato il nome: " + viaggio.getNome(), Toast.LENGTH_SHORT).show();



                //TODO per ora non ci interessano tutti gli itinerari associati al viaggio
                //Intent intent = new Intent(ListaViaggiActivity.this, ViaggioActivity.class);

                Intent intent = new Intent(ListaViaggiActivity.this, ListaTappeActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("codiceViaggio", viaggio.getCodice());

                startActivity(intent);



            }
        });

    }

*/


    private class MyTask extends AsyncTask<Void, Void, Void> {
        InputStream is = null;


        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("temperatura", temperature));
            dataToSend.add(new BasicNameValuePair("pressione", pressure));
            dataToSend.add(new BasicNameValuePair("umidita", humidity));
            dataToSend.add(new BasicNameValuePair("velocitaVento", speedWind));




            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ADDRESS_PRELIEVO);
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

                        TextFile = sb.toString();

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Errore nella connessione http " + e.toString(), Toast.LENGTH_LONG).show();
                //Log.e("TEST", "Errore nella connessione http "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //popolamento della ListView

            String emailPerAddress = email;
            int positionDot = emailPerAddress.indexOf(".");
            emailPerAddress = emailPerAddress.substring(0,positionDot);

            ADDRESS_PRELIEVO2 = ADDRESS_PRELIEVO2 + "ScegliMeta2" + emailPerAddress + ".php";
            Toast.makeText(getBaseContext(), "stringa risultante: " + ADDRESS_PRELIEVO2, Toast.LENGTH_LONG).show();


            new MyTask2().execute();




        }
    }

    private class MyTask2 extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();


            //dataToSend.add(new BasicNameValuePair("email", email));

            dataToSend.add(new BasicNameValuePair("temperatura", temperature));
            dataToSend.add(new BasicNameValuePair("pressione", pressure));
            dataToSend.add(new BasicNameValuePair("umidita", humidity));
            dataToSend.add(new BasicNameValuePair("velocitaVento", speedWind));




            try {
                Log.d("TEST", ADDRESS_PRELIEVO2);

                //ADDRESS_PRELIEVO2 = URLEncoder.encode(ADDRESS_PRELIEVO2, "UTF-8");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ADDRESS_PRELIEVO2);
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

                        String result2 = sb.toString();

                        stringaFinale = result2;



                        JSONArray jArray = new JSONArray(result2);

                        if(jArray != null && result2 != null){
                            for(int i=0;i<jArray.length();i++){
                                JSONObject json_data = jArray.getJSONObject(i);
                                String nomeMeta = json_data.getString("nome").toString();

                                nomiCitta.add(nomeMeta);
                            }
                        }




                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                //Toast.makeText(getBaseContext(), "Errore nella connessione http " + e.toString(), Toast.LENGTH_LONG).show();
                Log.e("TEST", "Errore nella connessione http " + e.toString() + "  "+ ADDRESS_PRELIEVO2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //popolamento della ListView
            Toast.makeText(getBaseContext(), "stringa risultante2: " + temperature + " " + pressure + " " + humidity + " " +speedWind , Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "stringa risultante2: " + stringaFinale , Toast.LENGTH_LONG).show();


            for(int i=0; i<nomiCitta.size(); i++){
                Log.d("TEST", nomiCitta.get(i));

            }
            //Log.d("TEST", nomiCitta);

            //PopolaLista();

            super.onPostExecute(aVoid);

        }
    }
}

package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.Classes.ViaggioAdapter;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class ListaViaggiActivity extends AppCompatActivity {


    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryViaggi.php";


    private ListView lista;
    private ArrayList<Viaggio> viaggi;
    private String email;


    private TextView ViewCaricamentoInCorso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_viaggi);

        lista = (ListView)findViewById(R.id.listViewTravels);

        ViewCaricamentoInCorso = (TextView)findViewById(R.id.TextViewCaricamentoInCorso);

        viaggi = new ArrayList<Viaggio>();



        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            Log.i("TEST", "email utente in lista viaggi: " + email);

        }


        ViewCaricamentoInCorso.setVisibility(View.VISIBLE);

        MyTask mT = new MyTask();
        mT.execute();

    }



    private void PopolaLista(){

        final ViaggioAdapter adapter = new ViaggioAdapter(this,R.layout.entry_travels_listview, viaggi);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                final Viaggio viaggio = (Viaggio) adattatore.getItemAtPosition(pos);
                // Toast.makeText(getBaseContext(), "hai cliccato il viaggio: " + viaggio.getNome(), Toast.LENGTH_SHORT).show();

                //TODO per ora non ci interessano tutti gli itinerari associati al viaggio
                //Intent intent = new Intent(ListaViaggiActivity.this, ViaggioActivity.class);

                Intent intent = new Intent(ListaViaggiActivity.this, ViaggioActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("codiceViaggio", viaggio.getCodice());
                intent.putExtra("nomeViaggio", viaggio.getNome());

                startActivity(intent);

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_viaggi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class MyTask extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));



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

                        String result = sb.toString();

                        Log.i("TEST", "result da queryViaggi: " + result);


                        if(result.equals("null\n")){
                            //TODO: convertire in values
                            stringaFinale = "Non sono presenti viaggi";
                            Log.i("TEST", "result da queryViaggi: " + stringaFinale);

                        }
                        else{
                            JSONArray jArray = new JSONArray(result);

                            if(jArray != null && result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codiceViaggio = json_data.getString("codiceViaggio").toString();
                                    String nomeViaggio = json_data.getString("nomeViaggio").toString();
                                    viaggi.add(new Viaggio(codiceViaggio, nomeViaggio));
                                }
                            }

                            Log.i("TEST", "lista viaggi di " + email + ": " + viaggi);
                        }


                    } catch (Exception e) {
                        Log.e("TEST", "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e("TEST", "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ViewCaricamentoInCorso.setVisibility(View.INVISIBLE);


            if(stringaFinale.equals("")) {
                PopolaLista();
            }
            else{
                //TODO: creare un dialog più carino, con la possibiltà di aggiungere da qui un nuovo viaggio
                Toast.makeText(getBaseContext(), stringaFinale, Toast.LENGTH_LONG).show();
            }


            super.onPostExecute(aVoid);

        }
    }
}

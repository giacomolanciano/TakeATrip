package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.InternetConnection;

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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lucagiacomelli on 29/04/16.
 */
public class RicercaUtenteTask  extends AsyncTask<Void, Void, Set<Profilo>> {

    private static final String ADDRESS = "QueryRicercaUtenti.php";
    private static final String TAG = "TEST RicercaUtentTask";

    private String testoRicerca;
    private Context context;
    private Set<Profilo> profiliRisultanti;


    InputStream is = null;
    String result, stringaFinale = "";
    String idProfiles, idCovers;

    public RicercaUtenteTask(Context context, String testo){
        testoRicerca = testo;
        this.context = context;
        profiliRisultanti = new HashSet<Profilo>();
    }



    protected Set<Profilo> doInBackground(Void... params) {


        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("ricerca", testoRicerca));

        try {
            if (InternetConnection.haveInternetConnection(context)) {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS);
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
                                String nomeUtente = json_data.getString("nome");
                                String cognomeUtente = json_data.getString("cognome");
                                String emailUtente = json_data.getString("email");
                                String username = json_data.getString("username");
                                String sesso = json_data.getString("sesso");
                                String dataNascita = json_data.getString("dataNascita");
                                String lavoro = json_data.getString("lavoro");
                                String nazionalita = json_data.getString("nazionalita");
                                String descrizione = json_data.getString("descrizione");
                                String tipo = json_data.getString("tipo");
                                String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                if(urlImmagineProfilo.equals("null")){
                                    idProfiles = null;
                                }
                                else {
                                    idProfiles = urlImmagineProfilo;
                                }

                                if (urlImmagineCopertina.equals("null")){
                                    idCovers = null;
                                }
                                else{
                                    idCovers = urlImmagineCopertina;
                                }

                                Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, dataNascita, nazionalita, sesso, username, lavoro, descrizione, tipo,idProfiles,idCovers);
                                profiliRisultanti.add(p);
                            }
                        }


                    } catch (Exception e) {
                        Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                }
                else {
                    Toast.makeText(context, "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }

            }
            else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.toString(),e.getMessage());
        }
        return profiliRisultanti;
    }



}

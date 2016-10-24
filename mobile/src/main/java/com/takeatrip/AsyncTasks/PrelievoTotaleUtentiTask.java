package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.takeatrip.Classes.Profilo;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lucagiacomelli on 28/04/16.
 */

public class PrelievoTotaleUtentiTask extends AsyncTask<Void, Void, Set<Profilo>> {


    private final String ADDRESS = "QueryNomiUtenti.php";
    private final static String TAG = "PrelievoTotTask";

    InputStream is = null;
    String result, stringaFinale = "";
    String idProfiles, idCovers;

    private Context context;
    private Set<Profilo> profiles;

    public PrelievoTotaleUtentiTask(Context context){
        this.context = context;
        profiles = new HashSet<Profilo>();
    }


    @Override
    protected Set<Profilo> doInBackground(Void... params) {
        try {
            if (InternetConnection.haveInternetConnection(context)) {
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

                                Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente, null, null, sesso, username, null, null, null,idProfiles,idCovers);
                                profiles.add(p);
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
        return profiles;
    }

    @Override
    protected void onPostExecute(Set<Profilo> result) {
        super.onPostExecute(result);
    }
}
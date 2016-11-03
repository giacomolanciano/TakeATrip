package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.takeatrip.Classes.Following;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Interfaces.AsyncResponseFollowers;
import com.takeatrip.Utilities.Constants;

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

/**
 * Created by lucagiacomelli on 03/11/16.
 */

public class MyTaskFollowers extends AsyncTask<Void, Void, Void> {

    private final String ADDRESS_PRELIEVO_FOLLOWING = "PrendiFollowing.php";
    private static final String TAG = "TaskFollowers";

    InputStream is = null;
    String stringaFinale = "";
    public AsyncResponseFollowers delegate = null;

    private Context context;
    private String email;
    private Profilo profiloCorrente;
    private ArrayList<Following> following;



    public MyTaskFollowers(Context context, String email, Profilo profiloCorrente){
        this.context = context;
        this.email = email;
        this.profiloCorrente = profiloCorrente;
        following = new ArrayList<Following>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("email", email));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_PRELIEVO_FOLLOWING);
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

                    if (result.equals("null\n")) {
                        stringaFinale = "Non sono presenti followers";
                        Log.i(TAG, "result da Followers: " + stringaFinale);
                    } else {
                        JSONArray jArray = new JSONArray(result);

                        if (jArray != null && result != null) {
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                String emailSeguito = json_data.getString("email").toString();
                                String nomeUtente = json_data.getString("nome");
                                String cognomeUtente = json_data.getString("cognome");
                                String username = json_data.getString("username");
                                String sesso = json_data.getString("sesso");
                                String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                String dataNascita = json_data.getString("dataNascita");
                                String lavoro = json_data.getString("lavoro");
                                String nazionalita = json_data.getString("nazionalita");
                                String descrizione = json_data.getString("descrizione");
                                String tipo = json_data.getString("tipo");
                                Profilo seguace = new Profilo(emailSeguito, nomeUtente, cognomeUtente, dataNascita, nazionalita, sesso, username, lavoro, descrizione, tipo,urlImmagineProfilo,urlImmagineCopertina);
                                following.add(new Following(seguace,profiloCorrente));
                            }
                        }

                        for (int i = 0; i < following.size(); i++) {
                            Log.i(TAG, "followers : " + following.get(i).toString());
                        }
                    }


                } catch (Exception e) {
                    Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                }
            } else {
                Log.e(TAG, "Input Stream uguale a null");
            }

        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http " + e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        delegate.processFinishForFollowers(following);
        super.onPostExecute(aVoid);
    }
}
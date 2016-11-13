package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.takeatrip.Classes.Profilo;
import com.takeatrip.Interfaces.AsyncResponseLogin;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;

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
 * Created by lucagiacomelli on 10/04/16.
 */

public class LoginTask extends AsyncTask<Void, Void, Profilo> {

    private static final String TAG = "TEST LoginTask";

    private final String ADDRESS_VERIFICA_LOGIN = "VerificaLogin.php";


    public AsyncResponseLogin delegate = null;

    InputStream is = null;
    String result, stringaFinale = "";
    String email, password, nome,cognome,data,nazionalita,sesso,lavoro,username,descrizione,tipo;
    Context context;
    Profilo profilo;
    private String emailProfilo;

    public LoginTask(Context context, String email, String password){
        this.context = context;
        this.email = email;
        this.password = password;
    }


    @Override
    protected Profilo doInBackground(Void... params) {

        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("email", email));
        dataToSend.add(new BasicNameValuePair("password", password));

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");
                Log.i(TAG, "verifico se è presente l'utente: " + email + " " + password);


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_VERIFICA_LOGIN);
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
                        if(result!= null && !result.equals("null\n")){
                            JSONArray jArray = new JSONArray(result);
                            for(int i=0;i<jArray.length();i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                if(json_data != null){
                                    stringaFinale = json_data.getString("email").toString() + " " + json_data.getString("password").toString();
                                    email = json_data.getString("email").toString();
                                    emailProfilo = json_data.getString("email2").toString();
                                    nome =  json_data.getString("nome").toString();
                                    cognome = json_data.getString("cognome").toString();
                                    data = json_data.getString("dataNascita").toString();
                                    nazionalita = json_data.getString("nazionalita").toString();
                                    sesso = json_data.getString("sesso").toString();
                                    username = json_data.getString("username").toString();
                                    lavoro = json_data.getString("lavoro").toString();
                                    descrizione = json_data.getString("descrizione").toString();
                                    tipo = json_data.getString("tipo").toString();

                                    profilo = new Profilo(email, emailProfilo,nome,cognome,data,nazionalita,sesso,username,lavoro,descrizione,tipo);

                                }

                            }
                        }

                    } catch (Exception e) {
                        Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                }
                else {
                    Log.i(TAG, "Input Stream uguale a null");
                }
            }
            else
                Log.e("CONNESSIONE Internet", "Assente!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.toString(),e.getMessage());
        }

        return profilo;
    }

    @Override
    protected void onPostExecute(Profilo profilo) {
        Log.i(TAG, "profilo: " + profilo);
        delegate.processFinish(profilo);

        super.onPostExecute(profilo);

    }
}
package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.InternetConnection;
import com.google.android.gms.drive.DriveId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 24/04/16.
 */
public class InserimentoImmagineCopertinaTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST InsImmCopTask";

    private final String ADDRESS_INSERT_COVER_PROFILE = "InserimentoImmagineCopertina.php";
    InputStream is = null;
    String emailUser, result, urlImmagine;
    DriveId idFile;
    Context context;

    public InserimentoImmagineCopertinaTask(Context c, String emailUtente, DriveId id){
        context  = c;
        emailUser = emailUtente;
        idFile = id;
    }

    public InserimentoImmagineCopertinaTask(Context c, String emailUtente, DriveId id, String url){
        context  = c;
        emailUser = emailUtente;
        idFile = id;
        urlImmagine = url;
    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("email", emailUser));
        dataToSend.add(new BasicNameValuePair("id", idFile+""));
        dataToSend.add(new BasicNameValuePair("url", urlImmagine));


        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i("CONNESSIONE Internet", "Presente!");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_COVER_PROFILE);
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


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i(TAG, "risultato operazione di inserimento immagine copertina nel DB:" + result);
        super.onPostExecute(aVoid);

    }



}

package com.example.david.takeatrip.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Utilities.Constants;
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
 * Created by Giacomo Lanciano on 20/04/2016.
 */
public class InserimentoVideoTappaTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST InsImmTappaTask";
    private final static String ADDRESS_INSERT_IMAGE_STOP = "InserimentoVideoTappa.php";

    private Context context;

    InputStream is = null;
    String result, stringaFinale = "";
    String email, codiceViaggio, urlVideo, condivisione;
    DriveId idDrive;
    int ordine;


    public InserimentoVideoTappaTask(Context context, String email, String codiceViaggio, int ordine,
                                     DriveId idDrive, String urlVideo, String condivisione) {
        this.email = email;
        this.codiceViaggio = codiceViaggio;
        this.ordine = ordine;
        this.idDrive = idDrive;
        this.urlVideo = urlVideo;
        this.condivisione = condivisione;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("email", email));
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("ordine", String.valueOf(ordine)));
        dataToSend.add(new BasicNameValuePair("url", urlVideo));
        dataToSend.add(new BasicNameValuePair("condivisione", condivisione));

        try {

            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_IMAGE_STOP);
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
                        Log.i(TAG, "result: " + result);

                    } catch (Exception e) {
                        Toast.makeText(context, "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }

            } else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http " + e.toString());
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}

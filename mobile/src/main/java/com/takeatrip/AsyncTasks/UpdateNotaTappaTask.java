package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 29/10/16.
 */
public class UpdateNotaTappaTask extends AsyncTask<Void, Void, Void> {

    private static final String ADDRESS = "UpdateNotaTappa.php";
    private static final String TAG = "UpNotaTask";

    private Context context;
    private String emailProfilo, codiceViaggio, vecchiaNota, nuovaNota;
    private int ordine;

    public UpdateNotaTappaTask(Context context, String emailProfilo, String codiceViaggio,
                               int ordine, String vecchiaNota, String nuovaNota){
        this.context = context;
        this.emailProfilo = emailProfilo;
        this.codiceViaggio = codiceViaggio;
        this.ordine = ordine;
        this.vecchiaNota = vecchiaNota;
        this.nuovaNota = nuovaNota;
    }


    @Override
    protected Void doInBackground(Void... params) {


        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("emailProfilo", emailProfilo));
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("ordine", ordine+""));
        dataToSend.add(new BasicNameValuePair("vecchiaNota", vecchiaNota));
        dataToSend.add(new BasicNameValuePair("nuovaNota", nuovaNota));

        Log.i(TAG, emailProfilo+ " " + codiceViaggio + " " + ordine + " " +vecchiaNota + " " + nuovaNota);

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS);
                httppost.setEntity(new UrlEncodedFormEntity(dataToSend));

                HttpResponse response = httpclient.execute(httppost);


            } else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

    }
}

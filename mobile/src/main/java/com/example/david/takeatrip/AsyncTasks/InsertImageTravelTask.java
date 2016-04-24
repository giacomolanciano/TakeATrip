package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

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
 * Created by Giacomo Lanciano on 24/04/2016.
 */
public class InsertImageTravelTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST InsImageTravelTask";
    private static final String ADDRESS_INSERT_IMAGE_COVER_TRAVEL = "InserimentoImmagineCopertinaViaggio.php";


    InputStream is = null;
    String emailUser,codiceViaggio, result, urlImmagine;
    DriveId idFile;
    Context context;
    private Bitmap bitmapImageTravel;
    private LinearLayout layoutCopertinaViaggio;


    public InsertImageTravelTask(Context c, String emailUtente, DriveId id){
        context  = c;
        emailUser = emailUtente;
        idFile = id;
    }

    public InsertImageTravelTask(Context c, String emailUtente, String codiceViaggio, DriveId id,
                                 String url, Bitmap bitmapImageTravel, LinearLayout layoutCopertinaViaggio){
        context  = c;
        emailUser = emailUtente;
        this.codiceViaggio = codiceViaggio;
        idFile = id;
        urlImmagine = url;
        this.bitmapImageTravel = bitmapImageTravel;
        this.layoutCopertinaViaggio = layoutCopertinaViaggio;
    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("id", urlImmagine));


        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_IMAGE_COVER_TRAVEL);
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
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.toString(),e.getMessage());
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i(TAG, "risultato operazione di inserimento immagine viaggio nel DB:" + result);
        if(!result.equals("OK")){
            //upload dell'immagine
            Drawable d = new BitmapDrawable(context.getResources(), bitmapImageTravel);
            layoutCopertinaViaggio.setBackground(d);
        }

        super.onPostExecute(aVoid);

    }

}
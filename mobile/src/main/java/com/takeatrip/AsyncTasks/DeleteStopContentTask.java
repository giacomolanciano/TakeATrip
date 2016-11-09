package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.UtilS3Amazon;

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
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giacomo Lanciano on 17/08/2016.
 */
public class DeleteStopContentTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TEST DelStopContTask";

    private InputStream is = null;
    private String result;

    private Context context;
    private String emailProfilo;
    private String query;
    private String codiceViaggio;
    private String id;

    private TransferUtility transferUtility;
    private SimpleAdapter simpleAdapter;
    private List<TransferObserver> observers;
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;
    private AmazonS3Client s3;

    public DeleteStopContentTask(Context context, String query, String codiceViaggio, String id) {
        this.context = context;
        this.query = query;
        this.codiceViaggio = codiceViaggio;
        this.id = id;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }


    public DeleteStopContentTask(Context context, String query, String emailProfilo, String codiceViaggio, String id) {
        this.context = context;
        this.emailProfilo = emailProfilo;
        this.query = query;
        this.codiceViaggio = codiceViaggio;
        this.id = id;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }



    @Override
    protected Boolean doInBackground(Void... params) {


        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));

        if (query.equals(Constants.QUERY_DEL_NOTE)) {
            dataToSend.add(new BasicNameValuePair("nota", id));
            dataToSend.add(new BasicNameValuePair("emailProfilo", emailProfilo));
        } else {
            dataToSend.add(new BasicNameValuePair("url", id));
        }

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + query);
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
                        Log.i(TAG, "result: " +result);
                        //elimina contenuto da amazon s3
                        s3.deleteObject(Constants.BUCKET_TRAVELS_NAME, id);

                    } catch (Exception e) {
                        Log.e(TAG, "Errore nella connessione http "+e.toString());
                    }
                }
                else {
                    Log.e(TAG, "Errore nella connessione http ");
                }
            } else{
                Log.e(TAG,"no internet connection");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
            return false;
        }


        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);

    }
}
package com.example.david.takeatrip.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 11/03/16.
 */


public class UploadFilePHP extends AsyncTask<Void, Void, Void> {
    private final String ADDRESS_UPLOAD_IMAGE = "UploadFile.php";

    InputStream is = null;
    Context context;
    Bitmap bitmap;
    String result, path, nomeFile;

    public UploadFilePHP(Context c, Bitmap bitmap){
        context  = c;
        this.bitmap = bitmap;
    }

    public UploadFilePHP(Context c, Bitmap bitmap, String path, String nome){
        context  = c;
        this.bitmap = bitmap;
        this.path = path;
        this.nomeFile = nome;
    }


    @Override
    protected Void doInBackground(Void... params) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.QUALITY_PHOTO, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = android.util.Base64.encodeToString(byte_arr, android.util.Base64.DEFAULT);
        ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

        Log.i("TEST","parametri della post in upload file: " + path + " " + nomeFile);

        nameValuePairs.add(new BasicNameValuePair("path", path));
        nameValuePairs.add(new BasicNameValuePair("nome",nomeFile));
        nameValuePairs.add(new BasicNameValuePair("image",image_str));

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.ADDRESS_TAT+ADDRESS_UPLOAD_IMAGE);

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
                    Log.i("TEST", "result: "+ result);


                } catch (Exception e) {
                    Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                }
            }
            else {
                Log.i("TEST", "Input Stream uguale a null");
            }

        }catch(Exception e){
            Log.i("TEST","Error in http connection "+e.toString());
        }


        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
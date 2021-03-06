package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Giacomo Lanciano on 25/04/2016.
 */
public class InserimentoFiltroTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST ListaTappeAct";

    private static final String ADDRESS_INSERIMENTO_FILTRO = "InserimentoFiltro.php";


    private InputStream is = null;
    private String result, stringaFinale = "";

    private Context context;
    private String codiceViaggio, placeName;

    public InserimentoFiltroTask(Context context, String codiceViaggio, String placeName) {
        this.context = context;
        this.codiceViaggio = codiceViaggio;
        this.placeName = placeName;
    }

    @Override
    protected Void doInBackground(Void... params) {


        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("filtro", creaStringaFiltro()));
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));

        Log.i(TAG, "filtro: " + creaStringaFiltro());

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_FILTRO);
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

                    } catch (Exception e) {
                        Toast.makeText(context, "Errore nel risultato o nel convertire il risultato",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(context, "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }


            } else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if(!result.equals("OK\n")){
            Log.e(TAG, "filtro non inserito");
        }
        else{
            Log.i(TAG, "filtro inserito correttamente");

        }
        super.onPostExecute(aVoid);
    }



    private String creaStringaFiltro() {
        return placeName.toLowerCase().replaceAll(" ", "_");
    }

}
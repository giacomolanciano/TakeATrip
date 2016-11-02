package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.takeatrip.Interfaces.AsyncResponseInsertStop;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatesUtils;
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
public class InserimentoTappaTask extends AsyncTask<Void, Void, Void> {
    private static final String ADDRESS_INSERIMENTO_TAPPA = "InserimentoTappa.php";
    private static final String TAG = "InsTappaTask";

    private InputStream is = null;
    private String result;
    public AsyncResponseInsertStop delegate = null;

    private Context context;
    private String email, codiceViaggio, placeId, nome;
    private int ordine;

    public InserimentoTappaTask(Context context, String email, String codiceViaggio, int ordine, String placeId, String nome){
        this.context = context;
        this.email = email;
        this.codiceViaggio = codiceViaggio;
        this.ordine = ordine;
        this.placeId = placeId;
        this.nome = nome;
    }

    @Override
    protected Void doInBackground(Void... params) {

        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();

        dataToSend.add(new BasicNameValuePair("emailProfilo", email));
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("ordine", ""+ordine));
        dataToSend.add(new BasicNameValuePair("POI", "" + placeId));
        dataToSend.add(new BasicNameValuePair("nome", nome));


        String data = DatesUtils.getCurrentDateString();
        dataToSend.add(new BasicNameValuePair("data", ""+data));


        try {

            if (InternetConnection.haveInternetConnection(context)) {
                Log.i("CONNESSIONE Internet", "Presente!");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_TAPPA);
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
                        Log.i(TAG, "InserimentoTappaTask result: " +result);

                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel convertire il risultato");
                    }
                }
                else {
                    Log.e(TAG, "Input Stream uguale a null");
                }


            } else
                Log.e("CONNESSIONE Internet", "Assente!");
        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG, "tappa inserita correttamente");
        Toast.makeText(context, "tappa inserita correttamente", Toast.LENGTH_LONG).show();

        delegate.processFinishForInsertStop();
    }
}
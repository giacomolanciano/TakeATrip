package com.example.david.takeatrip.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.InternetConnection;

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
public class AggiornamentoDataTappaTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST AggDataTappaTask";

    private static final String ADDRESS_AGGIORNAMENTO_TAPPA = "UpdateDataTappa.php";


    private InputStream is = null;
    private String result, stringaFinale = "";

    private Context context;
    private int ordineTappa;
    private String codiceViaggio;
    private String email;
    private String dataTappa;

    private ProgressDialog mProgressDialog;


    public AggiornamentoDataTappaTask(Context context, int ordineTappa, String codiceViaggio,
                                      String email, String dataTappa) {
        this.context = context;
        this.ordineTappa = ordineTappa;
        this.codiceViaggio = codiceViaggio;
        this.email = email;
        this.dataTappa = dataTappa;
    }

    @Override
    protected Void doInBackground(Void... params) {


        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("ordine", ""+ ordineTappa));
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("emailProfilo", email));
        dataToSend.add(new BasicNameValuePair("data", dataTappa));


        Log.i(TAG, "ordine: " + ordineTappa);
        Log.i(TAG, "codiceViaggio: " + codiceViaggio);
        Log.i(TAG, "emailProfilo: " + email);
        Log.i(TAG, "dataTappa: " + dataTappa);

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");





                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_AGGIORNAMENTO_TAPPA);
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
                        Toast.makeText(context, "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
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
            Log.e(TAG, "data non aggiornata");
        }
        else{
            Log.i(TAG, "data aggiornata");

        }
        hideProgressDialog();
        super.onPostExecute(aVoid);
    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}

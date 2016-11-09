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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Giacomo Lanciano on 25/04/2016.
 */
public class InserimentoNotaTappaTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST InsNotaTappaTask";
    private static final String ADDRESS_INSERIMENTO_NOTA = "InsertStopNote.php";

    private InputStream is = null;
    private String result, stringaFinale = "";
    private Context context;
    private int ordineAux;
    private String codiceViaggio, livelloCondivisione;
    private String email;
    private List<String> noteInserite;

    public InserimentoNotaTappaTask(Context context, int ordine, String codiceViaggio, String email,
                                    String livelloCondivisione, List<String> noteInserite) {
        this.context = context;
        this.ordineAux = ordine;
        this.codiceViaggio = codiceViaggio;
        this.email = email;
        this.livelloCondivisione = livelloCondivisione;
        this.noteInserite = noteInserite;
    }

    @Override
    protected Void doInBackground(Void... params) {


        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("ordine", ordineAux+""));
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("emailProfilo", email));
        dataToSend.add(new BasicNameValuePair("livelloCondivisione", livelloCondivisione));

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");
                Log.i(TAG, "noteInserite: " + noteInserite);

                HttpClient httpclient;
                HttpPost httppost;
                HttpResponse response;
                HttpEntity entity;
                BufferedReader reader;
                StringBuilder sb;
                String line;

                for (String nota : noteInserite) {

                    String nota_modificata = nota.replace("'","''").replace("€","euro");

                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                    dataToSend.add(new BasicNameValuePair("timestamp", timestamp));
                    dataToSend.add(new BasicNameValuePair("nota", nota_modificata));



                    httpclient = new DefaultHttpClient();
                    httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_NOTA);
                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));

                    response = httpclient.execute(httppost);

                    entity = response.getEntity();

                    is = entity.getContent();

                    if (is != null) {
                        //converto la risposta in stringa
                        try {
                            reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                            sb = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            is.close();

                            result = sb.toString();
                            Log.i(TAG, "result: " + result);

                        } catch (Exception e) {
                            Toast.makeText(context, "Errore nel risultato o nel convertire il risultato",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "Input Stream uguale a null",
                                Toast.LENGTH_LONG).show();
                    }
                }

                noteInserite.clear();


            } else
                Log.e("CONNESSIONE Internet", "Assente!");
        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http " + e.toString());
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        noteInserite.clear();

        if (!result.equals("OK\n")) {
            Log.e(TAG, "note non inserite");
        } else {
            Log.i(TAG, "note inserite correttamente");

        }
        super.onPostExecute(aVoid);
    }
}
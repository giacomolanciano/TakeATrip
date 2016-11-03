package com.takeatrip.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.takeatrip.Activities.NuovoViaggioActivity;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.Interfaces.AsyncResponseTravels;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucagiacomelli on 02/11/16.
 */

public class GetViaggiTask extends AsyncTask<Void, Void, Void> {
    private static final String ADDRESS_PRELIEVO = "QueryViaggi.php";
    private static final String TAG = "GetViaggiTask";


    public AsyncResponseTravels delegate = null;
    InputStream is = null;
    private String stringaFinale = "", email;
    private Context context;
    private List<Viaggio> viaggi;
    private ProgressDialog progressDialog;


    public GetViaggiTask(Context context, String email){
        this.context = context;
        this.email = email;
        viaggi = new ArrayList<Viaggio>();
    }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_PRELIEVO);
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

                        String result = sb.toString();

                        Log.i(TAG, "result da queryViaggi: " + result);


                        if (result.equals("null\n")) {
                            stringaFinale = context.getString(R.string.NoTravels);

                        } else {
                            JSONArray jArray = new JSONArray(result);

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codiceViaggio = json_data.getString("codiceViaggio");
                                    String nomeViaggio = json_data.getString("nomeViaggio");
                                    String urlImmagineViaggio = json_data.getString("idFotoViaggio");
                                    String condivisioneDefault = json_data.getString("livelloCondivisione");

                                    viaggi.add(new Viaggio(codiceViaggio, nomeViaggio, urlImmagineViaggio, condivisioneDefault));
                                }
                            }
                        }


                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e(TAG, "Errore nella connessione http " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgressDialog();

            if (stringaFinale.equals("")) {
                delegate.processFinishForTravels(viaggi);
            } else {
                adviseNewTravel();
            }
            super.onPostExecute(aVoid);

        }

    //allert di avviso per uscita senza salvataggio
    private void adviseNewTravel() {

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.adviseNoTravel))
                .setMessage(context.getString(R.string.adviseNewTravel))
                .setPositiveButton(context.getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent openNewTravel = new Intent(context, NuovoViaggioActivity.class);
                        openNewTravel.putExtra("email", email);
                        context.startActivity(openNewTravel);
                    }
                })

                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(ContextCompat.getDrawable(context,R.drawable.logodefbordo))
                .show();
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.CaricamentoInCorso));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }
}

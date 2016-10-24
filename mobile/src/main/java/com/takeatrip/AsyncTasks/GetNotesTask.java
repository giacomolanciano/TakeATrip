package com.takeatrip.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.takeatrip.Adapters.GridViewAdapter;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.ScrollListener;

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
 * Created by Giacomo Lanciano on 28/04/2016.
 */
public class GetNotesTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST GetNotesTask";

    private Context context;
    private GridView gridView;
    private String phpFile, emailProfilo;
    private int ordineTappa;

    private String codiceViaggio;
    InputStream is = null;
    String result, stringaFinale = "";
    private List<String> listContents;
    private String[] notes;

    private ProgressDialog mProgressDialog;

    public GetNotesTask(Context context, String codiceViaggio, String emailProfilo,
                        GridView gridView, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.gridView = gridView;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        listContents = new ArrayList<String>();

    }

    public GetNotesTask(Context context, String codiceViaggio, GridView gridView, String phpFile,
                               String emailProfilo, int ordineTappa) {

        this(context, codiceViaggio, emailProfilo, gridView, phpFile);

        this.ordineTappa = ordineTappa;

    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("email", emailProfilo));

        Log.i(TAG, "codice: " + codiceViaggio);
        Log.i(TAG, "email: " + emailProfilo);


        if (phpFile.equals(Constants.QUERY_STOP_NOTES)) {

            dataToSend.add(new BasicNameValuePair("ordineTappa", ordineTappa + ""));

            Log.i(TAG, "ordineTappa: " + ordineTappa);
        }


        try {
            if (InternetConnection.haveInternetConnection(context)) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + phpFile);
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
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

                if(result != null && !result.equals("null\n")){
                    JSONArray jArray = new JSONArray(result);
                    String nota;

                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            nota = json_data.getString("nota");

                            listContents.add(nota);

                        }
                    }

                }

            } else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.toString(), e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        hideProgressDialog();

        super.onPostExecute(aVoid);

        Log.i(TAG, "BEGIN onPostExecute");

        GridView gv = gridView;
        gv.setOnScrollListener(new ScrollListener(context));

        Log.i(TAG, "listContents.size() = " + listContents.size());

        if (listContents.size() > 0) {
            //se la lista di elementi da caricare è non vuota, il linear layout parent viene visualizzato
            LinearLayout parent = (LinearLayout) gv.getParent();
            parent.setVisibility(View.VISIBLE);

            notes = new String[listContents.size()];

            notes = listContents.toArray(notes);

            if (notes[0] == null || notes[0].equals("null")) {
                return;
            }

            String debug = "";
            for (String s:notes)
                debug += s + ", ";

            Log.i(TAG, "notes: " + debug);



        } else {
            return;
        }



        gv.setAdapter(new GridViewAdapter(context, notes, Constants.NOTE_FILE, codiceViaggio));
        Log.i(TAG, "settato l'adapter per il grid");

    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}

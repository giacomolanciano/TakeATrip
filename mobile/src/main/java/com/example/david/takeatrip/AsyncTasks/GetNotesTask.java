package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.david.takeatrip.Adapters.GridViewAdapter;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.InternetConnection;
import com.example.david.takeatrip.Utilities.ScrollListener;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;

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
import java.util.HashMap;
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


    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;


    // The S3 client
    private AmazonS3Client s3;


    public GetNotesTask(Context context, String codiceViaggio, String emailProfilo,
                        GridView gridView, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.gridView = gridView;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        listContents = new ArrayList<String>();

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
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

            dataToSend.add(new BasicNameValuePair("ordine", ordineTappa + ""));

            Log.i(TAG, "ordine: " + ordineTappa);
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
        super.onPostExecute(aVoid);

        Log.i(TAG, "BEGIN onPostExecute");

        GridView gv = gridView;
        gv.setOnScrollListener(new ScrollListener(context));

        Log.i(TAG, "listContents.size() = " + listContents.size());

        if (listContents.size() > 0) {
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
            //se la lista di elementi da caricare è vuota, il linear layout parent viene nascosto
            LinearLayout parent = (LinearLayout) gv.getParent();
            parent.setVisibility(View.GONE);
            return;
        }



        gv.setAdapter(new GridViewAdapter(context, notes, Constants.NOTE_FILE));

        Log.i(TAG, "settato l'adapter per il grid");

    }

}

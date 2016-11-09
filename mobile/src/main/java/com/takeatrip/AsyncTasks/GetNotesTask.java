package com.takeatrip.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import com.takeatrip.Classes.NotaTappa;
import com.takeatrip.Interfaces.AsyncResponseNotes;
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
public class GetNotesTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TEST GetNotesTask";
    public AsyncResponseNotes delegate = null;

    private Context context;
    private ListView listView;
    private GridView gridView;
    private String phpFile, emailProfilo;
    private int ordineTappa;

    private String codiceViaggio;
    InputStream is = null;
    String result, stringaFinale = "";
    private List<NotaTappa> listContents;
    private NotaTappa[] notes;

    private ProgressDialog mProgressDialog;

    public GetNotesTask(Context context, String codiceViaggio, String emailProfilo,
                        ListView listView, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.listView = listView;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        listContents = new ArrayList<NotaTappa>();

    }

    public GetNotesTask(Context context, String codiceViaggio, ListView listView, String phpFile,
                               String emailProfilo, int ordineTappa) {
        this(context, codiceViaggio, emailProfilo, listView, phpFile);
        this.ordineTappa = ordineTappa;

    }



    @Override
    protected Boolean doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("email", emailProfilo));

        if (phpFile.equals(Constants.QUERY_STOP_NOTES)) {
            dataToSend.add(new BasicNameValuePair("ordineTappa", ordineTappa + ""));
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
                    String nota, livelloCondivisione, username, email;

                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            nota = json_data.getString("nota");
                            livelloCondivisione = json_data.getString("livelloCondivisione");
                            username = json_data.getString("username");
                            email = json_data.getString("email");
                            if(ordineTappa == 0)
                                ordineTappa = json_data.getInt("ordineTappa");
                            listContents.add(new NotaTappa(email, username, codiceViaggio, ordineTappa, livelloCondivisione, nota));
                        }
                    }

                }

            } else{
                Log.e(TAG, "no internet connection");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        hideProgressDialog();
        super.onPostExecute(aVoid);

        ListView lv = listView;
        GridView gv = gridView;
        if(gv != null)
        gv.setOnScrollListener(new ScrollListener(context));

        Log.i(TAG, "listContents.size() = " + listContents.size());
        notes = new NotaTappa[listContents.size()];
        notes = listContents.toArray(notes);
        if(aVoid)
            delegate.processFinishForNotes(notes);
        else
            delegate.processFinishForNotes(null);
        return;

    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
        }
    }

}

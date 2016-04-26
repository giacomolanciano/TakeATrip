package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.david.takeatrip.Classes.Profilo;
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

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Giacomo Lanciano on 26/04/2016.
 */
public class ItinerariesTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST ItinerariesTask";

    private static final String ADDRESS_INSERIMENTO_ITINERARIO = "InserimentoItinerario.php";


    private InputStream is = null;
    private String result, stringaFinale = "";

    private Profilo profilo;
    private Context context;
    private String codiceViaggio;
    private String nameForUrl;

    public ItinerariesTask(Context context, Profilo profilo, String codiceViaggio, String nameForUrl) {
        this.profilo = profilo;
        this.context = context;
        this.codiceViaggio = codiceViaggio;
        this.nameForUrl = nameForUrl;
    }



    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("email", profilo.getEmail()));

        String url = profilo.getEmail()+"/"+ nameForUrl;

        Log.i(TAG, "url della cartella del nuovo partecipante: " + url);
        dataToSend.add(new BasicNameValuePair("urlCartella",url));
        dataToSend.add(new BasicNameValuePair("nomeCartella", nameForUrl));

        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_INSERIMENTO_ITINERARIO);
                httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();

                is = entity.getContent();

            }
            else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString()+ ": " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
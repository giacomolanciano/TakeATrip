package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.takeatrip.Adapters.GridViewAdapter;
import com.takeatrip.Adapters.GridViewImageAdapter;
import com.takeatrip.Classes.ContenutoMultimediale;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by Giacomo Lanciano on 20/04/2016.
 */
public class GetUrlsContentsTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST GetUrlsContTask";

    private Context context;
    private GridView gridView;
    private String phpFile, emailProfilo;
    private int ordineTappa;

    private String codiceViaggio;
    InputStream is = null;
    String result, stringaFinale = "";
    private List<ContenutoMultimediale> listContents;
    private String[] URLs;

    private ImageView coverImageTappa;


    public GetUrlsContentsTask(Context context, String codiceViaggio, String emailProfilo,
                               GridView gridView, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.gridView = gridView;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        listContents = new ArrayList<ContenutoMultimediale>();

    }

    public GetUrlsContentsTask(Context context, String codiceViaggio, GridView gridView, String phpFile,
                               String emailProfilo, int ordineTappa) {
        this(context, codiceViaggio, emailProfilo, gridView, phpFile);
        this.ordineTappa = ordineTappa;

    }

    public GetUrlsContentsTask(Context context, String codiceViaggio, GridView gridView, String phpFile,
                               String emailProfilo, int ordineTappa, ImageView coverImageTappa) {

        this(context, codiceViaggio, gridView, phpFile, emailProfilo, ordineTappa);
        this.coverImageTappa = coverImageTappa;
    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("email", emailProfilo));

        if (phpFile.equals(Constants.QUERY_STOP_IMAGES)
                || phpFile.equals(Constants.QUERY_STOP_VIDEOS)
                || phpFile.equals(Constants.QUERY_STOP_AUDIO)) {

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

                        Log.i(TAG, "result: " + result);

                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

                if (result != null && !result.equals("null\n")) {
                    JSONArray jArray = new JSONArray(result);
                    String url, livelloCondivisione;

                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            if (phpFile.equals(Constants.QUERY_TRAVEL_IMAGES)
                                    || phpFile.equals(Constants.QUERY_STOP_IMAGES)) {

                                url = json_data.getString("urlImmagineViaggio");

                            } else if (phpFile.equals(Constants.QUERY_TRAVEL_VIDEOS)
                                    || phpFile.equals(Constants.QUERY_STOP_VIDEOS)) {
                                url = json_data.getString("urlVideo");
                            } else {
                                url = json_data.getString("urlAudio");
                            }


                            livelloCondivisione = json_data.getString("livelloCondivisione");
                            listContents.add(new ContenutoMultimediale(url, livelloCondivisione));

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

        GridView gv = gridView;
        gv.setOnScrollListener(new ScrollListener(context));

        Log.i(TAG, "listContents.size() = " + listContents.size());

        if (listContents.size() > 0) {
            //se la lista di elementi da caricare è non vuota, il linear layout parent viene visualizzato
            LinearLayout parent = (LinearLayout) gv.getParent();
            parent.setVisibility(View.VISIBLE);

            URLs = new String[listContents.size()];
            int i = 0;
            for (ContenutoMultimediale image : listContents) {

                //TODO la query restituisce già solamente i contenuti public/travel, valutare eliminazione controllo
                if (image.getLivelloCondivisione().equalsIgnoreCase("public")
                        || image.getLivelloCondivisione().equalsIgnoreCase("travel")) {

                    //NOTA: la traduzione dell'id del contenuto in url viene delegata al GridViewAdapter,
                    //per facilitare l'eliminazione del contenuto

                    URLs[i] = image.getUrlContenuto();

                    //Log.i(TAG, "url ["+i+"]: "+ URLs[i]);

                    i++;
                }
            }
            if (URLs[0] == null || URLs[0].equals("null")) {
                return;
            }

            String debug = "";
            for (String s:URLs)
                debug += s + ", ";

            Log.i(TAG, "URLs: " + debug);

        } else {
            return;
        }


        if (phpFile.equals(Constants.QUERY_TRAVEL_IMAGES)
                || phpFile.equals(Constants.QUERY_STOP_IMAGES)) {

            gv.setAdapter(new GridViewImageAdapter(context, URLs, Constants.IMAGE_FILE, codiceViaggio));

            //nel caso di immagini della tappa, la prima viene impostata come copertina
            if(phpFile.equals(Constants.QUERY_STOP_IMAGES) && coverImageTappa != null) {
                Bitmap bitmap  = null;
                try {
                    bitmap = new BitmapWorkerTask(coverImageTappa).execute(URLs[0]).get();
                    coverImageTappa.setImageBitmap(bitmap);

                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

        } else if (phpFile.equals(Constants.QUERY_TRAVEL_VIDEOS)
                || phpFile.equals(Constants.QUERY_STOP_VIDEOS)) {

            gv.setAdapter(new GridViewAdapter(context, URLs, Constants.VIDEO_FILE, codiceViaggio));
        } else {
            gv.setAdapter(new GridViewAdapter(context, URLs, Constants.AUDIO_FILE, codiceViaggio));
        }


        Log.i(TAG, "settato l'adapter per il grid");

    }


}

package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.takeatrip.Activities.ViaggioActivityConFragment;
import com.takeatrip.Adapters.GridViewAdapter;
import com.takeatrip.Adapters.GridViewImageAdapter;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.Interfaces.AsyncResponseVideos;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.ScrollListener;
import com.takeatrip.Utilities.UtilS3Amazon;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by Giacomo Lanciano on 20/04/2016.
 */
public class GetUrlsContentsTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TEST GetUrlsContTask";

    private Context context;
    private GridView gridView;
    private String phpFile, emailProfilo, emailProprietarioTappa;
    private int ordineTappa;

    private String codiceViaggio;
    InputStream is = null;
    String result, stringaFinale = "";
    private List<ContenutoMultimediale> listContents;
    private List<ContenutoMultimediale> URLs;

    private ImageView coverImageTappa;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    private SimpleAdapter simpleAdapter;
    private List<TransferObserver> observers;
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;
    private AmazonS3Client s3;

    private ListView listViewVideos;
    public AsyncResponseVideos delegate = null;


    //Task for contents of the whole travel
    public GetUrlsContentsTask(Context context, String codiceViaggio, String emailProfilo,
                               GridView gridView, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.gridView = gridView;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        listContents = new ArrayList<ContenutoMultimediale>();

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);

    }

    //Task for contents of the videos of travel
    public GetUrlsContentsTask(ViaggioActivityConFragment context, String codiceViaggio, String emailProfilo, ListView listViewVideos, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        this.listViewVideos = listViewVideos;
        listContents = new ArrayList<ContenutoMultimediale>();

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }





    //Task for contents of a particular stop
    public GetUrlsContentsTask(Context context, String codiceViaggio, GridView gridView, String phpFile,
                               String emailProfilo, String emailProprietarioTappa, int ordineTappa, ImageView coverImageTappa) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.gridView = gridView;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        this.emailProprietarioTappa = emailProprietarioTappa;
        this.coverImageTappa = coverImageTappa;
        this.ordineTappa = ordineTappa;

        listContents = new ArrayList<ContenutoMultimediale>();

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }


    public GetUrlsContentsTask(Context context, String codiceViaggio, ListView listViewVideos, String phpFile,
                               String emailProfilo, String emailProprietarioTappa, int ordineTappa, ImageView coverImageTappa) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.phpFile = phpFile;
        this.emailProfilo = emailProfilo;
        this.emailProprietarioTappa = emailProprietarioTappa;
        this.coverImageTappa = coverImageTappa;
        this.listViewVideos = listViewVideos;
        listContents = new ArrayList<ContenutoMultimediale>();
        this.ordineTappa = ordineTappa;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
        dataToSend.add(new BasicNameValuePair("email", emailProfilo));

        if (phpFile.equals(Constants.QUERY_STOP_IMAGES)
                || phpFile.equals(Constants.QUERY_STOP_VIDEOS)
                || phpFile.equals(Constants.QUERY_STOP_AUDIO)) {
            dataToSend.add(new BasicNameValuePair("emailProprietarioTappa", emailProprietarioTappa));
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

                if (result != null && !result.equals("null\n")) {
                    JSONArray jArray = new JSONArray(result);
                    String emailProfilo, url, livelloCondivisione;
                    int ordineTappa = 0;



                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            emailProfilo = json_data.getString("emailProfilo");

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

                            ordineTappa = json_data.getInt("ordineTappa");

                            listContents.add(new ContenutoMultimediale(emailProfilo,url, codiceViaggio, ordineTappa, livelloCondivisione));
                        }
                    }

                }

            } else{
                Log.e(TAG, "CONNESSIONE Internet Assente!");
                return false;
            }
        } catch (Exception e) {
            Log.e(e.toString(), e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);

        if(aVoid){

            GridView gv = gridView;
            if(gv != null) {
                gv.setOnScrollListener(new ScrollListener(context));
            }

            if (listContents.size() > 0) {
                //se la lista di elementi da caricare è non vuota, il linear layout parent viene visualizzato

                if(gv != null){
                    LinearLayout parent = (LinearLayout) gv.getParent();
                    parent.setVisibility(View.VISIBLE);
                }

                URLs = new ArrayList<ContenutoMultimediale>();
                int i = 0;
                for (ContenutoMultimediale image : listContents) {
                    URLs.add(image);
                    i++;
                }
                if (URLs.get(0) == null || URLs.get(0).equals("null")) {
                    return;
                }

            } else {
                return;
            }


            if (phpFile.equals(Constants.QUERY_TRAVEL_IMAGES)
                    || phpFile.equals(Constants.QUERY_STOP_IMAGES)) {
                Log.i(TAG, "immagini scaricate: " + URLs);

                GridViewImageAdapter adapter = new GridViewImageAdapter(context, gv, URLs, Constants.IMAGE_FILE, codiceViaggio, emailProfilo);
                gv.setAdapter(adapter);

                //nel caso di immagini della tappa, la prima viene impostata come copertina
                if(phpFile.equals(Constants.QUERY_STOP_IMAGES) && coverImageTappa != null) {
                    Bitmap bitmap  = null;
                    try {
                        final String url = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_TRAVELS_NAME,URLs.get(0).getUrlContenuto());
                        bitmap = new BitmapWorkerTask(coverImageTappa).execute(url).get();
                        if(bitmap != null)
                            coverImageTappa.setImageBitmap(getScaledBitmap(bitmap));

                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

            }
            else if (phpFile.equals(Constants.QUERY_TRAVEL_VIDEOS) || phpFile.equals(Constants.QUERY_STOP_VIDEOS)){
                Log.i(TAG,"video caricati: " + URLs);
                delegate.processFinishForVideos(URLs);
            }
            else {
                gv.setAdapter(new GridViewAdapter(context, gv, URLs, Constants.AUDIO_FILE, codiceViaggio));
            }

        }
        else{
            Toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
        }



    }



    private Bitmap getScaledBitmap(Bitmap bitmap){
        float density = context.getResources().getDisplayMetrics().density;
        int heigh = 300;
        if(density == 3.0 || density == 4.0){
            heigh = 600;
        }
        return bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), heigh, false);
    }

}

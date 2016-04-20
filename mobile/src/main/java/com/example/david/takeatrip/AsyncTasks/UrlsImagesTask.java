package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.david.takeatrip.Adapters.GridViewAdapter;
import com.example.david.takeatrip.Classes.Immagine;
import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Utilities.Constants;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giacomo Lanciano on 20/04/2016.
 */
public class UrlsImagesTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST UrlsImagesTask";

    private Context context;
    private GridView gridView;
    private String phpFile, emailProfilo;
    private int ordineTappa;

    private String codiceViaggio;
    InputStream is = null;
    String result, stringaFinale = "";
    private List<Immagine> listImages;
    private String[] URLs;


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


    public UrlsImagesTask(Context context, String codiceViaggio, GridView gridView, String phpFile) {
        this.codiceViaggio = codiceViaggio;
        this.context = context;
        this.gridView = gridView;
        this.phpFile = phpFile;
        listImages = new ArrayList<Immagine>();

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }

    public UrlsImagesTask(Context context, String codiceViaggio, GridView gridView, String phpFile,
                          String emailProfilo, int ordineTappa) {

        this(context, codiceViaggio, gridView, phpFile);

        this.emailProfilo = emailProfilo;
        this.ordineTappa = ordineTappa;

    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));

        Log.i(TAG, "codice: " + codiceViaggio);


        if (phpFile.equals(Constants.QUERY_STOP_IMAGE)) {
            dataToSend.add(new BasicNameValuePair("ordine", ordineTappa + ""));
            dataToSend.add(new BasicNameValuePair("email", emailProfilo));

            Log.i(TAG, "ordine: " + ordineTappa);
            Log.i(TAG, "email: " + emailProfilo);

        }


        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");
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
                        Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.i(TAG, "Input Stream uguale a null");
                }

                JSONArray jArray = new JSONArray(result);

                if (jArray != null && result != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        String urlImmagine = json_data.getString("urlImmagineViaggio");
                        int orineTappa = json_data.getInt("ordineTappa");
                        String livelloCondivisione = json_data.getString("livelloCondivisione");
                        listImages.add(new Immagine(urlImmagine, livelloCondivisione));

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

        Log.i(TAG, "array di url: ");


        //TODO: controllare i livelli di condivisione e mettere nell'array solo quelle giuste
        if (listImages.size() > 0) {
            URLs = new String[listImages.size()];
            int i = 0;
            for (Immagine image : listImages) {
                if (image.getLivelloCondivisione().equalsIgnoreCase("public")
                        || image.getLivelloCondivisione().equalsIgnoreCase("travel")) {

                    URLs[i] = beginDownloadFile(image.getUrlImmagine());

                    //Log.i(TAG, "url ["+i+"]: "+ URLs[i]);

                    i++;
                }
            }
            if (URLs[0] == null || URLs[0].equals("null")) {
                return;
            }
        } else {
            return;
        }


        GridView gv = gridView;
        gv.setAdapter(new GridViewAdapter(context, URLs));
        gv.setOnScrollListener(new ScrollListener(context));

        Log.i(TAG, "settato l'adapter per il grid");


//            ImageGridFragment fragment = (ImageGridFragment)getFragmentManager().findFragmentById(R.id.fragment_images);
//
//            ImageGridFragment fragment1 = fragment.newInstance(URLs);
//
//            //fragment.setArguments(fragment1.getArguments());
//
//            fragment.onDestroy();
//
//            Log.i(TAG, "creato un nuovo fragment with bundle: " + fragment1.getArguments().getStringArray("urls"));
//
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_images, fragment1);
//            transaction.addToBackStack(null);
//            transaction.commit();


    }


    private String beginDownloadFile(String key) {
        // Location to download files from S3 to. You can choose any accessible
        // file.


        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += Constants.ONE_HOUR_IN_MILLISEC; // 1 hour.
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(Constants.BUCKET_TRAVELS_NAME, key);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(expiration);

        Log.i(TAG, "expiration date image: " + generatePresignedUrlRequest.getExpiration());

        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);


        // Initiate the download
        //TransferObserver observer = transferUtility.download(email, key, file);
        //Log.i(TAG, "downloaded file: " + file);
        //Log.i(TAG, "key file: " + key);

        //Log.i(TAG, "url file: " + url);

        //observer.setTransferListener(new DownloadListener());


        return url.toString();

    }


}




package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.takeatrip.Interfaces.AsyncResponseUrl;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.UtilS3Amazon;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giacomo Lanciano on 19/04/2016.
 */
public class LoadGenericImageTask extends AsyncTask<Void, Void, URL>  {

    private static final String TAG = "TEST LoadGenImgTask";

    //private ImageView immagineViaggio;
    private String urlImmagine, codiceViaggio;

    private Context context;

    private AsyncResponseUrl delegate = null;

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

    public LoadGenericImageTask(String urlImmagine, String codiceViaggio, Context context) {
        this.urlImmagine = urlImmagine;
        this.codiceViaggio = codiceViaggio;
        this.context = context;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);

    }

    public LoadGenericImageTask(String urlImmagine, Context context) {
        this.urlImmagine = urlImmagine;
        this.context = context;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);

    }


    @Override
    protected URL doInBackground(Void... params) {

        URL url = null;
        try {

            if (InternetConnection.haveInternetConnection(context)) {
                java.util.Date expiration = new java.util.Date();
                long msec = expiration.getTime();
                msec += Constants.ONE_HOUR_IN_MILLISEC;
                expiration.setTime(msec);

                if(codiceViaggio != null){
                    GeneratePresignedUrlRequest generatePresignedUrlRequest =
                            new GeneratePresignedUrlRequest(Constants.BUCKET_TRAVELS_NAME,codiceViaggio
                                    + "/" +Constants.TRAVEL_COVER_IMAGE_LOCATION+"/"+urlImmagine);
                    generatePresignedUrlRequest.setMethod(HttpMethod.GET);
                    generatePresignedUrlRequest.setExpiration(expiration);

                    url = s3.generatePresignedUrl(generatePresignedUrlRequest);
                }
                else{
                    GeneratePresignedUrlRequest generatePresignedUrlRequest =
                            new GeneratePresignedUrlRequest(Constants.BUCKET_NAME, urlImmagine);
                    generatePresignedUrlRequest.setMethod(HttpMethod.GET);
                    generatePresignedUrlRequest.setExpiration(expiration);
                    url = s3.generatePresignedUrl(generatePresignedUrlRequest);
                }




            } else
                Log.e("CONNESSIONE Internet", "Assente!");

        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
        }


        return url;
    }

    @Override
    protected void onPostExecute(URL url) {
        //delegate.processFinish(url);
        super.onPostExecute(url);

    }
}

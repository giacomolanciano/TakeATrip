package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Interfaces.AsyncResponseUrl;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giacomo Lanciano on 19/04/2016.
 */
public class LoadTravelImageTask extends AsyncTask<Void, Void, URL>  {

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

    public LoadTravelImageTask(String urlImmagine, String codiceViaggio, Context context) {
        this.urlImmagine = urlImmagine;
        this.codiceViaggio = codiceViaggio;
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
                Log.i("CONNESSIONE Internet", "Presente!");


                java.util.Date expiration = new java.util.Date();
                long msec = expiration.getTime();
                msec += Constants.ONE_HOUR_IN_MILLISEC;
                expiration.setTime(msec);

                GeneratePresignedUrlRequest generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(Constants.BUCKET_TRAVELS_NAME,codiceViaggio
                                + "/" +Constants.TRAVEL_COVER_IMAGE_LOCATION+"/"+urlImmagine);
                generatePresignedUrlRequest.setMethod(HttpMethod.GET);
                generatePresignedUrlRequest.setExpiration(expiration);

                Log.i("TEST", "expiration date image: " + generatePresignedUrlRequest.getExpiration());


                url = s3.generatePresignedUrl(generatePresignedUrlRequest);


                Log.i("TEST", "url file: " + url);



            } else
                Log.e("CONNESSIONE Internet", "Assente!");

        } catch (Exception e) {
            Log.e("TEST", "Errore nella connessione http "+e.toString());
        }


        return url;
    }

    @Override
    protected void onPostExecute(URL url) {

        //delegate.processFinish(url);


        super.onPostExecute(url);

    }
}
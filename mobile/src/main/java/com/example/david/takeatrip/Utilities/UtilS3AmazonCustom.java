package com.example.david.takeatrip.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.david.takeatrip.AsyncTasks.InsertImageTravelTask;

import java.io.File;
import java.net.URL;

/**
 * Created by Giacomo Lanciano on 24/04/2016.
 */
public class UtilS3AmazonCustom {

    private static final String TAG = "TEST UtilS3AmazonCustom";

    public static String getS3FileURL(AmazonS3Client s3, String bucket, String key) {
        // Location to download files from S3 to. You can choose any accessible
        // file.

        URL url = null;

        try {
            java.util.Date expiration = new java.util.Date();
            long msec = expiration.getTime();
            msec += Constants.ONE_HOUR_IN_MILLISEC; // 1 hour.
            expiration.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucket,key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);

            url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }


        return url.toString();

    }


    public static void uploadTravelPicture(Context context, TransferUtility transferUtility,
                                          String filePath, String codiceViaggio, String email,
                                          Bitmap bitmapImageTravel, LinearLayout layoutCopertinaViaggio) {

        if (filePath == null) {
            Toast.makeText(context, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);

        ObjectMetadata myObjectMetadata = new ObjectMetadata();

        TransferObserver observer = transferUtility.upload("takeatriptravels", codiceViaggio + "/" +
                "coverTravelImages" +"/"+file.getName(), file);


        Log.i(TAG, "inserimento nel DB del path: " +  codiceViaggio + "/" + "coverTravelImages" +"/"+file.getName());

        new InsertImageTravelTask(context,email,codiceViaggio, null, file.getName(),
                bitmapImageTravel, layoutCopertinaViaggio).execute();

        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUploadProfilePicture -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }




}

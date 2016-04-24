package com.example.david.takeatrip.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.LinearLayout;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.david.takeatrip.AsyncTasks.InsertImageTravelTask;
import com.example.david.takeatrip.AsyncTasks.UploadFileS3Task;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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


    public static void uploadTravelCoverPicture(Context context, String filePath, String codiceViaggio,
                                                String email, Bitmap bitmapImageTravel,
                                                LinearLayout layoutCopertinaViaggio) {

        Log.i(TAG, "enter uploadTravelCoverPicture(...)");

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());
        String newFileName = timeStamp + Constants.IMAGE_EXT;

        new UploadFileS3Task(context, Constants.BUCKET_TRAVELS_NAME, codiceViaggio,
                Constants.TRAVEL_COVER_IMAGE_LOCATION, email, filePath, newFileName);

        new InsertImageTravelTask(context,email,codiceViaggio, null, newFileName,
                bitmapImageTravel, layoutCopertinaViaggio).execute();
    }




}

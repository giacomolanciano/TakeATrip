package com.takeatrip.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.takeatrip.AsyncTasks.InsertCoverImageTravelTask;
import com.takeatrip.AsyncTasks.UploadFileS3Task;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Giacomo Lanciano on 24/04/2016.
 */
public class UtilS3AmazonCustom {

    private static final String TAG = "TEST UtilS3AmazonCustom";

    public static String getS3FileURL(Context context, AmazonS3Client s3, String bucket, String key) {
        // Location to download files from S3 to. You can choose any accessible
        // file.

        if(s3 == null){
            s3 = UtilS3Amazon.getS3Client(context);
        }

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
            Log.e(TAG, "thrown exception "+exception + " " + exception.getMessage() );
            //exception.printStackTrace();
        }

        if(url != null)
            return url.toString();

        return null;
    }


    public static void uploadTravelCoverPicture(Context context, String filePath, String codiceViaggio,
                                                String email, Bitmap bitmapImageTravel,
                                                //LinearLayout layoutCopertinaViaggio) {
                                                ImageView layoutCopertinaViaggio, Uri selectedImage) {


        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());
        String newFileName = timeStamp + Constants.IMAGE_EXT;

        try {
            boolean result = new UploadFileS3Task(context, Constants.BUCKET_TRAVELS_NAME, codiceViaggio,
                    Constants.TRAVEL_COVER_IMAGE_LOCATION, email, filePath, newFileName).execute().get();

            if(result){
                new InsertCoverImageTravelTask(context,email,codiceViaggio, null, newFileName,
                        bitmapImageTravel, layoutCopertinaViaggio, selectedImage).execute();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    public static void deleteObjectsInFolder(AmazonS3Client s3, String bucketName, String folderPath) {
        for (S3ObjectSummary file : s3.listObjects(bucketName, folderPath).getObjectSummaries()){
            s3.deleteObject(bucketName, file.getKey());
        }
    }


}

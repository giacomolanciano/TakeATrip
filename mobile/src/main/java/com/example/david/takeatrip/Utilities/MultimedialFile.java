package com.example.david.takeatrip.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Giacomo Lanciano on 06/03/2016.
 */
public class MultimedialFile {

    private static final String TAG = "TEST MultimedialFile";


    public static File createImageFile() throws IOException {

        String mCurrentMediaPath, mediaFileName;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());



        mediaFileName = timeStamp + Constants.IMAGE_EXT;


        File mediaFile = new File(DeviceStorageUtils.getImagesStoragePath(), mediaFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMediaPath = mediaFile.getAbsolutePath();

        Log.i(TAG, "path media file: " + mCurrentMediaPath);

        return mediaFile;

    }

    public static File createVideoFile() throws IOException {


        String mCurrentMediaPath, mediaFileName;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());



        mediaFileName = timeStamp + Constants.VIDEO_EXT;


        File mediaFile = new File(DeviceStorageUtils.getVideosStoragePath(), mediaFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMediaPath = mediaFile.getAbsolutePath();

        Log.i(TAG, "path media file: " + mCurrentMediaPath);

        return mediaFile;

    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {

        Log.i(TAG, "entro in getRealPathFromURI(...)");

        Cursor cursor = null;
        String result = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor
                        //.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        .getColumnIndex(proj[0]);

                result = cursor.getString(columnIndex);
                Log.i(TAG, "result: "+result);
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, "eccezione nel restituire il path: "+e.toString());
            return null;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}

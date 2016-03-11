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

    public static File createMediaFile(int tipoFile, String mCurrentMediaPath,
                                       String mediaFileName) throws IOException {


        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


        if (tipoFile == Constants.IMAGE_FILE) {
            mediaFileName = timeStamp + ".jpg";
        } else {    //if (tipoFile == Constants.VIDEO_FILE) {
            mediaFileName = timeStamp + ".3gp";
        }

        File mediaFile = new File(android.os.Environment.getExternalStorageDirectory(), mediaFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMediaPath = mediaFile.getAbsolutePath();

        Log.i("TEST", "path media file: " + mCurrentMediaPath);

        return mediaFile;

    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {

        Log.i("TEST", "entro in getRealPathFromURI(...)");

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
                Log.i("TEST", "result: "+result);
            }

            return result;

        } catch (Exception e) {
            Log.e("TEST", "eccezione nel restituire il path: "+e.toString());
            return null;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}

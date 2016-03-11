package com.example.david.takeatrip.Utilities;

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

}

package com.example.david.takeatrip.Utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Giacomo Lanciano on 25/04/2016.
 */
public class DeviceStorageUtils {

    private static final String TAG = "TEST DeviceStorageUtils";

    public static String getRootStoragePath() {
        return Environment.getExternalStorageDirectory() + "/" + Constants.DEVICE_DIR_ROOT;
    }

    public static String getImagesStoragePath() {
        return getRootStoragePath() + "/" + Constants.DEVICE_DIR_IMAGES;
    }

    public static String getVideosStoragePath() {
        return getRootStoragePath() + "/" + Constants.DEVICE_DIR_VIDEOS;
    }

    public static String getAudioStoragePath() {
        return getRootStoragePath() + "/" + Constants.DEVICE_DIR_AUDIO;
    }

    public static void createExternalStorageDirectories() {
        boolean successo;
        File directory;
        String rootPath = DeviceStorageUtils.getRootStoragePath();

        Log.i(TAG, "rootPath: " + rootPath);

        directory = new File(rootPath, Constants.DEVICE_DIR_IMAGES);
        if (!directory.exists()) {
            successo = directory.mkdirs();

            if(successo)
                Log.i(TAG, Constants.DEVICE_DIR_IMAGES + " creata con successo");
            else
                Log.e(TAG, "creazione " + Constants.DEVICE_DIR_IMAGES + " fallita");
        }


        directory = new File(rootPath, Constants.DEVICE_DIR_VIDEOS);
        if (!directory.exists()) {
            successo = directory.mkdirs();

            if(successo)
                Log.i(TAG, Constants.DEVICE_DIR_VIDEOS + " creata con successo");
            else
                Log.e(TAG, "creazione " + Constants.DEVICE_DIR_VIDEOS + " fallita");
        }

        directory = new File(rootPath, Constants.DEVICE_DIR_AUDIO);
        if (!directory.exists()) {
            successo = directory.mkdirs();

            if(successo)
                Log.i(TAG, Constants.DEVICE_DIR_AUDIO + " creata con successo");
            else
                Log.e(TAG, "creazione " + Constants.DEVICE_DIR_AUDIO + " fallita");
        }


    }

}

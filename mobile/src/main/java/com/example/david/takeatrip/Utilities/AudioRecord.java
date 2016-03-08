package com.example.david.takeatrip.Utilities;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Giacomo Lanciano on 08/03/2016.
 */
public class AudioRecord  {

    private static final String LOG_TAG = "TEST";

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static String mFileName = null;

    public AudioRecord() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFileName += Constants.PATH_AUDIO_FILES;

        Calendar calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cYear = calendar.get(Calendar.YEAR);
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        int cMin = calendar.get(Calendar.MINUTE);
        int cSec = calendar.get(Calendar.SECOND);
        String data = String.format("%d%02d%02d",cYear,cMonth,cDay)
                + "-" +  String.format("%02d-%02d-%02d", cHour, cMin, cSec);

        mFileName += "/" + data +".3gp";
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, e.toString());
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();    // set state to idle
        mRecorder.release();
        mRecorder = null;
    }

    public static String getFileName() {
        return mFileName;
    }
}

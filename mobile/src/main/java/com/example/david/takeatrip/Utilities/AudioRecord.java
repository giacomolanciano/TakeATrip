package com.example.david.takeatrip.Utilities;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Giacomo Lanciano on 08/03/2016.
 */
public class AudioRecord  {

    private static final String TAG = "TEST AudioRecord";

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static String mFileName = null;

    private static File fileAudio;

    public AudioRecord() {
        mFileName = DeviceStorageUtils.getAudioStoragePath();
        fileAudio = new File(mFileName);

        Calendar calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cYear = calendar.get(Calendar.YEAR);
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        int cMin = calendar.get(Calendar.MINUTE);
        int cSec = calendar.get(Calendar.SECOND);
        String data = String.format("%d%02d%02d",cYear,cMonth,cDay)
                + "-" +  String.format("%02d-%02d-%02d", cHour, cMin, cSec);

        mFileName += "/" + data +Constants.AUDIO_EXT;
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
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
            Log.e(TAG, "prepare() failed");
            Log.e(TAG, e.toString());
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();    // set state to idle
        mRecorder.release();
        mRecorder = null;
    }

    public String getFileName() {
        return mFileName;
    }

    public File getFileAudio(){
        return fileAudio;
    }
}

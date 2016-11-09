package com.takeatrip.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.MediaController;

import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.SquaredImageView;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

import java.util.List;

/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class GridViewVideoAdapter extends GridViewAdapter {

    private static final String TAG = "TEST GridViewVidAdapt";
    private static final int THREE = 3;
    private static final int SIX = 3;

    private String[] strings, subs;
    private int[] arr_images;

    private ProgressDialog progressDialog;
    private MediaController mediaControls;



    public GridViewVideoAdapter(Context context, GridView gv, List<ContenutoMultimediale> URLs, int tipoContenuto, String emailProfiloLoggato) {
        super(context,gv, URLs, tipoContenuto, emailProfiloLoggato);
    }

    public GridViewVideoAdapter(Context context, GridView gv, List<ContenutoMultimediale> URLs, int tipoContenuto, String codiceViaggio, String emailProfiloLoggato) {
        super(context, gv, URLs, tipoContenuto, codiceViaggio, emailProfiloLoggato);

    }


    SquaredImageView result;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //ImageView result = (ImageView) super.getView(position, convertView, parent);
        final Context context = this.getContext();

        result = (SquaredImageView) convertView;
        if (result == null) {
            result = new SquaredImageView(context);
        }

        result.setContentDescription(position+"");

        final ContenutoMultimediale contenutoMultimediale = getItem(Integer.parseInt(result.getContentDescription().toString()));

        Log.i(TAG, "contenuto video: " + contenutoMultimediale);
        Log.i(TAG, "url contenuto video: " + contenutoMultimediale.getUrlContenuto());

        /*

        try {

            if (mediaControls == null) {
                mediaControls = new MediaController(context);
            }
            //result.setMediaController(mediaControls);

            final Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,
                    contenutoMultimediale.getUrlContenuto()));
            result.setVideoURI(uri);
        } catch (Exception e) {
            Log.e(TAG,"Error " +  e.getMessage());
        }


        result.requestFocus();

        //we also set an setOnPreparedListener in order to know when the video file is ready for playback

        result.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                result.seekTo(position);
                if (position == 0) {
                    result.start();

                } else {
                    result.pause();
                }

            }
        });
        */

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "cliccato il video " +v);

                final Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,
                        contenutoMultimediale.getUrlContenuto()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                intent.setDataAndType(uri, "video/*");
                context.startActivity(intent);
            }
        });

        if (codiceViaggio != null) {
            result.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    cm = getItem(Integer.parseInt(v.getContentDescription().toString()));

                    if(cm.getEmailProfilo().equals(emailProfiloLoggato)){

                        Log.i(TAG, "file da eliminare: " + cm.getUrlContenuto());
                        confirmFileDeletion(v, Constants.QUERY_DEL_VIDEO);
                    }
                    return false;
                }
            });
        }



        return result;
    }

}

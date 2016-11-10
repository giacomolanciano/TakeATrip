package com.takeatrip.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.UtilS3Amazon;
import com.takeatrip.Utilities.UtilS3AmazonCustom;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class ListViewVideoAdapter extends ArrayAdapter<ContenutoMultimediale> {

    private static final String TAG = "TEST ListViewVidAdapt";
    private static final int THREE = 3;
    private static final int SIX = 3;

    private  Context context;
    protected final List<String> urls = new ArrayList<String>();
    protected String codiceViaggio, emailProfiloLoggato;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;
    protected  ContenutoMultimediale cm;
    protected List<ContenutoMultimediale> contents;


    private AmazonS3Client s3;


    public ListViewVideoAdapter(Context context, int textViewResourceId, List<ContenutoMultimediale> URLs, String codiceViaggio, String emailProfiloLoggato) {

        super(context, textViewResourceId, URLs);
        this.context = context;
        this.codiceViaggio = codiceViaggio;
        this.emailProfiloLoggato = emailProfiloLoggato;
        this.contents = URLs;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View cView = convertView;
        if (cView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cView = infalInflater.inflate(R.layout.entry_list_videos, null);
        }

        cView.setBackground(context.getResources().getDrawable(R.drawable.video_content));


        final ContenutoMultimediale contenutoMultimediale = getItem(position);
        Log.i(TAG, "contenuto video: " + contenutoMultimediale);
        Log.i(TAG, "url contenuto video: " + contenutoMultimediale.getUrlContenuto());

        //final VideoPlayerView mVideoPlayer_1 = (VideoPlayerView)cView.findViewById(R.id.video_player_1);


        final String url = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_TRAVELS_NAME,contenutoMultimediale.getUrlContenuto());

        cView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mVideoPlayerManager.playNewVideo(null, mVideoPlayer_1, url);

                Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_TRAVELS_NAME,contenutoMultimediale.getUrlContenuto()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "video/*");
                context.startActivity(intent);
            }
        });




        return cView;
    }

    @Override
    public ContenutoMultimediale getItem(int position) {
        return contents.get(position);
    }


    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

}

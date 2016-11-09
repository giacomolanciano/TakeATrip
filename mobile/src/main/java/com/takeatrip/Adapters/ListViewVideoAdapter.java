package com.takeatrip.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.UtilS3Amazon;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

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

    JCVideoPlayerStandard videoPlayer;


    public ListViewVideoAdapter(Context context, int textViewResourceId, List<ContenutoMultimediale> URLs, String codiceViaggio, String emailProfiloLoggato) {

        super(context, textViewResourceId, URLs);
        this.context = context;
        this.codiceViaggio = codiceViaggio;
        this.emailProfiloLoggato = emailProfiloLoggato;
        this.contents = URLs;



        /*
        if(contents != null && contents.size() >0 ) {
            String[] URLS = new String[contents.size()];
            for (int i = 0; i < contents.size(); i++) {
                URLS[i] = contents.get(i).getUrlContenuto();
            }
            Collections.addAll(urls, URLS);
        }
        */

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

        final ContenutoMultimediale contenutoMultimediale = getItem(position);
        Log.i(TAG, "contenuto video: " + contenutoMultimediale);
        Log.i(TAG, "url contenuto video: " + contenutoMultimediale.getUrlContenuto());

        videoPlayer = (JCVideoPlayerStandard) cView.findViewById(R.id.custom_videoplayer_standard);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest( Constants.BUCKET_TRAVELS_NAME,contenutoMultimediale.getUrlContenuto());

        generatePresignedUrlRequest.setMethod(HttpMethod.GET);

        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        //Log.i(TAG,"url video: " +url);
        videoPlayer.setUp(url.toString(), JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, "");
        videoPlayer.thumbImageView.setImageResource(R.drawable.video_content);


        return cView;
    }

    @Override
    public ContenutoMultimediale getItem(int position) {
        return contents.get(position);
    }

}

package com.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 17/03/16.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.takeatrip.Activities.TappaActivity;
import com.takeatrip.AsyncTasks.DeleteStopContentTask;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.SquaredImageView;
import com.takeatrip.Utilities.UtilS3Amazon;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GridViewAdapter extends BaseAdapter {

    private static final String TAG = "TEST GridViewAdapter";
    private static final int TRIPLE = 3;

    protected final Context context;
    protected final List<String> urls = new ArrayList<String>();
    private final int tipoContenuti;
    protected String codiceViaggio, emailProfiloLoggato;



    private Handler mainHandler;
    //private EventLogger eventLogger;
    private SimpleExoPlayerView simpleExoPlayerView;
    public LinearLayout debugRootView;
    private TextView textViewVideo;
    private Button retryButton;

    private DataSource.Factory mediaDataSourceFactory;
    public SimpleExoPlayer player;
    private MappingTrackSelector trackSelector;
    //private TrackSelectionHelper trackSelectionHelper;
    private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsSource;

    private Timeline.Window window;
    private boolean shouldAutoPlay;
    private boolean isTimelineStatic;
    private int playerWindow;
    private long playerPosition;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();



    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;
    protected  GridViewAdapter adapter;
    protected  ContenutoMultimediale cm;
    protected List<ContenutoMultimediale> contents;
    private GridView gridView;


    // The S3 client
    private AmazonS3Client s3;

    public GridViewAdapter(Context context, GridView gv, List<ContenutoMultimediale> URLs, int tipoContenuti, String emailProfiloLoggato) {
        this.context = context;
        this.tipoContenuti = tipoContenuti;
        this.codiceViaggio = null;
        this.emailProfiloLoggato = emailProfiloLoggato;
        this.contents = URLs;
        this.gridView = gv;

        if(contents != null && contents.size() >0 ) {
            String[] URLS = new String[contents.size()];
            for (int i = 0; i < contents.size(); i++) {
                URLS[i] = contents.get(i).getUrlContenuto();
            }
            Collections.addAll(urls, URLS);
        }

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);


        shouldAutoPlay = false;

        mainHandler = new Handler();
        window = new Timeline.Window();

    }

    public GridViewAdapter(Context context, GridView gv,  List<ContenutoMultimediale> URLs, int tipoContenuti, String codiceViaggio, String emailProfiloLoggato) {
        this.context = context;
        this.tipoContenuti = tipoContenuti;
        this.codiceViaggio = codiceViaggio;
        this.emailProfiloLoggato = emailProfiloLoggato;
        this.contents = URLs;
        this.gridView = gv;
        if(contents != null && contents.size() >0 ) {
            String[] URLS = new String[contents.size()];
            for (int i = 0; i < contents.size(); i++) {
                URLS[i] = contents.get(i).getUrlContenuto();
            }
            Collections.addAll(urls, URLS);
        }

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);



        shouldAutoPlay = false;

        mainHandler = new Handler();
        window = new Timeline.Window();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        adapter = this;

        if(convertView == null){
            if (tipoContenuti == Constants.VIDEO_FILE) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.entry_list_videos, null);

                convertView.setContentDescription(position+"");

                final ContenutoMultimediale contenutoMultimediale = getItem(position);
                Log.i(TAG, "contenuto video: " + contenutoMultimediale);
                Log.i(TAG, "url contenuto video: " + contenutoMultimediale.getUrlContenuto());

                Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_TRAVELS_NAME,contenutoMultimediale.getUrlContenuto()));


                debugRootView = (LinearLayout) convertView.findViewById(R.id.controls_root);
                textViewVideo = (TextView) convertView.findViewById(R.id.text_view_video);

                if(emailProfiloLoggato.equals(contenutoMultimediale.getEmailProfilo())){
                    textViewVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmFileDeletion(v, Constants.QUERY_DEL_VIDEO);
                        }
                    });
                }
                else{
                    textViewVideo.setVisibility(View.INVISIBLE);
                }


                retryButton = (Button) convertView.findViewById(R.id.retry_button);
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "cliccato il bottone retry");
                    }
                });

                simpleExoPlayerView = (SimpleExoPlayerView) convertView.findViewById(R.id.player_view);

                simpleExoPlayerView.setControllerVisibilityListener((TappaActivity)context);
                initializePlayer((TappaActivity)context, uri);


                simpleExoPlayerView.requestFocus();




            }
            else if (tipoContenuti == Constants.AUDIO_FILE) {
                convertView = new SquaredImageView(context);
                convertView.setContentDescription(position+"");

                // Trigger the download of the URL asynchronously into the image view.
                Picasso.with(context)
                        .load(R.drawable.audio_content)
                        .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE)
                        .centerCrop()
                        .tag(context)
                        .into((SquaredImageView) convertView);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cm = getItem(Integer.parseInt(v.getContentDescription().toString()));

                        Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,cm.getUrlContenuto()));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "audio/*");
                        context.startActivity(intent);

                    }
                });

                if (codiceViaggio != null) {
                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(cm.getEmailProfilo().equals(emailProfiloLoggato)) {
                                confirmFileDeletion(v, Constants.QUERY_DEL_AUDIO);
                            }

                            return false;
                        }
                    });
                }
            }
        }

        //è utile solamente nel caso delle immagini

/*
        if (tipoContenuti == Constants.VIDEO_FILE) {

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cm = getItem(Integer.parseInt(v.getContentDescription().toString()));

                    Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,cm.getUrlContenuto()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/*");
                    context.startActivity(intent);
                }
            });

            if (codiceViaggio != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
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

        }
        */
        return convertView;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public ContenutoMultimediale getItem(int position) {
        return contents.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return context;
    }

    public List<String> getUrls() {
        return urls;
    }

    public int getTipoContenuti() {
        return tipoContenuti;
    }

    public String getCodiceViaggio() {
        return codiceViaggio;
    }

    public AmazonS3Client getS3() {
        return s3;
    }



    protected void confirmFileDeletion(View v, String q) {
        final View view = v;
        final String query = q;

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.confirm))
                .setMessage(context.getString(R.string.delete_content_alert))
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            cm = getItem(Integer.parseInt(view.getContentDescription().toString()));
                            boolean result = new DeleteStopContentTask(context, query, codiceViaggio,
                                    cm.getUrlContenuto()).execute().get();

                            if(result){
                                cm = getItem(Integer.parseInt(view.getContentDescription().toString()));
                                Log.i(TAG, "deleted file: " + cm.getUrlContenuto());
                                contents.remove(cm);
                                urls.remove(cm.getUrlContenuto());
                                adapter.notifyDataSetChanged();

                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(ContextCompat.getDrawable(context, R.drawable.logodefbordo))
                .show();
    }




    private void initializePlayer(TappaActivity listener, Uri uri) {


        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        //trackSelector.addListener(listener);
        player = ExoPlayerFactory.newSimpleInstance(listener, trackSelector, new DefaultLoadControl());
        Log.i(TAG, "generata istanza di player in tappa");

        player.addListener(listener);
        simpleExoPlayerView.setPlayer(player);

        if (isTimelineStatic) {
            if (playerPosition == C.TIME_UNSET) {
                player.seekToDefaultPosition(playerWindow);
            } else {
                player.seekTo(playerWindow, playerPosition);
            }
        }

        player.setPlayWhenReady(shouldAutoPlay);

        //debugViewHelper = new DebugTextViewHelper(player, textViewVideo);
        //debugViewHelper.start();


        playerNeedsSource = true;
        if (playerNeedsSource) {


            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(listener,
                    Util.getUserAgent(listener, "TakeATrip"), bandwidthMeter);

            // Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource(uri,
                    dataSourceFactory, extractorsFactory, null, null);

            // Prepare the player with the source.
            player.prepare(videoSource);


            playerNeedsSource = false;
        }
    }


    public void releasePlayer() {
        if (player != null) {
            //debugViewHelper.stop();
            //debugViewHelper = null;
            shouldAutoPlay = player.getPlayWhenReady();
            playerWindow = player.getCurrentWindowIndex();
            playerPosition = C.TIME_UNSET;
            Timeline timeline = player.getCurrentTimeline();
            if (timeline != null && timeline.getWindow(playerWindow, window).isSeekable) {
                playerPosition = player.getCurrentPosition();
            }
            player.release();
            player = null;
            trackSelector = null;
        }
    }


}
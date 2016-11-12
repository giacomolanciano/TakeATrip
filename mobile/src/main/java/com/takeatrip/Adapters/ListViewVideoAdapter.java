package com.takeatrip.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.takeatrip.Activities.ViaggioActivityConFragment;
import com.takeatrip.AsyncTasks.DeleteStopContentTask;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.UtilS3Amazon;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class ListViewVideoAdapter extends ArrayAdapter<ContenutoMultimediale> {

    private static final String TAG = "TEST ListViewVidAdapt";
    private static final int THREE = 3;
    private static final int SIX = 3;

    private  Context context;
    protected final List<String> urls = new ArrayList<String>();
    protected String codiceViaggio, emailProfiloLoggato, phpFile;


    protected List<ContenutoMultimediale> contents;

    protected  ListViewVideoAdapter adapter;


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



    private AmazonS3Client s3;


    public ListViewVideoAdapter(Context context, int textViewResourceId, List<ContenutoMultimediale> URLs, String codiceViaggio, String emailProfiloLoggato, String phpFile) {

        super(context, textViewResourceId, URLs);
        this.context = context;
        this.codiceViaggio = codiceViaggio;
        this.emailProfiloLoggato = emailProfiloLoggato;
        this.phpFile = phpFile;

        this.contents = URLs;
        s3 = UtilS3Amazon.getS3Client(context);


        shouldAutoPlay = false;

        mainHandler = new Handler();
        window = new Timeline.Window();
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        adapter = this;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.entry_list_videos, null);

            //cView.setBackground(context.getResources().getDrawable(R.drawable.video_content));


            final ContenutoMultimediale contenutoMultimediale = getItem(position);
            Log.i(TAG, "contenuto video: " + contenutoMultimediale);
            Log.i(TAG, "url contenuto video: " + contenutoMultimediale.getUrlContenuto());


            final String url = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_TRAVELS_NAME,contenutoMultimediale.getUrlContenuto());

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


            if(phpFile.equals(Constants.QUERY_TRAVEL_VIDEOS)){
                simpleExoPlayerView.setControllerVisibilityListener((ViaggioActivityConFragment)context);
                initializePlayer((ViaggioActivityConFragment)context, uri);
            }

            simpleExoPlayerView.requestFocus();


            return convertView;
        }
        else {
            return convertView;
        }
    }

    @Override
    public ContenutoMultimediale getItem(int position) {
        return contents.get(position);
    }







    private void initializePlayer(ViaggioActivityConFragment listener, Uri uri) {


        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);


        //trackSelector.addListener(listener);



        player = ExoPlayerFactory.newSimpleInstance(listener, trackSelector, new DefaultLoadControl());
        Log.i(TAG, "generata istanza di player");

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



    public void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
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

                            ContenutoMultimediale cm = getItem(Integer.parseInt(view.getContentDescription().toString()));

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

}
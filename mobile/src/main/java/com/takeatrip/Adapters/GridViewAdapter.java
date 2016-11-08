package com.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 17/03/16.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.squareup.picasso.Picasso;
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

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class GridViewAdapter extends BaseAdapter {

    private static final String TAG = "TEST GridViewAdapter";
    private static final int TRIPLE = 3;

    protected final Context context;
    protected final List<String> urls = new ArrayList<String>();
    private final int tipoContenuti;
    protected String codiceViaggio, emailProfiloLoggato;

    // The TransferUtility is the primary class for managing transfer to S3
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
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }


        adapter = this;
        cm = getItem(position);

        Log.i(TAG, view + " " + " position " +position+" "+ getItem(position));


        // Get the URL for the current position (o il testo nel caso delle note).
        final String url = cm.getUrlContenuto();

        //è utile solamente nel caso delle immagini
        view.setContentDescription(url);

        if (tipoContenuti == Constants.VIDEO_FILE) {
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context)
                    .load(R.drawable.video_content)
                    .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE)
                    .centerCrop()
                    .tag(context)
                    .into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "content: "+v.getContentDescription());
                    Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,url));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/*");
                    context.startActivity(intent);
                }
            });

            if (codiceViaggio != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(cm.getEmailProfilo().equals(emailProfiloLoggato)){
                            Log.i(TAG, "file da eliminare: " + v.getContentDescription());
                            confirmFileDeletion(v, Constants.QUERY_DEL_VIDEO);
                        }
                        return false;
                    }
                });
            }
        } else if (tipoContenuti == Constants.AUDIO_FILE) {

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context)
                    .load(R.drawable.audio_content)
                    .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE)
                    .centerCrop()
                    .tag(context)
                    .into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri uri = Uri.parse(UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,url));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "audio/*");
                    context.startActivity(intent);

                }
            });

            if (codiceViaggio != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(cm.getEmailProfilo().equals(emailProfiloLoggato)) {
                            Log.i(TAG, "file da eliminare: " + v.getContentDescription());
                            confirmFileDeletion(v, Constants.QUERY_DEL_AUDIO);

                        }

                        return false;
                    }
                });
            }
        }
        return view;
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
                            boolean result = new DeleteStopContentTask(context, query, codiceViaggio,
                                    view.getContentDescription().toString()).execute().get();

                            if(result){
                                Log.i(TAG, "deleted file with url: " + view.getContentDescription().toString());

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
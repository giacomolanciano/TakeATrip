package com.example.david.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 17/03/16.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.david.takeatrip.AsyncTasks.DeleteStopContentTask;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.SquaredImageView;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;
import com.example.david.takeatrip.Utilities.UtilS3AmazonCustom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class GridViewAdapter extends BaseAdapter {

    private static final String TAG = "TEST GridViewAdapter";
    private static final int TRIPLE = 3;

    private final Context context;
    private final List<String> urls = new ArrayList<String>();
    private final int tipoContenuti;
    private final String codiceViaggio;

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


    // The S3 client
    private AmazonS3Client s3;

    public GridViewAdapter(Context context, String[] URLs, int tipoContenuti) {
        this.context = context;
        this.tipoContenuti = tipoContenuti;
        this.codiceViaggio = null;

        if(URLs != null)
            Collections.addAll(urls, URLs);

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);
    }

    public GridViewAdapter(Context context, String[] URLs, int tipoContenuti, String codiceViaggio) {
        this.context = context;
        this.tipoContenuti = tipoContenuti;
        this.codiceViaggio = codiceViaggio;

        if(URLs != null)
            Collections.addAll(urls, URLs);

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

        // Get the URL for the current position (o il testo nel caso delle note).
        final String url = getItem(position);

        //è utile solamente nel caso delle immagini
        view.setContentDescription(url);



        if (tipoContenuti == Constants.VIDEO_FILE) {

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context)
                    .load(R.drawable.empty_image)   //TODO sostituire con logo video
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
                        Log.i(TAG, "file da eliminare: " + v.getContentDescription());
                        confirmFileDeletion(v, Constants.QUERY_DEL_VIDEO);

                        return false;
                    }
                });
            }
        } else if (tipoContenuti == Constants.AUDIO_FILE) {

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context)
                    .load(R.drawable.empty_image)   //TODO sostituire con logo audio
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
                    intent.setDataAndType(uri, "audio/*");
                    context.startActivity(intent);

                }
            });

            if (codiceViaggio != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.i(TAG, "file da eliminare: " + v.getContentDescription());
                        confirmFileDeletion(v, Constants.QUERY_DEL_AUDIO);

                        return false;
                    }
                });
            }
        } else if (tipoContenuti == Constants.NOTE_FILE) {

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context)
                    .load(R.drawable.empty_image)   //TODO sostituire con logo note
                    .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE)
                    .centerCrop()
                    .tag(context)
                    .into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "content: "+v.getContentDescription());

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    final View dialogView = inflater.inflate(R.layout.material_edit_text, null);
                    final TextInputEditText textInputEditText= (TextInputEditText) dialogView.findViewById(R.id.editText);
                    textInputEditText.setText(url);

                    //TODO per ora neghiamo la possibilità di editare, valutare aggiunta feature in seguito
                    textInputEditText.setEnabled(false);
                    textInputEditText.setFocusable(false);
                    textInputEditText.setFilters(new InputFilter[] {
                            new InputFilter() {
                                public CharSequence filter(CharSequence src, int start,
                                                           int end, Spanned dst, int dstart, int dend) {
                                    return src.length() < 1 ? dst.subSequence(dstart, dend) : "";
                                }
                            }
                    });

                    new android.support.v7.app.AlertDialog.Builder(context)
                            .setView(dialogView)
                            .setTitle(context.getString(R.string.labelNoEditNote))
//                            .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    noteInserite.add(textInputEditText.getText().toString());
//                                    Log.i(TAG, "edit text dialog confirmed");
//                                }
//                            })
                            .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Log.i(TAG, "edit text dialog canceled");
                                }
                            })
                            .setIcon(ContextCompat.getDrawable(context, R.drawable.logodefbordo))
                            .show();
                }
            });

            if (codiceViaggio != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.i(TAG, "file da eliminare: " + v.getContentDescription());
                        confirmFileDeletion(v, Constants.QUERY_DEL_NOTE);

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

    @Override public String getItem(int position) {
        return urls.get(position);
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
                        view.setVisibility(View.GONE);
                        new DeleteStopContentTask(context, query, codiceViaggio,
                                view.getContentDescription().toString()).execute();
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
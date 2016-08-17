package com.example.david.takeatrip.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.takeatrip.AsyncTasks.DeleteStopContentTask;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.SquaredImageView;
import com.example.david.takeatrip.Utilities.UtilS3AmazonCustom;
import com.squareup.picasso.Picasso;

/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class GridViewImageAdapter extends GridViewAdapter {

    private static final String TAG = "TEST GridViewImgAdapt";
    private static final int TRIPLE = 3;

    public GridViewImageAdapter(Context context, String[] URLs, int tipoContenuto) {
        super(context, URLs, tipoContenuto);
    }

    public GridViewImageAdapter(Context context, String[] URLs, int tipoContenuto, String codiceViaggio) {
        super(context, URLs, tipoContenuto, codiceViaggio);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView result = (SquaredImageView) super.getView(position, convertView, parent);
        final Context context = this.getContext();
        final String url = UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,result.getContentDescription().toString());

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.empty_image)
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*TRIPLE)
                .centerCrop()
                .tag(context)
                .into(result);


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "content: "+v.getContentDescription());

                Uri uri = Uri.parse(url);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "image/*");
                context.startActivity(intent);

            }
        });

        if (getCodiceViaggio() != null) {
            result.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(TAG, "file da eliminare: " + v.getContentDescription());

                    final View view = v;

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.confirm))
                            .setMessage(context.getString(R.string.delete_content_alert))
                            .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    view.setVisibility(View.GONE);
                                    new DeleteStopContentTask(context, Constants.QUERY_DEL_IMAGE, getCodiceViaggio(),
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
                    return false;
                }
            });
        }

        return result;
    }
}

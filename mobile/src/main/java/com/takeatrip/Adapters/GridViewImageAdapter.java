package com.takeatrip.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.SquaredImageView;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class GridViewImageAdapter extends GridViewAdapter {

    private static final String TAG = "TEST GridViewImgAdapt";
    private static final int TRIPLE = 3;

    public GridViewImageAdapter(Context context, ContenutoMultimediale[] URLs, int tipoContenuto, String emailProfiloLoggato) {
        super(context, URLs, tipoContenuto, emailProfiloLoggato);
    }

    public GridViewImageAdapter(Context context, ContenutoMultimediale[] URLs, int tipoContenuto, String codiceViaggio, String emailProfiloLoggato) {
        super(context, URLs, tipoContenuto, codiceViaggio, emailProfiloLoggato);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SquaredImageView result = (SquaredImageView) super.getView(position, convertView, parent);
        final Context context = this.getContext();
        final String url = UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,result.getContentDescription().toString());

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.empty_image)
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *TRIPLE)
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
                    if(getItem(position).getEmailProfilo().equals(emailProfiloLoggato)){
                        Log.i(TAG, "file da eliminare: " + v.getContentDescription());
                        confirmFileDeletion(v, Constants.QUERY_DEL_IMAGE);
                    }

                    return false;
                }
            });
        }

        return result;
    }
}

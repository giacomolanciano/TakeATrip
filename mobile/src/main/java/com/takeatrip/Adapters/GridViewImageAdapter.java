package com.takeatrip.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    private static final int THREE = 3;
    private static final int SIX = 3;


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
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *THREE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *THREE)
                .centerCrop()
                .tag(context)
                .into(result);


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                dialog.setContentView(R.layout.photos_view);
                ImageView imageProfile = (ImageView) dialog.findViewById(R.id.imageDialog);
                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                float density = context.getResources().getDisplayMetrics().density;
                if(density < 3.0){
                    Picasso.with(context)
                            .load(url)
                            .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *6, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *6)
                            .into(imageProfile);
                }
                else if(density == 3.0 || density == 4.0){
                    Picasso.with(context)
                            .load(url)
                            .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *10, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *10)
                            .into(imageProfile);
                }


                dialog.show();
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

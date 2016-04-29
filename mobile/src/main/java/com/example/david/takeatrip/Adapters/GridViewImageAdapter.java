package com.example.david.takeatrip.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.SquaredImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class GridViewImageAdapter extends GridViewAdapter {

    private static final String TAG = "TEST GridViewImgAdapt";

    public GridViewImageAdapter(Context context, String[] URLs) {
        super(context, URLs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView result = (SquaredImageView) super.getView(position, convertView, parent);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(this.getContext())
                .load(result.getContentDescription().toString())
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*3, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*3)
                .centerCrop()
                .tag(this.getContext())
                .into(result);


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO definire comportamento per ingrandimento immagine

                Log.i(TAG, "content: "+v.getContentDescription());

            }
        });

        return result;
    }
}

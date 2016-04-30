package com.example.david.takeatrip.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.SquaredImageView;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView result = (SquaredImageView) super.getView(position, convertView, parent);
        final Context context = this.getContext();
        final String url = result.getContentDescription().toString();

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

        return result;
    }
}

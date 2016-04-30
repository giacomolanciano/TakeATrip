package com.example.david.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 17/03/16.
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class GridViewAdapter extends BaseAdapter {

    private static final String TAG = "TEST GridViewAdapter";

    private final Context context;
    private final List<String> urls = new ArrayList<String>();

    public GridViewAdapter(Context context,String[] URLs) {
        this.context = context;

        // Ensure we get a different ordering of images on each run.

        if(URLs != null)
            Collections.addAll(urls, URLs);
        //Collections.shuffle(urls);

        // Triple up the list.
        //ArrayList<String> copy = new ArrayList<String>(urls);
        //urls.addAll(copy);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        view.setContentDescription(url);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context)
                .load(R.drawable.empty_image)
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*3, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*3)
                .centerCrop()
                .tag(context)
                .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO definire comportamento per streaming contenuto audio/video o visualizzazione nota

                Log.i(TAG, "content: "+v.getContentDescription());

            }
        });

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
}
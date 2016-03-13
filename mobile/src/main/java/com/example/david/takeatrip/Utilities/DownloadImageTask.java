package com.example.david.takeatrip.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;

/**
 * Created by lucagiacomelli on 05/03/16.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    LinearLayout layout;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    public DownloadImageTask(ImageView bmImage, LinearLayout layout) {
        this.bmImage = bmImage;
        this.layout = layout;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);

        if(layout != null){

            Drawable dr = new BitmapDrawable(result);

            layout.setBackground(dr);
            bmImage.setVisibility(View.INVISIBLE);
        }

    }
}

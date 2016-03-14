package com.example.david.takeatrip.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by lucagiacomelli on 05/03/16.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    //ImageView bmImage;
    LinearLayout layout;
    int width, height;
    private final WeakReference<ImageView> imageViewReference;



    public DownloadImageTask(ImageView bmImage) {
        //this.bmImage = bmImage;
        imageViewReference = new WeakReference<ImageView>(bmImage);

        width = bmImage.getWidth();
        height = bmImage.getHeight();
    }

    public DownloadImageTask(ImageView bmImage, LinearLayout layout) {
        //this.bmImage = bmImage;
        imageViewReference = new WeakReference<ImageView>(bmImage);

        this.layout = layout;
        width = layout.getWidth();
        height = layout.getHeight();
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();


            final BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = true;
            mIcon11 = BitmapFactory.decodeStream(in,null,options);

            Log.i("TEST", "bitmap decoded: " + mIcon11);

            /*
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height);

            //String imageType = options.outMimeType;
            Log.i("TEST", "inSampleSize: " + options.inSampleSize);
            Log.i("TEST", "width of image view: " + width);
            Log.i("TEST", "height of image view: " + height);
            Log.i("TEST", "width of image: " + options.outWidth);
            Log.i("TEST", "height of image: " + options.outHeight);

            // Decode bitmap with inSampleSize set

            final BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = options.inSampleSize;


            mIcon11 = BitmapFactory.decodeStream(in, null,options2);
            Log.i("TEST", "bitmap decoded: " + mIcon11);
            */


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        if(imageViewReference != null && result != null){
            final ImageView bmImage = imageViewReference.get();

            if (bmImage != null) {
                bmImage.setImageBitmap(result);
            }

            if(layout != null){
                Drawable dr = new BitmapDrawable(result);
                layout.setBackground(dr);
                if(bmImage != null){
                    bmImage.setVisibility(View.INVISIBLE);
                }
            }
        }



    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

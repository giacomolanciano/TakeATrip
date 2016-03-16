package com.example.david.takeatrip.Utilities;

import android.content.res.Resources;
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

import java.io.IOException;
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
    private int data = 0;



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
        return decodeSampledBitmapFromResource(urldisplay, width, height);

    }

    protected void onPostExecute(Bitmap result) {

        if(imageViewReference != null && result != null){
            final ImageView bmImage = imageViewReference.get();


            final DownloadImageTask bitmapWorkerTask =
                    getBitmapWorkerTask(bmImage);

            if (this == bitmapWorkerTask && bmImage != null) {
                bmImage.setImageBitmap(result);
            }



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




    private static DownloadImageTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }




    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<DownloadImageTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             DownloadImageTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<DownloadImageTask>(bitmapWorkerTask);
        }

        public DownloadImageTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final DownloadImageTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }




    public static Bitmap decodeSampledBitmapFromResource(String url,
                                                         int reqWidth, int reqHeight) {
        InputStream in = null;
        Bitmap mIcon11 = null;

        try {
            in = new java.net.URL(url).openStream();

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            Log.i("TEST", "width of image: " + options.outWidth);
            Log.i("TEST", "height of image: " + options.outHeight);

            options.inSampleSize = 1;

            if(options.outHeight > 200 && options.outWidth > 200){
                options.inSampleSize = 1;
            }
            if(options.outHeight > 400 && options.outWidth > 400){
                options.inSampleSize = 2;
            }
            if(options.outHeight > 600 && options.outWidth > 600){
                options.inSampleSize = 4;
            }


            if(reqWidth != 0 && reqHeight != 0){
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            }

            Log.i("TEST", "inSampleSize: " + options.inSampleSize);

            options.inJustDecodeBounds = false;

            in.close();
            in = new java.net.URL(url).openStream();

            mIcon11 = BitmapFactory.decodeStream(in, null, options);
            Log.i("TEST", "bitmap decoded: " + mIcon11);
            in.close();



        } catch (Exception e) {
            e.printStackTrace();
        }


        return mIcon11;

    }


    public static Bitmap decodeSampledBitmapFromPath(String path,
                                                         int reqWidth, int reqHeight) {
        Bitmap mIcon11 = null;

        try {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            Log.i("TEST", "width of image: " + options.outWidth);
            Log.i("TEST", "height of image: " + options.outHeight);

            options.inSampleSize = 1;
            if (options.outHeight > 200 && options.outWidth > 200) {
                options.inSampleSize = 1;
            }
            if (options.outHeight > 400 && options.outWidth > 400) {
                options.inSampleSize = 2;
            }
            if (options.outHeight > 600 && options.outWidth > 600) {
                options.inSampleSize = 4;
            }

            if(reqWidth != 0 && reqHeight != 0){
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            }


            Log.i("TEST", "inSampleSize: " + options.inSampleSize);
            options.inJustDecodeBounds = false;

            mIcon11 = BitmapFactory.decodeFile(path, options);
            Log.i("TEST", "bitmap decoded: " + mIcon11);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return mIcon11;
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

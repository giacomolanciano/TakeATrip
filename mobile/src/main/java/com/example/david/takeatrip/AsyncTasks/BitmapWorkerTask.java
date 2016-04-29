package com.example.david.takeatrip.AsyncTasks;

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
import java.lang.ref.WeakReference;

/**
 * Created by lucagiacomelli on 05/03/16.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private static String TAG = "TEST BitmapWorkerTask";

    ImageView bmImage;
    LinearLayout layout;
    int width, height;
    private WeakReference<ImageView> imageViewReference;
    public String data = "null";



    public BitmapWorkerTask(ImageView bmImage) {
        //this.bmImage = bmImage;
        imageViewReference = new WeakReference<ImageView>(bmImage);

        width = bmImage.getWidth();
        height = bmImage.getHeight();
    }

    public BitmapWorkerTask(ImageView bmImage, LinearLayout layout) {
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


            if (bmImage != null) {
                final BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(bmImage);

                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(result, bitmapWorkerTask);
                bmImage.setImageDrawable(asyncDrawable);
                //bmImage.setImageBitmap(result);
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




    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();

            Log.i(TAG, "image view diversa da null");
            Log.i(TAG, "drawable: " +drawable);


            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;

                Log.i(TAG, "result form getBitmapWorkerTask: " + asyncDrawable.getBitmapWorkerTask());

                return asyncDrawable.getBitmapWorkerTask();
                //return "OK";
            }
        }
        return null;
    }




    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);

            Log.i(TAG, "taskPreference");
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
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

            Log.i(TAG, "width of image: " + options.outWidth);
            Log.i(TAG, "height of image: " + options.outHeight);

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

            Log.i(TAG, "inSampleSize: " + options.inSampleSize);

            options.inJustDecodeBounds = false;

            in.close();
            in = new java.net.URL(url).openStream();

            mIcon11 = BitmapFactory.decodeStream(in, null, options);
            Log.i(TAG, "bitmap decoded: " + mIcon11);
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

            Log.i(TAG, "width of image: " + options.outWidth);
            Log.i(TAG, "height of image: " + options.outHeight);

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


            Log.i(TAG, "inSampleSize: " + options.inSampleSize);
            options.inJustDecodeBounds = false;

            mIcon11 = BitmapFactory.decodeFile(path, options);
            Log.i(TAG, "bitmap decoded: " + mIcon11);

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

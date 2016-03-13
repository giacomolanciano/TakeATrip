package com.example.david.takeatrip.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.david.takeatrip.Classes.ApiClientAsyncTask;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RetrieveImageTask extends ApiClientAsyncTask<DriveId, Void, Bitmap>{

    private static final String TAG = "RetrieveImageTask";
    private String idContent, typeContent;
    private DriveId contenuto;
    private Bitmap bitmap;
    private ImageView viewImage;
    LinearLayout layoutCopertina;
    private Context context;

    private ProgressDialog mProgressDialog;



    public RetrieveImageTask(Context context, ImageView view, DriveId idImage){
        super(context);
        this.context = context;
        viewImage = view;
        contenuto = idImage;
    }

    public RetrieveImageTask(Context context, ImageView view, DriveId idImage, LinearLayout layout){
        super(context);
        viewImage = view;
        contenuto = idImage;
        layoutCopertina = layout;
    }

    public RetrieveImageTask(Context context, ImageView view, DriveId idImage, LinearLayout layout,String type){
        super(context);
        this.context = context;
        viewImage = view;
        contenuto = idImage;
        layoutCopertina = layout;
        typeContent = type;
        showProgressDialog();
    }


    @Override
    protected Bitmap doInBackgroundConnected(DriveId... params) {
        String contents = null;
        DriveFile file = contenuto.asDriveFile();
        DriveApi.DriveContentsResult driveContentsResult =
                file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            return null;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        InputStream stream = driveContents.getInputStream();

        bitmap =  BitmapFactory.decodeStream(stream);
        Log.i("TEST", "prelevato bitmap: " +bitmap);

        driveContents.discard(getGoogleApiClient());
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.i("TEST", "bitmap: " + bitmap);

        if(layoutCopertina != null && typeContent.equals("little_image")){
            viewImage.setImageBitmap(bitmap);

            Log.i("TEST", "context: " + context);

            layoutCopertina.addView(viewImage, 60, 60);
            layoutCopertina.addView(new TextView(context), 20, 60);
            Log.i(TAG, "aggiungo la view nel layout orizzonale");


            hideProgressDialog();
            return;
        }
        if(layoutCopertina != null){

            Log.i("TEST", "bitmap image profile: " + bitmap);
            Drawable dr = new BitmapDrawable(bitmap);
            Log.i("TEST", "drawable immagine copertina: " + dr);

            layoutCopertina.setBackground(dr);

            hideProgressDialog();
            return;
        }

        viewImage.setImageBitmap(bitmap);
        hideProgressDialog();


    }



    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.CaricamentoInCorso));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
package com.example.david.takeatrip.Utilities;

import android.app.Activity;
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

public class RetrieveImage extends ApiClientAsyncTask<DriveId, Void, Bitmap>{

    private static final String TAG = "RetrieveImage";
    private String idContent, typeContent;
    private DriveId contenuto;
    private Bitmap bitmap;
    private ImageView viewImage;
    LinearLayout layoutCopertina;



    public RetrieveImage(Context context, ImageView view, DriveId idImage){
        super(context);
        viewImage = view;
        contenuto = idImage;
    }

    public RetrieveImage(Context context, ImageView view, DriveId idImage, LinearLayout layout){
        super(context);
        viewImage = view;
        contenuto = idImage;
        layoutCopertina = layout;
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

        viewImage.setImageBitmap(bitmap);

        if(layoutCopertina != null){

            Log.i("TEST", "bitmap image profile: " + bitmap);
            Drawable dr = new BitmapDrawable(bitmap);
            Log.i("TEST", "drawable immagine copertina: " +  dr);

            layoutCopertina.setBackground(dr);
            viewImage.setVisibility(View.INVISIBLE);
        }
    }
}
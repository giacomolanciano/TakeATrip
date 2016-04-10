package com.example.david.takeatrip.AsyncTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.david.takeatrip.Interfaces.AsyncResponseDriveId;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveIdCover;
import com.example.david.takeatrip.Utilities.Constants;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//import com.google.api.services.drive.model.Permission;

public class UploadImageTask extends ApiClientAsyncTask<DriveId, Void, DriveId> {

    public AsyncResponseDriveId delegate = null;
    public AsyncResponseDriveIdCover delegate2 = null;

    private static final String TAG = "UploadImageTask";
    private String nameImage, typeContent;
    private DriveId driveIdImage, driveIdFolder;
    private Bitmap bitmap;
    LinearLayout layoutCopertina;




    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i("TEST","Error while trying to create the file");
                        return;
                    }
                    Log.i("TEST","uploaded a file: " + result.getDriveFile().getDriveId());

                    driveIdImage = result.getDriveFile().getDriveId();
                }
            };




    public UploadImageTask(Context context, Bitmap image, String nameImage, DriveId idFolder, String type){
        super(context);
        bitmap = image;
        this.nameImage = nameImage;
        driveIdFolder = idFolder;
        typeContent = type;

        Log.i("TEST", "parametri asynch task: " + bitmap + " "+ nameImage + " " +driveIdFolder);
    }

    @Override
    protected DriveId doInBackgroundConnected(DriveId... params) {

        Log.i("TEST","googleApiClient: " + getGoogleApiClient());
        Log.i("TEST", "googleApiClient is conncected?: " + getGoogleApiClient().isConnected());

        DriveApi.DriveContentsResult result= Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();

        if (!result.getStatus().isSuccess()) {
            Log.i(TAG, "Failed to create new contents.");
            return null;
        }

        Log.i("TEST", "result form DriveContent: " + result);

        OutputStream outputStream = result.getDriveContents().getOutputStream();

        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, Constants.QUALITY_PHOTO, bitmapStream);
        try {
            outputStream.write(bitmapStream.toByteArray());
        } catch (IOException e1) {
            Log.i(TAG, "Unable to write file contents.");
        }

        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setMimeType("image/jpeg")
                .setPinned(true)
                .setViewed(true)
                .setTitle(nameImage).build();

        Log.i("TEST", "I'm creating the file into Drive...");

        DriveFolder folder = driveIdFolder.asDriveFolder();
        DriveFolder.DriveFileResult result2 = folder.createFile(getGoogleApiClient(), metadataChangeSet, result.getDriveContents()).await();

        driveIdImage = result2.getDriveFile().getDriveId();

        return driveIdImage;


    }


    @Override
    protected void onPostExecute(DriveId idImage) {
        super.onPostExecute(idImage);
        if(typeContent.equals("profile")){
            delegate.processFinish(idImage);
        }
        else if(typeContent.equals("cover")){
            delegate2.processFinish2(idImage);
        }
    }

    //Permission permission = new Permission();


}
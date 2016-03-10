package com.example.david.takeatrip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.david.takeatrip.Classes.TakeATrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class UploadFileDrive  extends DriveActivity{

    private static final String TAG = "TEST-UPLOAD";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private Bitmap mBitmapToSave;

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");

        final Bitmap image = getImage();

        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }


                        OutputStream outputStream = result.getDriveContents().getOutputStream();

                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }

                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle(getNomeFile()).build();

                        DriveFolder folder = getIdFolder().asDriveFolder();
                        folder.createFile(getGoogleApiClient(), metadataChangeSet, result.getDriveContents())
                                .setResultCallback(fileCallback);

                    }
                });
    }



    /*

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CREATOR:

                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    Log.i(TAG, "intent data received: " + data);






                    mBitmapToSave = null;

                }
                break;
        }
    }
*/

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");

        saveFileToDrive();
    }


    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    Log.i("TEST","Created a file: " + result.getDriveFile().getDriveId());

                    DriveId idFile = result.getDriveFile().getDriveId();
                    Intent data = new Intent();
                    data.putExtra("idFile", idFile);

                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, data);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, data);
                    }
                    finish();

                    hideProgressDialog();

                }
            };


}
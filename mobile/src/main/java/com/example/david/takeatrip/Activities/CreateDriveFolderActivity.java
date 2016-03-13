package com.example.david.takeatrip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.net.URL;

public class CreateDriveFolderActivity extends DriveActivity {
    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(getNameFolder()).build();

        if(getPrimaCartella() != null){
            Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                    getGoogleApiClient(), changeSet).setResultCallback(callback);
        }
        else{
            Drive.DriveApi.getFolder(getGoogleApiClient(), getIdFolder()).createFolder(
                    getGoogleApiClient(), changeSet).setResultCallback(callback);
        }

    }

    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }
            Log.i("TEST","Created a folder with id: " + result.getDriveFolder().getDriveId());

            //String idFolder = result.getDriveFolder().getDriveId()+"";
            DriveId idFolder = result.getDriveFolder().getDriveId();

            if(getPrimaCartella() != null){
                Intent data = new Intent();
                data.putExtra("idFolder", idFolder);
                data.putExtra("nameFolder", getNameFolder());


                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
            else {
                Intent data = new Intent();
                data.putExtra("idFolder", idFolder);
                data.putExtra("nameFolder", getNameFolder());
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }

            hideProgressDialog();
        }
    };
}
package com.example.david.takeatrip.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

public class CreateFolderActivity extends DriveActivity {



    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(getNameFolder()).build();




        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(callback);
    }

    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }
            Log.i("TEST","Created a folder with id: " + result.getDriveFolder().getDriveId());

            String idFolder = result.getDriveFolder().getDriveId()+"";

             /*
            //TODO: fa partire l'activity del Viaggio
            Intent intent = new Intent(MainActivity.this, ViaggioActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("codiceViaggio", UUIDViaggio);
            intent.putExtra("nomeViaggio", nomeViaggio);

            startActivity(intent);

            */
        }
    };
}
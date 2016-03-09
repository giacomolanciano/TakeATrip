package com.example.david.takeatrip.Activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.david.takeatrip.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

/**
 * An abstract activity that handles authorization and connection to the Drive
 * services.
 */
public abstract class DriveActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BaseDriveActivity";

    /**
     * DriveId of an existing folder to be used as a parent folder in
     * folder operations samples.
     */



    //public static final String EXISTING_FOLDER_ID = "0B2EEtIjPUdX6MERsWlYxN3J6RU0";

    /**
     * DriveId of an existing file to be used in file operation samples..
     */


    //public static final String EXISTING_FILE_ID = "0ByfSjdPVs9MZTHBmMVdSeWxaNTg";

    /**
     * Extra for account name.
     */
    protected static final String EXTRA_ACCOUNT_NAME = "account_name";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Next available request code.
     */
    protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    private String nameFolder;
    private String urlFolder;
    private String primaCartella;
    private String nomeFile;
    private String mimeType;

    private DriveId idFolder;
    private MetadataChangeSet metadataChangeSet;
    private Bitmap image;

    private ProgressDialog mProgressDialog;

    public String getNameFolder(){
        return nameFolder;
    }

    public DriveId getIdFolder() {
        return idFolder;
    }

    public String getUrlFolder() {
        return urlFolder;
    }

    public String getPrimaCartella() {
        return primaCartella;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public String getMimeType() {
        return mimeType;
    }


    public MetadataChangeSet getMetadataChangeSet() {
        return metadataChangeSet;
    }

    public Bitmap getImage() {
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setTheme(R.style.CustomDialog);


        Intent intent = getIntent();
        if(intent != null){
            nameFolder = intent.getStringExtra("nameFolder");
            idFolder = intent.getParcelableExtra("idFolder");
            urlFolder = intent.getStringExtra("urlFolder");
            primaCartella = intent.getStringExtra("firstFolder");
            nomeFile = intent.getStringExtra("nameFile");
            mimeType = intent.getStringExtra("mimeType");
            metadataChangeSet = intent.getParcelableExtra("metadata");
            byte[] bytes = intent.getByteArrayExtra("image");
            if(bytes != null){
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }


            Log.i(TAG,"nome cartella viaggio: " + nameFolder);
            Log.i(TAG,"id cartella viaggio: " + idFolder);
            Log.i(TAG,"url cartella viaggio: " + urlFolder);
            Log.i(TAG,"è prima cartella viaggio: " + primaCartella);
            Log.i(TAG,"nome file: " + nomeFile);
            Log.i(TAG,"mymeType file: " + mimeType);
            Log.i(TAG,"metadata file: " + metadataChangeSet);
            Log.i(TAG,"image file: " + image);




        }

        showProgressDialog();
    }

    /**
     * Called when activity gets visible. A connection to Drive services need to
     * be initiated as soon as the activity is visible. Registers
     * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onResume() {
        super.onResume();




        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }




    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
package com.example.david.takeatrip.Classes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.david.takeatrip.Utilities.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveId;

/**
 * Created by lucagiacomelli on 09/03/16.
 */
public class TakeATrip  extends Application {


    private GoogleApiClient mGoogleApiClient;
    private Profilo profiloCorrente;

    private DriveId idFolderCorrente;


    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public Profilo getProfiloCorrente() {
        return profiloCorrente;
    }

    public void setProfiloCorrente(Profilo profiloCorrente) {
        this.profiloCorrente = profiloCorrente;
    }

    public DriveId getIdFolderCorrente() {
        return idFolderCorrente;
    }

    public void setIdFolderCorrente(DriveId idFolderCorrente) {
        this.idFolderCorrente = idFolderCorrente;
    }
}

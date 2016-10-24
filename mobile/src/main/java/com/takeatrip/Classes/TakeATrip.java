package com.takeatrip.Classes;

import android.app.Application;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveId;

/**
 * Created by lucagiacomelli on 09/03/16.
 */
public class TakeATrip  extends Application {


    private GoogleApiClient mGoogleApiClient;
    private Profilo profiloCorrente;

    private DriveId idFolderCorrente;

    private CognitoCachingCredentialsProvider credentialsProvider;



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

    public void setCredentialsProvider(CognitoCachingCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public CognitoCachingCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

}

package com.takeatrip.Classes;

import android.app.Application;
import android.graphics.Bitmap;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by lucagiacomelli on 09/03/16.
 */
public class TakeATrip  extends Application {


    private GoogleApiClient mGoogleApiClient;
    private MapFragment fragment;
    private GoogleMap map;
    private Profilo profiloCorrente;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private Bitmap currentImage;
    private String activityCorrente;

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

    public void setCredentialsProvider(CognitoCachingCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public CognitoCachingCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public Bitmap getCurrentImage(){
        return currentImage;
    }

    public void setCurrentImage(Bitmap bitmap){
        currentImage = bitmap;
    }

    public String getActivityCorrente(){
        return activityCorrente;
    }

    public void setActivityCorrente(String activity){
        activityCorrente = activity;
    }

    public MapFragment getFragment() {
        return fragment;
    }

    public void setFragment(MapFragment fragment) {
        this.fragment = fragment;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }
}

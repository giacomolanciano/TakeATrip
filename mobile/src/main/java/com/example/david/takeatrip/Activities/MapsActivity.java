package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.david.takeatrip.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MapsActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private Button buttonSatellite, buttonHybrid, buttonTerrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        buttonSatellite = (Button) findViewById(R.id.buttonSatellite);
        buttonTerrain = (Button) findViewById(R.id.buttonTerrain);
        buttonHybrid = (Button) findViewById(R.id.buttonHybrid);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void onSatelliteButtonClick(View view) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void onHybridButtonClick(View view) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void onTerrainButtonClick(View view) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }



        @Override
    public void onMapReady(GoogleMap map) {
            googleMap = map;
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        map.setMyLocationEnabled(true);

    }
}
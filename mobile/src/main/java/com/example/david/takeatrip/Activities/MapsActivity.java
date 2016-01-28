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

        LatLng sydney = new LatLng(-33.867, 151.206);

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


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));


        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));



        /*

        map.addMarker(new MarkerOptions()
        //Scegliere l'icona per i Marker!
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.house_flag))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(41.889, -87.622)));
         */





        /*


         map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.


        LatLng mapCenter = new LatLng(41.889, -87.622);

        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
         */


        /*

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-18.142, 178.431), 2));

        // Polylines are useful for marking paths and routes on the map.
        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(-33.866, 151.195))  // Sydney
                .add(new LatLng(-18.142, 178.431))  // Fiji
                .add(new LatLng(21.291, -157.821))  // Hawaii
                .add(new LatLng(37.423, -122.091))  // Mountain View
        );
         */




    }








/*
    private class SnapToRoad extends AsyncTask<Void, Void, Void> {

        private final String TAG = SnapToRoad.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            Reader rd = null;
            try {
                URL url = new URL("http://maps.google.com/maps/api/directions/xml?origin=52.0,0&destination=52.0,0&sensor=true");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.connect();
                if (con.getResponseCode() == 200) {

                    rd = new InputStreamReader(con.getInputStream());
                    StringBuffer sb = new StringBuffer();
                    final char[] buf = new char[1024];
                    int read;
                    while ((read = rd.read(buf)) > 0) {
                        sb.append(buf, 0, read);
                    }
                    Log.v(TAG, sb.toString());
                }
                con.disconnect();
            } catch (Exception e) {
                Log.e("foo", "bar", e);
            } finally {
                if (rd != null) {
                    try {
                        rd.close();
                    } catch (IOException e) {
                        Log.e(TAG, "", e);
                    }
                }
            }
            return null;
        }

    }
    */


}
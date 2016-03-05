package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Itinerario;
import com.example.david.takeatrip.Classes.POI;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Tappa;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private Button buttonSatellite, buttonHybrid, buttonTerrain;
    private String email;
    private final String ADDRESS_PRELIEVO = "QueryDest.php";
    private Profilo profiloUtenteLoggato;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;

    private Map<Profilo,List<Tappa>> profiloTappe;
    private Map<Profilo, List<Place>> profiloNomiTappe;

    private List<Tappa> tappe;


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


        if(getIntent() != null){
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            Log.i("TEST", "email" + email);
        }

        tappe = new ArrayList<Tappa>();

        List<Tappa> listaTappe = new ArrayList<Tappa>();
        List<String> listaNomiTappe = new ArrayList<String>();
        profiloTappe = new HashMap<Profilo,List<Tappa>>();
        profiloNomiTappe = new HashMap<Profilo,List<Place>>();

        profiloUtenteLoggato = new Profilo(email, null,null,null, null, null, null, null, null, null);


        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();

        new MyTask().execute();
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



    Profilo currentProfile;
    List<Place> nomiTappe = new ArrayList<Place>();
    List<String> namesStops = new ArrayList<String>();

    private void AggiungiMarkedPointsOnMap(Profilo p, List<Tappa> tappe) {
        mGoogleApiClient.connect();

        nomiTappe.clear();
        namesStops.clear();
        for(Tappa t : tappe){
            findPlaceById(p, t);
        }

        if(tappe.size()==0){
            nomiTappe.clear();
            profiloNomiTappe.put(p,nomiTappe);
        }

        Log.i("TEST", "profiloNomiTappe: " + profiloNomiTappe);
        Log.i("TEST", "ho aggiunto i markedPoints di " + p);

    }






    private void findPlaceById(Profilo p, Tappa t) {
        if( TextUtils.isEmpty(t.getPoi().getCodicePOI()) || mGoogleApiClient == null){
            Log.i("TEST", "codice tappa: " + t.getPoi().getCodicePOI());
            Log.i("TEST", "return");
            return;
        }


        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        currentProfile = p;



        //Se sono presenti gia i nomi delle tappe non devo riprenderli
        if(profiloNomiTappe.get(p) != null){

            //TODO: aggiungere la classe Place che memorizza Nome e LatLong in modo da non richiamare sempre le API

            /*
            googleMap.clear();


            for(Place place : profiloNomiTappe.get(p)){
                googleMap.addMarker(new MarkerOptions()
                        .title(place.getName().toString())
                        .position(place.getLatLng()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));
            }

            */

            return;


        }
        Places.GeoDataApi.getPlaceById( mGoogleApiClient, t.getPoi().getCodicePOI() )
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override
                    public void onResult(PlaceBuffer places) {
                        Log.i("TEST", "sono in onResult");
                        Log.i("TEST", "PlaceBuffer: " + places.toString());
                        Log.i("TEST", "Status PlaceBuffer: " + places.getStatus());
                        Log.i("TEST", "Count PlaceBuffer: " + places.getCount());

                        if (places.getStatus().isSuccess()) {
                            Place place = places.get(0);
                            Log.i("TEST", "nome place: " + place.getName());


                            googleMap.addMarker(new MarkerOptions()
                                    .title(place.getName().toString())
                                    .position(place.getLatLng()));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));

                        }
                        //Release the PlaceBuffer to prevent a memory leak
                        places.release();
                    }
                });
    }













    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.ADDRESS_PRELIEVO + ADDRESS_PRELIEVO);
                httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                if (is != null) {
                    //converto la risposta in stringa
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();

                        String result = sb.toString();

                        Log.i("TEST", "result da queryViaggi: " + result);


                        if(result.equals("null\n")){
                            //TODO: convertire in values
                            stringaFinale = "Non sono presenti viaggi";
                            Log.i("TEST", "result da queryViaggi: " + stringaFinale);

                        }
                        else{
                            JSONArray jArray = new JSONArray(result);

                            if(jArray != null && result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codiceViaggio = json_data.getString("codiceViaggio");

                                    //TODO
                                    Itinerario itinerario = new Itinerario(new Profilo(email), new Viaggio(codiceViaggio));

                                    int ordine = json_data.getInt("ordine");

                                    stringaFinale = email + " " + codiceViaggio  +" "+ ordine;

                                    String paginaDiario = json_data.getString("paginaDiario");
                                    String codicePOI = json_data.getString("codicePOI");
                                    String fontePOI = json_data.getString("fontePOI");

                                    POI poi = new POI(codicePOI, fontePOI);




                                    tappe.add(new Tappa(itinerario, ordine, null, null, paginaDiario, poi));
                                }
                            }

                        }


                    } catch (Exception e) {
                        Log.e("TEST", "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e("TEST", "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("TEST", "lista tappe: " + tappe);
            AggiungiMarkedPointsOnMap(profiloUtenteLoggato,tappe);

            super.onPostExecute(aVoid);

        }
    }

}


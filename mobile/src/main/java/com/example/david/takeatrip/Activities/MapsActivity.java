package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener {


    private Button buttonSatellite, buttonHybrid, buttonTerrain;
    private String email, emailEsterno, nomeViaggio, urlImmagineViaggio, codiceViaggio;
    ;
    private final String ADDRESS_PRELIEVO = "QueryDest.php";
    private Profilo profiloUtente;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;

    private Map<Profilo,List<Tappa>> profiloTappe;
    private Map<Profilo, List<Tappa>> profiloNomiTappe;
    private Map<List<Tappa>, List<Viaggio>> viaggioTappa;
    private Map<String,Viaggio> combo;
    private Map<String,String> comboCodice;

    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    private LatLngBounds.Builder mapBoundsBuilder;
    private LatLngBounds mapBounds;




    private List<Tappa> tappe;
    private List<Tappa> nomeTappa;
    private List<Viaggio> nome;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initViews();

        if(getIntent() != null){
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            emailEsterno = intent.getStringExtra("emailEsterno");
            Log.i("TEST", "email" + email);
            Log.i("TEST", "email esterno: " + emailEsterno);

        }

        tappe = new ArrayList<Tappa>();
        nomeTappa = new ArrayList<Tappa>();
        nome = new ArrayList<Viaggio>();

        mapBoundsBuilder = new LatLngBounds.Builder();

        List<Tappa> listaTappe = new ArrayList<Tappa>();
        List<String> listaNomiTappe = new ArrayList<String>();
        profiloTappe = new HashMap<Profilo,List<Tappa>>();
        profiloNomiTappe = new HashMap<>();
        viaggioTappa = new HashMap<>();

        if(email == null || (email != null && emailEsterno!= null)) {
            if(email != null && email.equals(emailEsterno)){
                profiloUtente = new Profilo(email, null, null, null, null, null, null, null, null, null);
            }
            else {
                profiloUtente = new Profilo(emailEsterno, null, null, null, null, null, null, null, null, null);
            }
        }
        else{
            profiloUtente = new Profilo(email, null, null, null, null, null, null, null, null, null);
        }

        Log.i("TEST", "Profilo utente corrente: " + profiloUtente);


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


    private void initViews() {

        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.imageTravelOnMap);

      SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        // map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        //  map.setOnMarkerClickListener(this);
        map.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.view_custom_marker, null);

                ImageView imageTravel = (ImageView) v.findViewById(R.id.imageTravelOnMap);

                String urlImmagineViaggio = null;
                for(Viaggio viaggio: combo.values()){
                    if(viaggio.getCodice().equals(comboCodice.get(marker.getTitle()))){
                        urlImmagineViaggio = viaggio.getUrlImmagine();
                        break;
                    }
                }

                Log.i("TEST", "urlImmagine del viaggio " + marker.getTitle() + ": " + urlImmagineViaggio );
                if(urlImmagineViaggio!= null && !urlImmagineViaggio.equals("null")){
                    Log.i("TEST", "esecuzione download immagine viaggio... ");

                    //TODO: con il thread non si visualizza questo bitmap
                    // l'allocazione di memoria per visualizzare questo bitmap
                    //new DownloadImageTask(imageTravel).execute(Constants.ADDRESS_TAT + urlImmagineViaggio);
                }

                // Getting reference to the TextView to set title
                TextView note = (TextView) v.findViewById(R.id.note);
                note.setText(marker.getTitle());

                // Returning the view containing InfoWindow contents
                return v;

            }

        });
    }



    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(this, ViaggioActivity.class);

        if(email == null || (email != null && emailEsterno!= null)) {
            i.putExtra("email", email);
        }
        else if(email != null && emailEsterno == null){
            i.putExtra("email", email);
        }
        else{
            i.putExtra("email", emailEsterno);
        }
        //TODO passare codice e nome per ricreare viaggio
        i.putExtra("codiceViaggio", comboCodice.get(marker.getTitle()));
        i.putExtra("nomeViaggio", marker.getTitle());
        Log.i("TEST", "#email  " + profiloUtente.getEmail());
        Log.i("TEST", "#nomedelviaggio  " + marker.getTitle() );
        Log.i("TEST", "#codicedelviaggio  " + comboCodice.get(marker.getTitle()));

        startActivity(i);
    }

    public boolean onMarkerClick(Marker arg0) {
        return false;
    }


    Profilo currentProfile;
    List<Place> nomiTappe = new ArrayList<Place>();
    List<String> namesStops = new ArrayList<String>();
    int count =0;

    private void AggiungiMarkedPointsOnMap(Profilo p, List<Tappa> tappe, List<Viaggio> viaggio) {
        mGoogleApiClient.connect();
        count = 0;
        namesStops.clear();
        for (Tappa t : tappe){
            findPlaceById(p, t);
        }
        profiloNomiTappe.put(p, tappe);
        viaggioTappa.put(tappe, nome);
        Log.i("TEST", "profiloNomiTappe: " + profiloNomiTappe);
        Log.i("TEST", "viaggioTappa: " + viaggioTappa);
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



        Log.i("TEST", "Profilo utente corrente in findPlace: " + profiloUtente);


        //Se sono presenti gia i nomi delle tappe non devo riprenderli
        if (profiloNomiTappe.get(p) != null) {

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


        Places.GeoDataApi.getPlaceById(mGoogleApiClient, t.getPoi().getCodicePOI())
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
                            Log.i("TEST", "idPlace " + place.getId());
                            Log.i("TEST", "titolo Viaggio " + combo.get(place.getId()));


                            googleMap.addMarker(new MarkerOptions()
                                            .title(combo.get(place.getId()).getNome())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                                    //  .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.default_male)))
                                            .position(place.getLatLng())
                            );


                            mapBoundsBuilder.include(place.getLatLng());
                            mapBounds = mapBoundsBuilder.build();

                            count++;

                            Log.i("TEST", "count: " + count);


                            if (count == 1) {
                                CameraUpdate cu = CameraUpdateFactory.newLatLng(place.getLatLng());
                                googleMap.moveCamera(cu);
                            } else {
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mapBounds, Constants.LATLNG_BOUNDS_PADDING);
                                googleMap.moveCamera(cu);
                            }



                            // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 4));

                        }

                        //Release the PlaceBuffer to prevent a memory leak
                        places.release();
                    }
                });
    }

    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.imageTravelOnMap);
        markerImageView.setImageResource(resId);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
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
            dataToSend.add(new BasicNameValuePair("email", profiloUtente.getEmail()));
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_PRELIEVO);
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
                                combo = new HashMap<>();
                                comboCodice = new HashMap<>();
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);


                                    String codiceViaggio = json_data.getString("codiceViaggio");
                                    nomeViaggio = json_data.getString("nomeViaggio");
                                    urlImmagineViaggio = json_data.getString("urlImmagineViaggio");

                                    Viaggio viaggio = new Viaggio(codiceViaggio, nomeViaggio,urlImmagineViaggio);

                                    Itinerario itinerario = new Itinerario(new Profilo(email),viaggio);

                                    int ordine = json_data.getInt("ordine");

                                    stringaFinale = email + " " + codiceViaggio  +" "+ nomeViaggio  +" "+ ordine + " "+ urlImmagineViaggio;
                                    Log.i("TEST", "result da queryDEST: " + stringaFinale);


                                    String paginaDiario = json_data.getString("paginaDiario");
                                    String codicePOI = json_data.getString("codicePOI");
                                    String fontePOI = json_data.getString("fontePOI");

                                    combo.put(codicePOI, viaggio);
                                    comboCodice.put(nomeViaggio, codiceViaggio);

                                    POI poi = new POI(codicePOI, fontePOI);

                                    nome.add(new Viaggio(codiceViaggio, nomeViaggio));
                                    tappe.add(new Tappa(itinerario, ordine, null, null, paginaDiario, poi));
                                    nomeTappa.add(new Tappa(null, ordine, null, null, codicePOI, null));
                                }
                                Log.i("TEST", " combo finale: " + combo);
                                Log.i("TEST", " combo finale Codice: " + comboCodice);

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
            Log.i("TEST", "lista viaggi: " + nome);


            Log.i("TEST", "Profilo utente corrente: " + profiloUtente);

            AggiungiMarkedPointsOnMap(profiloUtente,tappe, nome);

            super.onPostExecute(aVoid);

        }
    }

}

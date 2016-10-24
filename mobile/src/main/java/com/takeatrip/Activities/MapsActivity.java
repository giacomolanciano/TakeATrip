package com.takeatrip.Activities;

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
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
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
import com.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.takeatrip.AsyncTasks.LoadGenericImageTask;
import com.takeatrip.Classes.Itinerario;
import com.takeatrip.Classes.POI;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.Tappa;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.UtilS3Amazon;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener {

    private static final String TAG = "TEST MapsActivity";


    private final int DIMENSION_IMAGE_TRAVEL = Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT;
    private String email, emailEsterno, nomeViaggio, urlImmagineViaggio, codiceViaggio, currentUrlImageTravel;
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


    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;


    // The S3 client
    private AmazonS3Client s3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initViews();

        if(getIntent() != null){
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            emailEsterno = intent.getStringExtra("emailEsterno");
            Log.i(TAG, "email" + email);
            Log.i(TAG, "email esterno: " + emailEsterno);

        }

        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(MapsActivity.this);

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

        Log.i(TAG, "Profilo utente corrente: " + profiloUtente);

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

                return;
            }
        }
        // map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);

        // Returning the view containing InfoWindow contents
        map.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.view_custom_marker, null);
                final ImageView imageTravel = (ImageView) v.findViewById(R.id.imageTravelOnMap);

                String urlImmagineViaggio = null;
                for(Viaggio viaggio: combo.values()){
                    if(viaggio.getCodice().equals(comboCodice.get(marker.getTitle().split("@@@")[0]))){
                        urlImmagineViaggio = downloadUrlOfImage(viaggio.getCodice(), viaggio.getUrlImmagine());
                        break;
                    }
                }

                TextView note = (TextView) v.findViewById(R.id.note);
                note.setText(marker.getTitle().split("@@@")[0]);
                if(urlImmagineViaggio!= null && !urlImmagineViaggio.equals("")){
                    currentUrlImageTravel = urlImmagineViaggio;

                    imageTravel.setContentDescription(urlImmagineViaggio);

                    try {
                        Bitmap bitmap = new BitmapWorkerTask(imageTravel).execute(urlImmagineViaggio).get();
                        imageTravel.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return v;

            }

        });
    }



    private String downloadUrlOfImage(String codiceViaggio, String key){
        URL url = null;

        try {
            url = new LoadGenericImageTask(key, codiceViaggio, MapsActivity.this).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return  url.toString();

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

        i.putExtra("codiceViaggio", comboCodice.get(marker.getTitle().split("@@@")[0]));
        i.putExtra("nomeViaggio", marker.getTitle().split("@@@")[0]);
        i.putExtra("livelloCondivisione", marker.getTitle().split("@@@")[1]);

        if(currentUrlImageTravel != null){
            i.putExtra("urlImmagineViaggio", currentUrlImageTravel);
        }
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
        Log.i(TAG, "profiloNomiTappe: " + profiloNomiTappe);
        Log.i(TAG, "viaggioTappa: " + viaggioTappa);
        Log.i(TAG, "ho aggiunto i markedPoints di " + p);
    }






    private void findPlaceById(Profilo p, Tappa t) {
        if( TextUtils.isEmpty(t.getPoi().getCodicePOI()) || mGoogleApiClient == null){
            Log.i(TAG, "codice tappa: " + t.getPoi().getCodicePOI());
            Log.i(TAG, "return");
            return;
        }


        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        currentProfile = p;



        Log.i(TAG, "Profilo utente corrente in findPlace: " + profiloUtente);


        //Se sono presenti gia i nomi delle tappe non devo riprenderli
        if (profiloNomiTappe.get(p) != null) {

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
                        Log.i(TAG, "sono in onResult");
                        Log.i(TAG, "PlaceBuffer: " + places.toString());
                        Log.i(TAG, "Status PlaceBuffer: " + places.getStatus());
                        Log.i(TAG, "Count PlaceBuffer: " + places.getCount());

                        if (places.getStatus().isSuccess()) {
                            Place place = places.get(0);
                            Log.i(TAG, "nome place: " + place.getName());
                            Log.i(TAG, "idPlace " + place.getId());
                            Log.i(TAG, "titolo Viaggio " + combo.get(place.getId()));


                            googleMap.addMarker(new MarkerOptions()
                                    .title(combo.get(place.getId()).getNome()+"@@@"+combo.get(place.getId()).getCondivisioneDefault())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                    //  .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.default_male)))
                                    .position(place.getLatLng())
                            );


                            mapBoundsBuilder.include(place.getLatLng());
                            mapBounds = mapBoundsBuilder.build();

                            count++;

                            Log.i(TAG, "count: " + count);


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

                        Log.i(TAG, "result da queryViaggi: " + result);


                        if(result.equals("null\n")){
                            stringaFinale = getString(R.string.no_travels);
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
                                    urlImmagineViaggio = json_data.getString("idFotoViaggio");
                                    String condivisioneDefault = json_data.getString("livelloCondivisione");


                                    Viaggio viaggio = new Viaggio(codiceViaggio, nomeViaggio,urlImmagineViaggio, condivisioneDefault);

                                    Itinerario itinerario = new Itinerario(new Profilo(email),viaggio);

                                    int ordine = json_data.getInt("ordine");

                                    stringaFinale = email + " " + codiceViaggio  +" "+ nomeViaggio  +" "+ ordine + " "+ urlImmagineViaggio;
                                    Log.i(TAG, "result da queryDEST: " + stringaFinale);


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
                                Log.i(TAG, " combo finale: " + combo);
                                Log.i(TAG, " combo finale Codice: " + comboCodice);

                            }

                        }


                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e(TAG, "Errore nella connessione http "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "lista tappe: " + tappe);
            Log.i(TAG, "lista viaggi: " + nome);


            Log.i(TAG, "Profilo utente corrente: " + profiloUtente);

            AggiungiMarkedPointsOnMap(profiloUtente,tappe, nome);

            super.onPostExecute(aVoid);

        }
    }

}

package com.example.david.takeatrip.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.browse.MediaBrowser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Itinerario;
import com.example.david.takeatrip.Classes.POI;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Tappa;
import com.example.david.takeatrip.Classes.TappaAdapter;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ListaTappeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;


    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryTappe.php";
    private ListView lista;
    private ArrayList<Tappa> tappe;
    private String email, codiceViaggio;

    private TextView ViewCaricamentoInCorso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tappe);

        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .addApi(Places.GEO_DATA_API)
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        lista = (ListView)findViewById(R.id.listViewTappe);

        ViewCaricamentoInCorso = (TextView)findViewById(R.id.TextViewCaricamentoInCorso);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        tappe = new ArrayList<Tappa>();


        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
        }


        //TODO prova statica
        /* Date data = new Date(System.currentTimeMillis());
        Viaggio vi = new Viaggio("123", "Corsica 2013");
        Profilo pr = new Profilo("ciao@gmail.com", "giac", "lan");
        Itinerario it = new Itinerario(vi, pr, data, data);
        tappe.add(new Tappa(it, 1, data , null));
        tappe.add(new Tappa(it, 2, data , null));
        tappe.add(new Tappa(it, 3, data , null));
        PopolaLista(); */

        ViewCaricamentoInCorso.setVisibility(View.VISIBLE);

        MyTask mT = new MyTask();
        mT.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();

        }
    }

    private void PopolaLista(){

        final TappaAdapter adapter = new TappaAdapter(this,R.layout.entry_tappe_listview, tappe);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                final Tappa tappa = (Tappa) adattatore.getItemAtPosition(pos);
                Toast.makeText(getBaseContext(), "hai cliccato la tappa: " + tappa.getNome(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ListaTappeActivity.this, TappaActivity.class);

                startActivity(intent);

            }
        });

    }






/*
    public LatLng getGeoCoordsFromAddress(Context c, String address)
    {
        Geocoder geocoder = new Geocoder(c);
        List<Address> addresses;
        try
        {
            addresses = geocoder.getFromLocationName(address, 1);
            if(addresses.size() > 0)
            {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                Log.d("TEST", String.valueOf(latitude));
                Log.d("TEST", String.valueOf(longitude));
                return new LatLng(latitude, longitude);
            }
            else
            {
                return null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    */

/*
    private void findPlaceById( String id ) {
        if( TextUtils.isEmpty(id) || mGoogleApiClient == null){
            Log.i("TEST", "codice tappa: " + id);
            Log.i("TEST", "return");
            return;
        }
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();

            Places.GeoDataApi.getPlaceById( mGoogleApiClient, id ).setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    Log.i("TEST", "sono in onResult");

                    Log.i("TEST", "PlaceBuffer: " + places.toString());
                    Log.i("TEST", "Status PlaceBuffer: " + places.getStatus());
                    Log.i("TEST", "Count PlaceBuffer: " + places.getCount());



                    //Place place = places.get(0);
                    //Log.i("TEST", "nome place: " + place.getName());


                    if (places.getStatus().isSuccess()) {


                    }

                    //Release the PlaceBuffer to prevent a memory leak
                    places.release();
                }
            });




        }
    }
    */

    private void AggiungiMarkedPointsOnMap(List<Tappa> tappe) {

        mGoogleApiClient.connect();

        //findPlaceById(tappe.get(0).getPoi().getCodicePOI());



        Log.i("TEST", "codice tappa: " + tappe.get(0).getPoi().getCodicePOI());

        Places.GeoDataApi.getPlaceById( mGoogleApiClient, tappe.get(0).getPoi().getCodicePOI()).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                Log.i("TEST", "sono in onResult");

                Log.i("TEST", "PlaceBuffer: " + places.toString());
                Log.i("TEST", "Status PlaceBuffer: " + places.getStatus());
                Log.i("TEST", "Count PlaceBuffer: " + places.getCount());


                //Place place = places.get(0);
                //Log.i("TEST", "nome place: " + place.getName());


                if (places.getStatus().isSuccess()) {


                }

                //Release the PlaceBuffer to prevent a memory leak
                places.release();
            }
        });



        /*
        for(Tappa t : tappe){
            //LatLng latlng = getGeoCoordsFromAddress(this,t.getPoi().getCodicePOI());

            /*
            googleMap.addMarker(new MarkerOptions()
                    .title(t.getNome())
                    .snippet(t.getPaginaDiario())
                    .position(latlng));



        }

        */


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_viaggi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("TEST", "sono in onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("TEST", "sono in onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TEST", "sono in onConnectionFailed");

    }


    private class MyTask extends AsyncTask<Void, Void, Void> {
        private final static int DEFAULT_INT = 0;
        private static final String DEFAULT_STRING = "default";
        private static final String DEFAULT_DATE = "2010-01-11";
        InputStream is = null;
        String stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));



            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ADDRESS_PRELIEVO);
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

                        //Log.e("TEST", "json ricevuto:\n" + result);

                        JSONArray jArray = new JSONArray(result);

                        if(jArray != null && result != null){
                            for(int i=0;i<jArray.length();i++){
                                JSONObject json_data = jArray.getJSONObject(i);


                                String email = json_data.getString("emailProfilo");

                                //Log.e("TEST", "email:\n" + email);

                                String codiceViaggio = json_data.getString("codiceViaggio");

                                //Log.e("TEST", "codiceViaggio:\n" + codiceViaggio);

                                Itinerario itinerario = new Itinerario(new Profilo(email), new Viaggio(codiceViaggio));

                                int ordine = json_data.getInt("ordine");

                                //Log.e("TEST", "ordine:\n" + ordine);

                                stringaFinale = email + " " + codiceViaggio  +" "+ ordine;



                                int ordineTappaPrecedente = json_data.optInt("ordineTappaPrecedente", DEFAULT_INT);

                                //Log.e("TEST", "ordinePrec:\n" + ordineTappaPrecedente);

                                Tappa tappaPrecedente = new Tappa(itinerario, (ordineTappaPrecedente));


                                String paginaDiario = json_data.getString("paginaDiario");

                                //Log.e("TEST", "pagina diario:\n" + paginaDiario);


                                //String codicePOI = json_data.optString("codicePoi", DEFAULT_STRING);
                                String codicePOI = json_data.getString("codicePOI");
                                //Log.e("TEST", "codicePOI:\n" + codicePOI);

                                //String fontePOI = json_data.optString("fontePOI", DEFAULT_STRING);
                                String fontePOI = json_data.getString("fontePOI");
                                //Log.e("TEST", "fontePOI:\n" + fontePOI);


                                POI poi = new POI(codicePOI, fontePOI);


                                String dataString = json_data.optString("data", DEFAULT_DATE);
                                Date data = Date.valueOf(dataString);


                                //TODO rispristinare
                                //Date data = (Date) json_data.get("data");
                                //Date data = null;

                                //Log.e("TEST", "data:\n" + data);

                                //stringaFinale = itinerario + " " + ordine +" "+ tappaPrecedente +" "+ data +" "+ paginaDiario+" " + poi;
                                tappe.add(new Tappa(itinerario, ordine, tappaPrecedente, data, paginaDiario, poi));
                            }
                        }


                    } catch (Exception e) {
                        //Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                //Toast.makeText(getBaseContext(), "Errore nella connessione http " + e.toString(), Toast.LENGTH_LONG).show();
                //Log.e("TEST", "Errore nella connessione http "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //popolamento della ListView
            //Toast.makeText(getBaseContext(), "stringa risultante: "+ stringaFinale, Toast.LENGTH_LONG).show();
            PopolaLista();

            ViewCaricamentoInCorso.setVisibility(View.INVISIBLE);



            AggiungiMarkedPointsOnMap(tappe);



            super.onPostExecute(aVoid);

        }
    }
}

package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Itinerario;
import com.example.david.takeatrip.Classes.POI;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Tappa;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ListaTappeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/QueryTappe.php";
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Tappa> tappe;
    private ArrayList<String> nomiTappe;


    private String email, codiceViaggio, nomeViaggio;

    private NavigationView navigationView;
    private TextView ViewCaricamentoInCorso;
    private TextView ViewNomeViaggio;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tappe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ViewCaricamentoInCorso = (TextView)findViewById(R.id.TextViewCaricamentoInCorso);
        ViewNomeViaggio = (TextView)findViewById(R.id.textViewNomeViaggio);


        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tappe = new ArrayList<Tappa>();
        nomiTappe = new ArrayList<String>();


        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
        }

        ViewNomeViaggio.setText(nomeViaggio);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lista_tappe_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        Toast.makeText(getBaseContext(), "tappa selezionata" + tappe.get(id).getPoi().getCodicePOI(), Toast.LENGTH_LONG).show();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    private void CreaMenu(List<Tappa> tappe, List<String> nomiTappe){
        Menu menu = navigationView.getMenu();
        if(menu != null){
            int i=0;
            for(Tappa t : tappe){

                Log.i("TEST", "tappa: " + t.getPoi().getCodicePOI());

                if(nomiTappe.size() > 0){
                    Log.i("TEST", "nome tappa: " + nomiTappe.get(i));
                    menu.add(0, i, Menu.NONE, nomiTappe.get(i));
                }
                else{
                    menu.add(0, i, Menu.NONE, t.getPoi().getCodicePOI());
                }

                i++;
            }

        }
    }



    private void AggiungiMarkedPointsOnMap(List<Tappa> tappe) {
        mGoogleApiClient.connect();
        for(Tappa t : tappe){
            findPlaceById(t);
        }
    }



    private void findPlaceById( Tappa t) {
        if( TextUtils.isEmpty(t.getPoi().getCodicePOI()) || mGoogleApiClient == null){
            Log.i("TEST", "codice tappa: " + t.getPoi().getCodicePOI());
            Log.i("TEST", "return");
            return;
        }


        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
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
                            nomiTappe.add(place.getName().toString());
                            Log.i("TEST", "aggiunto ai nomi: " + nomiTappe);


                            googleMap.addMarker(new MarkerOptions()
                                    .title(place.getName().toString())
                                    .position(place.getLatLng()));

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5) );


                            if(nomiTappe.size() == tappe.size()){
                                Log.i("TEST", "nomi tappe: " + nomiTappe);
                                CreaMenu(tappe, nomiTappe);
                            }

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
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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

            ViewCaricamentoInCorso.setVisibility(View.INVISIBLE);

            AggiungiMarkedPointsOnMap(tappe);

            super.onPostExecute(aVoid);

        }
    }
}

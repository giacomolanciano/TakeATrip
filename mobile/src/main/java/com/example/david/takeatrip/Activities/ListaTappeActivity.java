package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Itinerario;
import com.example.david.takeatrip.Classes.POI;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Tappa;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.RoundedImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListaTappeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String ADDRESS_PRELIEVO = "QueryTappe.php";
    private final String TAG = "ListaTappeActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;

    private Profilo profiloUtenteLoggato;
    private Map<Profilo,List<Tappa>> profiloTappe;
    private Map<Profilo, List<Place>> profiloNomiTappe;

    private List<Profilo> partecipants;


    private String email, codiceViaggio, nomeViaggio;

    private NavigationView navigationView;
    private TextView ViewCaricamentoInCorso;
    private TextView ViewNomeViaggio;
    private FloatingActionButton buttonAddStop;
    private LinearLayout layoutProprietariItinerari;


    private boolean proprioViaggio = false;




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
        buttonAddStop = (FloatingActionButton) findViewById(R.id.buttonAddStop);
        buttonAddStop.setVisibility(View.INVISIBLE);
        layoutProprietariItinerari = (LinearLayout) findViewById(R.id.layoutProprietariItinerari);

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



        List<Tappa> listaTappe = new ArrayList<Tappa>();
        List<String> listaNomiTappe = new ArrayList<String>();
        partecipants = new ArrayList<Profilo>();
        profiloTappe = new HashMap<Profilo,List<Tappa>>();
        profiloNomiTappe = new HashMap<Profilo,List<Place>>();



        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
            ArrayList<CharSequence> listPartecipants = intent.getCharSequenceArrayListExtra("partecipanti");

            Log.i(TAG, "email profilo corrente: " + email+ " email partecipants: " + listPartecipants);

            for(CharSequence cs : listPartecipants){
                Profilo aux = new Profilo(cs.toString(), null,null,null, null, null, null, null, null, null);
                partecipants.add(aux);

                if(email.equals(cs.toString())){
                    proprioViaggio = true;
                    buttonAddStop.setVisibility(View.VISIBLE);

                    //questo campo deve puntare allo STESSO oggetto inserito nella lista partecipants
                    //altrimenti c'è bisogno di ridefinire equals(), che sbrasa searchActivity
                    profiloUtenteLoggato = aux;

                    Log.i("TEST", "sei compreso nel viaggio");
                }
            }

            Log.i(TAG, "email profilo corrente: " + email+ " profile partecipants: " + partecipants);


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

        //TODO gestire aggiornamento mappa, la chiamata all'asyntask provoca un inserimento errato dei partecipanti
        //new MyTask().execute();
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

        Toast.makeText(getBaseContext(), "tappa selezionata" + profiloTappe.get(0).get(id).getPoi().getCodicePOI(), Toast.LENGTH_LONG).show();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    private void PopolaPartecipanti(final Set<Profilo> partecipants){

        layoutProprietariItinerari.addView(new TextView(this), Constants.WIDTH_LAYOUT_PROPRIETARI_ITINERARI,
                Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);


        Log.i("TEST", "partecipants: " + partecipants);

        for(Profilo p : partecipants){

            ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getEmail());
            currentProfile = p;


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(Profilo p : partecipants){
                        if(p.getEmail().equals(v.getContentDescription())){
                            ClickImagePartecipant(p);
                            break;
                        }
                    }
                }
            });

            image.setImageResource(R.drawable.logodef);
            layoutProprietariItinerari.addView(image, Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI,
                    Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);
            layoutProprietariItinerari.addView(new TextView(this), Constants.WIDTH_LAYOUT_PROPRIETARI_ITINERARI,
                    Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);

        }

    }

    private void ClickImagePartecipant(Profilo p){
        AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
    }



    private void CreaMenu(List<Tappa> tappe, List<String > nomiTappe){
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

                            nomiTappe.add(place);
                            namesStops.add(place.getName().toString());
                            Log.i("TEST", "aggiunto ai places: " + nomiTappe);


                            googleMap.addMarker(new MarkerOptions()
                                    .title(place.getName().toString())
                                    .position(place.getLatLng()));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));


                            if (nomiTappe.size() == profiloTappe.get(currentProfile).size() && namesStops.size() == nomiTappe.size()) {
                                Log.i("TEST", "nomi places: " + namesStops);
                                CreaMenu(profiloTappe.get(currentProfile), namesStops);

                                profiloNomiTappe.put(currentProfile, nomiTappe);
                                Log.i("TEST", "profiloNomiTappe: " + profiloNomiTappe);
                                Log.i("TEST", "ho aggiunto i markedPoints di " + currentProfile);

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



//    public void ClickAddTappa(View view) {
//        Intent openAddTappa = new Intent(ListaTappeActivity.this, NuovaTappaActivity.class);
//
//        // passo all'attivazione dell'activity
//        startActivity(openAddTappa);
//    }



    public void onClickAddStop(View v){
        Intent intent = new Intent(ListaTappeActivity.this, NuovaTappaActivity.class);

        intent.putExtra("email", email);
        intent.putExtra("codiceViaggio", codiceViaggio);

        //TODO ricavare numero tappe itinerario utente
        intent.putExtra("ordine", calcolaNumUltimaTappaUtenteCorrente());

        startActivity(intent);

        //finish();
    }


    private int calcolaNumUltimaTappaUtenteCorrente() {

        int result = 0;

        Log.i("TEST", "profilo: "+profiloUtenteLoggato);
        Log.i("TEST", "mappa profiloTappe: " + profiloTappe);


        ArrayList<Tappa> listaTappe = (ArrayList<Tappa>) profiloTappe.get(profiloUtenteLoggato);

        Log.i("TEST", "lista tappe di "+ profiloUtenteLoggato+ ": "+listaTappe);

        if(listaTappe != null)
            result = listaTappe.size();

        Log.i("TEST", "result ordine tappa: " + result);

        return result;
    }





    private class MyTask extends AsyncTask<Void, Void, Void> {
        private final static int DEFAULT_INT = 0;
        private static final String DEFAULT_STRING = "default";
        private static final String DEFAULT_DATE = "2010-01-11";
        InputStream is = null;
        String stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {



            for(Profilo p : partecipants){

                List<Tappa> tappe = new ArrayList<Tappa>();

                ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
                dataToSend.add(new BasicNameValuePair("email", p.getEmail()));
                dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));


                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.ADDRESS_PRELIEVO+ADDRESS_PRELIEVO);
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
                                    String codiceViaggio = json_data.getString("codiceViaggio");

                                    //TODO
                                    Itinerario itinerario = new Itinerario(new Profilo(email), new Viaggio(codiceViaggio));

                                    int ordine = json_data.getInt("ordine");

                                    stringaFinale = email + " " + codiceViaggio  +" "+ ordine;


                                    int ordineTappaPrecedente = json_data.optInt("ordineTappaPrecedente", DEFAULT_INT);

                                    //Log.e("TEST", "ordinePrec:\n" + ordineTappaPrecedente);

                                    Tappa tappaPrecedente = new Tappa(itinerario, (ordineTappaPrecedente));


                                    String paginaDiario = json_data.getString("paginaDiario");
                                    String codicePOI = json_data.getString("codicePOI");
                                    String fontePOI = json_data.getString("fontePOI");



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
                    Log.e("TEST", "Errore nella connessione http "+e.toString());
                }


                profiloTappe.put(p, tappe);

            }




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ViewCaricamentoInCorso.setVisibility(View.INVISIBLE);


            Log.i("TEST", "profiloTappe: " + profiloTappe);
            Log.i("TEST", "numero profili: " + profiloTappe.size());
            Log.i("TEST", "profili: " + profiloTappe.keySet());
            Log.i("TEST", "tappe: " + profiloTappe.values());

            PopolaPartecipanti(profiloTappe.keySet());


            boolean aggiuntiMarkedPoints = false;

            //aggiungo sulla mappa solamente le tappe del profilo corrente, se partecipante al viaggio,
            //altrimenti aggiungo le tappe di un profilo casuale

            for(Profilo p : profiloTappe.keySet()){
                if(p.getEmail().equals(email)){
                    AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
                    aggiuntiMarkedPoints = true;
                    Log.i("TEST", "aggiunte tappe di " + p);

                    break;
                }
            }

            if(!aggiuntiMarkedPoints){
                for(Profilo p : profiloTappe.keySet()){
                    AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
                    Log.i("TEST", "aggiunte tappe di " + p);
                    break;
                }

            }

            super.onPostExecute(aVoid);

        }


    }
}

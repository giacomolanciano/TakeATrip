package com.takeatrip.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.takeatrip.AsyncTasks.GetStopsTask;
import com.takeatrip.AsyncTasks.InserimentoAudioTappaTask;
import com.takeatrip.AsyncTasks.InserimentoFiltroTask;
import com.takeatrip.AsyncTasks.InserimentoImmagineTappaTask;
import com.takeatrip.AsyncTasks.InserimentoNotaTappaTask;
import com.takeatrip.AsyncTasks.InserimentoTappaTask;
import com.takeatrip.AsyncTasks.InserimentoVideoTappaTask;
import com.takeatrip.AsyncTasks.LoadGenericImageTask;
import com.takeatrip.AsyncTasks.UploadFileS3Task;
import com.takeatrip.Classes.Itinerario;
import com.takeatrip.Classes.POI;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.Classes.Tappa;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.Interfaces.AsyncResponseInsertStop;
import com.takeatrip.Interfaces.AsyncResponseStops;
import com.takeatrip.R;
import com.takeatrip.Utilities.AudioRecord;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatesUtils;
import com.takeatrip.Utilities.RoundedImageView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ListaTappeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener, AsyncResponseStops, AsyncResponseInsertStop {

    private static final String TAG = "TEST ListaTappeAct";
    private static final int LIMIT_IMAGES_VIEWS = 10;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private MapFragment mapFragment;


    private ImageView addImage;
    private ImageView addVideo;
    private ImageView addRecord;
    private ImageView addNote;
    private TextView noteAdded;

    private Profilo profiloUtenteLoggato;

    //il profilo dell'utilizzatore è sempre il primo
    private Map<Profilo,List<Tappa>> profiloTappe;
    private Map<Profilo, List<Place>> profiloNomiTappe;

    private List<Profilo> partecipants;
    private List<Place> nomiTappe;

    private String email, codiceViaggio, nomeViaggio, urlImmagineViaggio;
    private String placeId, placeName, placeAddress, placeAttr;


    private Profilo profiloVisualizzazioneCorrente;
    private boolean visualizzazioneEsterna = false;
    private NavigationView navigationView;
    private TextView ViewCaricamentoInCorso;
    private TextView ViewNomeViaggio;
    private FloatingActionButton buttonAddStop;
    private LinearLayout layoutProprietariItinerari;
    private int ordine, checkSelectionSpinner = 0;
    LatLng placeLatLng;

    private int[] arr_images;
    private Dialog dialog;
    private TextView nameText;
    private TextView addressText;
    private Profilo currentProfile;
    private PolylineOptions polyline;
    private LatLngBounds.Builder mapBoundsBuilder;
    private LatLngBounds mapBounds;
    private boolean isCanceled, isRecordFileCreated;
    private int progressStatus;
    private Handler handler;

    private LinearLayout linearLayoutHeader;
    private LinearLayout layoutContents,rowHorizontal;
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    private RoundedImageView ViewImmagineViaggio;

    private ProgressDialog mProgressDialog;

    private String[] strings;
    private String[] subs;
    private Place[] arrayPlace;
    private String[] arrayNamePlace;
    private LatLng[] latLngs;

    //contents
    private AudioRecord record;
    private String imageFileName;
    private String videoFileName;
    private String livelloCondivisioneTappa;
    private String livelloCondivisioneDefaultViaggio;
    private List<Bitmap> immaginiSelezionate, videoSelezionati;
    private Map<Bitmap, String> bitmap_nomeFile;
    private Map<Bitmap, String> pathsImmaginiVideoSelezionati;
    private List<String> audioSelezionati;
    private List<String> noteInserite;

    // They depend on the screen density
    private int highContent = 30, widthContent = 60;

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
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);
        View layoutHeader = navigationView.getHeaderView(0);

        layoutProprietariItinerari = (LinearLayout) findViewById(R.id.layoutProprietariItinerari);
        ViewCaricamentoInCorso = (TextView) findViewById(R.id.TextViewCaricamentoInCorso);
        ViewNomeViaggio = (TextView) layoutHeader.findViewById(R.id.textViewNameTravel);
        linearLayoutHeader = (LinearLayout) layoutHeader.findViewById(R.id.layoutHeaderTravel);


        buttonAddStop = (FloatingActionButton) findViewById(R.id.fabAddStopInfoPoi);
        if (buttonAddStop != null)
            buttonAddStop.setVisibility(View.INVISIBLE);



        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .addApi(AppIndex.API).build();


        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapBoundsBuilder = new LatLngBounds.Builder();


        //Refer to contents of the stop
        partecipants = new ArrayList<Profilo>();
        profiloNomiTappe = new HashMap<Profilo, List<Place>>();
        immaginiSelezionate = new ArrayList<Bitmap>();
        videoSelezionati = new ArrayList<Bitmap>();
        bitmap_nomeFile = new HashMap<Bitmap,String>();
        pathsImmaginiVideoSelezionati = new HashMap<Bitmap, String>();
        noteInserite = new ArrayList<String>();
        audioSelezionati = new ArrayList<String>();


        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
            urlImmagineViaggio = intent.getStringExtra("urlImmagineViaggio");
            livelloCondivisioneDefaultViaggio = intent.getStringExtra("livelloCondivisione");
            livelloCondivisioneTappa = livelloCondivisioneDefaultViaggio;
            CharSequence[] namesPartecipants = intent.getCharSequenceArrayExtra("namesPartecipants");
            CharSequence[] listPartecipants = intent.getCharSequenceArrayExtra("partecipanti");
            CharSequence[] urlImagePartecipants = intent.getCharSequenceArrayExtra("urlImagePartecipants");
            CharSequence[] sessoPartecipants = intent.getCharSequenceArrayExtra("sessoPartecipants");

            if(email == null){
                TakeATrip TAT = (TakeATrip)getApplicationContext();
                email = TAT.getProfiloCorrente().getId();
            }


            //insert the travel image in the menu
            new BitmapWorkerTask(ViewImmagineViaggio, linearLayoutHeader).execute(urlImmagineViaggio);

            if (ViewNomeViaggio != null)
                ViewNomeViaggio.setText(nomeViaggio);


            //popolo i partecipanti al viaggio
            int i = 0;
            for (CharSequence cs : listPartecipants) {

                Profilo aux = new Profilo(cs.toString(), namesPartecipants[i].toString(), null, null, null,
                        sessoPartecipants[i].toString(), null, null, null,null, urlImagePartecipants[i].toString(), null);
                partecipants.add(aux);

                if (email.equals(cs.toString())) {
                    profiloVisualizzazioneCorrente = aux;
                    buttonAddStop.setVisibility(View.VISIBLE);
                    profiloUtenteLoggato = aux;
                }

                i++;
            }


            if(profiloUtenteLoggato == null){
                TakeATrip TAT = (TakeATrip) getApplicationContext();
                profiloUtenteLoggato = TAT.getProfiloCorrente();
            }

            if(profiloVisualizzazioneCorrente == null){
                visualizzazioneEsterna = true;
                buttonAddStop.setVisibility(View.INVISIBLE);
                profiloVisualizzazioneCorrente = partecipants.get(0);
            }
        }


        // This task build the map that associates each profile to the list of stops
        showProgressDialog();
        GetStopsTask mT = new GetStopsTask(ListaTappeActivity.this, partecipants, codiceViaggio);
        mT.delegate = ListaTappeActivity.this;
        mT.execute();


        //per dialog privacy level
        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;

        nomiTappe = new ArrayList<Place>();
        polyline = new PolylineOptions()
                .visible(true)
                .color(Color.parseColor(Constants.GOOGLE_MAPS_BLUE))
                .width(Constants.MAP_POLYLINE_THICKNESS)
                .geodesic(true);


        isCanceled = false;
        isRecordFileCreated = false;
        progressStatus = 0;
        handler = new Handler();

        setTitle(nomeViaggio);
    }


    @Override
    public void processFinishForStops(Map<Profilo, List<Tappa>> profilo_tappe) {

        if(profilo_tappe != null){
            ViewCaricamentoInCorso.setVisibility(View.INVISIBLE);
            profiloTappe = profilo_tappe;
            PopolaPartecipanti(profiloTappe.keySet());
            boolean aggiuntiMarkedPoints = false;

            //aggiungo sulla mappa solamente le tappe del profilo corrente, se partecipante al viaggio,
            //altrimenti aggiungo le tappe di un profilo casuale
            for(Profilo p : profiloTappe.keySet()){
                if(p.getId().equals(email)){
                    List<Tappa> aux = profiloTappe.get(p);
                    AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
                    aggiuntiMarkedPoints = true;

                    profiloVisualizzazioneCorrente = p;
                    break;
                }
            }

            if(!aggiuntiMarkedPoints){
                for(Profilo p : profiloTappe.keySet()){
                    AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
                    profiloVisualizzazioneCorrente =p;
                    break;
                }
            }
            ordine = calcolaNumUltimaTappaUtenteCorrente()+1;
        }
        else{
            buttonAddStop.setVisibility(View.INVISIBLE);
        }
        hideProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mGoogleApiClient.isConnected()){
            Log.i(TAG, "google api client is connected, disconnecting...");
            mGoogleApiClient.disconnect();
        }
        recreate();
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
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
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
        return super.onOptionsItemSelected(item);
    }


    //When click on item in the menu, open  TappaActivity
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id>0){

            ArrayList<Tappa> tappe = (ArrayList<Tappa>) profiloTappe.get(profiloVisualizzazioneCorrente);
            Tappa tappa = profiloTappe.get(profiloVisualizzazioneCorrente).get(id-1);

            Log.i(TAG, "tappa selezionata: " + tappa);

            Intent i = new Intent(this, TappaActivity.class);
            int ordineTappa = Integer.parseInt(item.getTitle().toString().split("\\. ")[0]);
            i.putExtra("email", email);
            i.putExtra("emailProprietarioTappa", profiloVisualizzazioneCorrente.getId());
            i.putExtra("codiceViaggio", codiceViaggio);
            i.putExtra("ordine", ordineTappa);
            i.putExtra("ordineDB", tappa.getOrdine());
            i.putExtra("nome", item.getTitle());
            i.putExtra("data", DatesUtils.getStringFromDate(tappa.getData(), Constants.DISPLAYED_DATE_FORMAT));
            i.putExtra("codAccount", 0);
            i.putExtra("livelloCondivisioneTappa", tappa.getLivelloCondivisione());
            i.putExtra("nomeViaggio", nomeViaggio);
            i.putParcelableArrayListExtra("tappeViaggio", tappe);



            if(profiloVisualizzazioneCorrente != profiloUtenteLoggato){
                i.putExtra("visualizzazioneEsterna","true");
            }

            startActivity(i);
            finish();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setMap(googleMap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (profiloVisualizzazioneCorrente.equals(profiloUtenteLoggato)) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(Constants.VIBRATION_MILLISEC);

                    try {
                        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

                        LatLngBounds bounds = new LatLngBounds(latLng, latLng);

                        intentBuilder.setLatLngBounds(bounds);
                        Intent intentPlacePicker = intentBuilder.build(ListaTappeActivity.this);
                        // Start the Intent by requesting a result, identified by a request code.
                        startActivityForResult(intentPlacePicker, Constants.REQUEST_PLACE_PICKER);


                    } catch (GooglePlayServicesRepairableException e) {
                        GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), ListaTappeActivity.this, 0);
                        Log.e(TAG, e.toString());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.e(TAG, e.toString());
                    }
                }

            }
        });

        googleMap.setOnInfoWindowClickListener(this);
    }


    public void onInfoWindowClick(Marker marker) {
        String nomeTappa = marker.getTitle();
        String labelTappa = nomeTappa.split("\\.")[0];
        int numeroTappa = Integer.parseInt(labelTappa)-1;

        ArrayList<Tappa> tappe = (ArrayList<Tappa>) profiloTappe.get(profiloVisualizzazioneCorrente);

        Tappa tappaSelezionata = profiloTappe.get(profiloVisualizzazioneCorrente).get(numeroTappa);
        int ordineTappa = Integer.parseInt(nomeTappa.split("\\. ")[0]);

        Intent i = new Intent(this, TappaActivity.class);

        if(profiloVisualizzazioneCorrente != profiloUtenteLoggato){
            i.putExtra("visualizzazioneEsterna","true");
        }
        i.putExtra("email", email);
        i.putExtra("emailProprietarioTappa", profiloVisualizzazioneCorrente.getId());
        i.putExtra("codiceViaggio", codiceViaggio);
        i.putExtra("ordine", ordineTappa);
        i.putExtra("ordineDB", tappaSelezionata.getOrdine());
        i.putExtra("nome", nomeTappa);
        i.putExtra("livelloCondivisioneTappa", tappaSelezionata.getLivelloCondivisione());
        i.putExtra("nomeViaggio", nomeViaggio);
        i.putParcelableArrayListExtra("tappeViaggio", tappe);


        Calendar cal = Calendar.getInstance();
        cal.setTime(tappaSelezionata.getData());
        i.putExtra("data", DatesUtils.getStringFromDate(tappaSelezionata.getData(), Constants.DISPLAYED_DATE_FORMAT));
        startActivity(i);
        finish();
    }






    private void PopolaPartecipanti(final Set<Profilo> partecipants){

        int widthLayoutProprietari = Constants.WIDTH_LAYOUT_PROPRIETARI_ITINERARI;
        int heighLayoutProprietari = Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI;

        float density = getResources().getDisplayMetrics().density;

        if(density == 3.0 || density == 4.0){
            widthLayoutProprietari = widthLayoutProprietari*2;
            heighLayoutProprietari = heighLayoutProprietari*2;
        }

        layoutProprietariItinerari.addView(new TextView(this), widthLayoutProprietari,
                heighLayoutProprietari);

        for(Profilo p : partecipants){
            ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getId());
            currentProfile = p;
            if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
                URL completeUrl = null;
                try {
                    completeUrl = new LoadGenericImageTask(p.getIdImageProfile(), this).execute().get();
                    Picasso.with(this)
                            .load(completeUrl.toString())
                            .resize(heighLayoutProprietari, heighLayoutProprietari)
                            .into(image);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                if(p.getSesso().equals("M"))
                    image.setImageResource(R.drawable.default_male);
                else
                    image.setImageResource(R.drawable.default_female);
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Profilo p : partecipants) {
                        if (p.getId().equals(v.getContentDescription())) {
                            ClickImagePartecipant(p);
                            break;
                        }
                    }
                }
            });

            layoutProprietariItinerari.addView(image, heighLayoutProprietari,
                    heighLayoutProprietari);
            layoutProprietariItinerari.addView(new TextView(this), widthLayoutProprietari,
                    heighLayoutProprietari);

        }
    }


    private void ClickImagePartecipant(Profilo p){
        profiloVisualizzazioneCorrente = p;

        if(!p.getId().equals(profiloUtenteLoggato.getId()))
            buttonAddStop.setVisibility(View.INVISIBLE);
        else
            buttonAddStop.setVisibility(View.VISIBLE);

        AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
    }


    private void AggiungiMarkedPointsOnMap(Profilo p, List<Tappa> tappe) {
        mGoogleApiClient.connect();

        googleMap.clear();
        polyline = new PolylineOptions()
                .visible(true)
                .color(Color.parseColor(Constants.GOOGLE_MAPS_BLUE))
                .width(Constants.MAP_POLYLINE_THICKNESS)
                .geodesic(true);
        nomiTappe.clear();

        int i = 1;
        arrayPlace = new Place[tappe.size()];
        arrayNamePlace = new String[tappe.size()];
        latLngs = new LatLng[tappe.size()];

        for(Tappa t : tappe){
            findPlaceById(p, t, i);
            i++;
        }

        //Svuota anche il menu
        if(tappe.size()==0){
            nomiTappe.clear();
            profiloNomiTappe.put(p,nomiTappe);
            CreaMenu(p,tappe);
        }
    }

    private void findPlaceById(final Profilo p, final Tappa t, int i) {
        final int index = i;

        if(mGoogleApiClient == null)
            return;

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        currentProfile = p;

        Places.GeoDataApi.getPlaceById( mGoogleApiClient, t.getPoi().getCodicePOI() )
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override
                    public void onResult(PlaceBuffer places) {

                        if (places.getStatus().isSuccess()) {
                            Place place = places.get(0);

                            LatLng currentLatLng = place.getLatLng();


                            //serve per mantenere l'ordine nella lista
                            int k=0;
                            for(Tappa t : profiloTappe.get(p)){
                                if(t.getPoi().getCodicePOI().equals(place.getId())){
                                    arrayPlace[k] = place;
                                    arrayNamePlace[k] = place.getName().toString();
                                    latLngs[k] = place.getLatLng();

                                    t.setName(arrayNamePlace[k]);


                                    break;
                                }
                                k++;
                            }

                            //add Marker
                            googleMap.addMarker(new MarkerOptions()
                                    .title(index + ". " + place.getName().toString())
                                    .position(currentLatLng)
                            );
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));

                            nomiTappe.add(place);
                            if (nomiTappe.size() == profiloTappe.get(currentProfile).size()) {
                                for(int i=0; i<arrayNamePlace.length; i++){
                                    if(arrayNamePlace[i] != null){
                                        polyline.add(latLngs[i]);
                                        mapBoundsBuilder.include(latLngs[i]);
                                    }
                                }

                                CreaMenu(currentProfile,profiloTappe.get(currentProfile));
                                profiloNomiTappe.put(currentProfile, nomiTappe);

                                //traccia linea
                                googleMap.addPolyline(polyline);

                                //update zoom
                                mapBounds = mapBoundsBuilder.build();
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mapBounds,
                                        Constants.LATLNG_BOUNDS_PADDING);
                                googleMap.moveCamera(cu);
                            }

                        }
                        //Release the PlaceBuffer to prevent a memory leak
                        places.release();
                    }
                });
    }

    private void CreaMenu(Profilo p, List<Tappa> tappe){
        Menu menu = navigationView.getMenu();
        menu.clear();
        menu.add(0, 0, Menu.NONE, p.getName()+"'s stops:");

        if(menu != null) {
            int i = 0;
            for (Tappa t : tappe) {
                menu.add(0, i + 1, Menu.NONE, (i+1) +". " + t.getName());
                i++;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_PLACE_PICKER:
                    Place place = PlacePicker.getPlace(this, data);
                    startAddingStop(place);
                    break;

                default:
                    Log.e(TAG, "requestCode non riconosciuto");
                    break;
            }

        } else {
            Log.e(TAG, "onActivityResult result: " + resultCode);
        }
    }


    /*
    * Da qui la gestione di una nuova tappa con i relativi contenuti
    *
    * */
    public void onClickAddStop(View v){

        pathsImmaginiVideoSelezionati.clear();
        immaginiSelezionate.clear();
        videoSelezionati.clear();
        bitmap_nomeFile.clear();
        audioSelezionati.clear();
        noteInserite.clear();

        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intentPlacePicker = intentBuilder.build(ListaTappeActivity.this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intentPlacePicker, Constants.REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), ListaTappeActivity.this, 0);
            Log.e(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void startAddingStop(final Place place) {
        final Place addedPlace = place;

        //prendere info poi
        placeId = ""+place.getId();
        placeName = ""+place.getName();
        placeLatLng = place.getLatLng();
        placeAddress = ""+place.getAddress();
        placeAttr = "";

        CharSequence aux = place.getAttributions();
        if(aux != null)
            placeAttr += aux;

        dialog = new Dialog(ListaTappeActivity.this);
        dialog.setContentView(R.layout.info_poi);
        dialog.setTitle(getResources().getString(R.string.insert_new_stop));

        Spinner mySpinner = (Spinner)dialog.findViewById(R.id.spinner);
        final PrivacyLevelAdapter adapter = new PrivacyLevelAdapter(ListaTappeActivity.this, R.layout.entry_privacy_level, strings);
        String livelloMaiuscolo = livelloCondivisioneDefaultViaggio.substring(0,1).toUpperCase()
                + livelloCondivisioneDefaultViaggio.substring(1,livelloCondivisioneDefaultViaggio.length());

        final int spinnerPosition = adapter.getPosition(livelloMaiuscolo);

        mySpinner.setAdapter(adapter);
        mySpinner.setSelection(spinnerPosition);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(checkSelectionSpinner > 0){
                    livelloCondivisioneTappa = adapter.getItem(position).toString();
                }
                checkSelectionSpinner++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        layoutContents = (LinearLayout) dialog.findViewById(R.id.layoutContents);


        //put dialog at bottom
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        nameText = (TextView) dialog.findViewById(R.id.POIName);
        addressText = (TextView) dialog.findViewById(R.id.POIAddress);
        nameText.setText(placeName);
        addressText.setText(placeAddress);

        //setting listener pulsanti dialog

        FloatingActionButton addStop = (FloatingActionButton) dialog.findViewById(R.id.fabAddStopInfoPoi);
        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int stopOrder = calcolaNumUltimaTappaUtenteCorrente()+1;

                //Insert all the content of the stop
                InserimentoTappaTask ITT = new InserimentoTappaTask(ListaTappeActivity.this, email, codiceViaggio, ordine, placeId, placeName, livelloCondivisioneTappa);
                ITT.delegate = ListaTappeActivity.this;
                ITT.execute();

                //per prevenire crash se si clicca sul marker appena aggiunto
                Itinerario itAux = new Itinerario(profiloUtenteLoggato, new Viaggio(codiceViaggio));

                Calendar cal = DatesUtils.getDateFromString(DatesUtils.getCurrentDateString(), Constants.DATABASE_DATE_FORMAT);

                POI poiAdded = new POI(placeId, "google");
                Tappa stopAdded = new Tappa(itAux, stopOrder, cal.getTime(), livelloCondivisioneTappa);
                stopAdded.setName(placeName);
                stopAdded.setPoi(poiAdded);

                profiloTappe.get(profiloVisualizzazioneCorrente).add(stopAdded);

                new InserimentoFiltroTask(ListaTappeActivity.this, codiceViaggio, placeName).execute();

                //add marker
                googleMap.addMarker(new MarkerOptions()
                        .title(stopOrder + ". " + placeName)
                        .position(placeLatLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addedPlace.getLatLng(), Constants.DEFAULT_ZOOM_MAP));

                //update polyline
                polyline.add(addedPlace.getLatLng());
                googleMap.addPolyline(polyline);


                //update zoom
                mapBoundsBuilder.include(addedPlace.getLatLng());
                mapBounds = mapBoundsBuilder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mapBounds, Constants.LATLNG_BOUNDS_PADDING);
                googleMap.moveCamera(cu);

                CreaMenu(profiloVisualizzazioneCorrente,profiloTappe.get(profiloVisualizzazioneCorrente));

                dialog.dismiss();
            }
        });

        noteAdded = (TextView) dialog.findViewById(R.id.noteAdded);
        noteAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddNote(view);
            }
        });


        addNote = (ImageView)dialog.findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddNote(view);
            }
        });

        dialog.show();

    }



    private void onClickAddNote(View v) {
        try {

            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(wrapper);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.material_edit_text, null);
            builder.setView(dialogView);

            textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.textInputLayout);
            textInputEditText = (TextInputEditText) dialogView.findViewById(R.id.editText);

            if(noteInserite.size() == 1){
                if (textInputLayout != null) {
                    textInputLayout.setCounterEnabled(true);
                    textInputLayout.setCounterMaxLength((Constants.NOTE_MAX_LENGTH - noteInserite.get(0).length()));
                }
            }
            else{

                if (textInputLayout != null) {
                    textInputLayout.setCounterEnabled(true);
                    textInputLayout.setCounterMaxLength(Constants.NOTE_MAX_LENGTH);
                }
            }

                builder.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(noteInserite.size() == 0)
                                    noteInserite.add(textInputEditText.getText().toString());
                                else{
                                    noteInserite.remove(0);
                                    noteInserite.add(textInputEditText.getText().toString());
                                }
                                noteAdded.setText(noteInserite.get(0));

                                if (!noteInserite.get(0).equals("")){
                                    addNote.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_blue_36dp));
                                }
                                else{
                                    addNote.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_36dp));
                                    if(noteInserite.size() >0)
                                        noteInserite.remove(0);
                                }

                            }
                        });


                builder.setTitle(getString(R.string.labelNote));

                android.support.v7.app.AlertDialog dialog = builder.create();


            dialog.show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }

    }


    @Override
    public void processFinishForInsertStop() {
        if(!noteInserite.isEmpty()) {
            new InserimentoNotaTappaTask(ListaTappeActivity.this, ordine, codiceViaggio, email,
                    livelloCondivisioneTappa, noteInserite).execute();
        }

        if(!immaginiSelezionate.isEmpty()){
            for(Bitmap bitmap : immaginiSelezionate) {

                String nameImage = bitmap_nomeFile.get(bitmap);
                String pathImage = pathsImmaginiVideoSelezionati.get(bitmap);


                try {
                    boolean result = new UploadFileS3Task(ListaTappeActivity.this, Constants.BUCKET_TRAVELS_NAME,
                            codiceViaggio, Constants.TRAVEL_IMAGES_LOCATION, email, pathImage, nameImage).execute().get();

                    if(result){
                        String completePath = codiceViaggio + "/" + Constants.TRAVEL_IMAGES_LOCATION + "/" + email + "_" + nameImage;

                        new InserimentoImmagineTappaTask(ListaTappeActivity.this, email,codiceViaggio,
                                ordine,null,completePath,livelloCondivisioneTappa).execute();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



            }

        }


        if(!videoSelezionati.isEmpty()){
            for(Bitmap bitmap : videoSelezionati) {

                String nameVideo = bitmap_nomeFile.get(bitmap);
                String pathVideo = pathsImmaginiVideoSelezionati.get(bitmap);

                Log.i(TAG, "email: " + email);
                Log.i(TAG, "codiceViaggio: " + codiceViaggio);
                Log.i(TAG, "name of the video: " + nameVideo);
                Log.i(TAG, "livello Condivisione: " + livelloCondivisioneTappa);


                try {
                    boolean result = new UploadFileS3Task(ListaTappeActivity.this, Constants.BUCKET_TRAVELS_NAME,
                            codiceViaggio, Constants.TRAVEL_VIDEOS_LOCATION, email, pathVideo, nameVideo).execute().get();

                    if(result){
                        String completePath = codiceViaggio + "/" + Constants.TRAVEL_VIDEOS_LOCATION + "/" + email + "_" + nameVideo;

                        new InserimentoVideoTappaTask(ListaTappeActivity.this, email,codiceViaggio,
                                ordine,null,completePath,livelloCondivisioneTappa).execute();
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



            }

        }

        if(!audioSelezionati.isEmpty()) {

            String newAudioName;
            String timeStamp;


            for(String pathAudio : audioSelezionati) {

                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                newAudioName = timeStamp + Constants.AUDIO_EXT;

                try {
                    boolean result = new UploadFileS3Task(ListaTappeActivity.this, Constants.BUCKET_TRAVELS_NAME,
                            codiceViaggio, Constants.TRAVEL_AUDIO_LOCATION, email, pathAudio, newAudioName).execute().get();

                    if(result){
                        String completePath = codiceViaggio + "/" + Constants.TRAVEL_AUDIO_LOCATION + "/" + email + "_" + newAudioName;

                        new InserimentoAudioTappaTask(ListaTappeActivity.this, email,codiceViaggio,
                                ordine,null,completePath,livelloCondivisioneTappa).execute();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }

        }


        //NB il clear() per le note viene chiamato alla fine del corrisposndente asyntask
        //altrimenti la lista viene svuotata prima della sua esecuzione
        //il problema non sussiste per queste altre, viene fatto partire un thread per ogni elemento
        pathsImmaginiVideoSelezionati.clear();
        immaginiSelezionate.clear();
        videoSelezionati.clear();
        bitmap_nomeFile.clear();
        audioSelezionati.clear();


        ordine += 1;
    }


    private class PrivacyLevelAdapter extends ArrayAdapter<String> {
        public PrivacyLevelAdapter(Context context, int textViewResourceId, String[] strings) {
            super(context, textViewResourceId, strings);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            convertView=inflater.inflate(R.layout.entry_privacy_level, parent, false);
            TextView label=(TextView)convertView.findViewById(R.id.privacyLevel);
            label.setText(strings[position]);

            TextView sub=(TextView)convertView.findViewById(R.id.description);
            sub.setText(subs[position]);

            ImageView icon=(ImageView)convertView.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);
            return convertView;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
        }
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

    public void onClickHome(View v) {
        // metodo per tornare alla home mantenendo i dati
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
    }


    private int calcolaNumUltimaTappaUtenteCorrente() {
        int result = 0;
        ArrayList<Tappa> listaTappe = (ArrayList<Tappa>) profiloTappe.get(profiloUtenteLoggato);
        Log.i(TAG, "lista tappe di " + profiloUtenteLoggato + ": " + listaTappe);
        if(listaTappe != null)
            result = listaTappe.size();

        return result;
    }
}

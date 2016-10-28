package com.takeatrip.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
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
import com.takeatrip.Utilities.DeviceStorageUtils;
import com.takeatrip.Utilities.RoundedImageView;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        ViewCaricamentoInCorso.setVisibility(View.INVISIBLE);

        profiloTappe = profilo_tappe;
        PopolaPartecipanti(profiloTappe.keySet());
        boolean aggiuntiMarkedPoints = false;

        //aggiungo sulla mappa solamente le tappe del profilo corrente, se partecipante al viaggio,
        //altrimenti aggiungo le tappe di un profilo casuale
        for(Profilo p : profiloTappe.keySet()){
            if(p.getEmail().equals(email)){
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
        //TODO find workaround to reload properly the map
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

        Tappa tappa = profiloTappe.get(profiloVisualizzazioneCorrente).get(id);
        Intent i = new Intent(this, TappaActivity.class);

        int ordineTappa = Integer.parseInt(item.getTitle().toString().split("\\. ")[0]);
        i.putExtra("email", email);
        i.putExtra("codiceViaggio", codiceViaggio);
        i.putExtra("ordine", ordineTappa);
        i.putExtra("nome", item.getTitle());
        i.putExtra("data", DatesUtils.getStringFromDate(tappa.getData(), Constants.DISPLAYED_DATE_FORMAT));
        i.putExtra("codAccount", 0);

        startActivity(i);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        //TODO il metodo non viene chiamato se la versione è Android 6.0+, trovare workaround

        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        TakeATrip TAT = (TakeATrip) getApplicationContext();
        TAT.setMap(googleMap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (profiloVisualizzazioneCorrente.equals(profiloUtenteLoggato)) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(Constants.VIBRATION_MILLISEC);

                    try {
                        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

                        //TODO gestire zoom con LatLngBounds
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

        Tappa tappaSelezionata = profiloTappe.get(profiloVisualizzazioneCorrente).get(numeroTappa);
        int ordineTappa = tappaSelezionata.getOrdine();

        Intent i = new Intent(this, TappaActivity.class);

        if(profiloVisualizzazioneCorrente != profiloUtenteLoggato){
            i.putExtra("visualizzazioneEsterna","true");
        }
        i.putExtra("email", email);
        i.putExtra("codiceViaggio", codiceViaggio);
        i.putExtra("ordine", ordineTappa);
        i.putExtra("nome", nomeTappa);

        Calendar cal = Calendar.getInstance();
        cal.setTime(tappaSelezionata.getData());
        i.putExtra("data", DatesUtils.getStringFromDate(tappaSelezionata.getData(), Constants.DISPLAYED_DATE_FORMAT));
        startActivity(i);
    }









    private void PopolaPartecipanti(final Set<Profilo> partecipants){
        layoutProprietariItinerari.addView(new TextView(this), Constants.WIDTH_LAYOUT_PROPRIETARI_ITINERARI,
                Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);

        for(Profilo p : partecipants){
            ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getEmail());
            currentProfile = p;
            if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
                URL completeUrl = null;
                try {
                    completeUrl = new LoadGenericImageTask(p.getIdImageProfile(), this).execute().get();
                    Picasso.with(this)
                            .load(completeUrl.toString())
                            .resize(Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI, Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI)
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
                        if (p.getEmail().equals(v.getContentDescription())) {
                            ClickImagePartecipant(p);
                            break;
                        }
                    }
                }
            });

            layoutProprietariItinerari.addView(image, Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI,
                    Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);
            layoutProprietariItinerari.addView(new TextView(this), Constants.WIDTH_LAYOUT_PROPRIETARI_ITINERARI,
                    Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);

        }
    }


    private void ClickImagePartecipant(Profilo p){
        profiloVisualizzazioneCorrente = p;

        Log.i(TAG,"PROFILO UTENTE CLICCATO: " + p);
        Log.i(TAG,"PROFILO UTENTE LOGGATO: " + profiloUtenteLoggato);

        if(!p.getEmail().equals(profiloUtenteLoggato.getEmail()))
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








    private static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        String result = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(proj[0]);
                result = cursor.getString(columnIndex);
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, "eccezione nel restituire il path: "+e.toString());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
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

                case Constants.REQUEST_IMAGE_CAPTURE:
                    File f = new File(DeviceStorageUtils.getImagesStoragePath());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(imageFileName)) {
                            f = temp;
                            break;
                        }
                    }
                    try {

                        Bitmap thumbnail = BitmapWorkerTask.decodeSampledBitmapFromPath(f.getAbsolutePath(), 0, 0);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

                        String nomeFile = timeStamp + Constants.IMAGE_EXT;
                        if(thumbnail != null){

                            pathsImmaginiVideoSelezionati.put(thumbnail, f.getAbsolutePath());
                            immaginiSelezionate.add(thumbnail);
                            bitmap_nomeFile.put(thumbnail,nomeFile);
                        }


                        //TODO: far partire direttamente TappaActivity


                        addImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_blue_36dp));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case Constants.REQUEST_IMAGE_PICK:
                    if (data != null) {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {

                            //TODO per selezione multipla, ancora non funzionante

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();

                                //In case you need image's absolute path
                                //String path= MultimedialFile.getRealPathFromURI(ListaTappeActivity.this, uri);
                                String path= getRealPathFromURI(ListaTappeActivity.this, uri);
                                Log.i(TAG, "image path: " + path);
                            }
                        } else {

                            layoutContents = data.getParcelableExtra("layoutContent");
                            Uri selectedImage = data.getData();

                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();

                            //TODO creazione bitmap per ora necessaria per far apparire miniature durante aggiunta tappa
                            //rivedere meccanismo usando Picasso
                            Bitmap thumbnail = BitmapWorkerTask.decodeSampledBitmapFromPath(picturePath, 0, 0);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                            String nomeFile = timeStamp + Constants.IMAGE_EXT;
                            if(thumbnail != null){
                                //inserire file in una lista di file per caricamento in s3
                                pathsImmaginiVideoSelezionati.put(thumbnail, picturePath);

                                immaginiSelezionate.add(thumbnail);
                                bitmap_nomeFile.put(thumbnail, nomeFile);
                            }
                        }

                    } else {
                        Log.e(TAG, "data is null");

                    }
                    break;

                case Constants.REQUEST_VIDEO_CAPTURE:

                    File fileVideo = new File(DeviceStorageUtils.getVideosStoragePath());
                    for (File temp : fileVideo.listFiles()) {
                        if (temp.getName().equals(videoFileName)) {
                            fileVideo = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bitmap;
                        bitmap = ThumbnailUtils.createVideoThumbnail(fileVideo.getAbsolutePath(),
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                        String nomeFile = timeStamp + Constants.VIDEO_EXT;
                        if(bitmap != null){
                            pathsImmaginiVideoSelezionati.put(bitmap, fileVideo.getAbsolutePath());
                            videoSelezionati.add(bitmap);
                            bitmap_nomeFile.put(bitmap,nomeFile);
                        }

                        Log.i(TAG, "path file video: " + fileVideo.getAbsolutePath());
                        Log.i(TAG, "bitmap file immagine: " + bitmap);

                    } catch (Exception e) {
                        Log.e(TAG,"thrown exception " + e);
                    }

                    break;

                case Constants.REQUEST_VIDEO_PICK:
                    Uri selectedVideo = data.getData();

                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor c = getContentResolver().query(selectedVideo, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String videoPath = c.getString(columnIndex);
                    c.close();

                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

                    String nomeFile = timeStamp + Constants.VIDEO_EXT;
                    if(thumbnail != null){
                        //inserire file in una lista di file per caricamento in s3
                        pathsImmaginiVideoSelezionati.put(thumbnail, videoPath);
                        videoSelezionati.add(thumbnail);
                        bitmap_nomeFile.put(thumbnail, nomeFile);
                    }

                    break;

                case Constants.REQUEST_RECORD_PICK:
                    Log.i(TAG, "REQUEST_RECORD_PICK");

                    Uri selectedAudio = data.getData();

                    String[] audioPath = {MediaStore.Audio.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedAudio, audioPath, null, null, null);
                    cursor.moveToFirst();
                    int columnIndexAudio = cursor.getColumnIndex(audioPath[0]);
                    String audioFilePath = cursor.getString(columnIndexAudio);
                    cursor.close();

                    audioSelezionati.add(audioFilePath);



                    break;
                default:
                    Log.e(TAG, "requestCode non riconosciuto");
                    break;
            }

        } else {
            Log.e(TAG, "onActivityResult result: " + resultCode);
        }
    }




    private void PopolaContenuti(){
        layoutContents = (LinearLayout) dialog.findViewById(R.id.layoutContents);

        layoutContents.removeAllViews();

        int i=0;
        for(Bitmap bitmap : immaginiSelezionate){
            if(i%LIMIT_IMAGES_VIEWS == 0){
                rowHorizontal = new LinearLayout(ListaTappeActivity.this);
                rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                //Log.i(TAG, "creato nuovo layout");
                layoutContents.addView(rowHorizontal);
                layoutContents.addView(new TextView(ListaTappeActivity.this), 10, 10);
            }


            final ImageView image = new ImageView(this, null);
            float density = getResources().getDisplayMetrics().density;
            Log.i(TAG, "density of the screen: " + density);

            if(density == 2.0){
                widthContent = 80;
                highContent = 40;
            }

            if(density == 3.0 || density == 4.0){
                widthContent = 100;
                highContent = 50;
            }

            Bitmap myBitmap = Bitmap.createScaledBitmap(bitmap, widthContent, highContent, true);
            image.setImageBitmap(myBitmap);

            rowHorizontal.addView(image, widthContent, highContent);
            i++;
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

    private void startAddingStop(Place place) {
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

        Log.i(TAG, "name: " + placeName);
        Log.i(TAG, "addr: " + placeAddress);

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
                InserimentoTappaTask ITT = new InserimentoTappaTask(ListaTappeActivity.this, email, codiceViaggio, ordine, placeId);
                ITT.delegate = ListaTappeActivity.this;
                ITT.execute();

                //per prevenire crash se si clicca sul marker appena aggiunto
                Itinerario itAux = new Itinerario(profiloUtenteLoggato, new Viaggio(codiceViaggio));

                Calendar cal = DatesUtils.getDateFromString(DatesUtils.getCurrentDateString(), Constants.DATABASE_DATE_FORMAT);

                POI poiAdded = new POI(placeId, "google");
                Tappa stopAdded = new Tappa(itAux, stopOrder, cal.getTime());
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


        /*
        addImage = (ImageView)dialog.findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddImage(view);
            }
        });

        addVideo = (ImageView)dialog.findViewById(R.id.addVideo);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddVideo(view);
            }
        });

        addRecord = (ImageView)dialog.findViewById(R.id.addRecord);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddRecord(view);
            }
        });
        */


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


    /*
    private void onClickAddImage(View v) {

        try {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            builder.setItems(R.array.CommandsAddPhotoToStop, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0: //pick images from gallery
                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentPick, Constants.REQUEST_IMAGE_PICK);


                            break;

                        case 1: //take a photo
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {

                                File photoFile = null;
                                try {

                                    photoFile = MultimedialFile.createImageFile();
                                    imageFileName = photoFile.getName();


                                } catch (IOException ex) {
                                    Log.e(TAG, "eccezione nella creazione di file immagine");
                                }

                                Log.i(TAG, "creato file immagine col nome: " +imageFileName);

                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);

                                }
                            }
                            break;


                        default:
                            Log.e(TAG, "azione non riconosciuta");
                            break;
                    }
                }
            });


            // Create the AlertDialog object and return it
            builder.create().show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }

    }


    private void onClickAddVideo(View v) {

        Log.i(TAG, "add video pressed");

        try {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this,
                    android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            builder.setItems(R.array.CommandsAddVideoToStop, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0: //pick videos from gallery

                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentPick, Constants.REQUEST_VIDEO_PICK);

                            //TODO per selezione multipla, non funzionante
//                            Intent intentPick = new Intent();
//                            intentPick.setType("video/*");
//                            intentPick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                            intentPick.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intentPick,"Select Video"),
//                                    Constants.REQUEST_VIDEO_PICK);

                            //TODO far diventare immagine blu

                            break;

                        case 1: //take a video
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {

                                File videoFile = null;
                                try {

                                    videoFile = MultimedialFile.createVideoFile();
                                    videoFileName = videoFile.getName();

                                } catch (IOException ex) {
                                    Log.e(TAG, "eccezione nella creazione di file video");
                                }

                                Log.i(TAG, "creato file video");

                                // Continue only if the File was successfully created
                                if (videoFile != null) {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                                    startActivityForResult(intent, Constants.REQUEST_VIDEO_CAPTURE);

                                    //TODO far diventare immagine blu
                                }
                            }
                            break;


                        default:
                            Log.e(TAG, "azione non riconosciuta");
                            break;
                    }
                }
            });


            // Create the AlertDialog object and return it
            builder.create().show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }

        Log.i(TAG, "END add video");


    }


    private void onClickAddRecord(View v) {

        try {
            final ContextThemeWrapper wrapper = new ContextThemeWrapper(this,
                    android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            builder.setItems(R.array.CommandsAddRecordToStop, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0: //pick audio from storage

                            Intent intentPick = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(intentPick, Constants.REQUEST_RECORD_PICK);

                            //TODO per selezione multipla, non funzionante
//                            Intent intentPick = new Intent();
//                            intentPick.setType("audio/*");
//                            intentPick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                            intentPick.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intentPick, "Select Record"),
//                                    Constants.REQUEST_IMAGE_PICK);

                            //TODO far diventare immagine blu

                            break;

                        case 1: //take a record

                            isCanceled = false;
                            isRecordFileCreated = false;
                            progressStatus = 0;


                            final ProgressDialog progressDialog = new ProgressDialog(ListaTappeActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setMax(Constants.MAX_RECORDING_TIME_IN_MILLISEC);
                            progressDialog.setTitle(getString(R.string.labelBeforeCaptureAudio));
                            //progressDialog.setMessage(getString(R.string.labelBeforeCaptureAudio));
                            progressDialog.setIndeterminate(false);
                            progressDialog.setCancelable(false);

                            //TODO formattare valore millisecondi per mostrare minuti
                            //progressDialog.setProgressNumberFormat("%1tL/%2tL");
                            progressDialog.setProgressNumberFormat(null);


                            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        // Set a click listener for progress dialog cancel button
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // dismiss the progress dialog
                                            progressDialog.dismiss();
                                            // Tell the system about cancellation
                                            isCanceled = true;

                                            if(isRecordFileCreated) {

                                                //TODO cancellare file

                                            }

                                            Log.i(TAG, "progress dialog canceled");
                                        }
                                    });


                            DialogInterface.OnClickListener listener = null;
                            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.start), listener);


                            progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {

                                    final Button b = progressDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    b.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {

                                            //TODO viene creato un file in locale, verificare se è utile manterlo
                                            isRecordFileCreated = true;
                                            record = new AudioRecord();
                                            record.startRecording();

                                            b.setText(getString(R.string.stop));
                                            b.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    isCanceled = true;

                                                    record.stopRecording();

                                                    progressDialog.dismiss();


                                                    Log.i(TAG, "file audio generato: " + record.getFileName());

                                                    audioSelezionati.add(record.getFileName());

                                                }
                                            });



                                            // Start the lengthy operation in a background thread
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    while (progressStatus < progressDialog.getMax()) {
                                                        // If user's click the cancel button from progress dialog
                                                        if (isCanceled) {
                                                            // Stop the operation/loop

                                                            Log.i(TAG, "thread stopped");

                                                            break;
                                                        }
                                                        // Update the progress status
                                                        progressStatus += Constants.ONE_SEC_IN_MILLISEC;

                                                        try {
                                                            Thread.sleep(Constants.ONE_SEC_IN_MILLISEC);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }


                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.setProgress(progressStatus);

                                                                if (progressStatus == progressDialog.getMax()) {

                                                                    Log.i(TAG, "thread stopped");

                                                                    record.stopRecording();

                                                                    progressDialog.dismiss();

                                                                    Log.i(TAG, "file audio generato: " + record.getFileName());

                                                                    audioSelezionati.add(record.getFileName());
                                                                }
                                                            }
                                                        });


                                                    }
                                                }
                                            }).start();

                                        }
                                    });
                                }
                            });

                            progressDialog.show();


                            break;


                        default:
                            break;
                    }
                }
            });

            builder.create().show();
        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }
    }
    */


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
                                Log.i(TAG, "edit text dialog canceled");
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
                                Log.i(TAG, "note inserite: " + noteInserite);
                                noteAdded.setText(noteInserite.get(0));

                                if (!noteInserite.get(0).equals("")){
                                    addNote.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_blue_36dp));
                                }
                                else{
                                    addNote.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_36dp));
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

                Log.i(TAG, "email: " + email);
                Log.i(TAG, "codiceViaggio: " + codiceViaggio);
                Log.i(TAG, "name of the image: " + nameImage);



                new UploadFileS3Task(ListaTappeActivity.this, Constants.BUCKET_TRAVELS_NAME,
                        codiceViaggio, Constants.TRAVEL_IMAGES_LOCATION, email, pathImage, nameImage).execute();


                //TODO nella colonna urlImmagine si potrebbe salvare soltanto il nome del file
                //si può riscostruire il path a partire dalle altre info nella riga corrispondente

                String completePath = codiceViaggio + "/" + Constants.TRAVEL_IMAGES_LOCATION + "/" + email + "_" + nameImage;

                new InserimentoImmagineTappaTask(ListaTappeActivity.this, email,codiceViaggio,
                        ordine,null,completePath,livelloCondivisioneTappa).execute();

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



                new UploadFileS3Task(ListaTappeActivity.this, Constants.BUCKET_TRAVELS_NAME,
                        codiceViaggio, Constants.TRAVEL_VIDEOS_LOCATION, email, pathVideo, nameVideo).execute();

                String completePath = codiceViaggio + "/" + Constants.TRAVEL_VIDEOS_LOCATION + "/" + email + "_" + nameVideo;

                new InserimentoVideoTappaTask(ListaTappeActivity.this, email,codiceViaggio,
                        ordine,null,completePath,livelloCondivisioneTappa).execute();

            }

        }

        if(!audioSelezionati.isEmpty()) {

            String newAudioName;
            String timeStamp;


            for(String pathAudio : audioSelezionati) {

                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                newAudioName = timeStamp + Constants.AUDIO_EXT;

                new UploadFileS3Task(ListaTappeActivity.this, Constants.BUCKET_TRAVELS_NAME,
                        codiceViaggio, Constants.TRAVEL_AUDIO_LOCATION, email, pathAudio, newAudioName).execute();

                String completePath = codiceViaggio + "/" + Constants.TRAVEL_AUDIO_LOCATION + "/" + email + "_" + newAudioName;

                new InserimentoAudioTappaTask(ListaTappeActivity.this, email,codiceViaggio,
                        ordine,null,completePath,livelloCondivisioneTappa).execute();
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

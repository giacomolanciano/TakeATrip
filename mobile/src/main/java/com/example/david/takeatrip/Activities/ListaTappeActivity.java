package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.app.AlertDialog;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Itinerario;
import com.example.david.takeatrip.Classes.POI;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.Tappa;
import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveId;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveIdCover;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.AudioRecord;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatesUtils;
import com.example.david.takeatrip.Utilities.DownloadImageTask;
import com.example.david.takeatrip.Utilities.MultimedialFile;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UploadFilePHP;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveId;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.CollationElementIterator;
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
        AsyncResponseDriveId, AsyncResponseDriveIdCover, GoogleMap.OnInfoWindowClickListener {

    private final String ADDRESS_PRELIEVO_TAPPE = "QueryTappe.php";
    private final String ADDRESS_INSERIMENTO_TAPPA = "InserimentoTappa.php";
    private final String ADDRESS_INSERIMENTO_FILTRO = "InserimentoFiltro.php";
    private final int QUALITY_OF_IMAGE = Constants.QUALITY_PHOTO;


    private final int LIMIT_IMAGES_VIEWS = 10;


    private final String TAG = "ListaTappeActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;

    private Profilo profiloUtenteLoggato;

    //il profilo dell'utilizzatore è sempre il primo
    private Map<Profilo,List<Tappa>> profiloTappe;
    private int itinerarioVisualizzato;
    private Profilo profiloVisualizzazioneCorrente;
    private boolean visualizzazioneEsterna = false;


    private Map<Profilo, List<Place>> profiloNomiTappe;

    private List<Profilo> partecipants;
    private List<Tappa> stops;


    private String email, codiceViaggio, nomeViaggio, urlImmagineViaggio;

    private NavigationView navigationView;
    private TextView ViewCaricamentoInCorso;
    private TextView ViewNomeViaggio;
    private FloatingActionButton buttonAddStop;
    private LinearLayout layoutProprietariItinerari;


    private boolean proprioViaggio = false;

    private int ordine, codAccount;

    private String placeId, placeName, placeAddress, placeAttr;
    LatLng placeLatLng;

    private String[] strings;
    private String[] subs;
    private int[] arr_images;

    private Dialog dialog;
    private TextView nameText;
    private TextView addressText;

    private Profilo currentProfile;
    private List<Place> nomiTappe;
    private List<String> namesStops;
    private PolylineOptions polyline;

    private LatLngBounds.Builder mapBoundsBuilder;
    private LatLngBounds mapBounds;

    private boolean isCanceled, isRecordFileCreated;
    private int progressStatus;
    private Handler handler;
    private CollationElementIterator tv;
    private AudioRecord record;

    private DriveId idFolder;
    private String imageFileName, mCurrentPhotoPath;
    private String videoFileName, mCurrentVideoPath;
    private List<Bitmap> immaginiSelezionate, immaginiUpload;
    private Map<Bitmap,String> bitmap_nomeFile;
    private String livelloCondivisioneTappa;

    private LinearLayout layoutContents,rowHorizontal;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    private RoundedImageView ViewImmagineViaggio;

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
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            //TODO capire perchè da eccezione sporadicamente
            Log.e("TEST", "navigationView is null");
        }

        View layoutHeader = navigationView.getHeaderView(0);
        
        ViewNomeViaggio = (TextView) layoutHeader.findViewById(R.id.textViewNameTravel);
        ViewImmagineViaggio = (RoundedImageView) layoutHeader.findViewById(R.id.imageView_round);



        

        buttonAddStop = (FloatingActionButton) findViewById(R.id.fabAddStopInfoPoi);
        if (buttonAddStop != null) {
            buttonAddStop.setVisibility(View.INVISIBLE);
        } else {
            //TODO capire perchè da eccezione sporadicamente
            Log.e("TEST", "buttonAddStop is null");
        }

        layoutProprietariItinerari = (LinearLayout) findViewById(R.id.layoutProprietariItinerari);
        ViewCaricamentoInCorso = (TextView) findViewById(R.id.TextViewCaricamentoInCorso);


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

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapBoundsBuilder = new LatLngBounds.Builder();


        List<Tappa> listaTappe = new ArrayList<Tappa>();
        List<String> listaNomiTappe = new ArrayList<String>();
        partecipants = new ArrayList<Profilo>();
        profiloTappe = new HashMap<Profilo, List<Tappa>>();
        profiloNomiTappe = new HashMap<Profilo, List<Place>>();
        immaginiSelezionate = new ArrayList<Bitmap>();
        immaginiUpload = new ArrayList<Bitmap>();
        bitmap_nomeFile = new HashMap<Bitmap,String>();



        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            nomeViaggio = intent.getStringExtra("nomeViaggio");
            urlImmagineViaggio = intent.getStringExtra("urlImmagineViaggio");
            new DownloadImageTask(ViewImmagineViaggio).execute(Constants.ADDRESS_TAT + urlImmagineViaggio);
            CharSequence[] listPartecipants = intent.getCharSequenceArrayExtra("partecipanti");
            CharSequence[] urlImagePartecipants = intent.getCharSequenceArrayExtra("urlImagePartecipants");
            CharSequence[] sessoPartecipants = intent.getCharSequenceArrayExtra("sessoPartecipants");


            int i = 0;
            for (CharSequence cs : listPartecipants) {

                Profilo aux = new Profilo(cs.toString(), null, null, null, null, sessoPartecipants[i].toString(),
                                                        null, null, null,null, urlImagePartecipants[i].toString(), null);
                partecipants.add(aux);

                if (email.equals(cs.toString())) {
                    proprioViaggio = true;
                    //itinerarioVisualizzato = 0;
                    profiloVisualizzazioneCorrente = aux;
                    buttonAddStop.setVisibility(View.VISIBLE);

                    //questo campo deve puntare allo STESSO oggetto inserito nella lista partecipants
                    //altrimenti c'è bisogno di ridefinire equals(), che sbrasa searchActivity
                    profiloUtenteLoggato = aux;

                    Log.i("TEST", "sei compreso nel viaggio");
                }

                i++;
            }

            if(profiloVisualizzazioneCorrente == null){
                visualizzazioneEsterna = true;
                buttonAddStop.setVisibility(View.INVISIBLE);
                profiloVisualizzazioneCorrente = partecipants.get(0);
            }

        }

        if (ViewNomeViaggio != null) {
            ViewNomeViaggio.setText(nomeViaggio);
        } else {
            Log.e("TEST", "viewNomeViaggio is null");
        }

        MyTask mT = new MyTask();
        mT.execute();


        //per dialog privacy level
        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;


        nomiTappe = new ArrayList<Place>();
        namesStops = new ArrayList<String>();
        polyline = new PolylineOptions()
                .visible(true)
                .color(Color.parseColor(Constants.GOOGLE_MAPS_BLUE))
                .width(Constants.MAP_POLYLINE_THICKNESS)
                .geodesic(true);


        isCanceled = false;
        isRecordFileCreated = false;
        progressStatus = 0;
        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
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

        Tappa tappa = profiloTappe.get(profiloVisualizzazioneCorrente).get(id);
        Intent i = new Intent(this, TappaActivity.class);

        i.putExtra("email", email);
        i.putExtra("codiceViaggio", codiceViaggio);
        i.putExtra("ordine", tappa.getOrdine());
        i.putExtra("nome", item.getTitle());
        i.putExtra("data", tappa.getData().toString());

        //TODO sarà inutile una volta modificato il database
        i.putExtra("codAccount", 0);


        startActivity(i);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void PopolaContenuti(){
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

            Bitmap myBitmap = Bitmap.createScaledBitmap(bitmap, 60, 30, true);
            image.setImageBitmap(myBitmap);

            //TODO: sistemare in funzione dello schermo e migliorare allocazione memoria usando thread
            rowHorizontal.addView(image, 60, 30);

            i++;
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Constants.REQUEST_IMAGE_CAPTURE:

                    Log.i("TEST", "REQUEST_IMAGE_CAPTURE");

                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(imageFileName)) {
                            f = temp;
                            break;
                        }
                    }
                    try {

                        Bitmap thumbnail = DownloadImageTask.decodeSampledBitmapFromPath(f.getAbsolutePath(),0,0);


                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

                        String nomeFile = timeStamp + ".jpg";

                        Log.i("TEST", "timeStamp image: " + nomeFile);
                        if(thumbnail != null){
                            immaginiSelezionate.add(thumbnail);
                            bitmap_nomeFile.put(thumbnail,nomeFile);
                        }

                        Log.i("TEST", "path file immagine: " + f.getAbsolutePath());
                        Log.i("TEST", "bitmap file immagine: " + thumbnail);

                        PopolaContenuti();

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
                                Log.i("TEST", "image path: " + path);
                            }
                        } else {
                            Log.i("TEST", "clipdata is null");

                            Uri selectedImage = data.getData();

                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();


                            Bitmap thumbnail = DownloadImageTask.decodeSampledBitmapFromPath(picturePath, 0,0);

/*
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 16;
                            Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
                            */

                            Log.i("TEST", "image from gallery: " + picturePath + "");
                            Log.i("TEST", "bitmap from gallery: " + thumbnail + "");

                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

                            String nomeFile = timeStamp + ".jpg";

                            Log.i("TEST", "timeStamp image: " + nomeFile);


                            if(thumbnail != null){
                                immaginiSelezionate.add(thumbnail);
                                bitmap_nomeFile.put(thumbnail,nomeFile);
                            }


                            Log.i("TEST", "elenco immagini selezionate: " + immaginiSelezionate);
                            Log.i("TEST", "elenco nomi immagini: " + bitmap_nomeFile.values());




                            PopolaContenuti();

                            //TODO: visualizzare le miniature delle immagini sul dialog

                        }

                    } else {
                        Log.e("TEST", "data is null");

                    }



                    //TODO verifica caricamento su drive


                    break;

                case Constants.REQUEST_PLACE_PICKER:

                    Log.i("TEST", "REQUEST_PLACE_PICKER");

                    Place place = PlacePicker.getPlace(this, data);
                    Log.i("TEST", "Place: %s" + place.getName());

                    startAddingStop(place);

                    break;

                case Constants.REQUEST_VIDEO_CAPTURE:
                    Log.i("TEST", "REQUEST_VIDEO_CAPTURE");

                    File fileVideo = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : fileVideo.listFiles()) {
                        if (temp.getName().equals(videoFileName)) {
                            fileVideo = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bitmap;
//                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                        bitmap = BitmapFactory.decodeFile(fileVideo.getAbsolutePath(),
//                                bitmapOptions);

                        bitmap = ThumbnailUtils.createVideoThumbnail(fileVideo.getAbsolutePath(),
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        Log.i("TEST", "path file video: " + fileVideo.getAbsolutePath());

//                        UploadImageTask task = new UploadImageTask(this, bitmap,
//                                Constants.NAME_IMAGES_PROFILE_DEFAULT, idFolder, "profile");
//                        task.delegate = this;
//                        task.execute();


                        //TODO verifica caricamento su drive

//                        String path = Environment.getExternalStorageDirectory().toString();
//                        fileVideo.delete();
//
//                        OutputStream outFile = null;
//                        File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".3gp");
//                        try {
//                            outFile = new FileOutputStream(file);
//                            //bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_OF_IMAGE, outFile);
//                            outFile.flush();
//                            outFile.close();
//
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    break;

                case Constants.REQUEST_VIDEO_PICK:
                    Log.i("TEST", "REQUEST_VIDEO_PICK");

                    Uri selectedVideo = data.getData();

                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor c = getContentResolver().query(selectedVideo, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String videoPath = c.getString(columnIndex);
                    c.close();

                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);


                    break;

                case Constants.REQUEST_RECORD_PICK:
                    Log.i("TEST", "REQUEST_RECORD_PICK");

                    Uri selectedAudio = data.getData();

                    String[] audioPath = {MediaStore.Audio.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedAudio, audioPath, null, null, null);
                    cursor.moveToFirst();
                    int columnIndexAudio = cursor.getColumnIndex(audioPath[0]);
                    String audioFilePath = cursor.getString(columnIndexAudio);
                    cursor.close();

//                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(audioFilePath,
//                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                    break;


                default:
                    Log.e("TEST", "requestCode non riconosciuto");
                    break;
            }

        } else {

            Log.e("TEST", "result: " + resultCode);
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

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


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

                    //permette inserimento tappa solo per l'itinerario dell'utilizzatore

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
                        GooglePlayServicesUtil
                                .getErrorDialog(e.getConnectionStatusCode(), ListaTappeActivity.this, 0);

                        Log.e("TEST", e.toString());

                    } catch (GooglePlayServicesNotAvailableException e) {
                        Toast.makeText(ListaTappeActivity.this, "Google Play Services is not available.",
                                Toast.LENGTH_LONG)
                                .show();

                        Log.e("TEST", e.toString());
                    }
                }

            }
        });


        googleMap.setOnInfoWindowClickListener(this);
    }


    public void onInfoWindowClick(Marker marker) {

        Log.i("TEST", "click info");

        String nomeTappa = marker.getTitle();
        String labelTappa = nomeTappa.split("\\.")[0];
        int numeroTappa = Integer.parseInt(labelTappa)-1;

        Log.i("TEST", "tappa numero " + numeroTappa);


        Tappa tappaSelezionata = profiloTappe.get(profiloVisualizzazioneCorrente).get(numeroTappa);
        int ordineTappa = tappaSelezionata.getOrdine();


        Intent i = new Intent(this, TappaActivity.class);

        i.putExtra("email", email);
        i.putExtra("codiceViaggio", codiceViaggio);
        i.putExtra("ordine", ordineTappa);
        i.putExtra("nome", nomeTappa);

        Calendar cal = Calendar.getInstance();
        cal.setTime(tappaSelezionata.getData());
        String data = cal.get(Calendar.YEAR) +"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        i.putExtra("data", data);

        //TODO sarà inutile una volta modificato il database
        i.putExtra("codAccount", 0);

//        Log.e("TEST", "#email  " + profiloUtente.getEmail());
//        Log.e("TEST", "#nomedelviaggio  " + marker.getTitle() );
        Log.e("TEST", "ordine tappa: " + ordineTappa);
        Log.e("TEST", "data tappa: " + tappaSelezionata.getData().toString());

        startActivity(i);

    }


    public void onClickAddStop(View v){

        immaginiSelezionate.clear();
        bitmap_nomeFile.clear();


        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intentPlacePicker = intentBuilder.build(ListaTappeActivity.this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intentPlacePicker, Constants.REQUEST_PLACE_PICKER);


        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), ListaTappeActivity.this, 0);

            Log.e("TEST", e.toString());

        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(ListaTappeActivity.this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();

            Log.e("TEST", e.toString());
        }
    }


    @Override
    public void processFinish(DriveId output) {
        //TODO
    }

    @Override
    public void processFinish2(DriveId output) {
        //TODO
    }



    //metodi ausiliari

    private static String getRealPathFromURI(Context context, Uri contentUri) {

        Log.i("TEST", "entro in getRealPathFromURI(...)");

        Cursor cursor = null;
        String result = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor
                        .getColumnIndex(proj[0]);

                result = cursor.getString(columnIndex);
                Log.i("TEST", "result: "+result);
            }

            return result;

        } catch (Exception e) {
            Log.e("TEST", "eccezione nel restituire il path: "+e.toString());
            return null;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void PopolaPartecipanti(final Set<Profilo> partecipants){

        layoutProprietariItinerari.addView(new TextView(this), Constants.WIDTH_LAYOUT_PROPRIETARI_ITINERARI,
                Constants.HEIGH_LAYOUT_PROPRIETARI_ITINERARI);


        Log.i("TEST", "partecipants: " + partecipants);

        for(Profilo p : partecipants){

            ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getEmail());
            currentProfile = p;


            if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
                new DownloadImageTask(image).execute(Constants.ADDRESS_TAT + p.getIdImageProfile());
            }
            else{
                if(p.getSesso().equals("M")){
                    image.setImageResource(R.drawable.default_male);
                }
                else{
                    image.setImageResource(R.drawable.default_female);
                }
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
        AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
    }


    private void CreaMenu(List<Tappa> tappe, List<String > nomiTappe){
        Menu menu = navigationView.getMenu();
        menu.clear();


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


    private void AggiungiMarkedPointsOnMap(Profilo p, List<Tappa> tappe) {
        mGoogleApiClient.connect();

        googleMap.clear();
        nomiTappe.clear();
        namesStops.clear();
        int i = 1;
        for(Tappa t : tappe){
            findPlaceById(p, t, i);
            i++;
        }

        if(tappe.size()==0){
            nomiTappe.clear();
            profiloNomiTappe.put(p,nomiTappe);
        }


        Log.i("TEST", "profiloNomiTappe: " + profiloNomiTappe);
        Log.i("TEST", "ho aggiunto i markedPoints di " + p);

    }


    private void findPlaceById(Profilo p, Tappa t, int i) {
        final int index = i;

        if( TextUtils.isEmpty(t.getPoi().getCodicePOI()) || mGoogleApiClient == null){
            Log.i("TEST", "codice tappa: " + t.getPoi().getCodicePOI());
            Log.i("TEST", "return");
            return;
        }


        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        currentProfile = p;

        /*
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

            //return;}


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
                            LatLng currentLatLng = place.getLatLng();
                            Log.i("TEST", "nome place: " + place.getName());

                            nomiTappe.add(place);
                            namesStops.add(place.getName().toString());
                            Log.i("TEST", "aggiunto ai places: " + nomiTappe);


                            //add Marker
                            googleMap.addMarker(new MarkerOptions()
                                            .title(index + ". " + place.getName().toString())
                                            .position(currentLatLng)
                            );
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));

                            polyline.add(currentLatLng);

                            mapBoundsBuilder.include(currentLatLng);


                            if (nomiTappe.size() == profiloTappe.get(currentProfile).size()
                                    && namesStops.size() == nomiTappe.size()) {

                                Log.i("TEST", "nomi places: " + namesStops);
                                CreaMenu(profiloTappe.get(currentProfile), namesStops);

                                profiloNomiTappe.put(currentProfile, nomiTappe);
                                Log.i("TEST", "profiloNomiTappe: " + profiloNomiTappe);
                                Log.i("TEST", "ho aggiunto i markedPoints di " + currentProfile);


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



    private int calcolaNumUltimaTappaUtenteCorrente() {

        int result = 0;

        Log.i("TEST", "profilo: " + profiloUtenteLoggato);
        Log.i("TEST", "mappa profiloTappe: " + profiloTappe);


        ArrayList<Tappa> listaTappe = (ArrayList<Tappa>) profiloTappe.get(profiloUtenteLoggato);

        Log.i("TEST", "lista tappe di " + profiloUtenteLoggato + ": " + listaTappe);

        if(listaTappe != null)
            result = listaTappe.size();

        Log.i("TEST", "result ordine tappa: " + result);

        return result;

    }


    private void startAddingStop(Place place) {

        final Place addedPlace = place;

        //prendere info poi
        placeId = ""+place.getId();
        placeName = ""+place.getName();
        placeLatLng = place.getLatLng();
        placeAddress = ""+place.getAddress();
        placeAttr = "";

        //TODO chiarire se siamo obbligati da google a inserire attribution, vedi pagina PlacePicker
        CharSequence aux = place.getAttributions();
        if(aux != null)
            placeAttr += aux;


        Log.i("TEST", "name: " + placeName);
        Log.i("TEST", "addr: " + placeAddress);

        dialog = new Dialog(ListaTappeActivity.this);
        dialog.setContentView(R.layout.info_poi);
        dialog.setTitle(getResources().getString(R.string.insert_new_stop));

        Spinner mySpinner = (Spinner)dialog.findViewById(R.id.spinner);
        final PrivacyLevelAdapter adapter = new PrivacyLevelAdapter(ListaTappeActivity.this, R.layout.entry_privacy_level, strings);


        layoutContents = (LinearLayout) dialog.findViewById(R.id.layoutContents);



        mySpinner.setAdapter(adapter);

        //TODO per utlizzare adapter in classe esterna, non funziona per via del dialog
        //mySpinner.setAdapter(new PrivacyLevelAdapter(ListaTappeActivity.this, R.layout.entry_privacy_level, strings, subs, arr_images));


        //TODO: prendere di default il livello predefinito del viaggio

               mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                       Log.i("TEST", "elemento selezionato: " + adapter.getItem(position).toString());
                       livelloCondivisioneTappa = adapter.getItem(position).toString();
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> parent) {

                   }
               });


        //put dialog at bottom
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        //per non far diventare scuro lo sfondo
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

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

                new MyTaskInserimentoTappa().execute();

                //per prevenire crash se si clicca sul marker appena aggiunto
                Itinerario itAux = new Itinerario(profiloUtenteLoggato, new Viaggio(codiceViaggio));

                Calendar cal = DatesUtils.getDateFromString(getCurrentDateString(), Constants.DATABASE_DATE_FORMAT);

                Log.i("TEST", "cal.getTime: "+cal.getTime());

                profiloTappe.get(profiloVisualizzazioneCorrente).add(new Tappa(itAux, stopOrder, cal.getTime()));

                new MyTaskInserimentoFiltro().execute();


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


                dialog.dismiss();
            }
        });


        ImageView addImage = (ImageView)dialog.findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddImage(view);
            }
        });

        ImageView addVideo = (ImageView)dialog.findViewById(R.id.addVideo);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddVideo(view);
            }
        });

        ImageView addRecord = (ImageView)dialog.findViewById(R.id.addRecord);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddRecord(view);
            }
        });

        ImageView addNote = (ImageView)dialog.findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddNote(view);
            }
        });

        dialog.show();

    }


    private void onClickAddImage(View v) {

        Log.i("TEST", "add image pressed");

        try {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            builder.setItems(R.array.CommandsAddPhotoToStop, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0: //pick images from gallery

                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentPick, Constants.REQUEST_IMAGE_PICK);


                            //TODO per selezione multipla, non funzionante con galleria, si con google photo
//                            Intent intentPick = new Intent();
//                            intentPick.setType("image/*");
//                            intentPick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                            intentPick.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intentPick, "Select Picture"), Constants.REQUEST_IMAGE_PICK);


//                            ######### ALTERNATIVA #########
//                            Intent intentPick = new Intent(Intent.ACTION_PICK);
//                            intentPick.setType("image/*");
//                            intentPick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                            startActivityForResult(intentPick, Constants.REQUEST_IMAGE_PICK);



                            //TODO far diventare immagine blu

                            break;

                        case 1: //take a photo
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {

                                File photoFile = null;
                                try {

                                    photoFile = MultimedialFile.createMediaFile(Constants.IMAGE_FILE, mCurrentPhotoPath, imageFileName);
                                    imageFileName = photoFile.getName();


                                } catch (IOException ex) {
                                    Log.e("TEST", "eccezione nella creazione di file immagine");
                                }

                                Log.i("TEST", "creato file immagine col nome: " +imageFileName);

                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);

                                    //TODO far diventare immagine blu
                                }
                            }
                            break;


                        default:
                            Log.e("TEST", "azione non riconosciuta");
                            break;
                    }
                }
            });


            // Create the AlertDialog object and return it
            builder.create().show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }

        Log.i("TEST", "END add image");

    }


    private void onClickAddVideo(View v) {

        Log.i("TEST", "add video pressed");

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

                                    videoFile = MultimedialFile.createMediaFile(Constants.VIDEO_FILE,
                                            mCurrentVideoPath, videoFileName);

                                } catch (IOException ex) {
                                    Log.e("TEST", "eccezione nella creazione di file video");
                                }

                                Log.i("TEST", "creato file video");

                                // Continue only if the File was successfully created
                                if (videoFile != null) {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                                    startActivityForResult(intent, Constants.REQUEST_VIDEO_CAPTURE);

                                    //TODO far diventare immagine blu
                                }
                            }
                            break;


                        default:
                            Log.e("TEST", "azione non riconosciuta");
                            break;
                    }
                }
            });


            // Create the AlertDialog object and return it
            builder.create().show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }

        Log.i("TEST", "END add video");


    }


    private void onClickAddRecord(View v) {

        Log.i("TEST", "add record pressed");

        try {
            final ContextThemeWrapper wrapper = new ContextThemeWrapper(this,
                    android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            builder.setItems(R.array.CommandsAddRecordToStop, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0: //pick audio from storage

                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
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

                                    Log.i("TEST", "progress dialog canceled");
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


                                                    Log.i("TEST", "file audio generato: " + record.getFileName());

                                                    File fileAudio = record.getFileAudio();

                                                    Log.i("TEST", "file audio generato: " + fileAudio);

                                                    //TODO implementare caricamento su db

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

                                                            Log.i("TEST", "thread stopped");

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

                                                                    Log.i("TEST", "thread stopped");

                                                                    record.stopRecording();

                                                                    progressDialog.dismiss();

                                                                    //TODO implementare caricamento su db

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
                            Log.e("TEST", "azione non riconosciuta");
                            break;
                    }
                }
            });


            builder.create().show();


        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }



        Log.i("TEST", "END add record");

    }


    private void onClickAddNote(View v) {



        Log.i("TEST", "add note pressed");


        try {

            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

            builder.setNegativeButton(getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Log.i("TEST", "edit text dialog canceled");
                        }
                    });

            builder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //TODO caricare dati su db

                            Log.i("TEST", "edit text confirmed");
                        }
                    });


            //TODO: gestire compatibilità
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setView(R.layout.material_edit_text);
            } else {
                Log.e("TEST", "versione android < 21");
            }
            builder.setTitle(getString(R.string.labelNote));


            //Dialog dialog = builder.create();
            AlertDialog dialog = builder.create();
            dialog.show();





            textInputLayout = (TextInputLayout) dialog.findViewById(R.id.textInputLayout);
            if (textInputLayout != null) {
                textInputLayout.setCounterEnabled(true);
                textInputLayout.setCounterMaxLength(Constants.NOTE_MAX_LENGTH);
            }
            textInputEditText = (TextInputEditText) dialog.findViewById(R.id.editText);


        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }


        Log.i("TEST", "END add note");

    }




    private String creaStringaFiltro() {
        return placeName.toLowerCase().replaceAll(" ", "_");
    }

    private String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cYear = calendar.get(Calendar.YEAR);
        String data = cYear+"-"+cMonth+"-"+cDay;

        return data;
    }



    private class MyTask extends AsyncTask<Void, Void, Void> {

        //TODO implementare meccanismo di selezione del profilo di cui prelevare tappe
        //TODO aggiornare variabile profiloVisCorr con il numero dell'ordine


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
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_PRELIEVO_TAPPE);
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
                                    Calendar cal = DatesUtils.getDateFromString(dataString, Constants.DATABASE_DATE_FORMAT);
                                    Date data = cal.getTime();

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

            super.onPostExecute(aVoid);

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
                    List<Tappa> aux = profiloTappe.get(p);
                    AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
                    aggiuntiMarkedPoints = true;
                    Log.i("TEST", "aggiunte tappe di " + p);


                    profiloVisualizzazioneCorrente = p;
                    break;
                }
            }

            if(!aggiuntiMarkedPoints){
                for(Profilo p : profiloTappe.keySet()){
                    AggiungiMarkedPointsOnMap(p, profiloTappe.get(p));
                    Log.i("TEST", "aggiunte tappe di " + p);

                    profiloVisualizzazioneCorrente =p;
                    break;
                }

            }

            ordine = calcolaNumUltimaTappaUtenteCorrente()+1;
        }
    }







    private class MyTaskInserimentoTappa extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();

            dataToSend.add(new BasicNameValuePair("emailProfilo", email));
            //dataToSend.add(new BasicNameValuePair("emailProfilo", "pippo@gmail.com"));

            dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));
            dataToSend.add(new BasicNameValuePair("ordine", ""+ordine));
            dataToSend.add(new BasicNameValuePair("POI", "" + placeId));

            String data = getCurrentDateString();
            dataToSend.add(new BasicNameValuePair("data", ""+data));

            String paginaDiario = "";
            dataToSend.add(new BasicNameValuePair("paginaDiario", paginaDiario));

            Log.i("TEST", "email: " + email);
            Log.i("TEST", "codiceViaggio: " + codiceViaggio);
            Log.i("TEST", "ordine: " + ordine);
            Log.i("TEST", "placeId: " + placeId);
            Log.i("TEST", "date: " + data);
            Log.i("TEST", "paginaDiario: " + paginaDiario);


            try {

                if (InternetConnection.haveInternetConnection(ListaTappeActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_TAPPA);
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

                            result = sb.toString();
                            Log.i("TEST", "result: " +result);

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }


                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if(!result.equals("OK\n")){
                Log.e("TEST", "tappa non inserita");
            }
            else{
                Log.i("TEST", "tappa inserita correttamente");
                Toast.makeText(getBaseContext(), "tappa inserita correttamente", Toast.LENGTH_LONG).show();
            }


            if(immaginiSelezionate.size() > 0){
                for(Bitmap bitmap : immaginiSelezionate){
                    String pathImage = email+"/"+codiceViaggio +"/";
                    String nameImage = bitmap_nomeFile.get(bitmap);
                    Log.i("TEST", "email: " + email);
                    Log.i("TEST", "codiceViaggio: " + codiceViaggio);
                    Log.i("TEST", "path of the image: " + pathImage);
                    Log.i("TEST", "name of the image: " + nameImage);
                    Log.i("TEST", "livello Condivisione: " + livelloCondivisioneTappa);


                    new UploadFilePHP(ListaTappeActivity.this, bitmap,pathImage,nameImage).execute();
                    new TaskInserimentoImmagineTappa(email,codiceViaggio,ordine,null,pathImage + nameImage,livelloCondivisioneTappa).execute();
                }

            }


            ordine += 1;

            super.onPostExecute(aVoid);

        }
    }


    private class MyTaskInserimentoFiltro extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";


        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("filtro", creaStringaFiltro()));
            dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));

            Log.i("TEST", "filtro: " + creaStringaFiltro());

            try {
                if (InternetConnection.haveInternetConnection(ListaTappeActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_FILTRO);
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

                            result = sb.toString();
                            Log.i("TEST", "result: " +result);

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }


                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(!result.equals("OK\n")){
                Log.e("TEST", "filtro non inserito");
            }
            else{
                Log.i("TEST", "filtro inserito correttamente");

            }
            super.onPostExecute(aVoid);
        }
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

            //Log.i("TEST", "string: " + strings[position]);
            //Log.i("TEST", "sub: " + subs[position]);
            //Log.i("TEST", "img: " + arr_images[position]);

            return convertView;
        }
    }



    private class TaskInserimentoImmagineTappa extends AsyncTask<Void, Void, Void> {

        private final static String ADDRESS_INSERT_IMAGE_STOP = "InserimentoImmagineTappa.php";

        InputStream is = null;
        String result, stringaFinale = "";
        String email, codiceViaggio, urlImmagine, condivisione;
        DriveId idDrive;
        int ordine;


        public TaskInserimentoImmagineTappa(String email, String codiceViaggio, int ordine, DriveId idDrive, String urlimmagine, String condivisione){
            this.email = email;
            this.codiceViaggio = codiceViaggio;
            this.ordine = ordine;
            this.idDrive = idDrive;
            this.urlImmagine = urlimmagine;
            this.condivisione = condivisione;


        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("codice", codiceViaggio));
            dataToSend.add(new BasicNameValuePair("ordine", String.valueOf(ordine)));
            dataToSend.add(new BasicNameValuePair("url", urlImmagine));
            dataToSend.add(new BasicNameValuePair("condivisione", condivisione));

            try {

                if (InternetConnection.haveInternetConnection(ListaTappeActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_IMAGE_STOP);
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

                            result = sb.toString();
                            Log.i("TEST", "result: " +result);

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}

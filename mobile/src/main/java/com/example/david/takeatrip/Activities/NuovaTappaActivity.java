package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NuovaTappaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String ADDRESS_TAPPA = "InserimentoTappa.php";
    private final String ADDRESS_FILTRO = "InserimentoFiltro.php";

    private GoogleMap googleMap;

    private Button buttonSatellite, buttonHybrid, buttonTerrain;

    private PlaceAutocompleteFragment autocompleteFragment;

    private FrameLayout layoutInfoPoi;

    private TextView nameText;
    private TextView addressText;

    private String email, codiceViaggio;
    private int ordine, codAccount;

    private String placeId, placeName, placeAddress;
    LatLng placeLatLng;

    private String[] strings;
    private String[] subs;
    private int[] arr_images;

    private Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_tappa);

        buttonSatellite = (Button) findViewById(R.id.buttonSatellite);
        buttonTerrain = (Button) findViewById(R.id.buttonTerrain);
        buttonHybrid = (Button) findViewById(R.id.buttonHybrid);


        //in caso di utilizzo di frame anzichè di dialog
        //layoutInfoPoi = (FrameLayout)findViewById(R.id.FrameInfoPoi);
//        nameText = (TextView) findViewById(R.id.POIName);
//        addressText = (TextView) findViewById(R.id.POIAddress);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint(getResources().getString(R.string.search_poi));


        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                //prendere info poi
                placeId = ""+place.getId();
                placeName = ""+place.getName();
                placeLatLng = place.getLatLng();
                placeAddress = ""+place.getAddress();
                //TODO prendere altre info utili

                Log.i("TEST", "name: " + placeName);
                Log.i("TEST", "addr: " + placeAddress);

                //posizionare marker su mappa
                googleMap.addMarker(new MarkerOptions()
                        .title(placeName)
                        .position(placeLatLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), Constants.DEFAULT_ZOOM_MAP));


                //in caso di utilizzo di frame anzichè di dialog
//                nameText.setText(placeName);
//                addressText.setText(placeAddress);
//                layoutInfoPoi.setVisibility(View.VISIBLE);



                dialog = new Dialog(NuovaTappaActivity.this);
                dialog.setContentView(R.layout.info_poi);
                dialog.setTitle(getResources().getString(R.string.insert_new_stop));

                Spinner mySpinner = (Spinner)dialog.findViewById(R.id.spinner);
                mySpinner.setAdapter(new PrivacyLevelAdapter(NuovaTappaActivity.this, R.layout.entry_privacy_level, strings));


                //TODO verificare motivo errore
//                mySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                        //TODO implementare setting privacy, modificare database
//
//                    }
//                });




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

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        googleMap.clear();
                    }
                });


                //setting listener pulsanti dialog

                FloatingActionButton addStop = (FloatingActionButton) dialog.findViewById(R.id.buttonAddStop);
                addStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //TODO andrebbero eseguiti come una transazione atomica

                        new MyTaskInserimentoTappa().execute();

                        new MyTaskInserimentoFiltro().execute();

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




                dialog.show();

            }

            @Override
            public void onError(Status status) {
                Log.i("TEST", "An error occurred: " + status);
            }


        });


        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            ordine = intent.getIntExtra("ordine", 0) + 1;

        }
    }


    //TODO valutare eliminazione pulsanti
//    public void onSatelliteButtonClick(View view) {
//        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//    }
//
//    public void onHybridButtonClick(View view) {
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//    }
//
//    public void onTerrainButtonClick(View view) {
//        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//    }


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

        map.setMyLocationEnabled(true);


        //TODO to get last known location
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//
//        Location location = locationManager.getLastKnownLocation(locationManager
//                .getBestProvider(criteria, false));
//
//        Log.i("TEST", "location: "+location);
//
//
//        if (location != null) {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), Constants.DEFAULT_ZOOM_MAP));
//        }




        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //startAddingStop(place);

                googleMap.clear();

                googleMap.addMarker(new MarkerOptions()
                        .position(latLng));

                Vibrator v = (Vibrator) NuovaTappaActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);

                //autocompleteFragment.setText(latLng.toString());



            }
        });




    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
                Log.i("TEST", "REQUEST_IMAGE_CAPTURE");


//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals(imageFileName)) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            bitmapOptions);
//
//
//                    //TODO: update the db with new profile image
//
//
//                    //imageProfile.setImageBitmap(bitmap);
//
//                    String path = android.os.Environment.getExternalStorageDirectory().toString();
//                    f.delete();
//
//                    OutputStream outFile = null;
//                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    try {
//                        outFile = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
//                        outFile.flush();
//                        outFile.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


            } else if (requestCode == Constants.REQUEST_IMAGE_PICK) {

                Log.i("TEST", "REQUEST_IMAGE_PICK");

//                Uri selectedImage = data.getData();
//                String[] filePath = {MediaStore.Images.Media.DATA};
//
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                Log.i("image from gallery:", picturePath + "");
//
//
//                //TODO: update the db with new profile image
//
//
//                //imageProfile.setImageBitmap(thumbnail);


            }
        }
    }





    //metodi ausiliari


    private void startAddingStop(Place place) {

        //prendere info poi
        placeId = ""+place.getId();
        placeName = ""+place.getName();
        placeLatLng = place.getLatLng();
        placeAddress = ""+place.getAddress();
        //TODO prendere altre info utili

        Log.i("TEST", "name: " + placeName);
        Log.i("TEST", "addr: " + placeAddress);

        //posizionare marker su mappa
        googleMap.addMarker(new MarkerOptions()
                .title(placeName)
                .snippet(place.getAttributions().toString())
                .position(placeLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), Constants.DEFAULT_ZOOM_MAP));


        //in caso di utilizzo di frame anzichè di dialog
//                nameText.setText(placeName);
//                addressText.setText(placeAddress);
//                layoutInfoPoi.setVisibility(View.VISIBLE);



        dialog = new Dialog(NuovaTappaActivity.this);
        dialog.setContentView(R.layout.info_poi);
        dialog.setTitle(getResources().getString(R.string.insert_new_stop));

        Spinner mySpinner = (Spinner)dialog.findViewById(R.id.spinner);
        mySpinner.setAdapter(new PrivacyLevelAdapter(NuovaTappaActivity.this, R.layout.entry_privacy_level, strings));


        //TODO verificare motivo errore
//                mySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                        //TODO implementare setting privacy, modificare database
//
//                    }
//                });




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

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                googleMap.clear();
            }
        });


        //setting listener pulsanti dialog

        FloatingActionButton addStop = (FloatingActionButton) dialog.findViewById(R.id.buttonAddStop);
        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO andrebbero eseguiti come una transazione atomica

                new MyTaskInserimentoTappa().execute();

                new MyTaskInserimentoFiltro().execute();

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




        dialog.show();

    }


    private void onClickAddImage(View v) {

        Log.i("TEST", "add image pressed");

        try {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setItems(R.array.CommandsImageProfile, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //view image profile


                            break;
                        case 1: //change image profile
//                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(intentPick, Constants.REQUEST_IMAGE_PICK);

                            Intent intentPick = new Intent();
                            intentPick.setType("image/*");
                            intentPick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intentPick.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intentPick,"Select Picture"), Constants.REQUEST_IMAGE_PICK);



                            break;

                        case 2:  //take a photo
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {

                                File photoFile = null;
                                try {

                                    photoFile = createImageFile();

                                } catch (IOException ex) {
                                    Log.e("TEST", "eccezione nella creazione di file immagine");
                                }

                                Log.i("TEST", "creato file immagine");

                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);
                                }
                            }
                            break;

                        case 3: //exit
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

    public void onClickAddVideo(View v) {

        Log.i("TEST", "add video pressed");


    }

    public void onClickAddRecord(View v) {

        Log.i("TEST", "add record pressed");


    }

    public void onClickAddNote(View v) {

        Log.i("TEST", "add note pressed");


    }

    private File createImageFile() throws IOException {

        String mCurrentPhotoPath;
        String imageFileName;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = timeStamp + ".jpg";

        File image = new File(android.os.Environment.getExternalStorageDirectory(), imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.i("TEST", "path file immagine: " + mCurrentPhotoPath);

        return image;
    }

    private String creaStringaFiltro() {
        return placeName.toLowerCase().replaceAll(" ", "_");
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

            Calendar calendar = Calendar.getInstance();
            int cDay = calendar.get(Calendar.DAY_OF_MONTH);
            int cMonth = calendar.get(Calendar.MONTH) + 1;
            int cYear = calendar.get(Calendar.YEAR);
            String data = cYear+"-"+cMonth+"-"+cDay;
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

                if (InternetConnection.haveInternetConnection(NuovaTappaActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.ADDRESS_PRELIEVO + ADDRESS_TAPPA);
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

            if(!result.equals("OK")){
                //TODO il risultato restituito non è OK quando va a buon fine, capire perchè

                //Log.e("TEST", "tappa non inserita");

                //TODO definire comportamento errore
                //Toast.makeText(getBaseContext(), "tappa non inserita", Toast.LENGTH_LONG).show();

            }
            else{
                Log.i("TEST", "tappa inserita correttamente");

                Toast.makeText(getBaseContext(), "tappa inserita correttamente", Toast.LENGTH_LONG).show();

            }
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

                if (InternetConnection.haveInternetConnection(NuovaTappaActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.ADDRESS_PRELIEVO + ADDRESS_FILTRO);
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

            if(result.contains("")){
                Log.e("TEST", "filtro non inserito");

                //TODO definire comportamento errore

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

            Log.i("TEST", "string: " + strings[position]);
            Log.i("TEST", "sub: " + subs[position]);
            Log.i("TEST", "img: " + arr_images[position]);

            return convertView;
        }
    }

}




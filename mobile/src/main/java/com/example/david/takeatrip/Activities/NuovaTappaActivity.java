package com.example.david.takeatrip.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_tappa);

        buttonSatellite = (Button) findViewById(R.id.buttonSatellite);
        buttonTerrain = (Button) findViewById(R.id.buttonTerrain);
        buttonHybrid = (Button) findViewById(R.id.buttonHybrid);

        layoutInfoPoi = (FrameLayout)findViewById(R.id.FrameInfoPoi);
        nameText = (TextView) findViewById(R.id.POIName);
        addressText = (TextView) findViewById(R.id.POIAddress);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint(getResources().getString(R.string.search_poi));


        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = new int[]{R.drawable.ic_public_black_36dp, R.drawable.ic_people_black_36dp,
                R.drawable.ic_person_pin_circle_black_36dp, R.drawable.ic_settings_black_36dp};

        Spinner mySpinner = (Spinner)findViewById(R.id.spinner);
        mySpinner.setAdapter(new PrivacyLevelAdapter(NuovaTappaActivity.this, R.layout.entry_privacy_level, strings));

//        mySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //TODO
//            }
//        });


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                //prendere info poi
                placeId = ""+place.getId();
                placeName = ""+place.getName();
                placeLatLng = place.getLatLng();
                placeAddress = ""+place.getAddress();
                //...

                Log.i("TEST", "name: " + placeName);
                Log.i("TEST", "addr: " + placeAddress);

                //posizionare marker su mappa
                googleMap.addMarker(new MarkerOptions()
                        .title(placeName)
                        .position(placeLatLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));


                //inserire in layout
                nameText.setText(placeName);
                addressText.setText(placeAddress);


                layoutInfoPoi.setVisibility(View.VISIBLE);


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


    public void onSatelliteButtonClick(View view) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void onHybridButtonClick(View view) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void onTerrainButtonClick(View view) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }


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


  /*     map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/
    }

    public void onClickAddStop(View v){

        new MyTaskInserimentoTappa().execute();

        //TODO ripristinare
        //new MyTaskInserimentoFiltro().execute();

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
                Log.e("TEST", "tappa non inserita");

                //TODO definire comportamento errore
                Toast.makeText(getBaseContext(), "tappa non inserita", Toast.LENGTH_LONG).show();

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
                Log.e("TEST", "tappa non inserita");

                //TODO definire comportamento errore

            }
            else{
                Log.i("TEST", "tappa inserita correttamente");

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




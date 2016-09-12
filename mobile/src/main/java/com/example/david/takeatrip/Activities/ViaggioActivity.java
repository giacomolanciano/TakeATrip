package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.david.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.example.david.takeatrip.AsyncTasks.DeleteTravelTask;
import com.example.david.takeatrip.AsyncTasks.GetPartecipantiViaggioTask;
import com.example.david.takeatrip.AsyncTasks.ItinerariesTask;
import com.example.david.takeatrip.AsyncTasks.StartActivityWithIndetProgressTask;
import com.example.david.takeatrip.AsyncTasks.UpdateCondivisioneViaggioTask;
import com.example.david.takeatrip.AsyncTasks.UpdateTravelNameTask;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.GraphicalComponents.AdaptableGridView;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.InternetConnection;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;
import com.example.david.takeatrip.Utilities.UtilS3AmazonCustom;
import com.google.android.gms.drive.DriveId;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;


public class ViaggioActivity extends AppCompatActivity {

    private static final String TAG = "TEST ViaggioActivity";
    private static final String ADDRESS = "QueryNomiUtenti.php";
    private static final String ADDRESS_QUERY_FOLDER = "QueryCartellaGenerica.php";
    private static final String ADDRESS_INSERT_FOLDER = "CreazioneCartellaViaggio.php";
    private static final int DIMENSION_OF_IMAGE_PARTICIPANT = Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT;
    private static final int DIMENSION_OF_SPACE = Constants.BASE_DIMENSION_OF_SPACE;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int LIMIT_IMAGES_VIEWS = 4;

    private String email, emailEsterno, codiceViaggio, nomeViaggio;
    private boolean proprioViaggio = false;
    private List<Profilo> listPartecipants, profiles;
    private List<String> names;
    private LinearLayout layoutPartecipants;
    private LinearLayout rowHorizontal, layoutFAB;
    private ImageView imageTravel;
    private DriveId idFolder;
    private Bitmap bitmapImageTravel;
    private String nameImageTravel, urlImageTravel;
    private AdaptableGridView gridViewPhotos;
    private AdaptableGridView gridViewRecords;
    private AdaptableGridView gridViewVideos;
    private AdaptableGridView gridViewNotes;
    private String[] strings, subs;
    private int[] arr_images;
    private String livelloCondivisioneViaggio;
    private AppBarLayout appBarLayout;
    private int checkSelectionSpinner = 0;
    private EditText TextNameTravel;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton buttonStopsList;
    private FloatingActionButton buttonAddPartecipant;
    private FloatingActionButton buttonDelete;
    private String nameForUrl;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ProgressDialog progressDialog;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The S3 client
    private AmazonS3Client s3;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_viaggio_old);
        setContentView(R.layout.activity_viaggio);

        Intent intent;
        if((intent = getIntent()) != null){
            email = intent.getStringExtra("email");
            Log.i(TAG, "email: "+ email);
            emailEsterno = intent.getStringExtra("emailEsterno");
            Log.i(TAG, "emailEsterno: "+ emailEsterno);
            codiceViaggio = intent.getStringExtra("codiceViaggio");
            Log.i(TAG, "codiceViaggio da Viaggio: "+ codiceViaggio);
            nomeViaggio = intent.getStringExtra("nomeViaggio");
            Log.i(TAG, "nomeViaggio: "+ nomeViaggio);
            idFolder = intent.getParcelableExtra("idFolder");
            Log.i(TAG, "idFolder: "+ idFolder);
            urlImageTravel = intent.getStringExtra("urlImmagineViaggio");
            Log.i(TAG, "urlImmagineViaggio1: "+ urlImageTravel);
            livelloCondivisioneViaggio = intent.getStringExtra("livelloCondivisione");
            Log.i(TAG, "livelloCondivisione : "+ livelloCondivisioneViaggio);

        }


        //retreive the content view of the activity for GetPartecipantiViaggioTask to work
        View contentView = findViewById(android.R.id.content);


        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(nomeViaggio);
        } else {
            Log.e(TAG, "collapsingToolbar is null");
        }


        imageTravel = (ImageView) findViewById(R.id.coverImageTravel);
//        Picasso.with(ViaggioActivity.this)
//                .load(urlImageTravel)
//                .error(R.drawable.empty_image)
//                .into(imageTravel);


        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;


        Spinner privacySpinner = (Spinner) findViewById(R.id.spinnerPrivacyLevel);
        final PrivacyLevelAdapter adapter = new PrivacyLevelAdapter(ViaggioActivity.this, R.layout.entry_privacy_level, strings);
        String livelloMaiuscolo = livelloCondivisioneViaggio.substring(0,1).toUpperCase()
                + livelloCondivisioneViaggio.substring(1,livelloCondivisioneViaggio.length());

        final int spinnerPosition = adapter.getPosition(livelloMaiuscolo);

        if (privacySpinner != null) {

            privacySpinner.setAdapter(adapter);
            privacySpinner.setSelection(spinnerPosition);


            privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(checkSelectionSpinner > 0){
                        Log.i(TAG, "elemento selezionato spinner: "+ adapter.getItem(position));
                        livelloCondivisioneViaggio = adapter.getItem(position);
                        new UpdateCondivisioneViaggioTask(ViaggioActivity.this, codiceViaggio, livelloCondivisioneViaggio).execute();
                    }

                    checkSelectionSpinner++;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else {
            Log.e(TAG, "privacySpinner is null");
        }
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(ViaggioActivity.this);

        listPartecipants = new ArrayList<Profilo>();
        names = new ArrayList<String>();
        profiles = new ArrayList<Profilo>();


        nameForUrl = codiceViaggio.trim().replace(" ", "");


        if(email == null){
            TakeATrip TAT = (TakeATrip)getApplicationContext();
            email = TAT.getProfiloCorrente().getEmail();
        }
        if(email != null){
            String url = email+"/"+ nameForUrl;
        }

        Log.i(TAG, "email utente: " + email + " codiceViaggio: " + codiceViaggio + " nomeVaggio: " + nomeViaggio);

        gridViewPhotos = (AdaptableGridView) findViewById(R.id.grid_view_photos);
        gridViewVideos = (AdaptableGridView) findViewById(R.id.grid_view_videos);
        gridViewRecords = (AdaptableGridView) findViewById(R.id.grid_view_records);
        gridViewNotes = (AdaptableGridView) findViewById(R.id.grid_view_notes);

        //layoutCopertinaViaggio = (LinearLayout) findViewById(R.id.layoutCoverImageTravel);

        layoutPartecipants = (LinearLayout)findViewById(R.id.Partecipants);
        rowHorizontal = (LinearLayout) findViewById(R.id.layout_horizontal2);

        //action buttons
        fabMenu = (FloatingActionsMenu) findViewById(R.id.menu);
        buttonStopsList = (FloatingActionButton) findViewById(R.id.buttonStopsList);
        if (buttonStopsList != null) {
            buttonStopsList.setIcon(R.drawable.ic_place_black_36dp);

            buttonStopsList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i(TAG, "stops list pressed");

                    fabMenu.collapse();

                    onClickStopsList(view);

                }
            });
        }

        buttonDelete = (FloatingActionButton) findViewById(R.id.buttonDelete);
        if (buttonDelete != null) {
            buttonDelete.setIcon(R.drawable.ic_delete_black_36dp);

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i(TAG, "delete travel pressed");

                    fabMenu.collapse();

                    onClickDeleteTravel(view);

                }
            });
        }

        try {
            Log.i(TAG, "codiceViaggio prima di Get " + codiceViaggio);
            proprioViaggio = new GetPartecipantiViaggioTask(ViaggioActivity.this, contentView, s3,
                    codiceViaggio, listPartecipants, nomeViaggio, email, urlImageTravel,
                    layoutPartecipants, rowHorizontal, imageTravel, gridViewPhotos, gridViewVideos,
                    gridViewRecords, gridViewNotes).execute().get();

            popolaPartecipanti();

        } catch (InterruptedException e) {
            Log.e(TAG, "GetPartecipantiViaggioTask interrupted!");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e(TAG, "GetPartecipantiViaggioTask not executed!");
            e.printStackTrace();
        }


        showProgressDialog();
        new UtentiTask().execute();



        //new MyTaskIDFolder(this,email,url,nameForUrl).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    public void onClickStopsList(View v){
        CharSequence[] emailPartecipants = new CharSequence[listPartecipants.size()];
        CharSequence[] urlImagePartecipants = new CharSequence[listPartecipants.size()];
        CharSequence[] sessoPartecipants = new CharSequence[listPartecipants.size()];

        int i= 0;
        for(Profilo p: listPartecipants){
            emailPartecipants[i] = p.getEmail();
            urlImagePartecipants[i] = p.getIdImageProfile();
            sessoPartecipants[i] = p.getSesso();
            i++;
        }

        Log.i(TAG, "email partecipants: " + Arrays.toString(emailPartecipants));

        Intent intent = new Intent(ViaggioActivity.this, ListaTappeActivity.class);
        if(email != null){
            intent.putExtra("email", email);
        }
        else{
            TakeATrip TAT = (TakeATrip)getApplicationContext();
            email = TAT.getProfiloCorrente().getEmail();
            intent.putExtra("email", email);
        }

        intent.putExtra("codiceViaggio", codiceViaggio);
        intent.putExtra("nomeViaggio", nomeViaggio);
        intent.putExtra("urlImmagineViaggio", urlImageTravel);
        intent.putExtra("partecipanti", emailPartecipants);
        intent.putExtra("urlImagePartecipants", urlImagePartecipants);
        intent.putExtra("sessoPartecipants", sessoPartecipants);
        intent.putExtra("livelloCondivisione", livelloCondivisioneViaggio);

        //startActivity(intent);
        new StartActivityWithIndetProgressTask(ViaggioActivity.this, intent).execute();
    }


    public void onClickImageTravel(View v) {
        if(proprioViaggio){
            try {
                ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

                AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setItems(R.array.CommandsTravelImage, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //view cover image

                                break;
                            case 1: //change cover image
                                Intent intentPick = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                startActivityForResult(intentPick, REQUEST_IMAGE_PICK);
                                break;
                            case 2: //modify travel name
                                LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                final View dialogView = inflater.inflate(R.layout.material_edit_text, null);
                                final TextInputEditText textInputEditText= (TextInputEditText) dialogView.findViewById(R.id.editText);
                                textInputEditText.setText(collapsingToolbar.getTitle());

                                new android.support.v7.app.AlertDialog.Builder(ViaggioActivity.this)
                                        .setView(dialogView)
                                        .setTitle(getString(R.string.edit_travel_name))
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                String nuovoNome = textInputEditText.getText().toString();
                                                new UpdateTravelNameTask(ViaggioActivity.this, codiceViaggio, nuovoNome)
                                                        .execute();
                                                collapsingToolbar.setTitle(nuovoNome);

                                                Log.i(TAG, "edit text dialog confirmed");
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Log.i(TAG, "edit text dialog canceled");
                                            }
                                        })
                                        .setIcon(ContextCompat.getDrawable(ViaggioActivity.this, R.drawable.logodefbordo))
                                        .show();

                                break;
                            case 3: //exit
                                break;
                        }
                    }
                });

                builder.create().show();

            } catch (Exception e) {
                Log.e(e.toString().toUpperCase(), e.getMessage());
            }
        }


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmapImageTravel = (BitmapFactory.decodeFile(picturePath));
                Log.i(TAG, "image from gallery: " + picturePath);


//                UtilS3AmazonCustom.uploadTravelCoverPicture(ViaggioActivity.this, picturePath,
//                        codiceViaggio, email, bitmapImageTravel, layoutCopertinaViaggio);

                UtilS3AmazonCustom.uploadTravelCoverPicture(ViaggioActivity.this, picturePath,
                        codiceViaggio, email, bitmapImageTravel, imageTravel, selectedImage);


/*
                if(email!= null && codiceViaggio != null){
                    String nameForUrl = codiceViaggio.trim().replace(" ", "");
                    String url = email+"/"+nameForUrl;
                    new MyTaskIDFolder(this,email,url,nameForUrl).execute();
                }
                else{
                    Toast.makeText(getBaseContext(), R.string.update_failed, Toast.LENGTH_LONG);
                }
                */
            }
        }
    }


    private void popolaPartecipanti(){
        int i=0;
        for(Profilo p : listPartecipants){
            if(i%LIMIT_IMAGES_VIEWS == 0){
                rowHorizontal = new LinearLayout(ViaggioActivity.this);
                rowHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                //Log.i(TAG, "creato nuovo layout");
                layoutPartecipants.addView(rowHorizontal);
                layoutPartecipants.addView(new TextView(ViaggioActivity.this), DIMENSION_OF_SPACE, DIMENSION_OF_SPACE);
                //Log.i(TAG, "aggiunto row e view al layout verticale");
            }



            final ImageView image = new RoundedImageView(this, null);
            image.setContentDescription(p.getEmail());

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickImagePartecipant(v);
                }
            });


            if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){

                String signedUrl = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_NAME, p.getIdImageProfile());



                Picasso.with(ViaggioActivity.this).
                        load(signedUrl).
                        resize(DIMENSION_OF_IMAGE_PARTICIPANT, DIMENSION_OF_IMAGE_PARTICIPANT).
                        into(image);


            }else {
                if(p.getSesso().equals("M")){
                    image.setImageResource(R.drawable.default_male);
                }
                else{
                    image.setImageResource(R.drawable.default_female);
                }
            }


            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //image.setImageResource(R.drawable.logodef);
            rowHorizontal.addView(image, DIMENSION_OF_IMAGE_PARTICIPANT, DIMENSION_OF_IMAGE_PARTICIPANT);
            rowHorizontal.addView(new TextView(this), DIMENSION_OF_SPACE, DIMENSION_OF_IMAGE_PARTICIPANT);


            i++;

        }

        if(proprioViaggio){

            buttonAddPartecipant = new FloatingActionButton(this);
            buttonAddPartecipant.setIcon(R.drawable.ic_person_add_black_36dp);
            buttonAddPartecipant.setMinimumWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonAddPartecipant.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonAddPartecipant.setSize(FloatingActionButton.SIZE_MINI);
            buttonAddPartecipant.setTitle(getResources().getString(R.string.addPartecipant));
            buttonAddPartecipant.setColorPressed(R.color.white_pressed);
            buttonAddPartecipant.setColorNormalResId(R.color.biancoPallido);

            buttonAddPartecipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i(TAG, "stops list pressed");

                    fabMenu.collapse();

                    onClickAddPartecipant(view);

                }
            });

            fabMenu.addButton(buttonAddPartecipant);
        }


    }


    public void onClickImagePartecipant(final View v){

        //TODO implementare delete partecipant

        try {
            final Dialog dialog = new Dialog(this, R.style.CustomDialog);
            dialog.setContentView(R.layout.layout_dialog_profiles);

            TextView viewName = (TextView) dialog.findViewById(R.id.viewNameProfileDialog);
            LinearLayout layoutCopertina = (LinearLayout)dialog.findViewById(R.id.layoutCopertinaNelDialog);
            ImageView imageProfile = (ImageView) dialog.findViewById(R.id.imageView_round_Dialog);
            ImageView coverImageProfile = (ImageView) dialog.findViewById(R.id.CoverImageDialog);

            for(Profilo p : listPartecipants){
                if(p.getEmail().equals(v.getContentDescription())){
                    viewName.setText(p.getName() + " " + p.getSurname());

                    if(p.getIdImageProfile() != null && !p.getIdImageProfile().equals("null")){
                        String signedUrl = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_NAME,
                                p.getIdImageProfile());

                        Picasso.with(ViaggioActivity.this).
                                load(signedUrl).
                                resize(DIMENSION_OF_IMAGE_PARTICIPANT *2, DIMENSION_OF_IMAGE_PARTICIPANT *2).
                                into(imageProfile);


                    }
                    else{
                        if(p.getSesso().equals("M")){
                            imageProfile.setImageResource(R.drawable.default_male);
                        }
                        else{
                            imageProfile.setImageResource(R.drawable.default_female);
                        }
                    }

                    if(p.getGetIdImageCover() != null && !p.getGetIdImageCover().equals("null")){

                        //String urlCoverPartecipant =  Constants.ADDRESS_TAT+ p.getGetIdImageCover();

                        String signedUrl = UtilS3AmazonCustom.getS3FileURL(s3, Constants.BUCKET_NAME,
                                p.getGetIdImageCover());
                        new BitmapWorkerTask(null, layoutCopertina).execute(signedUrl);


                    }

                    break;
                }
            }


            Button viewProfileButton = (Button) dialog.findViewById(R.id.buttonViewProfile);
            viewProfileButton.setBackground(getDrawable(R.drawable.button_style));
            viewProfileButton.setTextSize(20);
            Button cancelDialogButton = (Button) dialog.findViewById(R.id.buttonCancelDialog);
            cancelDialogButton.setBackground(getDrawable(R.drawable.button_style));
            cancelDialogButton.setTextSize(20);



            viewProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG,"email profilo selezionato: "+ v.getContentDescription().toString());
                    for(Profilo p : listPartecipants){
                        if(p.getEmail().equals(v.getContentDescription())){

                            Intent openProfilo = new Intent(ViaggioActivity.this, ProfiloActivity.class);
                            openProfilo.putExtra("name", p.getName());
                            openProfilo.putExtra("surname", p.getSurname());
                            if(emailEsterno != null){
                                openProfilo.putExtra("emailEsterno", p.getEmail());
                            }
                            else{
                                openProfilo.putExtra("email", email);
                            }
                            openProfilo.putExtra("dateOfBirth", p.getDataNascita());
                            openProfilo.putExtra("nazionalita", p.getNazionalita());
                            openProfilo.putExtra("sesso", p.getSesso());
                            openProfilo.putExtra("username", p.getUsername());
                            openProfilo.putExtra("lavoro", p.getLavoro());
                            openProfilo.putExtra("descrizione", p.getDescrizione());
                            openProfilo.putExtra("tipo", p.getTipo());
                            openProfilo.putExtra("urlImmagineProfilo", p.getIdImageProfile());
                            openProfilo.putExtra("urlImmagineCopertina", p.getGetIdImageCover());

                            // passo all'attivazione dell'activity
                            startActivity(openProfilo);

                            break;
                        }
                    }
                }
            });

            cancelDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, e.toString().toUpperCase() + ": " + e.getMessage());
        }
    }


    public void onClickAddPartecipant(final View v){
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(wrapper);
        //dialog.setContentView(R.layout.dialog_insert_viaggio);

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_partecipant_viaggio, null);
        dialog.setTitle("Add Partecipant");
        dialog.setView(view);

        layoutFAB = (LinearLayout) view.findViewById(R.id.layoutFloatingButtonAdd);

        final AutoCompleteTextView text = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter adapter = new ArrayAdapter(ViaggioActivity.this, android.R.layout.test_list_item, names);
        text.setHint("Add partecipant");
        text.setAdapter(adapter);
        text.setThreshold(1);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    layoutFAB.setVisibility(View.GONE);
                else if (layoutFAB.getVisibility() != View.VISIBLE) {
                    layoutFAB.setVisibility(View.VISIBLE);
                    text.showDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //no op
            }
        });


        TextView travel = (TextView) view.findViewById(R.id.titoloViaggio);
        travel.setText(nomeViaggio);
        travel.setEnabled(false);

        /*
        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreateTravel);
        buttonCreate.setVisibility(View.INVISIBLE);
        Button buttonCancella = (Button) dialog.findViewById(R.id.buttonCancellaDialog);
        buttonCancella.setVisibility(View.INVISIBLE);
        */

        final android.support.design.widget.FloatingActionButton buttonAdd
                = (android.support.design.widget.FloatingActionButton) view.findViewById(R.id.floatingButtonAdd);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override

            public void onCancel(DialogInterface dialog) {
                   showAlertDialog(v);
                }
        });

        dialog.setPositiveButton(getString(R.string.finish),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    showAlertDialog(v);
                    }
                });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals("")) {

                    String newPartecipant = text.getText().toString();
                    Log.i(TAG, "lista Partecipanti al viaggio: " + listPartecipants);
                    Log.i(TAG, "nuovo Partecipante: " + newPartecipant);

                    String usernameUtenteSelezionato = newPartecipant.substring(newPartecipant.indexOf('(')+1,
                            newPartecipant.indexOf(')'));

                    for(Profilo p : profiles){
                        if(p.getUsername().equals(usernameUtenteSelezionato)){

                            //TODO: migliorare con override di equals in Profilo
                            boolean giaPresente = false;
                            for(Profilo partecipant : listPartecipants){
                                if(partecipant.getEmail().equals(p.getEmail())){
                                    giaPresente = true;
                                    break;
                                }
                            }
                            //if(!listPartecipants.contains(p)) {
                            if(!giaPresente) {
                                listPartecipants.add(p);
                                new ItinerariesTask(ViaggioActivity.this, p, codiceViaggio, nameForUrl).execute();

                                final ImageView image = new RoundedImageView(ViaggioActivity.this, null);
                                image.setContentDescription(p.getEmail());

                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onClickImagePartecipant(v);
                                    }
                                });

                                layoutPartecipants.removeAllViews();
                                popolaPartecipanti();
                                text.setText("");
                                Toast.makeText(getBaseContext(), R.string.success_add_participant, Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getBaseContext(), R.string.fail_add_participant, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }

                    Log.i(TAG, "lista Partecipanti al viaggio: " + listPartecipants);



                }

            }
        });

        dialog.show();




    }

    private void showAlertDialog(final View v){
        //Dialog per cancel o backPressed su aggiunta partecipanti
        new android.support.v7.app.AlertDialog.Builder(ViaggioActivity.this)
                .setTitle(getString(R.string.back))
                .setMessage(getString(R.string.alert_message))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onClickAddPartecipant(v);
                    }
                })
                .setIcon(ContextCompat.getDrawable(ViaggioActivity.this, R.drawable.logodefbordo))
                .show();

    }

    public void onClickDateHelp(View v) {
        new android.support.v7.app.AlertDialog.Builder(ViaggioActivity.this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.dateFormat))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(ContextCompat.getDrawable(ViaggioActivity.this, R.drawable.logodefbordo))
                .show();
    }

    private void onClickDeleteTravel(View view) {
        new android.support.v7.app.AlertDialog.Builder(ViaggioActivity.this)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.delete_travel_alert))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteTravelTask(ViaggioActivity.this, codiceViaggio).execute();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(ContextCompat.getDrawable(ViaggioActivity.this, R.drawable.logodefbordo))
                .show();

    }

    private class UtentiTask extends AsyncTask<Void, Void, Void> {

        //TODO task da modularizzare, side-effect importanti da gestire

        //private static final String TAG = "TEST UtentiTask";

        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (InternetConnection.haveInternetConnection(ViaggioActivity.this)) {
                    Log.i(TAG, "CONNESSIONE Internet Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS);
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
                                sb.append(line).append("\n");
                            }
                            is.close();
                            result = sb.toString();
                            JSONArray jArray = new JSONArray(result);

                            if(result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String nomeUtente = json_data.getString("nome");
                                    String cognomeUtente = json_data.getString("cognome");
                                    String emailUtente = json_data.getString("email");
                                    String usernameUtente = json_data.getString("username");
                                    String sesso = json_data.getString("sesso");
                                    String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                    String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                    Profilo p = new Profilo(emailUtente, nomeUtente, cognomeUtente,
                                            null, null, sesso, usernameUtente, null, null, null,
                                            urlImmagineProfilo, urlImmagineCopertina);

                                    profiles.add(p);
                                    stringaFinale = nomeUtente + " " + cognomeUtente + "\n" + "("+usernameUtente+")";
                                    names.add(stringaFinale);
                                }
                            }



                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }

                }
                else
                    Log.e(TAG, "CONNESSIONE Internet Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString()+ ": " + e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgressDialog();
            super.onPostExecute(aVoid);

        }
    }

    public void onClickHome(View v) {
       // metodo per tornare alla home mantenendo i dati
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private class PrivacyLevelAdapter extends ArrayAdapter<String> {

        //TODO inner class da rimuovere una volta sistemato l'adapter esterno


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

            //Log.i(TAG, "string: " + strings[position]);
            //Log.i(TAG, "sub: " + subs[position]);
            //Log.i(TAG, "img: " + arr_images[position]);

            return convertView;
        }



    }


    //TODO tasks da modularizzare

//    private class MyTaskIDFolder extends AsyncTask<Void, Void, Void> {
//
//
//        InputStream is = null;
//        String emailUser, nameFolder,result;
//        String urlFolder;
//        DriveId idFolder;
//        Context context;
//
//        public MyTaskIDFolder(Context c, String emailUtente, String urlFolder, String name){
//            context  = c;
//            emailUser = emailUtente;
//            this.urlFolder = urlFolder;
//            nameFolder = name;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
//            dataToSend.add(new BasicNameValuePair("email", emailUser));
//            dataToSend.add(new BasicNameValuePair("path", urlFolder));
//
//            try {
//                if (InternetConnection.haveInternetConnection(context)) {
//                    Log.i(TAG, "CONNESSIONE Internet Presente!");
//                    HttpClient httpclient = new DefaultHttpClient();
//
//                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_QUERY_FOLDER);
//                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
//                    HttpResponse response = httpclient.execute(httppost);
//                    HttpEntity entity = response.getEntity();
//                    is = entity.getContent();
//
//                    if (is != null) {
//                        //converto la risposta in stringa
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//                            StringBuilder sb = new StringBuilder();
//                            String line = null;
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line + "\n");
//                            }
//                            is.close();
//
//                            result = sb.toString();
//                        } catch (Exception e) {
//                            Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
//                        }
//                    }
//                    else {
//                        Log.i(TAG, "Input Stream uguale a null");
//                    }
//                }
//                else
//                    Log.e(TAG, "CONNESSIONE Internet Assente!");
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, e.toString()+ ": " + e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Log.i(TAG, "risultato della query: " + result);
//            if(result != null && !result.equals("null\n")){
//                Log.i(TAG, "Presente cartella di viaggio");
//
//                if(bitmapImageTravel != null){
//                    Log.i(TAG, "upload dell'immagine del viaggio");
//
//                    String pathImage = urlFolder+"/";
//                    nameImageTravel = String.valueOf(System.currentTimeMillis()) + ".jpg";
//
//                    Log.i(TAG, "nome immagine: " + nameImageTravel);
//                    new UploadFilePHP(ViaggioActivity.this,bitmapImageTravel,pathImage,
//                            Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
//
//                    //TODO da correggere
//                    new InsertCoverImageTravelTask(ViaggioActivity.this,email,codiceViaggio, null,
//                            pathImage + Constants.NAME_IMAGES_TRAVEL_DEFAULT, bitmapImageTravel,
//                            layoutCopertinaViaggio).execute();
//                }
//                else{
//                    Log.i(TAG, "solo prelievo immagine copertina viaggio gia presente");
//
//                    //TODO da correggere
//                    new BitmapWorkerTask(null,layoutCopertinaViaggio).execute(Constants.ADDRESS_TAT
//                            + urlFolder + "/" + Constants.NAME_IMAGES_TRAVEL_DEFAULT);
//
//                }
//            }
//            else{
//                //se Non è presente la cartella in fase di upload la creo per poi aggiungere l'immagine del viaggio
//                if(bitmapImageTravel != null){
//                    new MyTaskFolder(ViaggioActivity.this, emailUser,codiceViaggio,null,urlFolder,
//                            nameFolder).execute();
//                }
//            }
//            super.onPostExecute(aVoid);
//
//        }
//    }
//
//
//
//    private class MyTaskFolder extends AsyncTask<Void, Void, Void> {
//
//        InputStream is = null;
//        String emailUser, idTravel,result, urlCartella;
//        String nomeCartella;
//        DriveId idFolder;
//        Context context;
//
//        public MyTaskFolder(Context c, String emailUtente, String idTravel, DriveId id, String url,String n){
//            context  = c;
//            emailUser = emailUtente;
//            this.idTravel = idTravel;
//            idFolder = id;
//            nomeCartella = n;
//            urlCartella = url;
//        }
//
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
//            dataToSend.add(new BasicNameValuePair("emailUtente", emailUser));
//            dataToSend.add(new BasicNameValuePair("codiceViaggio", idTravel));
//            dataToSend.add(new BasicNameValuePair("codiceCartella", idFolder+""));
//            dataToSend.add(new BasicNameValuePair("nomeCartella", nomeCartella));
//            dataToSend.add(new BasicNameValuePair("urlCartella", urlCartella));
//
//            String urlImmagineViaggio = urlCartella + "/" + Constants.NAME_IMAGES_TRAVEL_DEFAULT;
//            dataToSend.add(new BasicNameValuePair("urlImmagine", urlImmagineViaggio));
//
//
//            try {
//                if (InternetConnection.haveInternetConnection(context)) {
//                    Log.i(TAG, "CONNESSIONE Internet Presente!");
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httppost = new HttpPost(Constants.ADDRESS_TAT+ADDRESS_INSERT_FOLDER);
//                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
//                    HttpResponse response = httpclient.execute(httppost);
//
//                    HttpEntity entity = response.getEntity();
//
//                    is = entity.getContent();
//
//                    if (is != null) {
//                        //converto la risposta in stringa
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//                            StringBuilder sb = new StringBuilder();
//                            String line = null;
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line).append("\n");
//                            }
//                            is.close();
//                            result = sb.toString();
//                        } catch (Exception e) {
//                            Log.i(TAG, "Errore nel risultato o nel convertire il risultato");
//                        }
//                    }
//                    else {
//                        Log.i(TAG, "Input Stream uguale a null");
//                    }
//                }
//                else
//                    Log.e(TAG, "CONNESSIONE Internet Assente!");
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, e.toString()+ ": " + e.getMessage());
//            }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//
//            String pathImage = urlCartella+"/";
//            new UploadFilePHP(ViaggioActivity.this,bitmapImageTravel,pathImage,
//                    Constants.NAME_IMAGES_TRAVEL_DEFAULT).execute();
//
//            //TODO da correggere
//            new InsertCoverImageTravelTask(ViaggioActivity.this,email,codiceViaggio, null,
//                    pathImage + Constants.NAME_IMAGES_TRAVEL_DEFAULT, bitmapImageTravel,
//                    layoutCopertinaViaggio).execute();
//
//            super.onPostExecute(aVoid);
//
//        }
//    }




}
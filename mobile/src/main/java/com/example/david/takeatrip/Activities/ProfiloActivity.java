package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.david.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.example.david.takeatrip.AsyncTasks.InserimentoImmagineCopertinaTask;
import com.example.david.takeatrip.AsyncTasks.InserimentoImmagineProfiloTask;
import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveId;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveIdCover;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UtilS3Amazon;
import com.facebook.Profile;
import com.google.android.gms.drive.DriveId;
import com.squareup.picasso.Picasso;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("deprecation")
public class ProfiloActivity extends TabActivity implements AsyncResponseDriveId, AsyncResponseDriveIdCover{

    private final int REQUEST_UPLOAD_PROFILE_IMAGE = 123;
    private final int REQUEST_UPLOAD_COVER_IMAGE = 124;
    private final int QUALITY_OF_IMAGE = Constants.QUALITY_PHOTO;
    private final int DIMENSION_PROFILE_IMAGE = Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT - 30;


    private final String ADDRESS_INSERIMENTO = "InserimentoFollow.php";
    private final String ADDRESS_PRELIEVO = "PrendiFollower.php";

    private final String QUERY_FOLLOWERS = "QueryCountFollowers.php";
    private final String QUERY_FOLLOWINGS = "QueryCountFollowings.php";

    private final String QUERY_VERIFICA_FOLLOWING = "QueryVerificaFollowing.php";
    private Profilo corrente;

    private TextView viewName;
    private TextView viewSurname, numFollowersView, numFollowingsView;
    private Button follow;

    private RoundedImageView imageProfile;
    private ImageView coverImage;
    private LinearLayout layoutCoverImage;

    private String name, surname, email, emailEsterno, emailProfiloVisitato, emailFollowing;
    private String date, password, nazionalita, sesso, username, lavoro, descrizione, tipo;

    private Profile profile;
    private TabHost TabHost;

    private boolean externalView = false;

    private String idFolder, idImageProfile, idCoverImage, numFollowers, numFollowings;

    private boolean alreadyFollowing = false;


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
    private ArrayList<HashMap<String, Object>> transferRecordMaps;


    // The S3 client used for getting the list of objects in the bucket
    private AmazonS3Client s3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        viewName = (TextView) findViewById(R.id.Nome);
        viewSurname = (TextView) findViewById(R.id.Cognome);
        imageProfile = (RoundedImageView) findViewById(R.id.imageView_round_Profile);
        layoutCoverImage = (LinearLayout) findViewById(R.id.layoutCoverImage);
        coverImage = (ImageView) findViewById(R.id.cover_image);
        follow= (Button) findViewById(R.id.segui);
        numFollowersView = (TextView) findViewById(R.id.numberFollowers);
        numFollowingsView = (TextView) findViewById(R.id.numberFollowings);



        transferUtility = UtilS3Amazon.getTransferUtility(this);
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();
        s3 = UtilS3Amazon.getS3Client(ProfiloActivity.this);


        if (getIntent() != null) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            emailEsterno = intent.getStringExtra("emailEsterno");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");
            nazionalita = intent.getStringExtra("nazionalita");
            sesso = intent.getStringExtra("sesso");
            username = intent.getStringExtra("username");
            lavoro = intent.getStringExtra("lavoro");
            descrizione = intent.getStringExtra("descrizione");
            tipo = intent.getStringExtra("tipo");
            profile = intent.getParcelableExtra("profile");
            idImageProfile = intent.getStringExtra("urlImmagineProfilo");
            idCoverImage = intent.getStringExtra("urlImmagineCopertina");


            //per aggiornamento numero follow...
            new MyTaskQueryNumFollowers().execute();
            new MyTaskQueryNumFollowings().execute();


            if(idImageProfile == null || idImageProfile.equals("null")){
                if(profile!= null){
                    final Uri image_uri = profile.getProfilePictureUri(DIMENSION_PROFILE_IMAGE, DIMENSION_PROFILE_IMAGE);
                    final URI image_URI;

                    try {
                        image_URI = new URI(image_uri.toString());
                        Picasso.with(this).load(image_URI.toURL().toString()).into(imageProfile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    if(sesso != null && sesso.equals("M")){
                        imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.default_male));
                    }
                    else if (sesso != null && sesso.equals("F")){
                        imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.default_female));
                    }
                }
            }
            else{

                beginDownloadProfilePicture(idImageProfile);
            }


            if(idCoverImage != null && !idCoverImage.equals("null")){
                beginDownloadCoverPicture(idCoverImage);
            }


                if(password != null) {
                corrente = new Profilo(email);
                follow.setVisibility(View.INVISIBLE);
            }
            else {
                MyTaskVerificaFollowing mTF = new MyTaskVerificaFollowing();
                mTF.execute();

                externalView = true;
                corrente = new Profilo(emailEsterno);
                if(email == null){
                    TakeATrip TAT = (TakeATrip)getApplicationContext();
                    if(TAT != null)
                        email = TAT.getProfiloCorrente().getEmail();
                }
                if(email.equals(emailEsterno)){
                    externalView = false;
                    follow.setVisibility(View.INVISIBLE);
                    Log.i("TEST", "email: " + email);
                    Log.i("TEST", "email esterno: " + emailEsterno);
                }
                else{
                    follow.setVisibility(View.VISIBLE);
                    Log.i("TEST", "settato visibile: ");

                }
            }

            if(externalView){
                Log.i("TEST", "visualizzazione esterna del profilo");
                Log.i("TEST", "email: "+ email);
                Log.i("TEST", "emailEsterno: " + emailEsterno);
            }

            if(name.length() > Constants.LIMIT_NAMES_PROFILE){
                String newName = name.substring(0,Constants.LIMIT_NAMES_PROFILE-2);
                newName += "...";
                viewName.setText(newName);
            }
            else{
                viewName.setText(name);
            }

            if(surname.length() > Constants.LIMIT_NAMES_PROFILE){
                String newName = surname.substring(0,Constants.LIMIT_NAMES_PROFILE-2);
                newName += "...";
                viewSurname.setText(newName);
            }
            else{
                viewSurname.setText(surname);
            }
        }

        TabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = TabHost.newTabSpec("INFO");
        TabHost.TabSpec tab2 = TabHost.newTabSpec("STATS");
        TabHost.TabSpec tab3 = TabHost.newTabSpec("DESTINATIONS");


        ImageView image1 = new ImageView(this);
        image1.setImageResource(R.drawable.ic_person_black_18dp);

        ImageView image2 = new ImageView(this);
        image2.setImageResource(R.drawable.ic_map_black_18dp);

        //ImageView image3 = new ImageView(this);
        tab1.setIndicator(image1);
        //tab2.setIndicator("STATS");
        tab3.setIndicator(image2);

        Intent intentInfo = new Intent(this, InfoActivity.class);
        intentInfo.putExtra("name", name);
        intentInfo.putExtra("surname", surname);
        if(password != null){
            intentInfo.putExtra("email", email);
            intentInfo.putExtra("emailEsterno", emailFollowing);
        }
        else{
            TakeATrip TAT = (TakeATrip)getApplicationContext();
            email = TAT.getProfiloCorrente().getEmail();
        }
        intentInfo.putExtra("emailEsterno", emailEsterno);
        intentInfo.putExtra("dateOfBirth", date);
        intentInfo.putExtra("pwd", password);
        intentInfo.putExtra("nazionalita", nazionalita);
        intentInfo.putExtra("sesso", sesso);
        intentInfo.putExtra("username", username);
        intentInfo.putExtra("lavoro", lavoro);
        intentInfo.putExtra("descrizione", descrizione);
        intentInfo.putExtra("tipo", tipo);
        intentInfo.putExtra("profile", profile);
        tab1.setContent(intentInfo);


        tab2.setContent(new Intent(this, StatsActivity.class));

        Intent intentDest = new Intent(this, MapsActivity.class);
        if(email != null){
            intentDest.putExtra("email", email);
        }
        intentDest.putExtra("emailEsterno", emailEsterno);

        Log.i("TEST", "email: " + email);
        Log.i("TEST", "email esterno: " + emailEsterno);

        tab3.setContent(intentDest);


        //TabHost.setup();
        TabHost.addTab(tab1);
        //TabHost.addTab(tab2);
        TabHost.addTab(tab3);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profilo, menu);
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






/*

    public void onClickInfoTab(View v) {
        Intent intentInfo = new Intent(this, InfoActivity.class);
        intentInfo.putExtra("name", name);
        intentInfo.putExtra("surname", surname);
        intentInfo.putExtra("email", email);
        intentInfo.putExtra("dateOfBirth", date);
        intentInfo.putExtra("pwd", password);
        intentInfo.putExtra("nazionalita", nazionalita);
        intentInfo.putExtra("sesso", sesso);
        intentInfo.putExtra("username", username);
        intentInfo.putExtra("lavoro", lavoro);
        intentInfo.putExtra("descrizione", descrizione);
        intentInfo.putExtra("tipo", tipo);

    }
    */



    String mCurrentPhotoPath;
    String imageFileName;

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = timeStamp + ".jpg";

        File image = new File(android.os.Environment.getExternalStorageDirectory(), imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.i("TEST", "path file immagine: " + mCurrentPhotoPath);

        return image;
    }


    public void ClickOnFollow(View v){

        MyTaskInsertFollowing mTF = new MyTaskInsertFollowing();
        Log.i("TEST", "CLICCATO INSERIMENTO FOLLOWER");
        mTF.execute();
        follow.setText("FOLLOWING");
        follow.setEnabled(false);

        follow.setBackground(getDrawable(R.drawable.button_follow_cliccato));
    }




    private void setButtonToFollowing() {

        if (alreadyFollowing) {
            Log.i("TEST", "GIA' SEGUI QUESTO UTENTE! ");
            follow.setText("FOLLOWING");
            follow.setEnabled(false);
            follow.setBackground(getDrawable(R.drawable.button_follow_cliccato));
        }
    }


    public void ClickFollowers(View v) {
        Intent intentFollowers = new Intent(this, VisualizzazioneFollowActivity.class);
        intentFollowers.putExtra("name", name);
        intentFollowers.putExtra("surname", surname);
        if(emailEsterno != null ){
            intentFollowers.putExtra("email", emailEsterno);
            Log.i("TEST", "Email di chi voglio vedere i followers (Esterno) " + emailEsterno);
        }
        else{
            TakeATrip TAT = (TakeATrip)getApplicationContext();
            email = TAT.getProfiloCorrente().getEmail();
            intentFollowers.putExtra("email", email);
            Log.i("TEST", "Email di chi voglio vedere i followers " + email);
        }
    startActivity(intentFollowers);

    }

//    public void ClickFollowing(View v) {
//        Intent intentFollowers = new Intent(this, VisualizzazioneFollowActivity.class);
//        intentFollowers.putExtra("name", name);
//        intentFollowers.putExtra("surname", surname);
//        if(emailEsterno != null ){
//            intentFollowers.putExtra("email", emailEsterno);
//            Log.i("TEST", "Email di chi voglio vedere i followers (Esterno) " + emailEsterno);
//        }
//        else{
//            TakeATrip TAT = (TakeATrip)getApplicationContext();
//            email = TAT.getProfiloCorrente().getEmail();
//            intentFollowers.putExtra("email", email);
//            Log.i("TEST", "Email di chi voglio vedere i followers " + email);
//        }
//        startActivity(intentFollowers);
//    }


    private ViewAnimator animator;

    public void ClickImageProfile(View v) {
        try {
            if(!externalView){
                ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);
                AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setItems(R.array.CommandsImageProfile, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //TODO: VIEW IMAGE PROFILE

                                break;
                            case 1: //change image profile
                                Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intentPick, Constants.REQUEST_IMAGE_PICK);

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
            }

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }
    }


    public void ClickOnCoverImage(View v) {
        if(!externalView){
            try {
                ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

                AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setItems(R.array.CommandsCoverImage, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //view cover image

                                //TODO : IMPLEMENT THE VIEW OF THE IMAGE

                                break;
                            case 1:
                                Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intentPick, Constants.REQUEST_COVER_IMAGE_PICK);
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
                                        startActivityForResult(intent, Constants.REQUEST_COVER_IMAGE_CAPTURE);
                                    }
                                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        File file = null;
        if (resultCode == RESULT_OK) {

            //return from take a photo
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
                Log.i("TEST", "immagine fatta");

                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(imageFileName)) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    Log.i("TEST", "path file immagine: " + f.getAbsolutePath());

                    try {
                        beginUploadProfilePicture(f.getAbsolutePath());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    imageProfile.setImageBitmap(bitmap);

                    String path = android.os.Environment.getExternalStorageDirectory().toString();
                    f.delete();

                    OutputStream outFile = null;
                    file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_OF_IMAGE, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if (requestCode == Constants.REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);

                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.i("TEST", "image from gallery:" + picturePath + "");
                Log.i("TEST", "bitmap:" + thumbnail + "");


                beginUploadProfilePicture(picturePath);
                imageProfile.setImageBitmap(thumbnail);


            } else if (requestCode == Constants.REQUEST_COVER_IMAGE_CAPTURE) {
                Log.i("TEST", "immagine copertina fatta");

                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(imageFileName)) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);


                    beginUploadCoverPicture(f.getAbsolutePath());


                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    layoutCoverImage.setBackground(d);

                    String path = android.os.Environment.getExternalStorageDirectory().toString();
                    f.delete();

                    OutputStream outFile = null;
                    file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_OF_IMAGE, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == Constants.REQUEST_COVER_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.i("image from gallery:", picturePath + "");


                beginUploadCoverPicture(picturePath);

                Drawable d = new BitmapDrawable(getResources(), thumbnail);
                layoutCoverImage.setBackground(d);
            }
        }
    }


    private void beginDownloadProfilePicture(String key) {

        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 60; // 1 hour.
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(Constants.BUCKET_NAME,key);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(expiration);

        Log.i("TEST", "expiration date image: " + generatePresignedUrlRequest.getExpiration());

        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);

        Picasso.with(this).
                load(url.toString()).
                resize(DIMENSION_PROFILE_IMAGE,DIMENSION_PROFILE_IMAGE).
                into(imageProfile);

        // Initiate the download
        //TransferObserver observer = transferUtility.download(email, key, file);
        //Log.i("TEST", "downloaded file: " + file);
        //Log.i("TEST", "key file: " + key);

        Log.i("TEST", "url file: " + url);

        //observer.setTransferListener(new DownloadListener());

    }


    private void beginDownloadCoverPicture(String key) {

        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 60; // 1 hour.
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(Constants.BUCKET_NAME,key);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);

        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);


        new BitmapWorkerTask(coverImage,layoutCoverImage).execute(url.toString());


        Log.i("TEST", "url file: " + url);

        //observer.setTransferListener(new DownloadListener());

    }



    private void beginUploadProfilePicture(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);

        ObjectMetadata myObjectMetadata = new ObjectMetadata();
        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, email + "/"+ Constants.PROFILE_PICTURES_LOCATION +"/"+file.getName(), file);
        new InserimentoImmagineProfiloTask(this,email,null,email + "/"+ Constants.PROFILE_PICTURES_LOCATION +"/"+file.getName()).execute();

        // observer.setTransferListener(new UploadListener());
    }



    private void beginUploadCoverPicture(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);

        ObjectMetadata myObjectMetadata = new ObjectMetadata();
        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, email +"/"+ Constants.COVER_IMAGES_LOCATION +"/"+file.getName(), file);
        new InserimentoImmagineCopertinaTask(this,email,null,email +"/"+ Constants.COVER_IMAGES_LOCATION +"/"+file.getName()).execute();

        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUploadProfilePicture -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }



    //Return the id of the uploaded profile image
    @Override
    public void processFinish(DriveId output) {
        Log.i("TEST", "uploaded profile image with id: " + output);

        new InserimentoImmagineProfiloTask(this,email,output).execute();

    }


    //Return the id of the uploaded cover image
    @Override
    public void processFinish2(DriveId output) {
        Log.i("TEST", "uploaded cover image with id: " + output);
        new InserimentoImmagineCopertinaTask(this,email,output).execute();
    }


    private class MyTaskVerificaFollowing extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("emailSeguito", emailEsterno));
            dataToSend.add(new BasicNameValuePair("emailSeguace", email));

            Log.i("TEST: ", "MIA MAIL ESISTE FOLLOWING: " + email);
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + QUERY_VERIFICA_FOLLOWING);
                httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                if (is != null) {
                    //converto la risposta in stringa
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();

                        String result = sb.toString();

                        Log.i("TEST", "result VERIFICA FOLLOWING " + result);


                        if (result.contains("null")) {

                            Log.i("TEST", "Non sono presenti following");

                            alreadyFollowing = false;
                        }else {

                            alreadyFollowing = true;

                        }


                    } catch (Exception e) {
                        Log.e("TEST", "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e("TEST", "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

              setButtonToFollowing();

            super.onPostExecute(aVoid);
        }
    }


    private class MyTaskInsertFollowing extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("emailEsterno", emailEsterno));


            Log.i("TEST", "dati follow: " + email + " " + emailEsterno);
            try {
                if (InternetConnection.haveInternetConnection(ProfiloActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost;
                    httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO);

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

                            Log.i("TEST", "result " + result);

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }
                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }
            return null;
        }
    }

    private class MyTaskQueryNumFollowers extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String result = "";

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();

            if (!externalView) {
                dataToSend.add(new BasicNameValuePair("email", email));
            } else {
                dataToSend.add(new BasicNameValuePair("email", emailEsterno));
            }


            try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + QUERY_FOLLOWERS);
                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                    HttpResponse response = httpclient.execute(httppost);

                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();

                    if (is != null) {
                        //converto la risposta in stringa
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            is.close();

                            result = sb.toString();

                            Log.i("TEST", "result: " + result);

                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            numFollowers = ""+jsonObject.getInt(Constants.COUNT_FOLLOW_ID);

                            Log.i("TEST", "numFollowers: " + numFollowers);

                        } catch (Exception e) {
                            //Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                            Log.e("TEST", "eccezione query followers: "+e.toString());

                        }
                    } else {
                        //Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }
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


    private class MyTaskQueryNumFollowings extends AsyncTask<Void, Void, Void> {
        InputStream is = null;
        String result = "";

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();

            if (!externalView) {
                dataToSend.add(new BasicNameValuePair("email", email));
            } else {
                dataToSend.add(new BasicNameValuePair("email", emailEsterno));
            }

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + QUERY_FOLLOWINGS);
                httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                if (is != null) {
                    //converto la risposta in stringa
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        is.close();

                        result = sb.toString();

                        Log.i("TEST", "result: " + result);

                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        numFollowings = ""+jsonObject.getInt(Constants.COUNT_FOLLOW_ID);

                        Log.i("TEST", "numFollowings: " + numFollowings);
                    } catch (Exception e) {
                        //Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        Log.e("TEST", "eccezione query followings: "+e.toString());

                    }
                } else {
                    //Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            //TODO: sono scambiati followers and following
            numFollowersView.setText(numFollowings);
            numFollowingsView.setText(numFollowers);
        }
    }


    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("TEST", "onError: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.i("TEST", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }
        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.i("TEST", "onStateChanged: " + id + ", " + state);
        }
    }

}

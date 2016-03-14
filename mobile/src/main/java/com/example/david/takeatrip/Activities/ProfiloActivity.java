package com.example.david.takeatrip.Activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveId;
import com.example.david.takeatrip.Interfaces.AsyncResponseDriveIdCover;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DownloadImageTask;
import com.example.david.takeatrip.Utilities.RetrieveImageTask;
import com.example.david.takeatrip.Utilities.RoundedImageView;
import com.example.david.takeatrip.Utilities.UploadFilePHP;
import com.example.david.takeatrip.Utilities.UploadImageTask;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.drive.DriveId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("deprecation")
public class ProfiloActivity extends TabActivity implements AsyncResponseDriveId, AsyncResponseDriveIdCover{

    private final int REQUEST_UPLOAD_PROFILE_IMAGE = 123;
    private final int REQUEST_UPLOAD_COVER_IMAGE = 124;
    private final int QUALITY_OF_IMAGE = Constants.QUALITY_PHOTO;


    private TextView viewName;
    private TextView viewSurname, viewDate, viewEmail;

    private RoundedImageView imageProfile;
    private ImageView coverImage;
    private LinearLayout layoutCoverImage;

    private String name, surname, email, emailEsterno;
    private String date, password, nazionalita, sesso, username, lavoro, descrizione, tipo;
    private int codice;

    private Profile profile;
    private Bitmap immagineProfilo, immagineCopertina;

    private TabHost TabHost;

    private boolean externalView = false;

    private Bitmap bitmap = null;
    private String idFolder, idImageProfile, idCoverImage;



    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private View thumb1View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        viewName = (TextView) findViewById(R.id.Nome);
        viewSurname = (TextView) findViewById(R.id.Cognome);
        imageProfile = (RoundedImageView) findViewById(R.id.imageView_round_Profile);
        layoutCoverImage = (LinearLayout) findViewById(R.id.layoutCoverImage);
        coverImage = (ImageView) findViewById(R.id.cover_image);

        thumb1View = findViewById(R.id.imageView_round_Profile);


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


            if(idImageProfile == null || idImageProfile.equals("null")){
                if(profile!= null){
                    final Uri image_uri = profile.getProfilePictureUri(70, 70);
                    final URI image_URI;

                    try {
                        image_URI = new URI(image_uri.toString());
                        DownloadImageTask task = new DownloadImageTask(imageProfile);
                        task.execute(image_URI.toURL().toString());
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

                DownloadImageTask task = new DownloadImageTask(imageProfile);
                task.execute(Constants.ADDRESS_TAT + idImageProfile);
            }

            if(idCoverImage == null || idCoverImage.equals("null")){
                if(profile!= null){
                    try {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {

                                            Log.i("TEST",  response.toString());

                                            JSONObject coverImageObject =  object.getJSONObject("cover");
                                            String url_cover_image = coverImageObject.getString("source");

                                            Log.i("TEST", "immagine copertina: " + url_cover_image);

                                            //URL newurl = new URL(url_image);
                                            DownloadImageTask task = new DownloadImageTask(coverImage, layoutCoverImage);
                                            task.execute(url_cover_image);


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("TEST", e.getMessage());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "cover");
                        request.setParameters(parameters);
                        request.executeAsync();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                new DownloadImageTask(coverImage,layoutCoverImage).execute(Constants.ADDRESS_TAT + idCoverImage);
            }


            if(password == null) {
                externalView = true;
                if (email.equals(emailEsterno)) {
                    externalView = false;
                }
            }
            if(externalView){
                Log.i("TEST", "visualizzazione esterna del profilo");
            }
            viewName.setText(name);
            viewSurname.setText(surname);
        }



        TabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = TabHost.newTabSpec("INFO");
        TabHost.TabSpec tab2 = TabHost.newTabSpec("STATS");
        TabHost.TabSpec tab3 = TabHost.newTabSpec("DESTINATIONS");


        //TODO: usare setIndicator(View) per personalizzare i tab
        tab1.setIndicator("INFO");
        tab2.setIndicator("STATS");
        tab3.setIndicator("DEST");

        Intent intentInfo = new Intent(this, InfoActivity.class);
        intentInfo.putExtra("name", name);
        intentInfo.putExtra("surname", surname);
        intentInfo.putExtra("email", email);
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
        intentDest.putExtra("email", email);
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



    public void ClickImageProfile(View v) {
        try {


            if(!externalView){
                ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);
                AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setItems(R.array.CommandsImageProfile, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //view image profile


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
            else{


                //TODO: far visualizzare solo la foto profilo
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

                                //TODO

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
        else{
            //TODO: far visualizzare solo l'immagine copertina
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


                        if(false){

                            /*//TODO: fornire anche il caricamento sul drive
                    UploadImageTask task = new UploadImageTask(this, bitmap, Constants.NAME_IMAGES_PROFILE_DEFAULT, idFolder, "profile");
                    task.delegate = this;
                    task.execute();
                    */
                        }
                        else{
                            String pathImage = email+"/";

                            new UploadFilePHP(this,bitmap,pathImage,Constants.NAME_IMAGES_PROFILE_DEFAULT).execute();
                            new MyTaskInsertImageProfile(this,email,null,pathImage + Constants.NAME_IMAGES_PROFILE_DEFAULT).execute();
                        }
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

                String pathImage = email+"/";

                try {

                    //TODO: loggato con google
                    if(false){

                    }
                    else{
                        new UploadFilePHP(this,thumbnail,pathImage,Constants.NAME_IMAGES_PROFILE_DEFAULT).execute();
                        new MyTaskInsertImageProfile(this,email,null,pathImage + Constants.NAME_IMAGES_PROFILE_DEFAULT).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //UploadImageTask task = new UploadImageTask(this, thumbnail, Constants.NAME_IMAGES_PROFILE_DEFAULT, idFolder, "profile");
                //task.delegate = this;
                //task.execute();

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

                    try {


                        //TODO: fornire anche il caricamento sul drive
                        if(false){
                            /*
                            UploadImageTask task = new UploadImageTask(this, bitmap, Constants.NAME_IMAGES_COVER_DEFAULT, idFolder, "cover");
                            task.delegate2 = this;
                            task.execute();
                            */
                        }
                        else{
                            String pathImage = email+"/";

                            new UploadFilePHP(this,bitmap,pathImage,Constants.NAME_IMAGES_COVER_DEFAULT).execute();
                            new MyTaskInsertCoverimage(this,email,null,pathImage + Constants.NAME_IMAGES_COVER_DEFAULT).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



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



                //TODO: fornire anche il caricamento sul drive
                if(false){
                    /*
                    UploadImageTask task = new UploadImageTask(this, thumbnail, Constants.NAME_IMAGES_COVER_DEFAULT, idFolder, "cover");
                    task.delegate2 = this;
                    task.execute();
                    */
                }
                else{
                    String pathImage = email+"/";
                    new UploadFilePHP(this,thumbnail,pathImage,Constants.NAME_IMAGES_COVER_DEFAULT).execute();
                    new MyTaskInsertCoverimage(this,email,null,pathImage + Constants.NAME_IMAGES_COVER_DEFAULT).execute();
                }

                Drawable d = new BitmapDrawable(getResources(), thumbnail);
                layoutCoverImage.setBackground(d);
            }
        }
    }


    //Return the id of the uploaded profile image
    @Override
    public void processFinish(DriveId output) {
        Log.i("TEST", "uploaded profile image with id: " + output);


        //TODO: aggiungere url al DB
        new MyTaskInsertImageProfile(this,email,output).execute();

    }


    //Return the id of the uploaded cover image
    @Override
    public void processFinish2(DriveId output) {
        Log.i("TEST", "uploaded cover image with id: " + output);

        //TODO: aggiungere url al DB
        new MyTaskInsertCoverimage(this,email,output).execute();
    }


    private class MyTaskInsertImageProfile extends AsyncTask<Void, Void, Void> {

        private final String ADDRESS_INSERT_IMAGE_PROFILE = "InserimentoImmagineProfilo.php";
        InputStream is = null;
        String emailUser, result, urlImmagine;
        DriveId idFile;
        Context context;

        public MyTaskInsertImageProfile(Context c, String emailUtente, DriveId id){
            context  = c;
            emailUser = emailUtente;
            idFile = id;
        }

        public MyTaskInsertImageProfile(Context c, String emailUtente, DriveId id, String url){
            context  = c;
            emailUser = emailUtente;
            idFile = id;
            urlImmagine = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));
            dataToSend.add(new BasicNameValuePair("id", idFile+""));
            dataToSend.add(new BasicNameValuePair("url", urlImmagine));
            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_IMAGE_PROFILE);
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


                        } catch (Exception e) {
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
                    }
                }
                else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(),e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("TEST", "risultato operazione di inserimento immagine profilo nel DB:" + result);

            super.onPostExecute(aVoid);

        }

    }

    private class MyTaskInsertCoverimage extends AsyncTask<Void, Void, Void> {
        private final String ADDRESS_INSERT_COVER_PROFILE = "InserimentoImmagineCopertina.php";
        InputStream is = null;
        String emailUser, result, urlImmagine;
        DriveId idFile;
        Context context;

        public MyTaskInsertCoverimage(Context c, String emailUtente, DriveId id){
            context  = c;
            emailUser = emailUtente;
            idFile = id;
        }

        public MyTaskInsertCoverimage(Context c, String emailUtente, DriveId id, String url){
            context  = c;
            emailUser = emailUtente;
            idFile = id;
            urlImmagine = url;
        }


        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", emailUser));
            dataToSend.add(new BasicNameValuePair("id", idFile+""));
            dataToSend.add(new BasicNameValuePair("url", urlImmagine));


            try {
                if (InternetConnection.haveInternetConnection(context)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERT_COVER_PROFILE);
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


                        } catch (Exception e) {
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
                    }
                }
                else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(),e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("TEST", "risultato operazione di inserimento immagine copertina nel DB:" + result);
            super.onPostExecute(aVoid);

        }


    }




}

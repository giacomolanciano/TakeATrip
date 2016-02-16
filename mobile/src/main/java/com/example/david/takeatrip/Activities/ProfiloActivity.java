package com.example.david.takeatrip.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.RoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("deprecation")
public class ProfiloActivity extends TabActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_COVER_IMAGE_CAPTURE = 3;
    private static final int REQUEST_COVER_IMAGE_PICK = 4;


    private TextView viewName;
    private TextView viewSurname, viewDate, viewEmail;

    private RoundedImageView imageProfile;
    private LinearLayout layoutCoverImage;

    private String name, surname, email;
    private String date, password;


    private TabHost TabHost;


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


        thumb1View = findViewById(R.id.imageView_round_Profile);


        if (getIntent() != null) {
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");

            viewName.setText(name);
            viewSurname.setText(surname);
        } else {
            //Prendi i dati dal database perche è gia presente l'utente
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
        intentInfo.putExtra("dateOfBirth", date);
        intentInfo.putExtra("pwd", password);

        tab1.setContent(intentInfo);


        tab2.setContent(new Intent(this, StatsActivity.class));
        tab3.setContent(new Intent(this, MapsActivity.class));


        //TabHost.setup();
        TabHost.addTab(tab1);
        TabHost.addTab(tab2);
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


    public void ModificaCategoria(View v) {

    }








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
                            startActivityForResult(intentPick, REQUEST_IMAGE_PICK);
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
                                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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


    public void ClickOnCoverImage(View v) {
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
                        case 1: //change cover image
                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentPick, REQUEST_COVER_IMAGE_PICK);
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
                                    startActivityForResult(intent, REQUEST_COVER_IMAGE_CAPTURE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
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


                    //TODO: update the db with new profile image


                    imageProfile.setImageBitmap(bitmap);

                    String path = android.os.Environment.getExternalStorageDirectory().toString();
                    f.delete();

                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
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
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.i("image from gallery:", picturePath + "");


                //TODO: update the db with new profile image


                imageProfile.setImageBitmap(thumbnail);


            } else if (requestCode == REQUEST_COVER_IMAGE_CAPTURE) {
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


                    //TODO: update the db with new profile image


                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    layoutCoverImage.setBackground(d);

                    String path = android.os.Environment.getExternalStorageDirectory().toString();
                    f.delete();

                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
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
            } else if (requestCode == REQUEST_COVER_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.i("image from gallery:", picturePath + "");


                //TODO: update the db with new profile image


                Drawable d = new BitmapDrawable(getResources(), thumbnail);
                layoutCoverImage.setBackground(d);

            }
        }
    }
}

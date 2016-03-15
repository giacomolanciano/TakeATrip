package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.david.takeatrip.Fragments.DatePickerFragment;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.AudioRecord;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatesUtils;
import com.example.david.takeatrip.Utilities.MultimedialFile;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class TappaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private String email, codiceViaggio, nomeTappa, data;
    private int ordineTappa;

    private TextView textDataTappa;
    private String[] strings, subs;
    private int[] arr_images;

    private FloatingActionsMenu fabMenu;
    private FloatingActionButton buttonAddNote, buttonAddRecord, buttonAddVideo, buttonAddPhoto;

    private String imageFileName;
    private String videoFileName;
    private String mCurrentVideoPath;
    private String mCurrentPhotoPath;
    private boolean isCanceled;
    private boolean isRecordFileCreated;
    private int progressStatus;
    private AudioRecord record;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        textDataTappa = (TextView) findViewById(R.id.textDataTappa);


        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;


        Spinner privacySpinner = (Spinner) findViewById(R.id.spinnerPrivacyLevel);
        if (privacySpinner != null) {
            privacySpinner.setAdapter(new PrivacyLevelAdapter(TappaActivity.this, R.layout.entry_privacy_level, strings));
        }


//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_settings_black_36dp);
//        ab.setDisplayHomeAsUpEnabled(true);




        Intent i = getIntent();
        if (i != null) {

            email = i.getStringExtra("email");
            codiceViaggio = i.getStringExtra("codiceViaggio");
            ordineTappa = i.getIntExtra("ordine", 0);
            nomeTappa = i.getStringExtra("nome");
            data = i.getStringExtra("data");

        }

        Log.i("TEST", "email: "+email);
        Log.i("TEST", "codice: "+codiceViaggio);
        Log.i("TEST", "ordine: "+ordineTappa);
        Log.i("TEST", "nome: "+nomeTappa);
        Log.i("TEST", "data: "+data);





        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(nomeTappa);
        }
        if (textDataTappa != null) {
            String date;
            date = DatesUtils.convertFormatStringDate(data, Constants.DATABASE_DATE_FORMAT, Constants.DISPLAYED_DATE_FORMAT);

            textDataTappa.setText(date);
        }




        fabMenu = (FloatingActionsMenu) findViewById(R.id.menuInserimentoContenuti);


        buttonAddNote = (FloatingActionButton) findViewById(R.id.buttonAddNote);
        if (buttonAddNote != null) {
            buttonAddNote.setIcon(R.drawable.ic_edit_black_36dp);

            buttonAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("TEST", "add note pressed");

                    fabMenu.collapse();

                    onClickAddNote(view);

                }
            });
        }

        buttonAddRecord = (FloatingActionButton) findViewById(R.id.buttonAddRecord);
        if (buttonAddRecord != null) {
            buttonAddRecord.setIcon(R.drawable.ic_mic_black_36dp);

            buttonAddRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("TEST", "add record pressed");

                    fabMenu.collapse();

                    onClickAddRecord(view);
                }
            });
        }

        buttonAddVideo = (FloatingActionButton) findViewById(R.id.buttonAddVideo);
        if (buttonAddVideo != null) {
            buttonAddVideo.setIcon(R.drawable.ic_videocam_black_36dp);

            buttonAddVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("TEST", "add video pressed");

                    fabMenu.collapse();

                    onClickAddVideo(view);

                }
            });
        }

        buttonAddPhoto = (FloatingActionButton) findViewById(R.id.buttonAddPhoto);
        if (buttonAddPhoto != null) {
            buttonAddPhoto.setIcon(R.drawable.ic_photo_camera_black_36dp);

            buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("TEST", "add photo pressed");

                    fabMenu.collapse();

                    onClickAddImage(view);
                }
            });
        }


        isCanceled = false;
        isRecordFileCreated = false;
        progressStatus = 0;
        handler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_tappa, menu);
        return true;
    }


    public void onClickChangeDate(View v) {

        Log.i("TEST", "changing date");

        DialogFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putString(Constants.CURRENT_DATE_ID, textDataTappa.getText().toString());
        args.putString(Constants.DATE_FORMAT_ID, Constants.DISPLAYED_DATE_FORMAT);
        newFragment.setArguments(args);

        newFragment.show(getFragmentManager(), "datePicker");

    }



    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date newDate = c.getTime();

        textDataTappa.setText(DatesUtils.getStringFromDate(newDate, Constants.DISPLAYED_DATE_FORMAT));


        //TODO asynctask per aggiornamento data su db


        Log.i("TEST", "date changed");

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
                        Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                bitmapOptions);
                        Log.i("TEST", "path file immagine: " + f.getAbsolutePath());

//                        UploadImageTask task = new UploadImageTask(this, bitmap,
//                                Constants.NAME_IMAGES_PROFILE_DEFAULT, idFolder, "profile");
//                        task.delegate = this;
//                        task.execute();


                        //TODO verifica caricamento su drive

                        String path = Environment.getExternalStorageDirectory().toString();
                        f.delete();

//                        OutputStream outFile = null;
//                        File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                        try {
//                            outFile = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_OF_IMAGE, outFile);
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

                case Constants.REQUEST_IMAGE_PICK:


                    //############## ALTERNATIVA ##############

                    if (data != null) {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {

                            //TODO per selezione multipla, ancora non funzionante

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();

                                //In case you need image's absolute path
                                //String path= MultimedialFile.getRealPathFromURI(ListaTappeActivity.this, uri);
                                String path= getRealPathFromURI(TappaActivity.this, uri);


                                Log.i("TEST", "image path: " + path);
                            }


                        } else {
                            Log.e("TEST", "clipdata is null");

                            Uri selectedImage = data.getData();

                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();
                            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));


                            //String picturePath = getRealPathFromURI(ListaTappeActivity.this, selectedImage);

                            Log.i("TEST", "image from gallery: " + picturePath + "");


//                            UploadImageTask task = new UploadImageTask(this, thumbnail, Constants.NAME_IMAGES_PROFILE_DEFAULT, idFolder, "profile");
//                            task.delegate = this;
//                            task.execute();

                        }

                    } else {
                        Log.e("TEST", "data is null");

                    }



                    //TODO verifica caricamento su drive


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

                                } catch (IOException ex) {
                                    Log.e("TEST", "eccezione nella creazione di file immagine");
                                }

                                Log.i("TEST", "creato file immagine");

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


                            final ProgressDialog progressDialog = new ProgressDialog(TappaActivity.this);
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setView(R.layout.material_edit_text);
            } else {
                //TODO gestire compatiilità con versioni precedenti

                Log.i("TEST", "versione SDK < 21");
            }

            builder.setTitle(getString(R.string.labelNote));

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


            AlertDialog dialog = builder.create();
            dialog.show();


            EditText et = (EditText) dialog.findViewById(R.id.editText);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NOTE_MAX_LENGTH)});

            //TODO impostare scrollview dialog in maniera che il counter non venga nascosto

            //TextView label = (TextView) dialog.findViewById(R.id.label);
            //label.setText(getString(R.string.labelNote));

            final TextView counter = (TextView) dialog.findViewById(R.id.counter);
            counter.setText(Constants.NOTE_MAX_LENGTH + "/" + Constants.NOTE_MAX_LENGTH);

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // this will show characters remaining
                    int remainingChar = Constants.NOTE_MAX_LENGTH - s.toString().length();

                    counter.setText(remainingChar + "/" + Constants.NOTE_MAX_LENGTH);

                    if (remainingChar == 0) {
                        counter.setTextColor(Color.RED);
                    } else {
                        counter.setTextColor(Color.GRAY);
                    }
                }
            });



        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }


        Log.i("TEST", "END add note");

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

            //Log.i("TEST", "string: " + strings[position]);
            //Log.i("TEST", "sub: " + subs[position]);
            //Log.i("TEST", "img: " + arr_images[position]);

            return convertView;
        }
    }

}

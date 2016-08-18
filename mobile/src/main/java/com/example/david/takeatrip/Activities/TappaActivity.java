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
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.david.takeatrip.AsyncTasks.AggiornamentoDataTappaTask;
import com.example.david.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.example.david.takeatrip.AsyncTasks.DeleteStopTask;
import com.example.david.takeatrip.AsyncTasks.GetNotesTask;
import com.example.david.takeatrip.AsyncTasks.GetUrlsContentsTask;
import com.example.david.takeatrip.AsyncTasks.InserimentoAudioTappaTask;
import com.example.david.takeatrip.AsyncTasks.InserimentoImmagineTappaTask;
import com.example.david.takeatrip.AsyncTasks.InserimentoNotaTappaTask;
import com.example.david.takeatrip.AsyncTasks.InserimentoVideoTappaTask;
import com.example.david.takeatrip.AsyncTasks.UploadFileS3Task;
import com.example.david.takeatrip.Fragments.DatePickerFragment;
import com.example.david.takeatrip.GraphicalComponents.AdaptableGridView;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.AudioRecord;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatesUtils;
import com.example.david.takeatrip.Utilities.DeviceStorageUtils;
import com.example.david.takeatrip.Utilities.MultimedialFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class TappaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "TEST TappaActivity";

    private static final String ADDRESS_QUERY_URLS= "QueryImagesOfStops.php";


    private String email, codiceViaggio, nomeTappa, data;
    private int ordineTappa;

    private TextView textDataTappa;
    private String[] strings, subs;
    private int[] arr_images;

    private FloatingActionsMenu fabMenu;
    private FloatingActionButton buttonAddNote, buttonAddRecord, buttonAddVideo, buttonAddPhoto, buttonDelete;

    private String imageFileName;
    private String videoFileName;
    private boolean isCanceled;
    private boolean isRecordFileCreated;
    private int progressStatus;
    private AudioRecord record;
    private Handler handler;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;

    private AppBarLayout appBarLayout;

    private String livelloCondivisioneTappa;





    private static final int INITIAL_DELAY_MILLIS = 500;
    //private MyExpandableListItemAdapter mExpandableListItemAdapter;
    private ListView listView;

    private List<Bitmap> immaginiSelezionate, videoSelezionati;
    private Map<Bitmap,String> bitmap_nomeFile;
    private Map<Bitmap, String> pathsImmaginiSelezionate;
    private List<String> audioSelezionati;

    private List<String> noteInserite;

    private AdaptableGridView gridViewPhotos;
    private AdaptableGridView gridViewRecords;
    private AdaptableGridView gridViewVideos;
    private AdaptableGridView gridViewNotes;

    private ImageView coverImageTappa;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa);

        Intent i = getIntent();
        if (i != null) {

            email = i.getStringExtra("email");
            codiceViaggio = i.getStringExtra("codiceViaggio");
            ordineTappa = i.getIntExtra("ordine", 0);   //è l'ordine del db
            nomeTappa = i.getStringExtra("nome");
            data = i.getStringExtra("data");

        }

        Log.i(TAG, "email: "+email);
        Log.i(TAG, "codice: "+codiceViaggio);
        Log.i(TAG, "ordine: "+ordineTappa);
        Log.i(TAG, "nome: "+nomeTappa);
        Log.i(TAG, "data: "+data);


/*
        mExpandableListItemAdapter = new MyExpandableListItemAdapter(this);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(mExpandableListItemAdapter);
        alphaInAnimationAdapter.setAbsListView(lista);

        assert alphaInAnimationAdapter.getViewAnimator() != null;
        alphaInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        lista.setAdapter(alphaInAnimationAdapter);
        //getListView().setAdapter(alphaInAnimationAdapter);

*/

        final Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        textDataTappa = (TextView) findViewById(R.id.textDataTappa);
        textDataTappa.setText(data);


        strings = getResources().getStringArray(R.array.PrivacyLevel);
        subs = getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;


        Spinner privacySpinner = (Spinner) findViewById(R.id.spinnerPrivacyLevel);

        final PrivacyLevelAdapter adapter = new PrivacyLevelAdapter(TappaActivity.this, R.layout.entry_privacy_level, strings);

        if (privacySpinner != null) {

            privacySpinner.setAdapter(adapter);

            privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "elemento selezionato: " + adapter.getItem(position));
                    livelloCondivisioneTappa = adapter.getItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else {
            Log.e(TAG, "privacySpinner is null");
        }





//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_settings_black_36dp);
//        ab.setDisplayHomeAsUpEnabled(true);



        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        final View view = findViewById(R.id.viewSfondoTitolo);


        //TODO rivedere comportamento collapsing toolbar
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if(verticalOffset == -toolbar.getHeight()){
//                    Log.i(TAG, "toolbar collapsed");
//
//                    view.setVisibility(View.INVISIBLE);
//                }else
//                    view.setVisibility(View.VISIBLE);
//
//
//            }
//        });


        gridViewPhotos = (AdaptableGridView) findViewById(R.id.grid_view_photos);
        gridViewVideos = (AdaptableGridView) findViewById(R.id.grid_view_videos);
        gridViewRecords = (AdaptableGridView) findViewById(R.id.grid_view_records);
        gridViewNotes = (AdaptableGridView) findViewById(R.id.grid_view_notes);



        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(nomeTappa);
        }
        if (textDataTappa != null) {
            String date = null;

            //TODO: il formato della data non consente questo metodo
            if(data != null && !data.equals(""))
                //date = DatesUtils.convertFormatStringDate(data, Constants.DATABASE_DATE_FORMAT, Constants.DISPLAYED_DATE_FORMAT);

            if(date != null)
                textDataTappa.setText(date);
        }




        fabMenu = (FloatingActionsMenu) findViewById(R.id.menuInserimentoContenuti);


        buttonAddNote = (FloatingActionButton) findViewById(R.id.buttonAddNote);
        if (buttonAddNote != null) {
            buttonAddNote.setIcon(R.drawable.ic_edit_black_36dp);

            buttonAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i(TAG, "add note pressed");

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

                    Log.i(TAG, "add record pressed");

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

                    Log.i(TAG, "add video pressed");

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

                    Log.i(TAG, "add photo pressed");

                    fabMenu.collapse();

                    onClickAddImage(view);
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

                    onClickDeleteStop(view);

                }
            });
        }


        isCanceled = false;
        isRecordFileCreated = false;
        progressStatus = 0;
        handler = new Handler();

        immaginiSelezionate = new ArrayList<Bitmap>();
        videoSelezionati = new ArrayList<Bitmap>();
        bitmap_nomeFile = new HashMap<Bitmap,String>();
        pathsImmaginiSelezionate = new HashMap<Bitmap, String>();


        noteInserite = new ArrayList<String>();
        audioSelezionati = new ArrayList<String>();

        coverImageTappa = (ImageView) findViewById(R.id.coverImageTappa);

        new GetUrlsContentsTask(TappaActivity.this, codiceViaggio, gridViewPhotos, Constants.QUERY_STOP_IMAGES,
                email, ordineTappa, coverImageTappa).execute();

        new GetUrlsContentsTask(TappaActivity.this, codiceViaggio, gridViewVideos, Constants.QUERY_STOP_VIDEOS,
                email, ordineTappa).execute();

        new GetUrlsContentsTask(TappaActivity.this, codiceViaggio, gridViewRecords, Constants.QUERY_STOP_AUDIO,
                email, ordineTappa).execute();

        new GetNotesTask(TappaActivity.this, codiceViaggio, gridViewNotes, Constants.QUERY_STOP_NOTES,
                email, ordineTappa).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_tappa, menu);
        return true;
    }


    public void onClickChangeDate(View v) {

        Log.i(TAG, "changing date");

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

        new AggiornamentoDataTappaTask(TappaActivity.this, ordineTappa, codiceViaggio, email,
                DatesUtils.getStringFromDate(newDate, Constants.DATABASE_DATE_FORMAT)).execute();

        //TODO
        // poiche le tappe vengono caricate tutte in lista tappe activity, se si aggiorna, si torna indietro
        // e si riclicca sulla tappa la data apare non aggiornata, quando invece lo è nel db
        // capire come sistemare questo inconveniente


        Log.i(TAG, "date changed");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case Constants.REQUEST_IMAGE_CAPTURE:

                    Log.i(TAG, "REQUEST_IMAGE_CAPTURE");

                    File f = new File(DeviceStorageUtils.getImagesStoragePath());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(imageFileName)) {
                            f = temp;
                            break;
                        }
                    }
                    try {

                        Bitmap thumbnail = BitmapWorkerTask.decodeSampledBitmapFromPath(f.getAbsolutePath(), 0, 0);
                        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                        String nomeFile = timeStamp + Constants.IMAGE_EXT;

                        Log.i(TAG, "timeStamp image: " + nomeFile);
                        if(thumbnail != null){

                            pathsImmaginiSelezionate.put(thumbnail, f.getAbsolutePath());

                            immaginiSelezionate.add(thumbnail);
                            bitmap_nomeFile.put(thumbnail,nomeFile);
                        }

                        Log.i(TAG, "path file immagine: " + f.getAbsolutePath());
                        Log.i(TAG, "bitmap file immagine: " + thumbnail);

                        uploadPhotos();


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
                                String path= getRealPathFromURI(TappaActivity.this, uri);
                                Log.i(TAG, "image path: " + path);
                            }
                        } else {
                            Log.i(TAG, "clipdata is null");

                            Uri selectedImage = data.getData();

                            Log.i(TAG, "uri selected image: " + selectedImage);

                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();


                            //TODO creazione bitmap per ora necessaria per far apparire miniature durante aggiunta tappa
                            //rivedere meccanismo usando Picasso

                            Bitmap thumbnail = BitmapWorkerTask.decodeSampledBitmapFromPath(picturePath, 0, 0);

                            /*
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 16;
                            Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
                            */

                            Log.i(TAG, "image from gallery: " + picturePath + "");

                            String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                            String nomeFile = timeStamp + Constants.IMAGE_EXT;

                            Log.i(TAG, "timeStamp image: " + nomeFile);


                            if(thumbnail != null){

                                //inserire file in una lista di file per caricamento in s3
                                pathsImmaginiSelezionate.put(thumbnail, picturePath);

                                immaginiSelezionate.add(thumbnail);
                                bitmap_nomeFile.put(thumbnail, nomeFile);
                            }


                            Log.i(TAG, "elenco immagini selezionate: " + immaginiSelezionate);
                            Log.i(TAG, "elenco path risorse selezionate: " + pathsImmaginiSelezionate);
                            Log.i(TAG, "elenco nomi risorse: " + bitmap_nomeFile.values());


                        }


                        uploadPhotos();




                    } else {
                        Log.e(TAG, "data is null");

                    }





                    break;

                case Constants.REQUEST_VIDEO_CAPTURE:
                    Log.i(TAG, "REQUEST_VIDEO_CAPTURE");

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

                        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                        String nomeFile = timeStamp + Constants.VIDEO_EXT;

                        Log.i(TAG, "timeStamp image: " + nomeFile);
                        if(bitmap != null){

                            pathsImmaginiSelezionate.put(bitmap, fileVideo.getAbsolutePath());

                            videoSelezionati.add(bitmap);
                            bitmap_nomeFile.put(bitmap,nomeFile);
                        }

                        Log.i(TAG, "path file video: " + fileVideo.getAbsolutePath());
                        Log.i(TAG, "bitmap file immagine: " + bitmap);


                        uploadVideos();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    break;

                case Constants.REQUEST_VIDEO_PICK:
                    Log.i(TAG, "REQUEST_VIDEO_PICK");

                    Uri selectedVideo = data.getData();

                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor c = getContentResolver().query(selectedVideo, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String videoPath = c.getString(columnIndex);
                    c.close();

                    Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);


                    Log.i(TAG, "video from gallery: " + videoPath + "");

                    String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                    String nomeFile = timeStamp + Constants.VIDEO_EXT;

                    Log.i(TAG, "timeStamp video: " + nomeFile);


                    if(thumbnail != null){

                        //inserire file in una lista di file per caricamento in s3
                        pathsImmaginiSelezionate.put(thumbnail, videoPath);

                        videoSelezionati.add(thumbnail);
                        bitmap_nomeFile.put(thumbnail, nomeFile);
                    }


                    Log.i(TAG, "elenco video selezionate: " + videoSelezionati);
                    Log.i(TAG, "elenco path risorse selezionate: " + pathsImmaginiSelezionate);
                    Log.i(TAG, "elenco nomi risorse: " + bitmap_nomeFile.values());


                    uploadVideos();


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

                    uploadAudio();


                    break;


                default:
                    Log.e(TAG, "requestCode non riconosciuto");
                    break;





            }

        } else {

            Log.e(TAG, "result: " + resultCode);
        }
    }




    //metodi ausiliari


    private static String getRealPathFromURI(Context context, Uri contentUri) {

        Log.i(TAG, "entro in getRealPathFromURI(...)");

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
                Log.i(TAG, "result: "+result);
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





    private void onClickAddImage(View v) {

        Log.i(TAG, "add image pressed");

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

        Log.i(TAG, "END add image");

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

        Log.i(TAG, "add record pressed");

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

                                                    //il caricamento viene iniziato immediatamente
                                                    uploadAudio();

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

                                                                    //il caricamento viene iniziato immediatamente
                                                                    uploadAudio();
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
                            Log.e(TAG, "azione non riconosciuta");
                            break;
                    }
                }
            });


            builder.create().show();


        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }



        Log.i(TAG, "END add record");

    }


    private void onClickAddNote(View v) {



        Log.i(TAG, "add note pressed");


        try {

            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(wrapper);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.material_edit_text, null);
            builder.setView(dialogView);

            textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.textInputLayout);
            if (textInputLayout != null) {
                textInputLayout.setCounterEnabled(true);
                textInputLayout.setCounterMaxLength(Constants.NOTE_MAX_LENGTH);
            }
            textInputEditText = (TextInputEditText) dialogView.findViewById(R.id.editText);

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

                            noteInserite.add(textInputEditText.getText().toString());

                            new InserimentoNotaTappaTask(TappaActivity.this, ordineTappa, codiceViaggio,
                                    email, livelloCondivisioneTappa, noteInserite).execute();
                            Log.i(TAG, "edit text confirmed");
                        }
                    });


            builder.setTitle(getString(R.string.labelNote));

            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }


        //NB il clear() per le note viene chiamato alla fine del corrisposndente asyntask
        //altrimenti la lista viene svuotata prima della sua esecuzione

        Log.i(TAG, "END add note");

    }


    public void onClickDateHelp(View v) {
        new android.support.v7.app.AlertDialog.Builder(TappaActivity.this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.dateFormat))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(ContextCompat.getDrawable(TappaActivity.this, R.drawable.logodefbordo))
                .show();
    }

    private void onClickDeleteStop(View view) {
        final View v = view;
        new android.support.v7.app.AlertDialog.Builder(TappaActivity.this)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.delete_stop_alert))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteStopTask(TappaActivity.this, codiceViaggio, ordineTappa).execute();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(ContextCompat.getDrawable(TappaActivity.this, R.drawable.logodefbordo))
                .show();
    }

    private void uploadPhotos() {

        if(immaginiSelezionate.size() > 0){
            for(Bitmap bitmap : immaginiSelezionate) {

                String nameImage = bitmap_nomeFile.get(bitmap);
                String pathImage = pathsImmaginiSelezionate.get(bitmap);

                Log.i(TAG, "email: " + email);
                Log.i(TAG, "codiceViaggio: " + codiceViaggio);
                Log.i(TAG, "name of the image: " + nameImage);
                Log.i(TAG, "livello Condivisione: " + livelloCondivisioneTappa);



                new UploadFileS3Task(TappaActivity.this, Constants.BUCKET_TRAVELS_NAME,
                        codiceViaggio, Constants.TRAVEL_IMAGES_LOCATION, email, pathImage, nameImage).execute();


                //TODO nella colonna urlImmagine si potrebbe salvare soltanto il nome del file
                //si può riscostruire il path a partire dalle altre info nella riga corrispondente

                String completePath = codiceViaggio + "/" + Constants.TRAVEL_IMAGES_LOCATION + "/" + email + "_" + nameImage;

                new InserimentoImmagineTappaTask(TappaActivity.this, email,codiceViaggio,
                        ordineTappa,null,completePath,livelloCondivisioneTappa).execute();

            }

        }


        //per evitare che un contenuto venga caricato due volte
        immaginiSelezionate.clear();

    }


    private void uploadVideos() {

        if(videoSelezionati.size() > 0){
            for(Bitmap bitmap : videoSelezionati) {

                String nameVideo = bitmap_nomeFile.get(bitmap);
                String pathVideo = pathsImmaginiSelezionate.get(bitmap);

                Log.i(TAG, "email: " + email);
                Log.i(TAG, "codiceViaggio: " + codiceViaggio);
                Log.i(TAG, "name of the video: " + nameVideo);
                Log.i(TAG, "livello Condivisione: " + livelloCondivisioneTappa);



                new UploadFileS3Task(TappaActivity.this, Constants.BUCKET_TRAVELS_NAME,
                        codiceViaggio, Constants.TRAVEL_VIDEOS_LOCATION, email, pathVideo, nameVideo).execute();


                //TODO nella colonna urlImmagine si potrebbe salvare soltanto il nome del file
                //si può riscostruire il path a partire dalle altre info nella riga corrispondente

                String completePath = codiceViaggio + "/" + Constants.TRAVEL_VIDEOS_LOCATION + "/" + email + "_" + nameVideo;

                new InserimentoVideoTappaTask(TappaActivity.this, email,codiceViaggio,
                        ordineTappa,null,completePath,livelloCondivisioneTappa).execute();

            }

        }

        //per evitare che un contenuto venga caricato due volte
        videoSelezionati.clear();
    }


    private void uploadAudio() {

        if(!audioSelezionati.isEmpty()) {

            String newAudioName;
            String timeStamp;


            for(String pathAudio : audioSelezionati) {

                timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());
                newAudioName = timeStamp + Constants.AUDIO_EXT;

                new UploadFileS3Task(TappaActivity.this, Constants.BUCKET_TRAVELS_NAME,
                        codiceViaggio, Constants.TRAVEL_AUDIO_LOCATION, email, pathAudio, newAudioName).execute();

                String completePath = codiceViaggio + "/" + Constants.TRAVEL_AUDIO_LOCATION + "/" + email + "_" + newAudioName;

                new InserimentoAudioTappaTask(TappaActivity.this, email,codiceViaggio,
                        ordineTappa,null,completePath,livelloCondivisioneTappa).execute();
            }

        }

        //per evitare che un contenuto venga caricato due volte
        audioSelezionati.clear();
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






}

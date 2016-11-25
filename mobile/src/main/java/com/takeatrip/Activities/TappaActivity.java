package com.takeatrip.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.takeatrip.Adapters.ExpandableListAdapter;
import com.takeatrip.Adapters.GridViewAdapter;
import com.takeatrip.Adapters.ListViewVideoAdapter;
import com.takeatrip.AsyncTasks.AggiornamentoDataTappaTask;
import com.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.takeatrip.AsyncTasks.DeleteStopTask;
import com.takeatrip.AsyncTasks.GetNotesTask;
import com.takeatrip.AsyncTasks.GetUrlsContentsTask;
import com.takeatrip.AsyncTasks.InserimentoAudioTappaTask;
import com.takeatrip.AsyncTasks.InserimentoImmagineTappaTask;
import com.takeatrip.AsyncTasks.InserimentoNotaTappaTask;
import com.takeatrip.AsyncTasks.InserimentoVideoTappaTask;
import com.takeatrip.AsyncTasks.UpdateCondivisioneTappaTask;
import com.takeatrip.AsyncTasks.UploadFileS3Task;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.Classes.NotaTappa;
import com.takeatrip.Classes.Tappa;
import com.takeatrip.Fragments.DatePickerFragment;
import com.takeatrip.GraphicalComponents.AdaptableExpandableListView;
import com.takeatrip.GraphicalComponents.AdaptableGridView;
import com.takeatrip.Interfaces.AsyncResponseNotes;
import com.takeatrip.Interfaces.AsyncResponseVideos;
import com.takeatrip.R;
import com.takeatrip.Utilities.AudioRecord;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatesUtils;
import com.takeatrip.Utilities.DeviceStorageUtils;
import com.takeatrip.Utilities.MultimedialFile;
import com.takeatrip.Utilities.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class TappaActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, AsyncResponseNotes, AsyncResponseVideos, PlaybackControlView.VisibilityListener, ExoPlayer.EventListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TEST TappaActivity";
    private static final String ADDRESS_QUERY_URLS= "QueryImagesOfStops.php";
    private static final int PICK_IMAGE_MULTIPLE = 100;

    private String email, codiceViaggio, nomeTappa, data, fromListOfStops;

    private String visualizzazioneEsterna = "";
    private boolean esterna = false;

    private int ordineTappa, ordineTappaDB;
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
    //private ExpandableListItemAdapter mExpandableListItemAdapter;
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
    private List<String> contentsToDelete;
    private ProgressDialog progressDialog;

    private GridViewAdapter adapterVideos;

    private LinearLayout lnrImages;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;

    ExpandableListAdapter listAdapter;
    AdaptableExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<NotaTappa>> listDataChild;
    private ListView listViewVideos;
    ListViewVideoAdapter listViewVideoAdapter;
    private String emailProprietarioTappa;


    private NavigationView navigationView;
    private TextView ViewNomeViaggio;
    private LinearLayout linearLayoutHeader;
    private LinearLayout layoutContents,rowHorizontal;
    private RoundedImageView ViewImmagineViaggio;


    //private Tappa[] tappeViaggio;
    private ArrayList<Tappa> tappeViaggio;
    private String nomeViaggio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa2);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);
        View layoutHeader = navigationView.getHeaderView(0);
        ViewNomeViaggio = (TextView) layoutHeader.findViewById(R.id.textViewNameTravel);
        linearLayoutHeader = (LinearLayout) layoutHeader.findViewById(R.id.layoutHeaderTravel);



        Intent i = getIntent();
        if (i != null) {
            visualizzazioneEsterna = i.getStringExtra("visualizzazioneEsterna");
            email = i.getStringExtra("email");
            emailProprietarioTappa = i.getStringExtra("emailProprietarioTappa");
            codiceViaggio = i.getStringExtra("codiceViaggio");
            ordineTappa = i.getIntExtra("ordine", 0);
            ordineTappaDB = i.getIntExtra("ordineDB", 0); //è l'ordine del db
            nomeTappa = i.getStringExtra("nome");
            data = i.getStringExtra("data");
            livelloCondivisioneTappa = i.getStringExtra("livelloCondivisioneTappa");
            nomeViaggio = i.getStringExtra("nomeViaggio");
            tappeViaggio = i.getParcelableArrayListExtra("tappeViaggio");

        }

        /*
        Log.i(TAG, "email: "+email);
        Log.i(TAG, "emailProprietarioTappa: "+emailProprietarioTappa);
        Log.i(TAG, "codice: "+codiceViaggio);
        Log.i(TAG, "ordine: "+ordineTappa);
        Log.i(TAG, "ordineDB: "+ordineTappaDB);
        Log.i(TAG, "nome: "+nomeTappa);
        Log.i(TAG, "data: "+data);
        Log.i(TAG, "livelloCondivisione: "+livelloCondivisioneTappa);
        Log.i(TAG, "tappe del viaggio: " + tappeViaggio);
        */


        if (ViewNomeViaggio != null)
            ViewNomeViaggio.setText(nomeViaggio);

        CreaMenu(tappeViaggio);


        if(visualizzazioneEsterna != null){
            esterna = true;
            Log.i(TAG, "siamo in visualizzazione esterna della tappa: ");
        }


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
            privacySpinner.setSelection(Integer.parseInt(livelloCondivisioneTappa));

            Log.i(TAG,"esterna? "+ esterna);
            if(esterna) {
                privacySpinner.setEnabled(false);
            }
            else{
                privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG, "elemento selezionato: " + adapter.getItem(position));
                        livelloCondivisioneTappa = position+"";


                        try {
                            boolean result = new UpdateCondivisioneTappaTask(TappaActivity.this, codiceViaggio, ordineTappaDB, livelloCondivisioneTappa).execute().get();

                            if(!result){
                                Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }



        } else {
            Log.e(TAG, "privacySpinner is null");
        }


        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        final View view = findViewById(R.id.viewSfondoTitolo);


        gridViewPhotos = (AdaptableGridView) findViewById(R.id.grid_view_photos);
        gridViewVideos = (AdaptableGridView) findViewById(R.id.grid_view_videos);
        gridViewRecords = (AdaptableGridView) findViewById(R.id.grid_view_records);
        //listViewVideos = (ListView) findViewById(R.id.list_view_videos);
        

        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(nomeTappa);
        }
        if (textDataTappa != null) {
            String date = null;
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

                    fabMenu.collapse();

                    onClickDeleteStop(view);

                }
            });
        }

        if(esterna){
            fabMenu.setVisibility(View.INVISIBLE);
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
        contentsToDelete = new ArrayList<String>();

        coverImageTappa = (ImageView) findViewById(R.id.coverImageTappa);

        showProgressDialog();

        new GetUrlsContentsTask(TappaActivity.this, codiceViaggio, gridViewPhotos, Constants.QUERY_STOP_IMAGES,
                email, emailProprietarioTappa, ordineTappaDB, coverImageTappa).execute();

        GetUrlsContentsTask videoTask = new GetUrlsContentsTask(TappaActivity.this, codiceViaggio, gridViewVideos, Constants.QUERY_STOP_VIDEOS,
                email, emailProprietarioTappa, ordineTappaDB, null);
        videoTask.delegate = this;
        videoTask.execute();

        new GetUrlsContentsTask(TappaActivity.this, codiceViaggio, gridViewRecords, Constants.QUERY_STOP_AUDIO,
                email, emailProprietarioTappa, ordineTappaDB, null).execute();


        GetNotesTask GTN = new GetNotesTask(TappaActivity.this, codiceViaggio, null, Constants.QUERY_STOP_NOTES,
                email, emailProprietarioTappa, ordineTappaDB);
        GTN.delegate = this;
        GTN.execute();

        // get the listview
        expListView = (AdaptableExpandableListView) findViewById(R.id.notesExpandable);
        // preparing list data
        prepareListData();
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<NotaTappa>>();
        listDataHeader.add("View notes");
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(adapterVideos != null){
            adapterVideos.releasePlayer();
        }

    }

    protected void onStop(){
        super.onStop();
        if(adapterVideos != null){
            adapterVideos.releasePlayer();

        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id>=0){

            Tappa tappa = tappeViaggio.get(id);

            Intent i = new Intent(this, TappaActivity.class);
            int ordineTappa = Integer.parseInt(item.getTitle().toString().split("\\. ")[0]);
            i.putExtra("email", email);
            i.putExtra("emailProprietarioTappa", emailProprietarioTappa);
            i.putExtra("codiceViaggio", codiceViaggio);
            i.putExtra("ordine", ordineTappa);
            i.putExtra("ordineDB", tappa.getOrdine());
            i.putExtra("nome", item.getTitle());
            i.putExtra("data", DatesUtils.getStringFromDate(tappa.getData(), Constants.DISPLAYED_DATE_FORMAT));
            i.putExtra("codAccount", 0);
            i.putExtra("livelloCondivisioneTappa", tappa.getLivelloCondivisione());
            i.putExtra("nomeViaggio", nomeViaggio);
            i.putParcelableArrayListExtra("tappeViaggio", tappeViaggio);

            if(!email.equals(emailProprietarioTappa)){
                i.putExtra("visualizzazioneEsterna","true");
            }

            startActivity(i);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void CreaMenu(List<Tappa> tappe){
        Menu menu = navigationView.getMenu();
        menu.clear();

        if(menu != null) {
            int i = 0;
            for (Tappa t : tappe) {
                menu.add(0, i, Menu.NONE, (i+1) +". " + t.getName());
                i++;
            }
        }
    }




    @Override
    public void processFinishForNotes(NotaTappa[] notes) {
        List<NotaTappa> noteTappa = new ArrayList<NotaTappa>();

        if(notes != null){

            for(NotaTappa nt : notes){
                noteTappa.add(nt);
            }
            listDataChild.put(listDataHeader.get(0), noteTappa);
            if(noteTappa.size() == 0){
                noteTappa.add(new NotaTappa(null,"There are no notes","",0,null,""));
                listDataChild.put(listDataHeader.get(0), noteTappa);
            }
        }
        else{
            noteTappa.add(new NotaTappa(null,"There are no notes","",0,null,""));
            listDataChild.put(listDataHeader.get(0), noteTappa);
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild,email);
        expListView.setAdapter(listAdapter);


        hideProgressDialog();
    }


    @Override
    public void processFinishForVideos(List<ContenutoMultimediale> URLS) {
        adapterVideos = new GridViewAdapter(this, gridViewVideos, URLS, Constants.VIDEO_FILE, codiceViaggio, email);
        gridViewVideos.setAdapter(adapterVideos);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_tappa, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onClickChangeDate(View v) {

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

        showProgressDialog();
        new AggiornamentoDataTappaTask(TappaActivity.this, ordineTappaDB, codiceViaggio, email,
                DatesUtils.getStringFromDate(newDate, Constants.DATABASE_DATE_FORMAT)).execute();
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

                int columnIndex = cursor
                        .getColumnIndex(proj[0]);

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
                        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                        String nomeFile = timeStamp + Constants.IMAGE_EXT;


                        if(thumbnail != null){

                            pathsImmaginiSelezionate.put(thumbnail, f.getAbsolutePath());

                            immaginiSelezionate.add(thumbnail);
                            bitmap_nomeFile.put(thumbnail,nomeFile);
                        }

                        uploadPhotos();

                        //refresh activity
                        recreate();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case Constants.REQUEST_IMAGE_PICK:
                    if (data != null) {

                        Uri selectedImage = data.getData();

                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        String picturePath = c.getString(columnIndex);
                        c.close();

                        Bitmap thumbnail = BitmapWorkerTask.decodeSampledBitmapFromPath(picturePath, 0, 0);
                        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                        String nomeFile = timeStamp + Constants.IMAGE_EXT;

                        if(thumbnail != null){
                            pathsImmaginiSelezionate.put(thumbnail, picturePath);

                            immaginiSelezionate.add(thumbnail);
                            bitmap_nomeFile.put(thumbnail, nomeFile);
                        }

                        uploadPhotos();

                        //refresh activity
                        recreate();


                    } else {
                        Log.e(TAG, "data is null");

                    }
                    break;

                case PICK_IMAGE_MULTIPLE:
                    imagesPathList = new ArrayList<String>();
                    String[] imagesPath = data.getStringExtra("data").split("\\|");

                    for (int i=0;i<imagesPath.length;i++){

                        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                        String nomeFile = i+timeStamp + Constants.IMAGE_EXT;

                        imagesPathList.add(timeStamp + imagesPath[i]);
                        yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);

                        pathsImmaginiSelezionate.put(yourbitmap, imagesPath[i]);

                        immaginiSelezionate.add(yourbitmap);
                        bitmap_nomeFile.put(yourbitmap,nomeFile);
                    }


                    uploadPhotos();

                    //refresh activity
                    recreate();

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

                        String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());

                        String nomeFile = timeStamp + Constants.VIDEO_EXT;

                        if(bitmap != null){

                            pathsImmaginiSelezionate.put(bitmap, fileVideo.getAbsolutePath());

                            videoSelezionati.add(bitmap);
                            bitmap_nomeFile.put(bitmap,nomeFile);
                        }

                        uploadVideos();

                        //refresh activity
                        recreate();

                    } catch (Exception e) {
                        e.printStackTrace();
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

                    String timeStamp = new SimpleDateFormat(Constants.FILE_NAME_TIMESTAMP_FORMAT).format(new Date());
                    String nomeFile = timeStamp + Constants.VIDEO_EXT;

                    if(thumbnail != null){
                        //inserire file in una lista di file per caricamento in s3
                        pathsImmaginiSelezionate.put(thumbnail, videoPath);
                        videoSelezionati.add(thumbnail);
                        bitmap_nomeFile.put(thumbnail, nomeFile);
                    }

                    uploadVideos();
                    //refresh activity
                    recreate();
                    break;


                case Constants.REQUEST_RECORD_PICK:

                    Uri selectedAudio = data.getData();

                    String[] audioPath = {MediaStore.Audio.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedAudio, audioPath, null, null, null);
                    cursor.moveToFirst();
                    int columnIndexAudio = cursor.getColumnIndex(audioPath[0]);
                    String audioFilePath = cursor.getString(columnIndexAudio);
                    cursor.close();

                    audioSelezionati.add(audioFilePath);

                    uploadAudio();

                    //refresh activity
                    recreate();


                    break;


                default:
                    Log.e(TAG, "requestCode non riconosciuto");
                    break;
            }

        } else {

            Log.e(TAG, "result: " + resultCode);
        }
    }


    private void onClickAddImage(View v) {

        try {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Material_Light_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            builder.setItems(R.array.CommandsAddPhotoToStop, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case 0:
                            Intent intentPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentPick, Constants.REQUEST_IMAGE_PICK);
                            break;

                        case 1: //pick multiple images from gallery

                            Intent intentPick2 = new Intent(TappaActivity.this,CustomPhotoGalleryActivity.class);
                            startActivityForResult(intentPick2,PICK_IMAGE_MULTIPLE);
                            break;

                        case 2: //take a photo
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) != null) {

                                File photoFile = null;
                                try {

                                    photoFile = MultimedialFile.createImageFile();
                                    imageFileName = photoFile.getName();


                                } catch (IOException ex) {
                                    Log.e(TAG, "eccezione nella creazione di file immagine");
                                }

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

                                // Continue only if the File was successfully created
                                if (videoFile != null) {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                                    startActivityForResult(intent, Constants.REQUEST_VIDEO_CAPTURE);
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

                                            }

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

                                                                    record.stopRecording();
                                                                    progressDialog.dismiss();
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


    }


    private void onClickAddNote(View v) {

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

                        }
                    });

            builder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            noteInserite.add(textInputEditText.getText().toString());

                            new InserimentoNotaTappaTask(TappaActivity.this, ordineTappaDB, codiceViaggio,
                                    email, livelloCondivisioneTappa, noteInserite).execute();

                            //refresh activity
                            recreate();
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
        new android.support.v7.app.AlertDialog.Builder(TappaActivity.this)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.delete_stop_alert))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        getContentsToDelete(gridViewPhotos);
                        getContentsToDelete(gridViewVideos);
                        getContentsToDelete(gridViewRecords);

                        boolean result = false;
                        try {
                            result = new DeleteStopTask(TappaActivity.this, codiceViaggio, ordineTappaDB,
                                    contentsToDelete).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        if(result)
                            finish();
                        else
                            Toast.makeText(getApplicationContext(), "Error in delete",Toast.LENGTH_LONG).show();

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

                try {
                    boolean uploaded = new UploadFileS3Task(TappaActivity.this, Constants.BUCKET_TRAVELS_NAME,
                            codiceViaggio, Constants.TRAVEL_IMAGES_LOCATION, email, pathImage, nameImage).execute().get();

                    String completePath = codiceViaggio + "/" + Constants.TRAVEL_IMAGES_LOCATION + "/" + email + "_" + nameImage;

                    if(uploaded){
                        new InserimentoImmagineTappaTask(TappaActivity.this, email,codiceViaggio,
                                ordineTappaDB,null,completePath,livelloCondivisioneTappa).execute();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), R.string.error_upload, Toast.LENGTH_SHORT).show();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



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

                try {
                    boolean result = new UploadFileS3Task(TappaActivity.this, Constants.BUCKET_TRAVELS_NAME,
                            codiceViaggio, Constants.TRAVEL_VIDEOS_LOCATION, email, pathVideo, nameVideo).execute().get();

                    if(result){
                        String completePath = codiceViaggio + "/" + Constants.TRAVEL_VIDEOS_LOCATION + "/" + email + "_" + nameVideo;

                        new InserimentoVideoTappaTask(TappaActivity.this, email,codiceViaggio,
                                ordineTappaDB,null,completePath,livelloCondivisioneTappa).execute();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

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

                try {
                    boolean result = new UploadFileS3Task(TappaActivity.this, Constants.BUCKET_TRAVELS_NAME,
                            codiceViaggio, Constants.TRAVEL_AUDIO_LOCATION, email, pathAudio, newAudioName).execute().get();
                    if(result){
                        String completePath = codiceViaggio + "/" + Constants.TRAVEL_AUDIO_LOCATION + "/" + email + "_" + newAudioName;

                        new InserimentoAudioTappaTask(TappaActivity.this, email,codiceViaggio,
                                ordineTappaDB,null,completePath,livelloCondivisioneTappa).execute();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }

        }

        //per evitare che un contenuto venga caricato due volte
        audioSelezionati.clear();
    }


    private void getContentsToDelete(GridView gridView) {
        View elem;
        int count = gridView.getChildCount();
        for(int i = 0; i < count; i++) {
            elem = gridView.getChildAt(i);
            contentsToDelete.add(elem.getContentDescription().toString());
        }
    }

    public void onClickHome(View v) {
        // metodo per tornare alla home mantenendo i dati
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
    }




    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

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
            sub.setText(subs[position].replace(getString(R.string.these),getString(R.string.future)));

            ImageView icon=(ImageView)convertView.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);
            return convertView;
        }
    }


    @Override
    public void onVisibilityChange(int visibility) {

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
            progressDialog.dismiss();
        }
    }



}

package com.example.david.takeatrip.Activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.david.takeatrip.Fragments.DatePickerFragment;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatesUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Calendar;
import java.util.Date;

public class TappaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String DISPLAYED_DATE_FORMAT = "dd/MM/yyyy";

    private String email, codiceViaggio, nomeTappa, data;
    private int ordineTappa;

    TextView textDataTappa;
    private String[] strings, subs;
    private int[] arr_images;

    private FloatingActionsMenu fabMenu;
    private FloatingActionButton buttonAddNote, buttonAddRecord, buttonAddVideo, buttonAddPhoto;

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
            date = DatesUtils.convertFormatStringDate(data, "yyyy-MM-dd", DISPLAYED_DATE_FORMAT);

            textDataTappa.setText(date);
        }




        fabMenu = (FloatingActionsMenu) findViewById(R.id.menuInserimentoContenuti);
        if (fabMenu != null) {

        }


        buttonAddNote = (FloatingActionButton) findViewById(R.id.buttonAddNote);
        if (buttonAddNote != null) {
            buttonAddNote.setIcon(R.drawable.ic_edit_black_36dp);

            buttonAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("TEST", "add note pressed");


//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    //TODO inserire logica per inserimento nuovo contenuto nella tappa

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


//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    //TODO inserire logica per inserimento nuovo contenuto nella tappa

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


//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    //TODO inserire logica per inserimento nuovo contenuto nella tappa

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


//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    //TODO inserire logica per inserimento nuovo contenuto nella tappa

                }
            });
        }


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
        args.putString(Constants.DATE_FORMAT_ID, DISPLAYED_DATE_FORMAT);
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

        textDataTappa.setText(DatesUtils.getStringFromDate(newDate, DISPLAYED_DATE_FORMAT));


        //TODO asynctask per aggiornamento data su db


        Log.i("TEST", "date changed");

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

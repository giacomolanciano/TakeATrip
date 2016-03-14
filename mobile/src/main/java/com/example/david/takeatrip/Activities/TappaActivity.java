package com.example.david.takeatrip.Activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.david.takeatrip.Fragments.DatePickerFragment;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatesUtils;

import java.util.Calendar;
import java.util.Date;

public class TappaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String DISPLAYED_DATE_FORMAT = "dd/MM/yyyy";

    private String email, codiceViaggio, nomeTappa, data;
    private int ordineTappa;

    TextView textDataTappa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        textDataTappa = (TextView) findViewById(R.id.textDataTappa);


//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_settings_black_36dp);
//        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    //TODO inserire logica per inserimento nuovo contenuto nella tappa

                }
            });
        }


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
//            SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat endFormat = new SimpleDateFormat("dd/MM/yyyy");
//            Date formattedDate;
//
//            formattedDate = startFormat.parse(data, new ParsePosition(0));

            String date;
            //date = endFormat.format(formattedDate);
            date = DatesUtils.convertFormatStringDate(data, "yyyy-MM-dd", DISPLAYED_DATE_FORMAT);

            textDataTappa.setText(date);
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


}

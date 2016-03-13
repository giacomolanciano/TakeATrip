package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.david.takeatrip.R;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TappaActivity extends AppCompatActivity {


    private String email, codiceViaggio, nomeTappa, data;
    private int ordineTappa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        TextView textDataTappa = (TextView) findViewById(R.id.textDataTappa);



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
            SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat endFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date formattedDate;
            String date;

            formattedDate = startFormat.parse(data, new ParsePosition(0));
            date = endFormat.format(formattedDate);

            textDataTappa.setText(date);
        }

        //TODO asynctask e fragment per cambio data

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_tappa, menu);
        return true;
    }






}

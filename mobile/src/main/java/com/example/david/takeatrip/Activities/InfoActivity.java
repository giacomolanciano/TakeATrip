package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;

import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DatesDiff;


public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";


    private String name, surname, email, date, password, nazionalità, dataToday, sesso, username, lavoro, descrizione, tipo;
    private int year, month, day, yearToday, monthToday, dayToday, eta, etaFinale;
    private TextView viewDate, viewEmail, viewNazionalità, viewEta, viewSesso, viewUsername, viewLavoro, viewDescrizione, viewTipo;



    private boolean visualizzazioneEsterna = false;

    private Button buttonEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        viewEta = (TextView) findViewById(R.id.Eta);
        viewEmail = (TextView) findViewById(R.id.Email);
        viewNazionalità = (TextView) findViewById(R.id.Nazionalità);
        viewSesso = (TextView) findViewById(R.id.Sesso);
        viewUsername = (TextView) findViewById(R.id.Username);
        viewLavoro = (TextView) findViewById(R.id.Lavoro);
        viewDescrizione = (TextView) findViewById(R.id.Descrizione);
        viewTipo = (TextView) findViewById(R.id.Tipo);
        buttonEdit = (Button) findViewById(R.id.buttonEditProfile);

        Calendar calendar = Calendar.getInstance();
        dayToday =  calendar.get(Calendar.DAY_OF_MONTH);
        monthToday = calendar.get(Calendar.MONTH) + 1;
        yearToday = calendar.get(Calendar.YEAR);
        dataToday = yearToday+"-"+monthToday+"-"+dayToday;

        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            etaFinale = DatesDiff.eta(date, dataToday);
            password = intent.getStringExtra("pwd");
            nazionalità = intent.getStringExtra("nazionalita");
            sesso = intent.getStringExtra("sesso");
            username = intent.getStringExtra("username");
            lavoro = intent.getStringExtra("lavoro");
            descrizione = intent.getStringExtra("descrizione");
            tipo = intent.getStringExtra("tipo");




            if(password == null){
                visualizzazioneEsterna = true;
                buttonEdit.setVisibility(View.INVISIBLE);

                Log.i(TAG, "visualizzazione esterna del profilo");

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewNazionalità.setText(nazionalità);
        viewEta.setText(etaFinale + "");
        viewSesso.setText(sesso);
        viewUsername.setText(username);
        viewLavoro.setText(lavoro);
        viewDescrizione.setText(descrizione);
        viewTipo.setText(tipo);
    }


    public void onClickEditButton(View v){


        Intent intent = new Intent(InfoActivity.this, RegistrazioneActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("surname",surname);
        intent.putExtra("date", date);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("nazionalita", nazionalità);
        intent.putExtra("sesso", sesso);
        intent.putExtra("username", username);
        intent.putExtra("lavoro", lavoro);
        intent.putExtra("descrizione", descrizione);
        intent.putExtra("tipo", tipo);



        //TODO vedere se i nuovi valori possono essere ritornati tramite la chiamata startActivityForResult
        startActivity(intent);

    }


/*
        String[] splittedDate = date.split("-");
        year = Integer.parseInt(splittedDate[0]);
        month = Integer.parseInt(splittedDate[1]);
        day = Integer.parseInt(splittedDate[2]);


        Calendar calendar = Calendar.getInstance();
        dayToday =  calendar.get(Calendar.DAY_OF_MONTH);
        monthToday = calendar.get(Calendar.MONTH) + 1;
        yearToday = calendar.get(Calendar.YEAR);


        int somma1 = month + day;
        int somma2 = monthToday+dayToday;

        if (somma2>somma1){
            eta = yearToday - year;

        }else{
            eta = yearToday - year;
            eta = eta - 1;
        }
*/



    }



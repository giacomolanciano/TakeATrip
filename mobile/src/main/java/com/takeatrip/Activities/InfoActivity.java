package com.takeatrip.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.R;
import com.takeatrip.Utilities.DatesUtils;

import java.util.Calendar;


public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "TEST InfoActivity";


    private String name, surname, email, date, password, nazionalità, dataToday, sesso, username, lavoro, descrizione, tipo,emailEsterno;
    private int  yearToday, monthToday, dayToday, etaFinale;
    private TextView viewEmail, viewNazionalità, viewEta, viewSesso, viewUsername, viewLavoro, viewDescrizione, viewTipo;
    private TextView textUsername, textEta, textSesso, textNationality, textType, textJob, textDescription;

    private Profile profile;

    private boolean visualizzazioneEsterna = false;

    private Button buttonEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        textUsername = (TextView) findViewById(R.id.Username1);
        textSesso = (TextView) findViewById(R.id.Sesso1);
        textNationality = (TextView) findViewById(R.id.Nazionalita1);
        textEta = (TextView) findViewById(R.id.Eta1);
        textType = (TextView) findViewById(R.id.Tipo1);
        textJob = (TextView) findViewById(R.id.Lavoro1);
        textDescription = (TextView) findViewById(R.id.Descrizione1);

        viewEta = (TextView) findViewById(R.id.Eta);
        viewEmail = (TextView) findViewById(R.id.Email);
        viewNazionalità = (TextView) findViewById(R.id.Nazionalita);
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
            emailEsterno = intent.getStringExtra("emailEsterno");
            date = intent.getStringExtra("dateOfBirth");
            etaFinale = DatesUtils.calcolaEta(date, dataToday);
            password = intent.getStringExtra("pwd");
            nazionalità = intent.getStringExtra("nazionalita");
            sesso = intent.getStringExtra("sesso");
            username = intent.getStringExtra("username");
            lavoro = intent.getStringExtra("lavoro");
            descrizione = intent.getStringExtra("descrizione");
            tipo = intent.getStringExtra("tipo");
            profile = intent.getParcelableExtra("profile");


            visualizzazioneEsterna = true;
            if(email == null){
                TakeATrip TAT = (TakeATrip)getApplicationContext();
                if(TAT != null)
                    email = TAT.getProfiloCorrente().getEmail();
            }

            if((email != null && emailEsterno == null) || (email != null && email.equals(emailEsterno))){
                visualizzazioneEsterna = false;
            }

            if(visualizzazioneEsterna){
                buttonEdit.setVisibility(View.INVISIBLE);
                Log.i(TAG, "visualizzazione esterna del profilo");
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewUsername.setText(username);

        viewSesso.setText(sesso);
        if(sesso != null && sesso.equals("")){

        }

        viewEta.setText(etaFinale + "");
        if(date != null && date.equals("")){

        }

        viewNazionalità.setText(nazionalità);
        if(nazionalità != null && nazionalità.equals("")){

        }

        viewTipo.setText(tipo);
        if(tipo != null && tipo.equals("")){

        }

        viewLavoro.setText(lavoro);
        if(lavoro != null && lavoro.equals("")){

        }

        viewDescrizione.setText(descrizione);
        if(descrizione != null && descrizione.equals("")){

        }

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
        intent.putExtra("profile", profile);
        intent.putExtra("provieneDa", "InfoActivity");

        startActivity(intent);

    }



}



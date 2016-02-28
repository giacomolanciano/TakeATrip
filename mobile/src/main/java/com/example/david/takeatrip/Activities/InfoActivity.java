package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.david.takeatrip.R;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";


    private String name, surname, email, date, password;
    private TextView viewDate, viewEmail;



    private boolean visualizzazioneEsterna = false;

    private Button buttonEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        viewDate = (TextView) findViewById(R.id.DataDiNascita);
        viewEmail = (TextView) findViewById(R.id.Email);
        buttonEdit = (Button) findViewById(R.id.buttonEditProfile);

        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");


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
        viewDate.setText(date);
        viewEmail.setText(email);
    }


    public void onClickEditButton(View v){


        Intent intent = new Intent(InfoActivity.this, RegistrazioneActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("surname",surname);
        intent.putExtra("date", date);
        intent.putExtra("email", email);
        intent.putExtra("password", password);




        //TODO vedere se i nuovi valori possono essere ritornati tramite la chiamata startActivityForResult
        startActivity(intent);

    }
}

package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.david.takeatrip.R;

public class InfoActivity extends AppCompatActivity {

    private String name, surname, email, date, password;
    private TextView viewDate, viewEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        viewDate = (TextView) findViewById(R.id.DataDiNascita);
        viewEmail = (TextView) findViewById(R.id.Email);

        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
            password = intent.getStringExtra("pwd");

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



        //TODO vedere se i nuovi valori possono essere ritornati tramite la chiamata startActivityForResult
        startActivity(intent);

    }
}

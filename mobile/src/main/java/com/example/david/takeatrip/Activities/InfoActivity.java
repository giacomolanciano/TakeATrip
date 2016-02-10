package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.david.takeatrip.R;

public class InfoActivity extends AppCompatActivity {

    private String email, date;
    private TextView viewDate, viewEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        viewDate = (TextView) findViewById(R.id.DataDiNascita);
        viewEmail = (TextView) findViewById(R.id.Email);

        if(getIntent() != null){
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewDate.setText(date);
        viewEmail.setText(email);
    }
}

package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.david.takeatrip.R;

public class MetaActivity extends AppCompatActivity {


    private String nome = "";
    private TextView viewNome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta);


        viewNome = (TextView)findViewById(R.id.ViewNomeMeta);

        if(getIntent() != null){
            Intent intent = getIntent();
            nome = intent.getStringExtra("nomeMeta");
        }


        viewNome.setText(nome);


    }
}

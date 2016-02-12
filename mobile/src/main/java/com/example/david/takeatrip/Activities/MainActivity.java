package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.david.takeatrip.R;

public class MainActivity extends AppCompatActivity {



    private String name, surname, email;
    private String date;

    private ImageView imageViewProfileRound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            date = intent.getStringExtra("dateOfBirth");

        }
        else{
            //Prendi i dati dal database perche è gia presente l'utente
        }

        imageViewProfileRound = (ImageView)findViewById(R.id.imageView_round);



    }


    public void ClickImageProfile(View v){
        Intent openProfilo = new Intent(MainActivity.this, ProfiloActivity.class);
        openProfilo.putExtra("name", name);
        openProfilo.putExtra("surname", surname);
        openProfilo.putExtra("email", email);
        openProfilo.putExtra("dateOfBirth", date);


        // passo all'attivazione dell'activity
        startActivity(openProfilo);
    }


    public void ClickTravels(View v){
        Intent openListaViaggi = new Intent(MainActivity.this, ListaViaggiActivity.class);
        openListaViaggi.putExtra("email", email);

        // passo all'attivazione dell'activity
        startActivity(openListaViaggi);
    }


    public void ClickNewTravel(View v){
        //Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        //imageViewProfileRound.setImageBitmap(icon);
        Intent intent = new Intent(MainActivity.this, ViaggioActivity.class);
        startActivity(intent);


    }
}

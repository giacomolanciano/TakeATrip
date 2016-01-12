package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.david.takeatrip.R;

public class ProfiloActivity extends AppCompatActivity {


    private Button btnViaggi, buttonCategoria;
    private TextView viewName;
    private TextView viewSurname, viewDate, viewEmail;

    private String name, surname, email;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);


        viewName = (TextView) findViewById(R.id.Nome);
        viewSurname = (TextView) findViewById(R.id.Cognome);
        viewDate = (TextView) findViewById(R.id.DataDiNascita);
        viewEmail = (TextView) findViewById(R.id.Email);


        if(getIntent() != null){
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");


            //TODO: modify from String to Date
            date = intent.getStringExtra("dataOfBirth");

            viewName.setText(name);
            viewSurname.setText(surname);
            viewDate.setText(date);
            viewEmail.setText(email);



        }
        else{


            //Prendi i dati dal database perche è gia presente l'utente

        }



        buttonCategoria = (Button)findViewById(R.id.CategoriaViaggio);
        buttonCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // definisco l'intenzione
                Intent openListaViaggi = new Intent(ProfiloActivity.this, CategoriaActivity.class);
                // passo all'attivazione dell'activity
                startActivity(openListaViaggi);
            }
        });


        
        btnViaggi=(Button)findViewById(R.id.Listaviaggi);
        btnViaggi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // definisco l'intenzione
                Intent openListaViaggi = new Intent(ProfiloActivity.this, ListaViaggiActivity.class);
                openListaViaggi.putExtra("email", email);

                // passo all'attivazione dell'activity
                startActivity(openListaViaggi);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profilo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void ModificaCategoria(View v){

    }
}

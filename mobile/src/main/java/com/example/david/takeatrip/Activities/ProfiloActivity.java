package com.example.david.takeatrip.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.R;

public class ProfiloActivity extends AppCompatActivity {


    private Button btnViaggi, buttonCategoria, buttonDestinationSelection, buttonRegistra;
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
            date = intent.getStringExtra("dateOfBirth");

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

        buttonDestinationSelection=(Button)findViewById(R.id.DestinationSelection);
        buttonDestinationSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // definisco l'intenzione
                Intent intent = new Intent(ProfiloActivity.this, DestinationSelectionActivity.class);
                intent.putExtra("email", email);

                // passo all'attivazione dell'activity
                startActivity(intent);
            }
        });


        buttonRegistra=(Button)findViewById(R.id.ButtonRegistraViaggio);
        buttonRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // definisco l'intenzione
                Intent intent = new Intent(ProfiloActivity.this, MapsActivity.class);
                startActivity(intent);
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

    public void ClickImageProfile(View v){
        try{
            ContextThemeWrapper wrapper = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog);

            AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setItems(R.array.CommandsImageProfile, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0: //view image profile
                                    startActivity(new Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.INTERNAL_CONTENT_URI));

                                    break;
                                case 1: //change image profile
                                    startActivity(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI));
                                    break;
                                case 2:  //take a photo
                                    startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
                                    break;
                                case 3: //exit
                                    break;
                            }
                        }
                    });


                // Create the AlertDialog object and return it
                builder.create().show();

            }
            catch(Exception e){
                Log.e(e.toString().toUpperCase(), e.getMessage());
            }
    }

}

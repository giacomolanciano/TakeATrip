package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class RegistrazioneActivity extends AppCompatActivity {



    private final String ADDRESS_INSERIMENTO_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoProfilo.php";


    private final int YEAR_MAX_PICKER = 2016;
    private final int YEAR_MIN_PICKER = 1900;
    private final int YEAR_DEFAULT_PICKER = 2000;
    private final int MONTH_MAX_PICKER = 12;
    private final int MONTH_MIN_PICKER = 1;
    private final int MONTH_DEFAULT_PICKER = 1;
    private final int DAY_MAX_PICKER = 31;
    private final int DAY_MIN_PICKER = 1;
    private final int DAY_DEFAULT_PICKER = 1;

    private final int TEN = 10;

    private Button btnInvio;

    private EditText campoNome;
    private EditText campoCognome;
    private EditText campoEmail;
    private EditText campoPassword;
    private EditText campoData;

    private String data;

    private String nome, cognome, email, password;

    private NumberPicker pickerYear, pickerMonth, pickerDay;

    private int month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);


        campoNome = (EditText) findViewById(R.id.Nome);
        campoCognome = (EditText) findViewById(R.id.Cognome);
        campoEmail = (EditText) findViewById(R.id.Email);
        campoPassword = (EditText) findViewById(R.id.Password);
        campoData = (EditText) findViewById(R.id.DataDiNascita);


        pickerYear = (NumberPicker)findViewById(R.id.pickerYear);
        pickerYear.setMaxValue(YEAR_MAX_PICKER);
        pickerYear.setMinValue(YEAR_MIN_PICKER);
        pickerYear.setWrapSelectorWheel(false);
        pickerYear.setValue(YEAR_DEFAULT_PICKER);

        pickerMonth = (NumberPicker)findViewById(R.id.pickerMonth);
        pickerMonth.setMaxValue(MONTH_MAX_PICKER);
        pickerMonth.setMinValue(MONTH_MIN_PICKER);
        pickerMonth.setWrapSelectorWheel(false);
        pickerMonth.setValue(MONTH_DEFAULT_PICKER);

        pickerDay = (NumberPicker)findViewById(R.id.pickerDay);
        pickerDay.setMaxValue(DAY_MAX_PICKER);
        pickerDay.setMinValue(DAY_MIN_PICKER);
        pickerDay.setWrapSelectorWheel(false);
        pickerDay.setValue(DAY_DEFAULT_PICKER);



        btnInvio=(Button)findViewById(R.id.Invio);
        btnInvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                nome = campoNome.getText().toString();
                cognome = campoCognome.getText().toString();
                email = campoEmail.getText().toString();
                password = campoPassword.getText().toString();


                data = String.valueOf(pickerYear.getValue()) + "-";
                if((month = pickerMonth.getValue()) < TEN) {
                    data += "0" + month + "-";
                }
                else {
                    data += month + "-";
                }
                if((day = pickerDay.getValue()) < TEN) {
                    data += "0" + day;
                }
                else {
                    data += day;
                }


                //Toast.makeText(getBaseContext(), data, Toast.LENGTH_LONG).show();


                boolean emailValida = isEmailValida(email);
                if (!emailValida) {

                    Toast.makeText(getBaseContext(), "Attenzione! \nL'email inserita non è valida!", Toast.LENGTH_LONG).show();
                } else if (password.length() < 5) {
                    Toast.makeText(getBaseContext(), "Attenzione! \nLa password deve contenere almeno 5 caratteri!", Toast.LENGTH_LONG).show();
                } else if (nome.length()==0 ) {
                    Toast.makeText(getBaseContext(), "Attenzione! \nInserire il nome!", Toast.LENGTH_LONG).show();
                }else if (cognome.length()==0) {
                    Toast.makeText(getBaseContext(), "Attenzione! \nInserire il cognome!", Toast.LENGTH_LONG).show();
                } else {


                    new MyTask().execute();


                    // definisco l'intenzione
                    Intent openProfilo = new Intent(RegistrazioneActivity.this, MainActivity.class);
                    openProfilo.putExtra("name", nome);
                    openProfilo.putExtra("surname", cognome);
                    openProfilo.putExtra("email", email);
                    openProfilo.putExtra("dateOfBirth", data);


                    // passo all'attivazione dell'activity
                    startActivity(openProfilo);
                }
            }
        });
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrazione, menu);
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

    public static boolean isEmailValida(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }






    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {




            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("nome", nome));
            dataToSend.add(new BasicNameValuePair("cognome", cognome));
            dataToSend.add(new BasicNameValuePair("dataNascita",data));
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("password", password));




            try {
                if (InternetConnection.haveInternetConnection(RegistrazioneActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(ADDRESS_INSERIMENTO_UTENTE);



                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));



                    httpclient.execute(httppost);
                }
                else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(),e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //Toast.makeText(getBaseContext(), "ID facebook: " + profile.getId(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getBaseContext(), "name facebook: " + profile.getName(), Toast.LENGTH_LONG).show();

            //Toast.makeText(getBaseContext(), "caricati i dati sul DB " + nome + " " + cognome + " " + data + " " + email + " " + password , Toast.LENGTH_SHORT).show();




            super.onPostExecute(aVoid);

        }
    }
}

package com.takeatrip.Activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.drive.DriveId;
import com.takeatrip.Fragments.DatePickerFragment;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatesUtils;
import com.takeatrip.Utilities.InternetConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class  RegistrazioneActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "TEST RegistrAct";

    private final String ADDRESS_INSERIMENTO_UTENTE = "InserimentoProfilo.php";
    private final String ADDRESS_UPDATE_UTENTE = "UpdateProfilo.php";
    private final String ADDRESS_VERIFICA_LOGIN = "VerificaLogin.php";

    private static final int REQUEST_FOLDER = 123;

    private boolean update, passwordModificata;
    private boolean loginFB = false, loginGoogle = false;
    private boolean updateProfilo = false;
    private boolean dataNonCorretta = false;

    private Calendar calendar = Calendar.getInstance();
    //private int cDay = calendar.get(Calendar.DAY_OF_MONTH);
    //private int cMonth = calendar.get(Calendar.MONTH) + 1;
    private int cYear = calendar.get(Calendar.YEAR);

    private final int YEAR_MAX_PICKER = cYear;
    private final int YEAR_MIN_PICKER = 1900;
    private final int YEAR_DEFAULT_PICKER = 1900;
    private final int MONTH_MAX_PICKER = 12;
    private final int MONTH_MIN_PICKER = 1;
    private final int MONTH_DEFAULT_PICKER = 1;
    private final int DAY_MAX_PICKER = 31;
    private final int DAY_MIN_PICKER = 1;
    private final int DAY_DEFAULT_PICKER = 1;

    private final int MAX_LENGTH_PWD = 5;

    private final int TEN = 10;

    private Button btnInvio;

    private EditText campoNome;
    private EditText campoCognome;
    private EditText campoEmail;
    private EditText campoPassword;
    private EditText campoNuovoUsername;
    private EditText campoNuovoSesso;
    private EditText campoNuovaNazionalita;
    private EditText campoNuovoLavoro;
    private EditText campoNuovaDescrizione;
    private EditText campoNuovoTipo;
    private EditText campoDataNascita;
    private TextView completeProfile;
    private ProgressDialog mProgressDialog;

    private CheckBox campoMale;
    private CheckBox campoFemale;


    private String data;

    private String nome, cognome, email, password, confermaPassword, vecchiaPassword, nuovaPassword,
            confermaNuovaPassword,  nazionalita, sesso, username, lavoro, descrizione, tipo;
    private String previousEmail, provieneDa;
    private boolean cartellaCreata = false;

    private Profile profile;

    //per allert
    private boolean doubleBackToExitPressedOnce = false;
    private String emailProfilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        update = false;

        Intent intent = getIntent();
        if(intent != null){
            nome = intent.getStringExtra("name");
            cognome = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            emailProfilo = intent.getStringExtra("emailProfilo");
            data = intent.getStringExtra("date");
            password = intent.getStringExtra("password");
            nazionalita = intent.getStringExtra("nazionalita");
            sesso = intent.getStringExtra("sesso");
            username = intent.getStringExtra("username");
            lavoro = intent.getStringExtra("lavoro");
            descrizione = intent.getStringExtra("descrizione");
            tipo = intent.getStringExtra("tipo");
            profile = intent.getParcelableExtra("profile");
            provieneDa = intent.getStringExtra("provieneDa");

            Log.i(TAG, "profileFB: " +  profile);

            if(provieneDa != null){
                updateProfilo = true;
            }

            //sempre soddisfatta la condizione senza nostro login
            if(email != null){
                update = true;

                //variabile che discrimina il login
                if(profile != null){
                    loginFB = true;
                }
                else{
                    loginGoogle = true;
                }

                setContentView(R.layout.edit_info_fb_google);

                campoNuovoUsername = (EditText) findViewById(R.id.InserisciNuovoUsername);
                campoMale = (CheckBox) findViewById(R.id.checkBoxMale);
                campoFemale = (CheckBox) findViewById(R.id.checkBoxFemale);
                campoNuovaNazionalita = (EditText) findViewById(R.id.InserisciNuovaNazionalita);
                campoNuovoLavoro = (EditText) findViewById(R.id.InserisciNuovoLavoro);
                campoNuovaDescrizione = (EditText) findViewById(R.id.InserisciNuovaDescrizione);
                campoNuovoTipo = (EditText) findViewById(R.id.InserisciNuovoTipo);

                campoDataNascita = (EditText) findViewById(R.id.campoDataNascita);
                if (campoDataNascita != null) {
                    campoDataNascita.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus)
                                onClickChangeDate(v);
                        }
                    });
                }

                completeProfile = (TextView)findViewById(R.id.completeProfile);
                btnInvio=(Button)findViewById(R.id.Invio);
                if(updateProfilo){
                    completeProfile.setText(R.string.edit_profile);
                    btnInvio.setText(R.string.SAVE);
                    previousEmail = email;
                }
            } else {
                setContentView(R.layout.activity_registrazione);
            }


        } else {
            setContentView(R.layout.activity_registrazione);
        }

        campoNome = (EditText) findViewById(R.id.Nome);
        campoCognome = (EditText) findViewById(R.id.Cognome);
        campoEmail = (EditText) findViewById(R.id.Email);

        if(update) {
            campoNome.setText(nome);
            campoCognome.setText(cognome);
            if(sesso != null && sesso.equals("M")){
                campoMale.setChecked(true);
            }
            else if (sesso != null && sesso.equals("F")){
                campoFemale.setChecked(true);
            }
            campoNuovaNazionalita.setText(nazionalita);
            campoNuovoLavoro.setText(lavoro);
            campoNuovaDescrizione.setText(descrizione);
            campoNuovoTipo.setText(tipo);

            if(username != null){
                campoNuovoUsername.setText(username);
                campoNuovoUsername.setEnabled(false);
            }

            if(updateProfilo){
                btnInvio.setText("SAVE");

                if(data != null && !data.equals(""))
                    campoDataNascita.setText(DatesUtils.convertFormatStringDate(data,
                        Constants.DATABASE_DATE_FORMAT, Constants.DISPLAYED_DATE_FORMAT));
            }
        }


        btnInvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                Log.i(TAG, "data nascita: " + campoDataNascita.getText().toString());

                if(campoDataNascita.getText() != null && !campoDataNascita.getText().toString().equals("")){
                    try{
                        data = DatesUtils.convertFormatStringDate(campoDataNascita.getText().toString(),
                                Constants.DISPLAYED_DATE_FORMAT, Constants.DATABASE_DATE_FORMAT);
                    }
                    catch (Exception e){
                        dataNonCorretta = true;
                    }
                }

                nome = campoNome.getText().toString();
                cognome = campoCognome.getText().toString();
                username = campoNuovoUsername.getText().toString();
                nazionalita = campoNuovaNazionalita.getText().toString();
                lavoro = campoNuovoLavoro.getText().toString();
                descrizione = campoNuovaDescrizione.getText().toString();
                tipo = campoNuovoTipo.getText().toString();

                Log.i(TAG, "dati inseriti: " + nome + " " + cognome + " " + username + " " + data + " " + email + "vpwd " + password);

                if(dataNonCorretta){
                    Toast.makeText(getBaseContext(), R.string.incorrect_date, Toast.LENGTH_LONG).show();
                    dataNonCorretta = false;
                }
                else if(username == null || username.equals("")){
                    Toast.makeText(getBaseContext(), R.string.incorrectUsername, Toast.LENGTH_LONG).show();
                }
                else if(campoMale.isChecked() && campoFemale.isChecked() ||
                        (!campoMale.isChecked() && !campoFemale.isChecked())){
                    Toast.makeText(getBaseContext(), R.string.sexIncorrect, Toast.LENGTH_LONG).show();
                }
                else{
                    if(campoMale.isChecked()){
                        sesso = campoMale.getText().toString();
                    }
                    else{
                        sesso = campoFemale.getText().toString();
                    }

                    new MyTask().execute();
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

    //CONTROLLO SULLE CREDENZIALI INSERITE
    private boolean confermaCredenziali(String password, String confermaPassword) {
        boolean result = false;

        boolean emailValida = isEmailValida(email);
        if (!emailValida) {
            Toast.makeText(getBaseContext(), "Attenzione! \nL'email inserita non è valida!", Toast.LENGTH_LONG).show();
        } else if (password.length() < MAX_LENGTH_PWD) {
            Toast.makeText(getBaseContext(), "Attenzione! \nLa password deve contenere almeno 5 caratteri!", Toast.LENGTH_LONG).show();
        } else if (nome.length()==0 ) {
            Toast.makeText(getBaseContext(), "Attenzione! \nInserire il nome!", Toast.LENGTH_LONG).show();
        }else if (cognome.length()==0) {
            Toast.makeText(getBaseContext(), "Attenzione! \nInserire il cognome!", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confermaPassword)) {
            Toast.makeText(getBaseContext(), "Attenzione! \nLa password non è stata confermata!", Toast.LENGTH_LONG).show();
        }else {
            result = true;
        }

        return result;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_FOLDER){
            if(resultCode == RESULT_OK) {
                String idTravel = "folderTakeATrip";
                DriveId idFolder = data.getParcelableExtra("idFolder");
                String nameFolder = data.getStringExtra("nameFolder");


                cartellaCreata = true;
            }

        }
    }


    public void onClickChangeDate(View v) {

        DialogFragment newFragment = new DatePickerFragment();

        EditText e = (EditText) v;
        String text = e.getText().toString();

        if(!text.equals("")) {
            Bundle args = new Bundle();
            args.putString(Constants.CURRENT_DATE_ID, text);
            args.putString(Constants.DATE_FORMAT_ID, Constants.DISPLAYED_DATE_FORMAT);
            newFragment.setArguments(args);
        }
        newFragment.show(getFragmentManager(), "datePicker");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date newDate = c.getTime();

        campoDataNascita.setText(DatesUtils.getStringFromDate(newDate, Constants.DISPLAYED_DATE_FORMAT));
    }

    //allert di avviso per uscita senza salvataggio
    private void prepareSignOut() {

        new AlertDialog.Builder(RegistrazioneActivity.this)
                .setTitle(getString(R.string.back))
                .setMessage(getString(R.string.alert_message_info_user))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doubleBackToExitPressedOnce = true;
                        onBackPressed();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(ContextCompat.getDrawable(RegistrazioneActivity.this,R.drawable.logodefbordo))
                .show();
    }

    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        prepareSignOut();
        deleteIdOnShared(RegistrazioneActivity.this);

    }
    public static void deleteIdOnShared(Context c){
        SharedPreferences prefs = c.getSharedPreferences("com.example.david.takeatrip", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }



    private class MyTask extends AsyncTask<Void, Void, Void> {


        InputStream is = null;
        String result, stringaFinale = "";

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("nome", nome));
            dataToSend.add(new BasicNameValuePair("cognome", cognome));
            dataToSend.add(new BasicNameValuePair("dataNascita", data));
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("emailProfilo", emailProfilo));
            dataToSend.add(new BasicNameValuePair("nazionalita", nazionalita));
            dataToSend.add(new BasicNameValuePair("sesso", sesso));
            dataToSend.add(new BasicNameValuePair("username", username));
            dataToSend.add(new BasicNameValuePair("lavoro", lavoro));
            dataToSend.add(new BasicNameValuePair("descrizione", descrizione));
            dataToSend.add(new BasicNameValuePair("tipo", tipo));


            try {
                if (InternetConnection.haveInternetConnection(RegistrazioneActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost;

                    if(updateProfilo){
                        httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_UPDATE_UTENTE);
                    }
                    else {
                        httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_INSERIMENTO_UTENTE);
                    }

                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();

                    if (is != null) {
                        //converto la risposta in stringa
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            is.close();

                            result = sb.toString();

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }


                } else
                    Log.e("CONNESSIONE Internet", "Assente!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString(), e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(result.contains("Duplicate")){
                Log.e(TAG, "duplicati nell'username");
                Toast.makeText(getBaseContext(), "Username already used", Toast.LENGTH_LONG).show();
            }
            else{
                Intent openProfilo = new Intent(RegistrazioneActivity.this, MainActivity.class);
                openProfilo.putExtra("name", nome);
                openProfilo.putExtra("surname", cognome);
                openProfilo.putExtra("email", email);
                openProfilo.putExtra("dateOfBirth", data);
                openProfilo.putExtra("pwd", password);
                openProfilo.putExtra("nazionalita", nazionalita);
                openProfilo.putExtra("sesso", sesso);
                openProfilo.putExtra("username", username);
                openProfilo.putExtra("lavoro", lavoro);
                openProfilo.putExtra("descrizione", descrizione);
                openProfilo.putExtra("tipo", tipo);
                openProfilo.putExtra("fbProfile", profile);

                startActivity(openProfilo);
                finish();

            }
        }
    }
}

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
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DatabaseHandler;
import com.example.david.takeatrip.Utilities.PasswordHashing;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class  RegistrazioneActivity extends AppCompatActivity {

    private final String ADDRESS_INSERIMENTO_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoProfilo.php";
    private final String ADDRESS_UPDATE_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/UpdateProfilo.php";
    private final String ADDRESS_VERIFICA_LOGIN = "http://www.musichangman.com/TakeATrip/InserimentoDati/VerificaLogin.php";


    private boolean update, passwordModificata;

    private final int YEAR_MAX_PICKER = 2016;
    private final int YEAR_MIN_PICKER = 1900;
    private final int YEAR_DEFAULT_PICKER = 2000;
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
    private EditText campoConfermaPassword;
    private EditText campoData;
    private EditText campoVecchiaPassword;
    private EditText campoNuovaPassword;
    private EditText campoConfermaNuovaPassword;
    private EditText campoNuovoUsername;

    private String data;

    private String nome, cognome, email, password, confermaPassword, vecchiaPassword, nuovaPassword, confermaNuovaPassword, username, nuovoUsername;
    private String previousEmail;

    private NumberPicker pickerYear, pickerMonth, pickerDay;

    private int year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        update = false;
        Intent intent = getIntent();
        if(intent != null){
            nome = intent.getStringExtra("name");
            cognome = intent.getStringExtra("surname");
            email = intent.getStringExtra("email");
            data = intent.getStringExtra("date");
            password = intent.getStringExtra("password");

            //se contiene i dati, allora puoi procedere con la modifica
            if(nome != null || cognome != null || email != null || data != null || username != null){
                update = true;

                setContentView(R.layout.edit_info);

                campoVecchiaPassword = (EditText) findViewById(R.id.VecchiaPassword);
                campoNuovaPassword = (EditText) findViewById(R.id.NuovaPassword);
                campoConfermaNuovaPassword = (EditText) findViewById(R.id.ConfermaNuovaPassword);
                campoNuovoUsername = (EditText) findViewById(R.id.InserisciNuovoUsername);


                String[] splittedDate = data.split("-");
                year = Integer.parseInt(splittedDate[0]);
                month = Integer.parseInt(splittedDate[1]);
                day = Integer.parseInt(splittedDate[2]);

                previousEmail = email;
            } else {
                setContentView(R.layout.activity_registrazione);
            }


        } else {
            setContentView(R.layout.activity_registrazione);
        }


        campoNome = (EditText) findViewById(R.id.Nome);
        campoCognome = (EditText) findViewById(R.id.Cognome);
        campoEmail = (EditText) findViewById(R.id.Email);
        campoPassword = (EditText) findViewById(R.id.Password);
        campoConfermaPassword = (EditText) findViewById(R.id.ConfermaPassword);

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


        if(update) {
            campoNome.setText(nome);
            campoCognome.setText(cognome);
            campoEmail.setText(email);

            //TODO: creare stringa
            campoEmail.setText("Non Modificabile");
            campoEmail.setEnabled(false);
            campoEmail.setVisibility(View.INVISIBLE);
            pickerYear.setValue(year);
            pickerMonth.setValue(month);
            pickerDay.setValue(day);
            btnInvio.setText("SAVE");
        }


        btnInvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

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


                if (!update) {
                    nome = campoNome.getText().toString();
                    cognome = campoCognome.getText().toString();
                    email = campoEmail.getText().toString();
                    password = campoPassword.getText().toString();
                    confermaPassword = campoConfermaPassword.getText().toString();
                    Log.i("TEST", "dati modificati: " + nome +" " + cognome + " " + data +" "+  email + " "+ password+" "+ nuovaPassword);

                    if(confermaCredenziali(password, confermaPassword)){

                        password = PasswordHashing.sha1Hash(campoPassword.getText().toString());

                        new MyTask().execute();

                        // definisco l'intenzione
                        Intent openProfilo = new Intent(RegistrazioneActivity.this, MainActivity.class);
                        openProfilo.putExtra("name", nome);
                        openProfilo.putExtra("surname", cognome);
                        openProfilo.putExtra("email", email);
                        openProfilo.putExtra("dateOfBirth", data);
                        openProfilo.putExtra("pwd", password);

                        // passo all'attivazione dell'activity
                        startActivity(openProfilo);


                    }
                } else {

                    nome = campoNome.getText().toString();
                    cognome = campoCognome.getText().toString();
                    email = campoEmail.getText().toString();
                    vecchiaPassword = PasswordHashing.sha1Hash(campoVecchiaPassword.getText().toString());
                    nuovaPassword = campoNuovaPassword.getText().toString();
                    confermaNuovaPassword = campoConfermaNuovaPassword.getText().toString();
                    nuovoUsername = campoNuovoUsername.getText().toString();

                    Log.i("TEST", "dati modificati: " + nome +" " + cognome + " " + data +" "+  email + "vpwd "+ password+" nuovapwd: "+ nuovaPassword+" nuovousername: "+ nuovoUsername);

                    if(nuovaPassword == null || nuovaPassword.equals("")){
                        passwordModificata = false;

                        Log.i("TEST", "dati modificati: " + nome +" " + cognome + " " + data +" "+  email + "vpwd "+ password);


                        new MyTask().execute();

                        Intent openProfilo = new Intent(RegistrazioneActivity.this, MainActivity.class);
                        openProfilo.putExtra("name", nome);
                        openProfilo.putExtra("surname", cognome);
                        openProfilo.putExtra("email", email);
                        openProfilo.putExtra("dateOfBirth", data);
                        openProfilo.putExtra("pwd", password);

                        startActivity(openProfilo);


                    }

                    else{
                        passwordModificata = true;
                        if(confermaCredenziali(nuovaPassword, confermaNuovaPassword)) {

                            nuovaPassword = PasswordHashing.sha1Hash(campoNuovaPassword.getText().toString());

                            new MyTaskUpdate().execute();

                            Intent openProfilo = new Intent(RegistrazioneActivity.this, MainActivity.class);
                            openProfilo.putExtra("name", nome);
                            openProfilo.putExtra("surname", cognome);
                            openProfilo.putExtra("email", email);
                            openProfilo.putExtra("dateOfBirth", data);
                            openProfilo.putExtra("pwd", nuovaPassword);

                            // passo all'attivazione dell'activity
                            startActivity(openProfilo);
                        }
                    }

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





    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("nome", nome));
            dataToSend.add(new BasicNameValuePair("cognome", cognome));
            dataToSend.add(new BasicNameValuePair("dataNascita",data));
            dataToSend.add(new BasicNameValuePair("email", email));
            if(update && passwordModificata)
                dataToSend.add(new BasicNameValuePair("password", nuovaPassword));
            else
                dataToSend.add(new BasicNameValuePair("password", password));


            Log.i("TEST", "dati modificati: " + nome +" " + cognome + " " + data +" "+  email + " "+ password+" "+ nuovaPassword);

            try {
                if (InternetConnection.haveInternetConnection(RegistrazioneActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost;

                    if(update){
                        httppost = new HttpPost(ADDRESS_UPDATE_UTENTE);
                    }
                    else{
                        httppost = new HttpPost(ADDRESS_INSERIMENTO_UTENTE);
                    }



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
            //Registro il profilo in locale per il futuro

            if(!update){
                DatabaseHandler db = new DatabaseHandler(RegistrazioneActivity.this);
                // Inserting Users
                Log.d("Insert: ", "Inserting ..");

                db.addUser(new Profilo(email, nome, cognome, data, null, null, null, null, null, null ), password);


                // Reading all contacts
                Log.d("Reading: ", "Reading all contacts..");
                List<Profilo> contacts = db.getAllContacts();

                for (Profilo cn : contacts) {
                    String log = "Email: "+cn.getEmail()+" ,Name: " + cn.getName() + " ,Surname: " + cn.getSurname() + " ,Date: "+ cn.getDataNascita()
                            + " ,HashPassword: " + cn.getPassword();
                    // Writing Contacts to log
                    Log.i("LOG: ", log);
                }
            }
            else{


                DatabaseHandler db = new DatabaseHandler(RegistrazioneActivity.this);
                // Inserting Users
                Log.d("Update: ", "Updating ..");

                if(passwordModificata)
                    db.updateContact(new Profilo(email, nome, cognome, data, null, null, null, null, null, null), nuovaPassword);
                else
                    db.updateContact(new Profilo(email, nome, cognome, data, null, null, null, null, null, null), password);



                /*

                // Reading all contacts
                Log.d("Reading: ", "Reading all contacts..");
                List<Profilo> contacts = db.getAllContacts();

                for (Profilo cn : contacts) {
                    String log = "Email: "+cn.getEmail()+" ,Name: " + cn.getName() + " ,Surname: " + cn.getSurname() + " ,Date: "+ cn.getDataNascita()
                            + " ,HashPassword: " + cn.getPassword();
                    // Writing Contacts to log
                    Log.i("LOG: ", log);
                }

                */
            }


            super.onPostExecute(aVoid);

        }
    }




    private class MyTaskUpdate extends AsyncTask<Void, Void, Void> {

        InputStream is = null;
        String result, stringaFinale = "";



        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("password", vecchiaPassword));




            try {
                if (InternetConnection.haveInternetConnection(RegistrazioneActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(ADDRESS_VERIFICA_LOGIN);
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


                            JSONArray jArray = new JSONArray(result);
                            //for(int i=0;i<jArray.length();i++){
                            JSONObject json_data = jArray.getJSONObject(0);

                            if(json_data != null){
                                stringaFinale = json_data.getString("email").toString() + " " + json_data.getString("password").toString();
                            }



                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Errore nel risultato o nel convertire il risultato", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Input Stream uguale a null", Toast.LENGTH_LONG).show();
                    }
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
            if(stringaFinale == ""){
                Toast.makeText(getBaseContext(), getResources().getString(R.string.LoginError), Toast.LENGTH_LONG).show();
            }
            else{
                new MyTask().execute();

            }
            super.onPostExecute(aVoid);

        }
    }




}

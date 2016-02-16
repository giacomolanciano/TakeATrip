package com.example.david.takeatrip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.R;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {

    private final String ADDRESS_VERIFICA_LOGIN = "http://www.musichangman.com/TakeATrip/InserimentoDati/VerificaLogin.php";


    private TextView btnRegistrati;
    private Button btnAccedi;
    private ImageView miaImmagine;




    private EditText campoEmail, campoPassword;
    private String email, password, nome, cognome, data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      /*  miaImmagine = (ImageView) findViewById(R.id.image);
        miaImmagine.setImageResource(R.drawable.ImmagineEsempio);*/



        campoEmail = (EditText) findViewById(R.id.campoEmail);
        campoPassword = (EditText) findViewById(R.id.campoPassword);








        btnRegistrati=(TextView)findViewById(R.id.Registrati);
        btnRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // definisco l'intenzione
                Intent openRegistrazione = new Intent(LoginActivity.this, RegistrazioneActivity.class);
                // passo all'attivazione dell'activity
                startActivity(openRegistrazione);
            }
        });

        btnAccedi=(Button)findViewById(R.id.Accedi);
        btnAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                email = campoEmail.getText().toString();
                password = campoPassword.getText().toString();


                boolean emailValida = isEmailValida(email);
                if (!emailValida) {

                    Toast.makeText(getBaseContext(), "Attenzione! \nL'email inserita non è valida!", Toast.LENGTH_LONG)
                            .show();
                } else if (password.length() < 5) {
                    Toast.makeText(getBaseContext(), "Attenzione! \nLa password deve contenere almeno 5 caratteri!", Toast.LENGTH_LONG)
                            .show();
                } else {


                    //verifica se l'utente ha inserito i dati correttamente (matching con il DB)
                    new MyTask().execute();



                    /*
                    // definisco l'intenzione
                    Intent openAccedi = new Intent(LoginActivity.this, ProfiloActivity.class);
                    // passo all'attivazione dell'activity
                    startActivity(openAccedi);
                    */
                }
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        InputStream is = null;
        String result, stringaFinale = "";



        @Override
        protected Void doInBackground(Void... params) {




            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("password", password));




            try {
                if (InternetConnection.haveInternetConnection(LoginActivity.this)) {
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
                                email = json_data.getString("email").toString();
                                nome =  json_data.getString("nome").toString();
                                cognome = json_data.getString("cognome").toString();
                                data = json_data.getString("dataNascita").toString();

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

            //Toast.makeText(getBaseContext(), "Stringa finale: " + stringaFinale, Toast.LENGTH_LONG).show();
            //Toast.makeText(getBaseContext(), "Stringa finale: " + stringaFinale, Toast.LENGTH_LONG).show();


            if(stringaFinale == ""){
                Toast.makeText(getBaseContext(), getResources().getString(R.string.LoginError), Toast.LENGTH_LONG).show();
            }
            else{

                //Toast.makeText(getBaseContext(), data, Toast.LENGTH_LONG).show();

                Intent openAccedi = new Intent(LoginActivity.this, MainActivity.class);
                openAccedi.putExtra("email", email);
                openAccedi.putExtra("name", nome);
                openAccedi.putExtra("surname", cognome);
                openAccedi.putExtra("dateOfBirth", data);
                openAccedi.putExtra("pwd", password);





                // passo all'attivazione dell'activity
                startActivity(openAccedi);


            }

            //Toast.makeText(getBaseContext(), "name facebook: " + profile.getName(), Toast.LENGTH_LONG).show();





            super.onPostExecute(aVoid);

        }
    }

}

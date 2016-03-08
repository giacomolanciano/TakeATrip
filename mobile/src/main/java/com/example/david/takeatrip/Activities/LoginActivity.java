package com.example.david.takeatrip.Activities;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatabaseHandler;
import com.example.david.takeatrip.Utilities.PasswordHashing;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.LoginAuthorizationType;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private final String ADDRESS_VERIFICA_LOGIN = "http://www.musichangman.com/TakeATrip/InserimentoDati/VerificaLogin.php";
    private final String ADDRESS_INSERIMENTO_UTENTE = "http://www.musichangman.com/TakeATrip/InserimentoDati/InserimentoProfilo.php";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;

    private TextView btnRegistrati;
    private Button btnAccedi;

    private EditText campoEmail, campoPassword;
    private String email, password, nome, cognome, data, nazionalita, sesso, username, lavoro, descrizione, tipo;

    LoginButton blogin;
    AccessToken accessToken;
    AccessTokenTracker tracker;
    ProfileTracker profileTracker;
    Profile profile;

    private CallbackManager callbackManager;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            if(Profile.getCurrentProfile() == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile profile2) {
                        // profile2 is the new profile
                        profile = profile2;

                        Log.v("TEST profileFB: ", profile.getFirstName());
                        Log.v("TEST id profile: ", profile.getId());

                        email = Constants.PREFIX_FACEBOOK + profile.getId();
                        password = "pwdFb";
                        nome = profile.getFirstName();
                        cognome = profile.getLastName();
                        data = "0000-00-00";

                        profileTracker.stopTracking();

                        new MyTask().execute();

                        //new MyTaskInsert().execute();
                    }
                };
                profileTracker.startTracking();
            }
            else {
                profile = Profile.getCurrentProfile();
                nome = profile.getFirstName();
                cognome = profile.getLastName();
                Log.v("TEST profileFB: ", profile.getFirstName());
                Log.v("TEST id profile: ", profile.getId());

            }

            Log.i("TEST", "onSuccess!");

        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            Log.e(e.toString().toUpperCase(), e.getMessage());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        campoEmail = (EditText) findViewById(R.id.campoEmail);
        campoPassword = (EditText) findViewById(R.id.campoPassword);


        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            Log.i("TEST", "accessToken:" + "user id: " + accessToken.getUserId() + "  token: " + accessToken.getToken());

            profile = Profile.getCurrentProfile();

            if(Profile.getCurrentProfile() == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile profile2) {
                        profile = profile2;
                        // profile2 is the new profile
                        Log.i("facebook - profile", profile.getName());
                        profileTracker.stopTracking();
                    }
                };
                profileTracker.startTracking();
            }
            else {
                profile = Profile.getCurrentProfile();
                Log.v("facebook - profile", profile.getFirstName());

                email = Constants.PREFIX_FACEBOOK  +profile.getId();
                password = "pwdFb";

                nome = profile.getFirstName();
                cognome = profile.getLastName();
                data = "0000-00-00";

                new MyTask().execute();
                //openMainActivity2(email, nome, cognome, data, password, nazionalita, sesso, username, lavoro, descrizione, tipo);
            }
        }


        //Questi due bottoni servono solo nel caso di login indipendente
        /*
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
                    password = PasswordHashing.sha1Hash(campoPassword.getText().toString());
                    //verifica se l'utente ha inserito i dati correttamente (matching con il DB)
                    new MyTask().execute();

                }
            }


        });

        //If the user is already registered, then skip this activity (MySqLite)
        DatabaseHandler db = new DatabaseHandler(LoginActivity.this);
        try{
            Log.d("Reading: ", "Reading all contacts..");
            List<Profilo> contacts = db.getAllContacts();

            if(contacts.size() != 0){
                for (Profilo cn : contacts) {
                    String log = "Email: "+cn.getEmail()+" ,Name: " + cn.getName() + " ,Surname: " + cn.getSurname() + " ,Date: "+ cn.getDataNascita()+ " ,Nazionalità: " + cn.getNazionalita()+ " ,Sesso: " + cn.getSesso() + " ,Username: " + cn.getUsername()+" ,Lavoro: " + cn.getLavoro() + " ,Descrizione: " + cn.getDescrizione() + " ,Tipo: " + cn.getTipo()
                            + " ,HashPassword: " + cn.getPassword();
                    Log.i("LOG: ", log);

                    //5633
                    //
                    // TODO: decommentare
                    openMainActivity(cn.getEmail(), cn.getName(),cn.getSurname(),cn.getDataNascita(),cn.getPassword(), cn.getNazionalita(), cn.getSesso(), cn.getUsername(), cn.getLavoro(), cn.getDescrizione(), cn.getTipo() );
                }

            }
        }
        catch (Exception e){
            Log.d("table does not exitst: ", "Recreating the table..");

            //TODO chiarire il motivo dell'errore
            //nel caso in cui l'esecuzione dia problemi per via della riga sottostante,
            //commentarla e decommentare quella successiva

            db.onCreate(db.getWritableDatabase());
     //       db.onUpgrade(db.getWritableDatabase(), 0,1);

        }

        */

        //TODO: cambiare in fase di release il WEBAPP_ID
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(Constants.WEBAPP_ID);

        GoogleSignInOptions gso = builder.build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);


    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        blogin = (LoginButton) findViewById(R.id.LoginButtonFb);


        /*
        blogin.setReadPermissions("user_friends");
        blogin.setReadPermissions(Arrays.asList("user_status"));
        blogin.setReadPermissions(Arrays.asList("user_photos"));
*/

        blogin.setPublishPermissions(Arrays.asList("publish_actions"));
        blogin.registerCallback(callbackManager, callback);

    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            if (!mGoogleApiClient.isConnecting() &&
                    !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }



            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i("TEST", "result: " + result.toString());
            handleSignInResult(result);
        }
        else{
        }

    }


    protected void handleSignInResult(GoogleSignInResult result) {
        Log.i("TEST", "handleSignInResult:" + result.isSuccess());
        Log.i("TEST", "status:" + result.getStatus().toString());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Log.i("TEST", "result success!!!");


            GoogleSignInAccount acct = result.getSignInAccount();

            email = acct.getEmail();
            email = Constants.PREFIX_GOOGLE  + acct.getId();

            int describeContents = acct.describeContents();
            String displayName = acct.getDisplayName();
            String idUser = acct.getId();
            String tokenId = acct.getIdToken();

            Log.i("TEST", "email: " + email + " describeContents: " + describeContents + " dispplayName: " + displayName
                    + " idUser: " + idUser + " tokenId: " + tokenId);
            password = "pwdGoogle";

            if(displayName != null){
                String [] nameSplitted = displayName.split(" ");
                if(nameSplitted.length==2){
                    nome = nameSplitted[0];
                    cognome = nameSplitted[1];
                }
                else{
                    nome = displayName;
                }
            }


            new MyTask().execute();

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void signIn() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        Log.i("TEST", "status ApiClient:" + mGoogleApiClient.isConnected());

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("TEST", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }



    /*
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TEST", "onConnectionFailed:" + connectionResult);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }
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
                            for(int i=0;i<jArray.length();i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                if(json_data != null){
                                    stringaFinale = json_data.getString("email").toString() + " " + json_data.getString("password").toString();
                                    email = json_data.getString("email").toString();
                                    nome =  json_data.getString("nome").toString();
                                    cognome = json_data.getString("cognome").toString();
                                    data = json_data.getString("dataNascita").toString();
                                    nazionalita = json_data.getString("nazionalita").toString();
                                    sesso = json_data.getString("sesso").toString();
                                    username = json_data.getString("username").toString();
                                    lavoro = json_data.getString("lavoro").toString();
                                    descrizione = json_data.getString("descrizione").toString();
                                    tipo = json_data.getString("tipo").toString();
                                }
                            }

                        } catch (Exception e) {
                            Log.i("TEST", "Errore nel risultato o nel convertire il risultato");
                        }
                    }
                    else {
                        Log.i("TEST", "Input Stream uguale a null");
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

                //Non presente ancora nel DB -> primo accesso a TakeATrip
                openMainActivity(email, nome, cognome, data, password, nazionalita, sesso, username, lavoro, descrizione, tipo);
                Log.i("TEST", "primo accesso a TakeATrip");
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.LoginError), Toast.LENGTH_LONG).show();
            }
            else{
/*
                //Questo serve solo nel caso di login indipendente

                DatabaseHandler db = new DatabaseHandler(LoginActivity.this);
                //SQLiteDatabase newdb = db.getWritableDatabase();
                //db.onUpgrade(newdb,2,1);

                // Inserting Users
                Log.d("Insert: ", "Inserting ..");

                //TODO dubbio su modifica
                db.addUser(new Profilo(email, nome, cognome, data, nazionalita, sesso, username, lavoro, descrizione, tipo), password);


                // Reading all contacts
                Log.d("Reading: ", "Reading all contacts..");
                List<Profilo> contacts = db.getAllContacts();

                for (Profilo cn : contacts) {
                    String log = "Email: "+cn.getEmail()+" ,Name: " + cn.getName() + " ,Surname: " + cn.getSurname() + " ,Date: "+ cn.getDataNascita()
                            + " ,Password: " + cn.getPassword();
                    // Writing Contacts to log
                    Log.i("LOG: ", log);
                }

*/

                Log.i("TEST", "non primo accesso a TakeATrip");

                openMainActivity2(email, nome,cognome,data,password,nazionalita,sesso,username,lavoro,descrizione,tipo);
            }
            super.onPostExecute(aVoid);

        }
    }


/*

//serve solo quando si ha un login indipendente

    private class MyTaskInsert extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("nome", nome));
            dataToSend.add(new BasicNameValuePair("cognome", cognome));
            dataToSend.add(new BasicNameValuePair("dataNascita",data));
            dataToSend.add(new BasicNameValuePair("email", email));
            dataToSend.add(new BasicNameValuePair("password", password));
            dataToSend.add(new BasicNameValuePair("nazionalita", nazionalita));
            dataToSend.add(new BasicNameValuePair("sesso", sesso));
            dataToSend.add(new BasicNameValuePair("username", username));
            dataToSend.add(new BasicNameValuePair("lavoro", lavoro));
            dataToSend.add(new BasicNameValuePair("descrizione", descrizione));
            dataToSend.add(new BasicNameValuePair("tipo", tipo));


            Log.i("TEST", "dati: " + nome + " " + cognome + " " + data + " " + email + " " + password);

            try {
                if (InternetConnection.haveInternetConnection(LoginActivity.this)) {
                    Log.i("CONNESSIONE Internet", "Presente!");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost;
                    httppost = new HttpPost(ADDRESS_INSERIMENTO_UTENTE);
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
            super.onPostExecute(aVoid);
            openMainActivity(email, nome,cognome,data,password,nazionalita,sesso,username,lavoro,descrizione,tipo);

        }
    }


    */



    private void openMainActivity(String e, String name, String surname, String date, String pwd, String n, String sex, String username,
                                  String job, String description, String type){

        Intent openAccedi = new Intent(LoginActivity.this, RegistrazioneActivity.class);
        openAccedi.putExtra("email", e);
        openAccedi.putExtra("name", name);
        openAccedi.putExtra("surname", surname);
        openAccedi.putExtra("dateOfBirth", date);
        openAccedi.putExtra("password", pwd);
        openAccedi.putExtra("nazionalita", n);
        openAccedi.putExtra("sesso", sex);
        openAccedi.putExtra("username", username);
        openAccedi.putExtra("lavoro", job);
        openAccedi.putExtra("descrizione", description);
        openAccedi.putExtra("tipo", type);
        openAccedi.putExtra("profile", profile);

        startActivity(openAccedi);

        finish();
    }

    private void openMainActivity2(String e, String name, String surname, String date, String pwd, String n, String sex, String username,
                                  String job, String description, String type){

        Intent openAccedi = new Intent(LoginActivity.this, MainActivity.class);
        openAccedi.putExtra("email", e);
        openAccedi.putExtra("name", name);
        openAccedi.putExtra("surname", surname);
        openAccedi.putExtra("dateOfBirth", date);
        openAccedi.putExtra("pwd", pwd);
        openAccedi.putExtra("nazionalita", n);
        openAccedi.putExtra("sesso", sex);
        openAccedi.putExtra("username", username);
        openAccedi.putExtra("lavoro", job);
        openAccedi.putExtra("descrizione", description);
        openAccedi.putExtra("tipo", type);
        openAccedi.putExtra("profile", profile);


        startActivity(openAccedi);

        finish();
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}



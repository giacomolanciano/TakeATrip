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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Classes.InternetConnection;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.PasswordHashing;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private final String ADDRESS_VERIFICA_LOGIN = "http://www.musichangman.com/TakeATrip/InserimentoDati/VerificaLogin.php";
    private static final int RC_SIGN_IN = 9001;
    private static final String WEBAPP_ID   = "272164392045-84rf9p4med24s1i0u6shu13fiila6k1e.apps.googleusercontent.com";

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;

    private TextView btnRegistrati;
    private Button btnAccedi;
    private ImageView miaImmagine;

    private EditText campoEmail, campoPassword;
    private String email, password, nome, cognome, data, nazionalità, sesso, username, lavoro, descrizione, tipo;
    private int codice;

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
                        profileTracker.stopTracking();
                    }
                };
                profileTracker.startTracking();
            }
            else {
                profile = Profile.getCurrentProfile();
                Log.v("TEST profileFB: ", profile.getFirstName());
                Log.v("TEST id profile: ", profile.getId());

            }

            Log.i("TEST", "onSuccess!");

            //DisplayMessage(profile);
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


        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken old, AccessToken newToken) {
                Log.i("TEST", "onCurrentAccessTokenChanged");
                accessToken = newToken;
                if(accessToken != null){
                    Log.i("TEST", "accessToken:" +  "user id: " + accessToken.getUserId() +"  token: " + accessToken.getToken());
                }
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                profile = newProfile;
                Log.i("TEST", "Profile changed");
                if(profile!=null){
                    Log.v("facebook - profile", newProfile.getFirstName());
                    profileTracker.stopTracking();
                }
            }
        };

        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            Log.i("TEST", "accessToken:" +  "user id: " + accessToken.getUserId() +"  token: " + accessToken.getToken());

            profile = Profile.getCurrentProfile();

            if(Profile.getCurrentProfile() == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        Log.v("facebook - profile", profile2.getFirstName());
                        profileTracker.stopTracking();
                    }
                };
                profileTracker.startTracking();
            }
            else {
                Profile profile = Profile.getCurrentProfile();
                Log.v("facebook - profile", profile.getFirstName());
            }

            //Intent openAccedi = new Intent(LoginActivity.this, MainActivity.class);
            //startActivity(openAccedi);
        }

        tracker.startTracking();
        profileTracker.startTracking();

        /*

        //Sign-in with Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEBAPP_ID)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);



        */






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

                    Log.i("TEST", "hash password: " + password);

                    //verifica se l'utente ha inserito i dati correttamente (matching con il DB)
                    new MyTask().execute();

                }
            }


        });
    }


    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        blogin = (LoginButton) findViewById(R.id.LoginButtonFb);
        blogin.setReadPermissions("user_friends");
        blogin.setReadPermissions(Arrays.asList("user_status"));
        blogin.registerCallback(callbackManager, callback);
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        /*

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
*/
    }


    protected void handleSignInResult(GoogleSignInResult result) {
        Log.i("TEST", "handleSignInResult:" + result.isSuccess());
        Log.i("TEST", "status:" + result.getStatus().toString());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //acct.getEmail();


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



    /*
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
    */


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
        tracker.stopTracking();
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


            Log.i("TEST", "dati da verificare: " + email + " "+ password);


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

                            Log.i("TEST", "dati prelevati dal db: "+ result);
/*
                            JSONArray jArray = new JSONArray(result);
                            for(int i=0;i<jArray.length();i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                if(json_data != null){
                                    stringaFinale = json_data.getString("email").toString() + " " + json_data.getString("password").toString();
                                    email = json_data.getString("email").toString();
                                    nome =  json_data.getString("nome").toString();
                                    cognome = json_data.getString("cognome").toString();
                                    data = json_data.getString("dataNascita").toString();
                                    nazionalità = json_data.getString("nazionalita").toString();
                                    sesso = json_data.getString("sesso").toString();
                                    username = json_data.getString("username").toString();
                                    lavoro = json_data.getString("lavoro").toString();
                                    descrizione = json_data.getString("descrizione").toString();
                                    tipo = json_data.getString("tipo").toString();

                                }
                            }

                            */
                            JSONArray jArray = new JSONArray(result);

                            if(jArray != null && result != null){
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    stringaFinale = json_data.getString("email").toString() + " " + json_data.getString("password").toString();
                                    email = json_data.getString("email").toString();
                                    nome =  json_data.getString("nome").toString();
                                    cognome = json_data.getString("cognome").toString();
                                    data = json_data.getString("dataNascita").toString();
                                    nazionalità = json_data.getString("nazionalita").toString();
                                    sesso = json_data.getString("sesso").toString();
                                    username = json_data.getString("username").toString();
                                    lavoro = json_data.getString("lavoro").toString();
                                    descrizione = json_data.getString("descrizione").toString();
                                    tipo = json_data.getString("tipo").toString();
                                }
                            }


                            Log.i("TEST", "dati prelevati dal db: "+ descrizione + " " + lavoro);


                        } catch (Exception e) {
                            Log.e("TEST", "errore nel convertire il risultato");
                            Log.e("TEST", e.getMessage() + "\n");
                        }
                    }
                    else {
                        Log.e("TEST", "errore2");
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
                openAccedi.putExtra("nazionalita", nazionalità);
                openAccedi.putExtra("sesso", sesso);
                openAccedi.putExtra("username", username);
                openAccedi.putExtra("lavoro", lavoro);
                openAccedi.putExtra("descrizione", descrizione);
                openAccedi.putExtra("tipo", tipo);





                // passo all'attivazione dell'activity
                startActivity(openAccedi);


            }

            //Toast.makeText(getBaseContext(), "name facebook: " + profile.getName(), Toast.LENGTH_LONG).show();





            super.onPostExecute(aVoid);

        }
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

package com.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.takeatrip.AsyncTasks.LoginTask;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.Interfaces.AsyncResponseLogin;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,AsyncResponseLogin {

    private static final String TAG = "TEST LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;

    private String email, password, nome, cognome, data, nazionalita, sesso, username, lavoro, descrizione, tipo;
    private String emailProfilo;

    LoginButton blogin;
    AccessToken fbAccessToken;
    AccessTokenTracker tracker;
    ProfileTracker profileTracker;
    Profile profile;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private Map<String, String> logins;

    private CallbackManager callbackManager;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
            credentialsProvider.setLogins(logins);

            TakeATrip TAT = ((TakeATrip) getApplicationContext());
            TAT.setCredentialsProvider(credentialsProvider);


            if(Profile.getCurrentProfile() == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile profile2) {
                        // profile2 is the new profile
                        profile = profile2;

                        Log.v("TEST profileFB: ", profile.getFirstName());
                        Log.v("TEST id profile: ", profile.getId());

                        email = Constants.PREFIX_FACEBOOK + profile.getId();
                        password = "";
                        nome = profile.getFirstName();
                        cognome = profile.getLastName();
                        data = "0000-00-00";
                        emailProfilo = "";


                        profileTracker.stopTracking();

                        //new MyTask().execute();
                        LoginTask task = new LoginTask(LoginActivity.this,email,password);
                        task.delegate = LoginActivity.this;
                        task.execute();
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
        logins = new HashMap<String, String>();


        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.AMAZON_POOL_ID, // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );

        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.EU_WEST_1, // Region
                credentialsProvider);



        //TODO: cambiare in fase di release il WEBAPP_ID
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(Constants.WEBAPP_ID).requestEmail();

        GoogleSignInOptions gso = builder.build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);

        if (!mGoogleApiClient.isConnecting() &&
                !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        blogin = (LoginButton) findViewById(R.id.LoginButtonFb);
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
            Log.i(TAG, "result: " + result.toString());
            handleSignInResult(result);
        }
        else {

        }

    }


    protected void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            TakeATrip TAT = ((TakeATrip) getApplicationContext());
            TAT.setmGoogleApiClient(mGoogleApiClient);

            GoogleSignInAccount acct = result.getSignInAccount();

            emailProfilo = acct.getEmail();
            email = Constants.PREFIX_GOOGLE  + acct.getId();

            int describeContents = acct.describeContents();
            String displayName = acct.getDisplayName();
            String idUser = acct.getId();
            String tokenId = acct.getIdToken();

            Log.i(TAG, "email: " + email + " emailProfilo: "+ emailProfilo + " describeContents: " + describeContents + " displayName: " + displayName
                    + " idUser: " + idUser + " tokenId: " + tokenId);
            password = "";

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


            logins.put("accounts.google.com", tokenId);
            credentialsProvider.setLogins(logins);

            TAT.setCredentialsProvider(credentialsProvider);

            LoginTask task = new LoginTask(LoginActivity.this,email,password);
            task.delegate = LoginActivity.this;
            task.execute();
            //new MyTask().execute();

        } else {
            Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



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
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }
    }

    public void onClickSignUpFacebook(View view) {
        blogin.performClick();
    }

    public void onClickSignUpGoogle(View view) {
        this.onClick(signInButton);
    }

    @Override
    public void processFinish(Profilo output) {
        Log.i(TAG, "login finished with profile: " + output);

        if(output != null){
            Log.i(TAG, "non primo accesso a TakeATrip");
            openMainActivity2(output.getId(), output.getEmail(), output.getName(), output.getSurname(), output.getDataNascita(),
                    output.getPassword(), output.getNazionalita(), output.getSesso(), output.getUsername(),output.getLavoro(),
                    output.getDescrizione(), output.getTipo());
        }
        else{
            openMainActivity(email, emailProfilo, nome, cognome, null,"", null, null, null,null, null, null);
        }


    }


    private void openMainActivity(String e, String emailProfilo, String name, String surname, String date,
                                  String pwd, String n, String sex, String username,
                                  String job, String description, String type){

        Intent openAccedi = new Intent(LoginActivity.this, RegistrazioneActivity.class);
        openAccedi.putExtra("email", e);
        openAccedi.putExtra("emailProfilo", emailProfilo);
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

    private void openMainActivity2(String e, String emailProfilo, String name, String surname, String date, String pwd, String n, String sex, String username,
                                   String job, String description, String type){

        Intent openAccedi = new Intent(LoginActivity.this, MainActivity.class);
        openAccedi.putExtra("email", e);
        openAccedi.putExtra("emailProfilo", emailProfilo);
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
        openAccedi.putExtra("fbProfile", profile);

        startActivity(openAccedi);

        finish();
    }


}



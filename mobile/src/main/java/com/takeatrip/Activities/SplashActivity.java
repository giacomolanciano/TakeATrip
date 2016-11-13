package com.takeatrip.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.takeatrip.AsyncTasks.LoginTask;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.Interfaces.AsyncResponseLogin;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DeviceStorageUtils;
import com.takeatrip.Utilities.InternetConnection;

import java.util.HashMap;
import java.util.Map;


/*
* This activity shows the icon of TAT and verify if the user is already logged into the application
*
* */
public class SplashActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, AsyncResponseLogin{

    private static final String TAG = "TEST SplashActivity";

    AccessToken fbAccessToken;
    AccessTokenTracker tracker;
    ProfileTracker profileTracker;
    Profile profile;
    private GoogleApiClient mGoogleApiClient;

    String email, password, nome, cognome, data;


    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private Map<String, String> logins;
    private String emailProfilo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int timeout = 2000;
        super.onCreate(savedInstanceState);

        if(InternetConnection.haveInternetConnection(getApplicationContext())){
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.activity_splash);


            //TODO: cambiare in fase di release il WEBAPP_ID
            GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
            builder.requestIdToken(Constants.WEBAPP_ID);

            GoogleSignInOptions gso = builder.build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

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
            DeviceStorageUtils.createExternalStorageDirectories();


            // If the Facebook access token is available already assign it.
            fbAccessToken = AccessToken.getCurrentAccessToken();
            if(fbAccessToken != null){
                Log.i(TAG, "fbAccessToken:" + "user id: " + fbAccessToken.getUserId() + "  token: " + fbAccessToken.getToken());

                profile = Profile.getCurrentProfile();

                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile profile2) {
                            profile = profile2;
                            // profile2 is the new profile
                            Log.i(TAG, "facebook - profile: " + profile.getName());
                            profileTracker.stopTracking();
                        }
                    };
                    profileTracker.startTracking();
                }
                else {
                    profile = Profile.getCurrentProfile();
                    Log.v(TAG, "facebook - profile: " + profile.getFirstName());

                    email = Constants.PREFIX_FACEBOOK  +profile.getId();
                    password = "";
                    nome = profile.getFirstName();
                    cognome = profile.getLastName();
                    data = "0000-00-00";

                    logins.put(TAG, "graph.facebook.com: " + fbAccessToken.getToken());

                    Log.i(TAG, "token FB: " + fbAccessToken.getToken());
                    Log.i(TAG, "logins: " + logins);

                    credentialsProvider.setLogins(logins);

                    TakeATrip TAT = ((TakeATrip) getApplicationContext());
                    TAT.setCredentialsProvider(credentialsProvider);

                    LoginTask task = new LoginTask(this,email,password);
                    task.delegate = this;
                    task.execute();
                    return;
                }
            }



            // If the Google access token is available already assign it.
            // Gather all the informations about the user through the LoginTask with the delegate. Then, Open the Main Activity
            else if(mGoogleApiClient != null){
                Log.i(TAG, "mGoogleApiClient diverso da null");

                mGoogleApiClient.connect();
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                if (opr.isDone()) {
                    // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                    // and the GoogleSignInResult will be available instantly.
                    Log.i(TAG, "Google Sign In isDone");
                    GoogleSignInResult result = opr.get();
                    handleSignInResult(result);
                }
                else{
                    openLoginActivity(timeout);
                }
            }

            //Otherwise we need the login of the user
            else{
                openLoginActivity(timeout);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            openLoginActivity(timeout);
        }
    }



    protected void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Log.i(TAG, "result success!!!");

            TakeATrip TAT = ((TakeATrip) getApplicationContext());
            TAT.setmGoogleApiClient(mGoogleApiClient);

            GoogleSignInAccount acct = result.getSignInAccount();

            emailProfilo = acct.getEmail();
            email = Constants.PREFIX_GOOGLE  + acct.getId();

            int describeContents = acct.describeContents();
            String displayName = acct.getDisplayName();
            String idUser = acct.getId();
            String tokenId = acct.getIdToken();

            Log.i(TAG, "email: " + email + " emailProfilo: "+ emailProfilo + " describeContents: " + describeContents + " dispplayName: " + displayName
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

            LoginTask task = new LoginTask(SplashActivity.this,email,password);
            task.delegate = SplashActivity.this;
            task.execute();
            //new MyTask().execute();

        } else {
            Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
        }
    }



    public void openLoginActivity(int timeout){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, timeout);

    }


    @Override
    public void processFinish(Profilo output) {
        if(output != null){
            Log.i(TAG, "non primo accesso a TakeATrip");
            openMainActivity2(output.getId(), output.getEmail(), output.getName(), output.getSurname(), output.getDataNascita(),
                    password, output.getNazionalita(), output.getSesso(), output.getUsername(),output.getLavoro(),
                    output.getDescrizione(), output.getTipo());

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
            openMainActivity(email, emailProfilo, nome, cognome,null,null,null,null,null,null,null,null);
        }

    }

    private void openMainActivity(String e, String emailprofilo, String name, String surname, String date, String pwd, String n, String sex, String username,
                                  String job, String description, String type){

        Intent openAccedi = new Intent(SplashActivity.this, RegistrazioneActivity.class);
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

        Intent openAccedi = new Intent(SplashActivity.this, MainActivity.class);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}





}

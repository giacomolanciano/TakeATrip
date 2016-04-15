package com.example.david.takeatrip.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
import com.example.david.takeatrip.AsyncTask.LoginTask;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.Classes.TakeATrip;
import com.example.david.takeatrip.Interfaces.AsyncResponseLogin;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,AsyncResponseLogin {

    private final String ADDRESS_VERIFICA_LOGIN = "VerificaLogin.php";
    private final String ADDRESS_INSERIMENTO_UTENTE = "InserimentoProfilo.php";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;

    private TextView btnRegistrati;
    private Button btnAccedi;

    private EditText campoEmail, campoPassword;
    private String email, password, nome, cognome, data, nazionalita, sesso, username, lavoro, descrizione, tipo;


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

            Log.i("TEST", "token: " + AccessToken.getCurrentAccessToken().getToken());
            Log.i("TEST", "logins: " + logins);

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
                        password = "pwdFb";
                        nome = profile.getFirstName();
                        cognome = profile.getLastName();
                        data = "0000-00-00";

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
        builder.requestIdToken(Constants.WEBAPP_ID);

        GoogleSignInOptions gso = builder.build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);



//        campoEmail = (EditText) findViewById(R.id.campoEmail);
//        campoPassword = (EditText) findViewById(R.id.campoPassword);


        // Create a record in a dataset and synchronize with the server
//        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
//        dataset.put("myKey", "myValue");
//        dataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                //Your handler code here
//            }
//        });


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

    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        blogin = (LoginButton) findViewById(R.id.LoginButtonFb);


        /*
        blogin.setReadPermissions("user_friends");
        blogin.setReadPermissions(Arrays.asList("user_status"));
        blogin.setReadPermissions(Arrays.asList("user_photos"));

        //TODO:decommentare quando diamo la possitibilità all'utente di immagazzinare i contenuti du fb
        blogin.setPublishPermissions(Arrays.asList("publish_actions"));

        */

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
        else {

        }

    }


    protected void handleSignInResult(GoogleSignInResult result) {

        Log.i("TEST", "handleSignInResult:" + result.isSuccess());
        Log.i("TEST", "status:" + result.getStatus().toString());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Log.i("TEST", "result success!!!");

            TakeATrip TAT = ((TakeATrip) getApplicationContext());
            TAT.setmGoogleApiClient(mGoogleApiClient);

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


            logins.put("accounts.google.com", tokenId);
            credentialsProvider.setLogins(logins);


            TAT.setCredentialsProvider(credentialsProvider);

            LoginTask task = new LoginTask(LoginActivity.this,email,password);
            task.delegate = LoginActivity.this;
            task.execute();
            //new MyTask().execute();

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

    public void onClickSignUpFacebook(View view) {
        blogin.performClick();
    }

    public void onClickSignUpGoogle(View view) {
        this.onClick(signInButton);
    }

    @Override
    public void processFinish(Profilo output) {
        Log.i("TEST", "login finished with profile: " + output);

        if(output != null){
            Log.i("TEST", "non primo accesso a TakeATrip");
            openMainActivity2(output.getEmail(), output.getName(), output.getSurname(), output.getDataNascita(),
                    output.getPassword(), output.getNazionalita(), output.getSesso(), output.getUsername(),output.getLavoro(),
                    output.getDescrizione(), output.getTipo());

        }
        else{
            openMainActivity(email, nome, cognome, output.getDataNascita(),
                    output.getPassword(), output.getNazionalita(), output.getSesso(), output.getUsername(),output.getLavoro(),
                    output.getDescrizione(), output.getTipo());

        }


    }


//serve solo quando si ha un login indipendente

//    private class MyTaskInsert extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
//            dataToSend.add(new BasicNameValuePair("nome", nome));
//            dataToSend.add(new BasicNameValuePair("cognome", cognome));
//            dataToSend.add(new BasicNameValuePair("dataNascita",data));
//            dataToSend.add(new BasicNameValuePair("email", email));
//            dataToSend.add(new BasicNameValuePair("password", password));
//            dataToSend.add(new BasicNameValuePair("nazionalita", nazionalita));
//            dataToSend.add(new BasicNameValuePair("sesso", sesso));
//            dataToSend.add(new BasicNameValuePair("username", username));
//            dataToSend.add(new BasicNameValuePair("lavoro", lavoro));
//            dataToSend.add(new BasicNameValuePair("descrizione", descrizione));
//            dataToSend.add(new BasicNameValuePair("tipo", tipo));
//
//
//            Log.i("TEST", "dati: " + nome + " " + cognome + " " + data + " " + email + " " + password);
//
//            try {
//                if (InternetConnection.haveInternetConnection(LoginActivity.this)) {
//                    Log.i("CONNESSIONE Internet", "Presente!");
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httppost;
//                    httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_INSERIMENTO_UTENTE);
//                    httppost.setEntity(new UrlEncodedFormEntity(dataToSend));
//                    httpclient.execute(httppost);
//                }
//                else
//                    Log.e("CONNESSIONE Internet", "Assente!");
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(e.toString(),e.getMessage());
//            }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            openMainActivity(email, nome,cognome,data,password,nazionalita,sesso,username,lavoro,descrizione,tipo);
//
//        }
//    }


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


}



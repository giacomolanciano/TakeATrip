package com.example.david.takeatrip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.david.takeatrip.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        int timeout = 2000;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        // Initialize the Amazon Cognito credentials provider
//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                Constants.AMAZON_POOL_ID, // Identity Pool ID
//                Regions.EU_WEST_1 // Region
//        );
//
//        // Initialize the Cognito Sync client
//        CognitoSyncManager syncClient = new CognitoSyncManager(
//                getApplicationContext(),
//                Regions.EU_WEST_1, // Region
//                credentialsProvider);
//
//        // Create a record in a dataset and synchronize with the server
//        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
//        dataset.put("myKey", "myValue");
//        dataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                //Your handler code here
//            }
//        });
//
//        Log.i("TEST", "amazon credential provider: "+ credentialsProvider);
//        Log.i("TEST", "amazon syncClient: "+ syncClient);
//        Log.i("TEST", "amazon dataset: "+ dataset);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, timeout);
    }


/*
    public boolean onCreateOptionMenu(Menu menu){
    getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
*/


}

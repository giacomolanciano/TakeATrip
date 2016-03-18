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

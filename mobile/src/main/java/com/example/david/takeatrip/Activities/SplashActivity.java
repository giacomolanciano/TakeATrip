package com.example.david.takeatrip.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.david.takeatrip.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread splash_screen = new Thread() {

            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    onDestroy();
                }
            }
        };
        splash_screen.start();
    }
/*
    public boolean onCreateOptionMenu(Menu menu){
    getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
*/
}

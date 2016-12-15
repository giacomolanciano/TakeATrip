package com.takeatrip.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.takeatrip.R;

public class TutorialActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv = (TextView) findViewById(R.id.viewPolicy);
        String text = "Click <a href='http://ec2-54-194-7-136.eu-west-1.compute.amazonaws.com/ppolicy'>here</a>";
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(Html.fromHtml(text));
        //EasterEgg
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Developers: Davide Mazza, Giacomo Lanciano and Luca Giacomelli", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } //Aggiungere luca dopo HCI xD
        });
    }
}

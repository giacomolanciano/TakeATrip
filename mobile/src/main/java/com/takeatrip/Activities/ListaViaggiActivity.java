package com.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.takeatrip.Adapters.RecyclerViewViaggiAdapter;
import com.takeatrip.AsyncTasks.GetViaggiTask;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.Interfaces.AsyncResponseTravels;
import com.takeatrip.R;
import com.takeatrip.Utilities.DataObject;

import java.util.ArrayList;
import java.util.List;


public class ListaViaggiActivity extends ActionBarActivity implements AsyncResponseTravels {

    private static final String TAG = "TEST ListaViaggiAct";

    private ArrayList<Viaggio> viaggi;
    private ArrayList<Profilo> profili;
    private ArrayList<DataObject> dataTravels;
    private String email;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ViewGroup group;
    private ImageView image_default;
    private ProgressDialog progressDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_lista_viaggi);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewViaggiAdapter(getDataSet(), ListaViaggiActivity.this);
        recyclerView.setAdapter(adapter);

/*      lista = (ListView)findViewById(R.id.listViewTravels);
        setContentView(R.layout.activity_cards);

        ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);


*/

        image_default = new ImageView(this);
        image_default.setImageDrawable(getDrawable(R.drawable.default_male));

        group = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };

        group.addView(image_default);


        viaggi = new ArrayList<Viaggio>();
        dataTravels = new ArrayList<DataObject>();


        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
        }

        GetViaggiTask GVT = new GetViaggiTask(ListaViaggiActivity.this, email);
        GVT.delegate = this;
        GVT.execute();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_viaggi, menu);
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


    @Override
    protected void onResume() {
        super.onResume();
        ((RecyclerViewViaggiAdapter) adapter).setOnItemClickListener(new RecyclerViewViaggiAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }



    @Override
    public void processFinishForTravels(List<Viaggio> travels) {

        Profilo p = new Profilo(email);
        for(Viaggio v : travels) {
            ImageView image = new ImageView(ListaViaggiActivity.this);
            dataTravels.add(new DataObject(v, p, image));
        }
        PopolaLista();
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 20; index++) {
            //DataObject obj = new DataObject("Some Primary Text " + index,
            //        "Secondary " + index);
            //results.add(index, obj);
        }
        return results;
    }


    private void PopolaLista() {

        RecyclerViewViaggiAdapter adapter = new RecyclerViewViaggiAdapter(dataTravels, ListaViaggiActivity.this);
        adapter.onCreateViewHolder(group, 0);
        recyclerView.setAdapter(adapter);
    }


}

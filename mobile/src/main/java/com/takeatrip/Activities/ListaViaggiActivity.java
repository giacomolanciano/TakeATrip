package com.takeatrip.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.takeatrip.Adapters.RecyclerViewViaggiAdapter;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DataObject;

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



public class ListaViaggiActivity extends ActionBarActivity {

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
        profili = new ArrayList<Profilo>();
        dataTravels = new ArrayList<DataObject>();


        Intent intent;
        if ((intent = getIntent()) != null) {
            email = intent.getStringExtra("email");
            Log.i(TAG, "email utente in lista viaggi: " + email);

        }

        new GetViaggiTask().execute();

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
        hideProgressDialog();

        /*
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {

                final Viaggio viaggio = (Viaggio) adattatore.getItemAtPosition(pos);
                // Toast.makeText(getBaseContext(), "hai cliccato il viaggio: " + viaggio.getNome(), Toast.LENGTH_SHORT).show();

                //TODO per ora non ci interessano tutti gli itinerari associati al viaggio
                //Intent intent = new Intent(ListaViaggiActivity.this, ViaggioActivity.class);
                Intent intent = new Intent(ListaViaggiActivity.this, ViaggioActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("codiceViaggio", viaggio.getCodice());
                intent.putExtra("nomeViaggio", viaggio.getNome());

                startActivity(intent);

            }
        });

        */

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
                Log.i(TAG, " Clicked on Item " + position);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.CaricamentoInCorso));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    private class GetViaggiTask extends AsyncTask<Void, Void, Void> {

        //private static final String TAG = "TEST GetViaggiTask";
        private static final String ADDRESS_PRELIEVO = "QueryViaggi.php";

        InputStream is = null;
        String stringaFinale = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", email));


            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_PRELIEVO);
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

                        String result = sb.toString();

                        Log.i(TAG, "result da queryViaggi: " + result);


                        if (result.equals("null\n")) {
                            stringaFinale = getString(R.string.NoTravels);

                        } else {
                            JSONArray jArray = new JSONArray(result);

                            if (jArray != null && result != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    String codiceViaggio = json_data.getString("codiceViaggio");
                                    String nomeViaggio = json_data.getString("nomeViaggio");
                                    String urlImmagineViaggio = json_data.getString("idFotoViaggio");
                                    String condivisioneDefault = json_data.getString("livelloCondivisione");

                                    viaggi.add(new Viaggio(codiceViaggio, nomeViaggio, urlImmagineViaggio, condivisioneDefault));
                                    profili.add(new Profilo(email));
                                }
                            }
                        }


                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                } else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

            } catch (Exception e) {
                Log.e(TAG, "Errore nella connessione http " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (stringaFinale.equals("")) {
                Profilo p = new Profilo(email);
                for(Viaggio v : viaggi) {
                    ImageView image = new ImageView(ListaViaggiActivity.this);
                    dataTravels.add(new DataObject(v, p, image));
                }

                PopolaLista();
            } else {
                hideProgressDialog();
                adviseNewTravel();

            }
            super.onPostExecute(aVoid);

        }


        //allert di avviso per uscita senza salvataggio
        private void adviseNewTravel() {

            new AlertDialog.Builder(ListaViaggiActivity.this)
                    .setTitle(getString(R.string.adviseNoTravel))
                    .setMessage(getString(R.string.adviseNewTravel))
                    .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent openNewTravel = new Intent(ListaViaggiActivity.this, NuovoViaggioActivity.class);
                            openNewTravel.putExtra("email", email);
                            startActivity(openNewTravel);
                            finish();
                        }
                    })

                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ListaViaggiActivity.this.onBackPressed();
                            return;
                        }
                    })
                    .setIcon(ContextCompat.getDrawable(ListaViaggiActivity.this,R.drawable.logodefbordo))
                    .show();
        }

    }

}

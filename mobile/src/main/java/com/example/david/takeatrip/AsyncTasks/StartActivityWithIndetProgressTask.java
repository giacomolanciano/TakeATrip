package com.example.david.takeatrip.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.david.takeatrip.R;

/**
 * Created by Giacomo Lanciano on 22/08/2016.
 */
public class StartActivityWithIndetProgressTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST StartActivityProgr";
    private static final int DELAY = 1000;

    private Context context;
    private Intent intent;
    private ProgressDialog progressDialog;

    public StartActivityWithIndetProgressTask(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.CaricamentoInCorso));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            //necessario per far visualizzare il progress dialog
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        context.startActivity(intent);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

}
package com.takeatrip.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.takeatrip.R;

/**
 * Created by Giacomo Lanciano on 22/08/2016.
 */
public class StartActivityWithIndetProgressTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "TEST StartActivityProgr";
    private static final int DELAY = 750;

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

        /*
        * start the activity from here (not from onPostExecute) to avoid that the screen keeps being
        * freezed after progress dialog dismissing
        * */
        context.startActivity(intent);
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

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

}
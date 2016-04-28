package com.example.david.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.InternetConnection;

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
import java.util.List;

/**
 * Created by Giacomo Lanciano on 24/04/2016.
 */
public class GetPartecipantiViaggioTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TEST GetPartViaggioTask";

    private static final String ADDRESS_PARTECIPANTS = "QueryPartecipantiViaggio.php";
    private static final String ADDRESS_QUERY_URLS= "QueryImagesOfTravel.php";

    private final int LIMIT_IMAGES_VIEWS = 4;

    private Context context;
    private View contentView;
    private AmazonS3Client s3;
    private String codiceViaggio;
    private List<Profilo> listPartecipants;
    private String nomeViaggio;
    private String email;
    private GridView gridViewPhotos;
    private String urlImageTravel;
    private LinearLayout layoutPartecipants;
    private LinearLayout rowHorizontal;

    private InputStream is = null;
    private String result;
    private TextView viewTitoloViaggio;
    private boolean proprioViaggio;
    //private LinearLayout layoutCopertinaViaggio;
    private ImageView layoutCopertinaViaggio;

    //TODO cancellare dopo prova
    private GridView gridViewVideos;



    public GetPartecipantiViaggioTask(Context context, View contentView, AmazonS3Client s3,
                                      String codiceViaggio, List<Profilo> listPartecipants,
                                      String nomeViaggio, String email, GridView gridViewPhotos,
                                      String urlImageTravel, LinearLayout layoutPartecipants,
                                      LinearLayout rowHorizontal, GridView gridViewVideos) {

        this.context = context;
        this.contentView = contentView;
        this.s3 = s3;
        this.codiceViaggio = codiceViaggio;
        this.listPartecipants = listPartecipants;
        this.nomeViaggio = nomeViaggio;
        this.email = email;
        this.gridViewPhotos = gridViewPhotos;
        this.urlImageTravel = urlImageTravel;
        this.layoutPartecipants = layoutPartecipants;
        this.rowHorizontal = rowHorizontal;

        //TODO cancellare dopo prova
        this.gridViewVideos = gridViewVideos;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));


        try {
            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS+ADDRESS_PARTECIPANTS);

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
                            sb.append(line).append("\n");
                        }
                        is.close();

                        result = sb.toString();

                        JSONArray jArray = new JSONArray(result);

                        if(result != null){
                            for(int i=0;i<jArray.length();i++){
                                JSONObject json_data = jArray.getJSONObject(i);
                                String emailProfilo = json_data.getString("emailProfilo");
                                String nomePartecipante = json_data.getString("nome");
                                String cognomePartecipante = json_data.getString("cognome");
                                String data = json_data.getString("dataNascita");
                                String nazionalita = json_data.getString("nazionalita");
                                String sesso = json_data.getString("sesso");
                                String username = json_data.getString("username");
                                String lavoro = json_data.getString("lavoro");
                                String descrizione = json_data.getString("descrizione");
                                String tipo = json_data.getString("tipo");
                                String urlImmagineProfilo = json_data.getString("urlImmagineProfilo");
                                String urlImmagineCopertina = json_data.getString("urlImmagineCopertina");

                                listPartecipants.add(new Profilo(emailProfilo, nomePartecipante,cognomePartecipante,
                                        data, nazionalita, sesso, username, lavoro, descrizione, tipo, urlImmagineProfilo, urlImmagineCopertina));


                                Log.i(TAG, "lista partecipanti al viaggio " + nomeViaggio + ": " + listPartecipants.toString());

                                //controllo se l'email dell'utente è tra quelle dei partecipanti al viaggio
                                for(Profilo p : listPartecipants){
                                    if(email != null && email.equals(p.getEmail())){
                                        proprioViaggio = true;
                                        Log.i(TAG, "sei compreso nel viaggio");
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Errore nel risultato o nel convertire il risultato");
                    }
                }
                else {
                    Log.e(TAG, "Input Stream uguale a null");
                }

            }
            else
                Log.e(TAG, "CONNESSIONE Internet Assente!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.toString(),e.getMessage());
        }
        return proprioViaggio;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {



        new UrlsImagesTask(context, codiceViaggio, gridViewPhotos, ADDRESS_QUERY_URLS).execute();

        //TODO cancellare dopo prova o modificare per ritornare i video
        new UrlsImagesTask(context, codiceViaggio, gridViewVideos, ADDRESS_QUERY_URLS).execute();


        //viewTitoloViaggio = (TextView) contentView.findViewById(R.id.titoloViaggio);
        //layoutCopertinaViaggio = (LinearLayout) contentView.findViewById(R.id.layoutCoverImageTravel);

        layoutCopertinaViaggio = (ImageView) contentView.findViewById(R.id.imageTravel);

        if(urlImageTravel != null && !urlImageTravel.equals("null")){

            //new BitmapWorkerTask(null,layoutCopertinaViaggio).execute(urlImageTravel);

            new BitmapWorkerTask(layoutCopertinaViaggio).execute(urlImageTravel);
        }

//        if (viewTitoloViaggio != null) {
//            viewTitoloViaggio.setText(nomeViaggio);
//        } else {
//            //TODO capire perchè da eccezione sporadicamente
//            Log.e(TAG, "viewTitoloViaggio is null");
//        }



        super.onPostExecute(aVoid);


        Log.i(TAG, "END onPostExecute()");


    }



}
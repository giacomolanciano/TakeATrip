package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.TakeATrip;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.InternetConnection;

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
import java.util.concurrent.ExecutionException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Giacomo Lanciano on 24/04/2016.
 */
public class GetPartecipantiViaggioTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TEST GetPartViaggioTask";

    private static final String ADDRESS_PARTECIPANTS = "QueryPartecipantiViaggio.php";


    private final int LIMIT_IMAGES_VIEWS = 4;

    private Context context;
    private View contentView;
    private AmazonS3Client s3;
    private String codiceViaggio;
    private List<Profilo> listPartecipants;
    private String nomeViaggio;
    private String email;
    private GridView gridViewPhotos;
    private GridView gridViewVideos;
    private GridView gridViewAudio;
    private GridView gridViewNotes;
    private String urlImageTravel;
    private LinearLayout layoutPartecipants;
    private LinearLayout rowHorizontal;

    private InputStream is = null;
    private String result;
    private TextView viewTitoloViaggio;
    private boolean proprioViaggio;
    //private LinearLayout layoutCopertinaViaggio;
    private ImageView layoutCopertinaViaggio;


    public GetPartecipantiViaggioTask(Context context, View contentView, AmazonS3Client s3,
                                      String codiceViaggio, List<Profilo> listPartecipants,
                                      String nomeViaggio, String email, String urlImageTravel,
                                      LinearLayout layoutPartecipants, LinearLayout rowHorizontal,
                                      ImageView layoutCopertinaViaggio, GridView gridViewPhotos,
                                      GridView gridViewVideos, GridView gridViewAudio, GridView gridViewNotes) {

        this.context = context;
        this.contentView = contentView;
        this.s3 = s3;
        this.codiceViaggio = codiceViaggio;
        this.listPartecipants = listPartecipants;
        this.nomeViaggio = nomeViaggio;
        this.email = email;
        this.urlImageTravel = urlImageTravel;
        this.layoutPartecipants = layoutPartecipants;
        this.rowHorizontal = rowHorizontal;
        this.layoutCopertinaViaggio = layoutCopertinaViaggio;

        this.gridViewPhotos = gridViewPhotos;
        this.gridViewVideos = gridViewVideos;
        this.gridViewAudio = gridViewAudio;
        this.gridViewNotes = gridViewNotes;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
        Log.i(TAG, "codiceViaggio Get: "+ codiceViaggio);
        // codiceViaggio="8f09454f-2fd7-48c0-8b7d-41aa1fc005aa";
        //Log.i(TAG, "codiceViaggio Get dopo modifica: "+ codiceViaggio);
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
                        Log.i(TAG, "result: " + result);


                        JSONArray jArray = new JSONArray(result);

                        if(result != null){
                            for(int i=0;i<jArray.length();i++){
                                JSONObject json_data = jArray.getJSONObject(i);
                                String idProfilo = json_data.getString("emailProfilo");
                                String emailProfilo = json_data.getString("email2");
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

                                listPartecipants.add(new Profilo(idProfilo, emailProfilo, nomePartecipante,cognomePartecipante,
                                        data, nazionalita, sesso, username, lavoro, descrizione, tipo, urlImmagineProfilo, urlImmagineCopertina));



                                //controllo se l'email dell'utente è tra quelle dei partecipanti al viaggio
                                for(Profilo p : listPartecipants){
                                    if(email != null && email.equals(p.getId())){
                                        proprioViaggio = true;
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
                Log.e(TAG, "no Internet connection");
        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
        }
        return proprioViaggio;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);

        if(urlImageTravel != null && !urlImageTravel.equals("null")){
            //new BitmapWorkerTask(null,layoutCopertinaViaggio).execute(urlImageTravel);

            TakeATrip TAT = (TakeATrip)getApplicationContext();
            Log.i(TAG, "TAT : " +TAT);
            Log.i(TAG, "TAT : " +TAT.getCurrentImage());

            try {
                if(TAT != null && TAT.getCurrentImage() != null){
                    Bitmap resizedBitmap = getScaledBitmap(TAT.getCurrentImage());
                    layoutCopertinaViaggio.setImageBitmap(resizedBitmap);
                    TAT.setCurrentImage(null);
                }
                else{
                    Bitmap bitmap  = new BitmapWorkerTask(layoutCopertinaViaggio).execute(urlImageTravel).get();
                    if(bitmap != null)
                        layoutCopertinaViaggio.setImageBitmap(getScaledBitmap(bitmap));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    private Bitmap getScaledBitmap(Bitmap bitmap){
        float density = context.getResources().getDisplayMetrics().density;
        int heigh = 300;
        Log.i(TAG, "density of the screen: " + density);
        if(density == 3.0 || density == 4.0){
            heigh = 600;
        }
        return bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), heigh, false);
    }

}
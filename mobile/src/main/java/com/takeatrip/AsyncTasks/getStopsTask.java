package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.takeatrip.Classes.Itinerario;
import com.takeatrip.Classes.POI;
import com.takeatrip.Classes.Profilo;
import com.takeatrip.Classes.Tappa;
import com.takeatrip.Classes.Viaggio;
import com.takeatrip.Interfaces.AsyncResponseStops;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.DatesUtils;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lucagiacomelli on 26/10/16.
 */

public class GetStopsTask extends AsyncTask<Void, Void, Void> {

    private static final String ADDRESS_PRELIEVO_TAPPE = "QueryTappe.php";
    private static final String TAG = "GetStopTask";
    private final static int DEFAULT_INT = 0;
    private static final String DEFAULT_DATE = "2010-01-11";
    private InputStream is = null;
    public AsyncResponseStops delegate = null;

    private List<Profilo> partecipants;
    private Context context;
    private Map<Profilo,List<Tappa>> profilo_tappe;
    private String codiceViaggio;

    public GetStopsTask(Context context, List<Profilo> partecipants, String codiceViaggio){
        this.context = context;
        this.partecipants = partecipants;
        this.codiceViaggio = codiceViaggio;
        profilo_tappe = new HashMap<Profilo, List<Tappa>>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for(Profilo p : partecipants){
            List<Tappa> tappe = new ArrayList<Tappa>();
            ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
            dataToSend.add(new BasicNameValuePair("email", p.getEmail()));
            dataToSend.add(new BasicNameValuePair("codiceViaggio", codiceViaggio));

            Log.i(TAG,"email: " + p.getEmail());
            Log.i(TAG,"codiceViaggio: " + codiceViaggio);

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.PREFIX_ADDRESS + ADDRESS_PRELIEVO_TAPPE);
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
                        JSONArray jArray = new JSONArray(result);
                        if(jArray != null && result != null){
                            for(int i=0;i<jArray.length();i++){
                                JSONObject json_data = jArray.getJSONObject(i);

                                String email = json_data.getString("emailProfilo");
                                String codiceViaggio = json_data.getString("codiceViaggio");

                                Itinerario itinerario = new Itinerario(new Profilo(email), new Viaggio(codiceViaggio));
                                int ordine = json_data.getInt("ordine");
                                int ordineTappaPrecedente = json_data.optInt("ordineTappaPrecedente", DEFAULT_INT);

                                Tappa tappaPrecedente = new Tappa(itinerario, (ordineTappaPrecedente));

                                String nome = json_data.getString("nome");
                                String codicePOI = json_data.getString("codicePOI");
                                String fontePOI = json_data.getString("fontePOI");
                                String livelloCondivisione = json_data.getString("livelloCondivisioneTappa");


                                POI poi = new POI(codicePOI, fontePOI);

                                String dataString = json_data.optString("data", DEFAULT_DATE);
                                Calendar cal = DatesUtils.getDateFromString(dataString, Constants.DATABASE_DATE_FORMAT);
                                Date data = cal.getTime();

                                tappe.add(new Tappa(itinerario, ordine, tappaPrecedente, data, nome, poi, livelloCondivisione));
                                Log.i(TAG, "tappa prelevata: " + tappe);

                            }
                        }



                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Errore nella connessione http "+e.toString());
            }

            profilo_tappe.put(p, tappe);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG,"tappe del viaggio: " + codiceViaggio);
        delegate.processFinishForStops(profilo_tappe);
    }
}

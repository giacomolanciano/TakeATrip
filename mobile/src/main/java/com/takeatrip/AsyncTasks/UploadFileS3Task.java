package com.takeatrip.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.takeatrip.Utilities.InternetConnection;
import com.takeatrip.Utilities.UtilS3Amazon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giacomo Lanciano on 20/04/2016.
 */
public class UploadFileS3Task extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TEST UploadFileS3Task";

    private Context context;
    private String filePath, bucketName, idViaggio, idUtente, tipoFile, completeUrl, newFileName;


    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, List<Object>>> transferRecordMaps;


    // The S3 client
    private AmazonS3Client s3;


    public UploadFileS3Task(Context context, String bucketName, String idViaggio, String tipoFile,
                            String idUtente, String filePath, String newFileName) {

        this.context = context;
        this.filePath = filePath;
        this.bucketName = bucketName;
        this.idViaggio = idViaggio;
        this.idUtente = idUtente;
        this.tipoFile = tipoFile;
        this.newFileName = newFileName;
        this.completeUrl = null;

        startupTranferServiceEarlyToAvoidBugs(context);

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);

    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            if (InternetConnection.haveInternetConnection(context)) {
                Log.i(TAG, "CONNESSIONE Internet Presente!");


                if (filePath == null) {
                    Toast.makeText(context, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
                    return false;
                }

                Log.i(TAG, "filePath: " + filePath);

                File file = new File(filePath);

                //ObjectMetadata myObjectMetadata = new ObjectMetadata();

                String key = idViaggio +"/" + tipoFile + "/" + idUtente + "_" + newFileName;

                TransferObserver observer = transferUtility.upload(bucketName, key, file);

                Log.i(TAG, "final url: " + key);


            } else{
                Log.e(TAG,"CONNESSIONE Internet Assente!");
                return false;
            }


        } catch (Exception e) {
            Log.e(TAG, "Errore nella connessione http "+e.toString());
            return false;
        }


        return true;
    }


    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
    }


    /** TODO da eliminare quando verrà risolto il bug
     * work around for a bug:
     * http://stackoverflow.com/questions/36587511/android-amazon-s3-uploading-crash
     */
    public static void startupTranferServiceEarlyToAvoidBugs(Context context) {
        final TransferUtility tu = new TransferUtility(
                new AmazonS3Client((AWSCredentials)null),
                context);
        tu.cancel(Integer.MAX_VALUE - 1);
    }
}

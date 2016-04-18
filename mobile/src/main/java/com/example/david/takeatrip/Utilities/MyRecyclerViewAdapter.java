package com.example.david.takeatrip.Utilities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amazonaws.HttpMethod;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.david.takeatrip.Activities.ViaggioActivity;
import com.example.david.takeatrip.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by david on 08/03/2016.
 */

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {

    private static final String LOG_TAG = "MyRecyclerViewAdapter";
    private static final String COVER_NAME = "cover.jpg";


    private ArrayList<DataObject> mDataset;
    private Context context;
    private boolean[] giaInserita;
    private static MyClickListener myClickListener;
    private String urlImmagineViaggio;
    //private Map<String,String> codice_urlImmagineViaggio;


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


    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView nomeViaggio;
        TextView codiceViaggio;
        TextView emailUser;
        TextView dateTime;
        ImageView imageTravel;

        public DataObjectHolder(View itemView) {
            super(itemView);

            nomeViaggio = (TextView) itemView.findViewById(R.id.NameTravel);
            codiceViaggio = (TextView) itemView.findViewById(R.id.CodeTravel);
            emailUser = (TextView) itemView.findViewById(R.id.EmailUser);
            imageTravel = (ImageView) itemView.findViewById(R.id.ImageTravel);


            //codice_urlImmagineViaggio = new HashMap<String,String >();

            //nomeViaggio.setText(());
            //   dateTime = (TextView) itemView.findViewById(R.id.textView2);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

           // myClickListener.onItemClick(getAdapterPosition(), v);
          //  Toast.makeText(v.getContext(), "PROVA", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(v.getContext(), ViaggioActivity.class);

            intent.putExtra("emailEsterno",emailUser.getText().toString());
           // Log.e("TEST", "email card viaggi: "+ emailUser.getText().toString());
            intent.putExtra("nomeViaggio", nomeViaggio.getText().toString());
            intent.putExtra("codiceViaggio", codiceViaggio.getText().toString());
            intent.putExtra("urlImmagineViaggio", imageTravel.getContentDescription());
            v.getContext().startActivity(intent);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;

        transferUtility = UtilS3Amazon.getTransferUtility(context);
        transferRecordMaps = new ArrayList<HashMap<String, List<Object>>>();
        s3 = UtilS3Amazon.getS3Client(context);

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_lista_viaggi, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        String codiceViaggio = mDataset.get(position).getCodiceViaggio();

        holder.nomeViaggio.setText(mDataset.get(position).getNomeViaggio());
        holder.codiceViaggio.setText(codiceViaggio);
        holder.emailUser.setText(mDataset.get(position).getEmail());


        String urlImmagine = mDataset.get(position).getUrlImageTravel();
        //ImageView immagineViaggio = mDataset.get(position).getImmagineViaggio();



        ImageView immagineViaggio = holder.imageTravel;
        immagineViaggio.setContentDescription(urlImmagine);

        if(urlImmagine != null && !urlImmagine.equals("null")){
            //new BitmapWorkerTask(immagineViaggio).execute(Constants.ADDRESS_TAT +urlImmagine);
            //Picasso.with(null).load(Constants.ADDRESS_TAT +urlImmagine).into(immagineViaggio);

            loadTravelImage(COVER_NAME, immagineViaggio, codiceViaggio);

        }
        else{
            Log.i("TEST", "urlImmagine = null");
            immagineViaggio.setImageResource(R.drawable.empty_image);
        }

        Log.i("TEST", "email: " + mDataset.get(position).getEmail());
        Log.i("TEST", "immagine del viaggio" + mDataset.get(position).getNomeViaggio()+": " +
                holder.imageTravel.getContentDescription() + " "+
                mDataset.get(position).getUrlImageTravel());

        //holder.nome.equals(mDataset.get(position).getNomeViaggio());
        //      holder.dateTime.setText(mDataset.get(position).getmText2());
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }


    private void loadTravelImage(String key, ImageView travelImage, String codiceViaggio) {

        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 60; // 1 hour.
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(Constants.BUCKET_TRAVELS_NAME,codiceViaggio
                        + "/" +Constants.TRAVEL_COVER_IMAGE_LOCATION+"/"+key);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(expiration);

        Log.i("TEST", "expiration date image: " + generatePresignedUrlRequest.getExpiration());

        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);

        Picasso.with(context).load(url.toString()).into(travelImage);

        // Initiate the download
        //TransferObserver observer = transferUtility.download(email, key, file);
        //Log.i("TEST", "downloaded file: " + file);
        //Log.i("TEST", "key file: " + key);

        Log.i("TEST", "url file: " + url);

        //observer.setTransferListener(new DownloadListener());

    }

}
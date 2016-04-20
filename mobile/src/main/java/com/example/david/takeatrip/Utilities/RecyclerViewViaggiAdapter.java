package com.example.david.takeatrip.Utilities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.david.takeatrip.Activities.ViaggioActivity;
import com.example.david.takeatrip.AsyncTask.LoadTravelImageTask;
import com.example.david.takeatrip.Interfaces.AsyncResponseUrl;
import com.example.david.takeatrip.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class RecyclerViewViaggiAdapter extends RecyclerView
        .Adapter<RecyclerViewViaggiAdapter
        .DataObjectHolder> implements AsyncResponseUrl {

    private static final String TAG = "RecyclerViewViaggiAdapt";


    private ArrayList<DataObject> mDataset;
    private Context context;
    private boolean[] giaInserita;
    private static MyClickListener myClickListener;
    private String urlImmagineViaggio;

    private ImageView immagineViaggio;
    private String urlImmagine, codiceViaggio;
    private URL completeUrl;


    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RecyclerViewViaggiAdapter(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
        completeUrl = null;

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


        codiceViaggio = mDataset.get(position).getCodiceViaggio();

        holder.nomeViaggio.setText(mDataset.get(position).getNomeViaggio());
        holder.codiceViaggio.setText(codiceViaggio);
        holder.emailUser.setText(mDataset.get(position).getEmail());


        urlImmagine = mDataset.get(position).getUrlImageTravel();
        //ImageView immagineViaggio = mDataset.get(position).getImmagineViaggio();



        immagineViaggio = holder.imageTravel;

        if(urlImmagine != null && !urlImmagine.equals("null")){

            try {

                //TODO verificare efficienza
                //la recyclerView chiama asynctask ogni volta che l'immagine esce fuori dallo schermo

                completeUrl = new LoadTravelImageTask(urlImmagine, codiceViaggio, context).execute().get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            immagineViaggio.setContentDescription(completeUrl.toString());

            Picasso.with(context).load(completeUrl.toString()).into(immagineViaggio);

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


    @Override
    public void processFinish(URL url) {
        this.completeUrl = url;
    }



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


            Log.i(TAG, "Add Listener");
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
            intent.putExtra("urlImmagineViaggio", immagineViaggio.getContentDescription());
            v.getContext().startActivity(intent);
        }
    }




}
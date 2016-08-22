package com.example.david.takeatrip.Adapters;

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
import com.example.david.takeatrip.AsyncTasks.LoadGenericImageTask;
import com.example.david.takeatrip.AsyncTasks.StartActivityWithIndetProgressTask;
import com.example.david.takeatrip.Interfaces.AsyncResponseUrl;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DataObject;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class RecyclerViewViaggiAdapter extends RecyclerView
        .Adapter<RecyclerViewViaggiAdapter.TravelViewHolder> implements AsyncResponseUrl {

    private static final String TAG = "TEST RecViewViaggiAdapt";
    private static final int HEIGHT_DIMENSION_IMAGE_TRAVEL = Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *10;
    private static final int WIDTH_DIMENSION_IMAGE_TRAVEL = Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *15;

    private ArrayList<DataObject> dataset;
    private Context context;
    private static ClickListener clickListener;
    private ImageView immagineViaggio;
    private String urlImmagine, codiceViaggio;
    private URL completeUrl;

    public RecyclerViewViaggiAdapter(ArrayList<DataObject> myDataset, Context context) {
        this.dataset = myDataset;
        this.context = context;
        this.completeUrl = null;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_lista_viaggi, parent, false);

        TravelViewHolder travelViewHolder = new TravelViewHolder(view);
        return travelViewHolder;
    }

    @Override
    public void onBindViewHolder(TravelViewHolder holder, int position) {


        codiceViaggio = dataset.get(position).getCodiceViaggio();

        holder.nomeViaggio.setText(dataset.get(position).getNomeViaggio());
        holder.codiceViaggio.setText(codiceViaggio);
        holder.emailUser.setText(dataset.get(position).getEmail());


        urlImmagine = dataset.get(position).getUrlImageTravel();
        //ImageView immagineViaggio = dataset.get(position).getImmagineViaggio();



        immagineViaggio = holder.imageTravel;

        if(urlImmagine != null && !urlImmagine.equals("null")){

            try {

                //TODO verificare efficienza
                //la recyclerView chiama asynctask ogni volta che l'immagine esce fuori dallo schermo

                completeUrl = new LoadGenericImageTask(urlImmagine, codiceViaggio, context).execute().get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            immagineViaggio.setContentDescription(completeUrl.toString());

            Picasso.with(context).
                    load(completeUrl.toString()).
                    resize(WIDTH_DIMENSION_IMAGE_TRAVEL, HEIGHT_DIMENSION_IMAGE_TRAVEL).
                    into(immagineViaggio);

        }
        else{
            Log.i(TAG, "urlImmagine = null");
            immagineViaggio.setImageResource(R.drawable.empty_image);
        }

        Log.i(TAG, "email: " + dataset.get(position).getEmail());
        Log.i(TAG, "immagine del viaggio: " + dataset.get(position).getNomeViaggio()+": " +
                holder.imageTravel.getContentDescription() + " "+
                dataset.get(position).getUrlImageTravel());

        //holder.nome.equals(dataset.get(position).getNomeViaggio());
        //      holder.dateTime.setText(dataset.get(position).getmText2());
    }

    public void addItem(DataObject dataObj, int index) {
        dataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        dataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface ClickListener {
        public void onItemClick(int position, View v);
    }

    @Override
    public void processFinish(URL url) {
        this.completeUrl = url;
    }

    public class TravelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nomeViaggio;
        TextView codiceViaggio;
        TextView emailUser;
        TextView dateTime;
        ImageView imageTravel;

        public TravelViewHolder(View itemView) {
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

            Intent intent = new Intent(v.getContext(), ViaggioActivity.class);

            intent.putExtra("emailEsterno",emailUser.getText().toString());
            intent.putExtra("nomeViaggio", nomeViaggio.getText().toString());
            intent.putExtra("codiceViaggio", codiceViaggio.getText().toString());


            //NB deve essere usata la variabile imageTravel,
            //non immagineViaggio poichè viene continuamente sovrascritta
            intent.putExtra("urlImmagineViaggio", imageTravel.getContentDescription());

            for(DataObject object : dataset){
                if(object.getViaggio().getCodice().equals(codiceViaggio.getText().toString())){
                    intent.putExtra("livelloCondivisione", object.getViaggio().getCondivisioneDefault());
                    break;
                }
            }

            //v.getContext().startActivity(intent);
            new StartActivityWithIndetProgressTask(context, intent).execute();

            Log.i(TAG, "urlImmagineViaggio: " + v.getContentDescription());

        }

    }

}
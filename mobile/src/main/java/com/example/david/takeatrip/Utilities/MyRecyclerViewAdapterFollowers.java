package com.example.david.takeatrip.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.david.takeatrip.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by david on 08/03/2016.
 */

public class MyRecyclerViewAdapterFollowers extends RecyclerView
        .Adapter<MyRecyclerViewAdapterFollowers
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapterFollowers";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView nomeUtente;
        TextView cognomeUtente;
        TextView emailUtente;
        TextView emailEsterno;
        TextView usernameUtente;
        TextView dataUtente;
        TextView sessoUtente;
        TextView lavoroUtente;
        TextView descrizioneUtente;
        TextView tipoUtente;
        TextView nazionalitaUtente;
        ImageView imageProfile;

        public DataObjectHolder(View itemView) {
            super(itemView);

            nomeUtente = (TextView) itemView.findViewById(R.id.NomeUtenteFollowers);
           //emailUtente = (TextView) itemView.findViewById(R.id.EmailUserFollowing);
            cognomeUtente = (TextView) itemView.findViewById(R.id.CognomeUtenteFollowers);
            usernameUtente = (TextView) itemView.findViewById(R.id.UsernameUtenteFollowers);
            sessoUtente = (TextView) itemView.findViewById(R.id.SessoUtenteFollowers);
            dataUtente = (TextView) itemView.findViewById(R.id.DataUtenteFollowers);
            nazionalitaUtente = (TextView) itemView.findViewById(R.id.NazionalitaUtenteFollowers);
            lavoroUtente = (TextView) itemView.findViewById(R.id.LavoroUtenteFollowers);
            descrizioneUtente = (TextView) itemView.findViewById(R.id.DescrizioneUtenteFollowers);
            tipoUtente = (TextView) itemView.findViewById(R.id.TipoUtenteFollowers);
            imageProfile = (RoundedImageView) itemView.findViewById(R.id.ImageProfile);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*
            Log.i("TEST:", "Email di cui voglio vedere il profilo dalla lista following: " + emailUtente.getText().toString());
            Intent openProfilo = new Intent(v.getContext(), ProfiloActivity.class);

            openProfilo.putExtra("name", nomeUtente.getText().toString());

            openProfilo.putExtra("surname", cognomeUtente.getText().toString());
            if(emailUtente!= null){
            //    openProfilo.putExtra("email", emailUtente.getText().toString());
            }
            else{
                Log.i("TEST: ", "EMAIL UTENTE: " + emailUtente.getText().toString());
                //openProfilo.putExtra("emailEsterno", emailUtente.getText().toString());
            }

            openProfilo.putExtra("dateOfBirth",dataUtente.getText().toString());
            openProfilo.putExtra("nazionalita", nazionalitaUtente.getText().toString());
            openProfilo.putExtra("sesso", sessoUtente.getText().toString());
            openProfilo.putExtra("username", usernameUtente.getText().toString());
            openProfilo.putExtra("lavoro",lavoroUtente.getText().toString());
            openProfilo.putExtra("descrizione", descrizioneUtente.getText().toString());
            openProfilo.putExtra("tipo",tipoUtente.getText().toString());
            //openProfilo.putExtra("urlImmagineProfilo", p.getIdImageProfile());
           // openProfilo.putExtra("urlImmagineCopertina", p.getGetIdImageCover());

            // passo all'attivazione dell'activity
            v.getContext().startActivity(openProfilo);
*/

        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;


    }

    public MyRecyclerViewAdapterFollowers(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_followers, parent, false);



        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.nomeUtente.setText(mDataset.get(position).getNomeFollow());
        holder.cognomeUtente.setText(mDataset.get(position).getCognomeFollow());
        holder.usernameUtente.setText("(" +  mDataset.get(position).getUsernameFollow() +")");
        //holder.emailUtente.setText(mDataset.get(position).getEmailFollow());
        //Log.i("TEST: ", "USERNAME UTENTE LISTA FOLLOWERS: " + mDataset.get(position).getEmailFollow());

        holder.dataUtente.setText(mDataset.get(position).getDataNascitaFollow());

        String sesso = mDataset.get(position).getSessoFollow();
        holder.sessoUtente.setText(sesso);

        holder.lavoroUtente.setText(mDataset.get(position).getLavoroFollow());

        holder.descrizioneUtente.setText(mDataset.get(position).getDescrizioneFollow());

        holder.tipoUtente.setText(mDataset.get(position).getTipoFollow());

        holder.nazionalitaUtente.setText(mDataset.get(position).getNazionalitaFollow());


        String urlImmagine = mDataset.get(position).getUrlImmagineProfilo();

        ImageView immagineProfilo = holder.imageProfile;
        immagineProfilo.setContentDescription(urlImmagine);

        if(urlImmagine != null && !urlImmagine.equals("null")){
            //new BitmapWorkerTask(immagineProfilo).execute(Constants.ADDRESS_TAT +urlImmagine);
            Picasso.with(null).load(Constants.ADDRESS_TAT +urlImmagine).into(immagineProfilo);

        }
        else{
            if(sesso.equals("M")){
                immagineProfilo.setImageResource(R.drawable.default_male);
            }
            else{
                immagineProfilo.setImageResource(R.drawable.default_female);
            }
        }

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
}
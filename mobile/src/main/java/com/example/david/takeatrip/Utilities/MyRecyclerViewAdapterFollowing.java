package com.example.david.takeatrip.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.david.takeatrip.R;

import java.util.ArrayList;

/**
 * Created by david on 08/03/2016.
 */

public class MyRecyclerViewAdapterFollowing extends RecyclerView
        .Adapter<MyRecyclerViewAdapterFollowing
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapterFollowing";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView nomeUtente;
        TextView cognomeUtente;
        TextView usernameUtente;

        public DataObjectHolder(View itemView) {
            super(itemView);

            nomeUtente = (TextView) itemView.findViewById(R.id.NomeUtenteFollowing);
            cognomeUtente = (TextView) itemView.findViewById(R.id.CognomeUtenteFollowing);
            usernameUtente = (TextView) itemView.findViewById(R.id.UsernameUtenteFollowing);
            //emailUser = (TextView) itemView.findViewById(R.id.EmailUser);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;


    }

    public MyRecyclerViewAdapterFollowing(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_following, parent, false);



        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
       holder.nomeUtente.setText(mDataset.get(position).getNomeFollow());
       holder.cognomeUtente.setText(mDataset.get(position).getCognomeFollow());
       holder.usernameUtente.setText(mDataset.get(position).getUsernameFollow());


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
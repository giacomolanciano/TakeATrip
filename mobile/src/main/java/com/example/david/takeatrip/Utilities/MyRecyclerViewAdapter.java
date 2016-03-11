package com.example.david.takeatrip.Utilities;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

/**
 * Created by david on 08/03/2016.
 */
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.takeatrip.Activities.ViaggioActivity;
import com.example.david.takeatrip.R;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView nomeViaggio;
        TextView codiceViaggio;
        String nome;
        TextView dateTime;

        public DataObjectHolder(View itemView) {
            super(itemView);

            nomeViaggio = (TextView) itemView.findViewById(R.id.NameTravel);
            codiceViaggio = (TextView) itemView.findViewById(R.id.CodeTravel);
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
            //TODO creare il passaggio dell'email (Profile da DataObject ecc.)
            intent.putExtra("email","");
            intent.putExtra("nomeViaggio", nomeViaggio.getText().toString());
            intent.putExtra("codiceViaggio", codiceViaggio.getText().toString());

            v.getContext().startActivity(intent);




        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;


    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
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
       holder.nomeViaggio.setText(mDataset.get(position).getNomeViaggio());
       holder.codiceViaggio.setText(mDataset.get(position).getCodiceViaggio());
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
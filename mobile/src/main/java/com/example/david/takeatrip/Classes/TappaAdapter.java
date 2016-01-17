package com.example.david.takeatrip.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.david.takeatrip.R;

import java.util.List;

/**
 * Created by Giacomo on 15/01/2016.
 */
public class TappaAdapter extends ArrayAdapter<Tappa> {
    private ListView listView;
    private List<Tappa> listaTappe;
    private Context context;

    public TappaAdapter(Context context, int resource, List<Tappa> objects) {
        super(context, resource, objects);
        listaTappe = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.entry_tappe_listview, null);
        }

        Tappa tappa = getItem(position);
        TextView viewNome;

        viewNome = (TextView)convertView.findViewById(R.id.NameTappa);
        viewNome.setText((tappa.getNome()));
        viewNome.setTextSize(18);


        viewNome = (TextView)convertView.findViewById(R.id.DateTappa);

        //TODO rispristinare
        //viewNome.setText((tappa.getData().toString()));
        viewNome.setText("data");
        viewNome.setTextSize(18);


        return convertView;
    }
}

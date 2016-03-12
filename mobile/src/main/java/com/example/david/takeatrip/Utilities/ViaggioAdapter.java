package com.example.david.takeatrip.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.david.takeatrip.Classes.Viaggio;
import com.example.david.takeatrip.R;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by lucagiacomelli on 16/10/15.
 */
public class ViaggioAdapter extends ArrayAdapter<Viaggio> {
    private ListView listView;
    private ArrayList<Viaggio>travelsList;
    private Context context;

    public ViaggioAdapter(Context context, int resource, ArrayList<Viaggio> objects) {
        super(context, resource, objects);
        travelsList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.entry_travels_listview, null);
        }
        Viaggio viaggio = getItem(position);
        TextView viewNome = (TextView)convertView.findViewById(R.id.NameTravel);

        //TODO verificare perchè lancia eccezione
        viewNome.setText((viaggio.getNome()));
        viewNome.setTextSize(18);


        return convertView;
    }
}

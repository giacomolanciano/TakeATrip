package com.takeatrip.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.takeatrip.Classes.NotaTappa;
import com.takeatrip.R;

/**
 * Created by lucagiacomelli on 28/10/16.
 */

public class ListViewNotesAdapter extends ArrayAdapter<NotaTappa> {

    public ListViewNotesAdapter(Context context, int textViewResourceId, NotaTappa [] objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);

    }



    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.entry_list_notes, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.user = (TextView)convertView.findViewById(R.id.UserTappa);
            viewHolder.note = (TextView)convertView.findViewById(R.id.NoteTappa);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        NotaTappa notaTappa = getItem(position);
        viewHolder.user.setText(notaTappa.getEmailProfilo());
        viewHolder.note.setText(notaTappa.getNota());
        return convertView;
    }


    private class ViewHolder {
        public TextView user;
        public TextView note;
    }
}

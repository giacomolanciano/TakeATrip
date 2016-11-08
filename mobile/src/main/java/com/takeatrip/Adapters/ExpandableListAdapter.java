package com.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 18/03/16.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.takeatrip.AsyncTasks.DeleteStopContentTask;
import com.takeatrip.AsyncTasks.UpdateNotaTappaTask;
import com.takeatrip.Classes.NotaTappa;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "ExpListAdapt";
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<NotaTappa>> _listDataChild;
    private String nuovaNota = "";
    private String email;
    private SwipeRevealLayout currentOpenSwipe;



    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<NotaTappa>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<NotaTappa>> listChildData, String email) {
        this(context,listDataHeader,listChildData);
        this.email = email;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {

        final NotaTappa notaTappa = (NotaTappa) getChild(groupPosition, childPosition);
        final View cView = convertView;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.entry_list_notes, null);
        }

        TextView userTappa = (TextView) convertView.findViewById(R.id.UserTappa);
        final TextView note = (TextView) convertView.findViewById(R.id.NoteTappa);
        final TextView editNotatappa = (TextView) convertView.findViewById(R.id.editNotaTappa);
        final SwipeRevealLayout swipeLayout = (SwipeRevealLayout) convertView.findViewById(R.id.swipeLayout);
        final FrameLayout deleteLayout = (FrameLayout) convertView.findViewById(R.id.layoutDeleteNote);

        final ExpandableListAdapter adapter = this;


        if(!notaTappa.getNota().equals("") && (email == null || email.equals(notaTappa.getEmailProfilo()))){
            editNotatappa.setVisibility(View.VISIBLE);
            editNotatappa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nuovaNota = modificaNotatappa(notaTappa,v, note);
                }
            });

            swipeLayout.setLockDrag(false);
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Ho cliccato la delete della nota: " + notaTappa);
                    noteDeletion(v, Constants.QUERY_DEL_NOTE, notaTappa);

                    //Zero because we have one header: "view Notes"
                    _listDataChild.get(_listDataHeader.get(0)).remove(notaTappa);
                    adapter.notifyDataSetChanged();
                    swipeLayout.close(true);
                    //swipeLayout.setDragEdge(SwipeRevealLayout.DRAG_EDGE_RIGHT);
                }
            });
        }else {
            swipeLayout.setLockDrag(true);
        }

        userTappa.setText(notaTappa.getUsername());
        note.setText(notaTappa.getNota());
        return convertView;
    }

    private String modificaNotatappa(final NotaTappa notaTappa, View v, final TextView note) {
        Log.i(TAG, "qui si modificherà la nota della tappa: " + notaTappa);
        final String vecchiaNota = notaTappa.getNota();

        ContextThemeWrapper wrapper = new ContextThemeWrapper(_context, android.R.style.Theme_Material_Light_Dialog);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(wrapper);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View dialogView = inflater.inflate(R.layout.material_edit_text, null);
        builder.setView(dialogView);

        TextInputLayout textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.textInputLayout);
        final TextInputEditText textInputEditText = (TextInputEditText) dialogView.findViewById(R.id.editText);
        textInputEditText.setText(vecchiaNota);

        textInputLayout.setCounterEnabled(true);
        textInputLayout.setCounterMaxLength((Constants.NOTE_MAX_LENGTH));

        builder.setNegativeButton(_context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.i(TAG, "edit text dialog canceled");
                    }
                });

        builder.setPositiveButton(_context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nuovaNota = textInputEditText.getText().toString();

                        Log.i(TAG, "vecchia nota: " + vecchiaNota);
                        Log.i(TAG, "nuova nota: " + nuovaNota);

                        new UpdateNotaTappaTask(_context, notaTappa.getEmailProfilo(),
                                notaTappa.getCodiceViaggio(),notaTappa.getOrdineTappa(), vecchiaNota, nuovaNota).execute();
                        note.setText(nuovaNota);
                        notaTappa.setNota(nuovaNota);

                    }
                });

        builder.setTitle(_context.getString(R.string.EditNote));
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

        return nuovaNota;
    }


    public void noteDeletion(View v, String q, NotaTappa nota){
        Log.i(TAG,"Ho eliminato la nota: " + nota);
        new DeleteStopContentTask(_context, q, nota.getEmailProfilo(),nota.getCodiceViaggio(),
                nota.getNota()).execute();
    }




    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.header_expandable_list_view, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
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
 * Created by lucagiacomelli on 16/10/15.
 */
public class MetaAdapter extends ArrayAdapter<Meta> {
    private ListView listView;
    private List<Meta> meteList;
    private Context context;

    public MetaAdapter(Context context, int resource, List<Meta> objects) {
        super(context, resource, objects);
        meteList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.entry_possibili_mete, null);
        }
        Meta meta = getItem(position);
        TextView viewNome = (TextView)convertView.findViewById(R.id.NameMeta);

        viewNome.setText((meta.getNome()));
        viewNome.setTextSize(18);


        return convertView;
    }
}

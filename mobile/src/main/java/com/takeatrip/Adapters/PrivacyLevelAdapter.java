package com.takeatrip.Adapters;

/**
 * Created by Giacomo Lanciano on 14/03/2016.
 */
public class PrivacyLevelAdapter { //extends ArrayAdapter<String> {

    private static String TAG = "TEST PrivLevelAdapt";


//    Context context;
//    private String[] strings;
//    private String[] subs;
//    private int[] arr_images;
//
//    public PrivacyLevelAdapter(Context context, int textViewResourceId, String[] strings, String[] subs, int[] arr_images) {
//        super(context, textViewResourceId);
//        this.context = context;
//
//        //strings = context.getResources().getStringArray(R.array.PrivacyLevel);
//        this.strings = strings;
//        this.subs = subs;
//        this.arr_images = arr_images;
//
//
//        Log.i(TAG, "context: "+context);
//        Log.i(TAG, "strings: "+strings);
//        Log.i(TAG, "subs: "+subs);
//    }
//
//    @Override
//    public View getDropDownView(int position, View convertView,ViewGroup parent) {
//        return getCustomView(position, convertView, parent);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return getCustomView(position, convertView, parent);
//    }
//
//    public View getCustomView(int position, View convertView, ViewGroup parent) {
//
////        if(convertView == null){
////            convertView= LayoutInflater.from(context).inflate(R.layout.entry_privacy_level, parent);
////        }
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        convertView=inflater.inflate(R.layout.entry_privacy_level, parent, false);
//
//
//        TextView label=(TextView)convertView.findViewById(R.id.privacyLevel);
//        label.setText(strings[position]);
//
//        TextView sub=(TextView)convertView.findViewById(R.id.description);
//        sub.setText(subs[position]);
//
//        ImageView icon=(ImageView)convertView.findViewById(R.id.image);
//        icon.setImageResource(arr_images[position]);
//
//        Log.i(TAG, "string: " + strings[position]);
//        Log.i(TAG, "sub: " + subs[position]);
//        Log.i(TAG, "img: " + arr_images[position]);
//
//        return convertView;
//    }
}

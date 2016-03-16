package com.example.david.takeatrip.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DownloadImageTask;

import java.net.URL;

/**
 * Created by lucagiacomelli on 16/03/16.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ImageAdapter mAdapter;

    // A static dataset to back the GridView adapter
    private static String[] URLs;

    // Empty constructor as per Fragment docs
    public ImageGridFragment() {}

    public static ImageGridFragment newInstance(String[] urls){

        ImageGridFragment newFragment = new ImageGridFragment();
        Bundle args = new Bundle();
        args.putStringArray("urls", urls);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ImageAdapter(getActivity());

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(getArguments() != null){
            Log.i("TEST", " arguments diversi da null");
            URLs = getArguments().getStringArray("urls");
        }


        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);

        Log.i("TEST", "URLs: " + URLs);

        if(URLs != null && URLs.length>0){

            final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
            mGridView.setAdapter(mAdapter);

            Log.i("TEST", "settato l'adapter per il grid");

            mGridView.setOnItemClickListener(this);
        }


        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //TODO
        /*
        final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, position);
        startActivity(i);

        */
    }

    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return URLs.length;
        }

        @Override
        public Object getItem(int position) {
            return URLs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView;


            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);


                imageView = new ImageView(mContext);
                //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);

            } else {
                imageView = (ImageView) convertView;
            }



            //TODO
            imageView.setImageResource(R.drawable.empty_image); // Load image into ImageView

            Log.i("TEST", "ora carico le foto nella gridView...");
            new DownloadImageTask(imageView).execute(Constants.ADDRESS_TAT + URLs[position]);


            return imageView;
        }
    }
}

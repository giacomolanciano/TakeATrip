package com.example.david.takeatrip.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.david.takeatrip.Adapters.GridViewAdapter;
import com.example.david.takeatrip.AsyncTasks.BitmapWorkerTask;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.ScrollListener;
import com.example.david.takeatrip.Utilities.SquaredImageView;

/**
 * Created by lucagiacomelli on 16/03/16.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "TEST ImageGridFragment";

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
        //mAdapter = new ImageAdapter(getActivity());

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(getArguments() != null){
            Log.i(TAG, " arguments diversi da null");
            URLs = getArguments().getStringArray("urls");
        }

        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
        Log.i(TAG, "URLs: " + URLs);

        if(URLs != null && URLs.length>0){

            GridView gv = (GridView) v.findViewById(R.id.grid_view);
            gv.setAdapter(new GridViewAdapter(getActivity(), URLs));
            gv.setOnScrollListener(new ScrollListener(getActivity()));

            Log.i(TAG, "settato l'adapter per il grid");
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


            SquaredImageView view = (SquaredImageView) convertView;
            if (view == null) {
                view = new SquaredImageView(mContext);
            }


            if (convertView == null) { // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);

                //TODO: vedere la risoluzione dello schermo


                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);

            } else {
                imageView = (ImageView) convertView;
            }


            //if(URLs[position] != null && !URLs[position].equals("null")){

            //imageView.setImageResource(R.drawable.empty_image); // Load image into ImageView
            //loadBitmap(URLs[position], imageView);


            //new BitmapWorkerTask(imageView).execute(Constants.ADDRESS_TAT + URLs[position]);
            //}


            return imageView;
        }
    }

    public void loadBitmap(String url, ImageView imageView) {
        if (cancelPotentialWork(url, imageView)) {
            if(url != null && !url.equals("null")){
                Log.i(TAG, "ora carico le foto nella gridView...");
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                task.execute(Constants.ADDRESS_TAT +url);
            }


        }
    }


    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        //final BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTask(imageView);
        final BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {


            Log.i(TAG, "worker task: " + bitmapWorkerTask.toString());
            Log.i(TAG, "worker task with data: " + bitmapWorkerTask.data);

            if (bitmapWorkerTask== null || !bitmapWorkerTask.equals(url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }



            //final String bitmapData = bitmapWorkerTask.data;

            /*
            Log.i(TAG, "worker task: " + bitmapWorkerTask.toString());
            Log.i(TAG, "worker task with data: " + bitmapWorkerTask.data);

            if (bitmapData== null || !bitmapData.equals(url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }*/
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

}

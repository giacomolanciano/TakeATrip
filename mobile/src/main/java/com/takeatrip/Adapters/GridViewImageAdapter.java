package com.takeatrip.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takeatrip.AsyncTasks.UpdateCondivisioneContentTask;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.SquaredImageView;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

import java.util.List;

/**
 * Created by Giacomo Lanciano on 29/04/2016.
 */
public class GridViewImageAdapter extends GridViewAdapter {

    private static final String TAG = "TEST GridViewImgAdapt";
    private static final int THREE = 3;
    private static final int SIX = 3;

    private String[] strings, subs;
    private int[] arr_images;

    private ProgressDialog progressDialog;


    public GridViewImageAdapter(Context context, GridView gv, List<ContenutoMultimediale> URLs, int tipoContenuto, String emailProfiloLoggato) {
        super(context,gv, URLs, tipoContenuto, emailProfiloLoggato);
    }

    public GridViewImageAdapter(Context context,GridView gv, List<ContenutoMultimediale> URLs, int tipoContenuto, String codiceViaggio, String emailProfiloLoggato) {
        super(context, gv, URLs, tipoContenuto, codiceViaggio, emailProfiloLoggato);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SquaredImageView result = (SquaredImageView) super.getView(position, convertView, parent);
        final Context context = this.getContext();

        final ContenutoMultimediale contenutoMultimediale = getItem(Integer.parseInt(result.getContentDescription().toString()));
        final String url = UtilS3AmazonCustom.getS3FileURL(getS3(), Constants.BUCKET_TRAVELS_NAME,
                contenutoMultimediale.getUrlContenuto());

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.empty_image)
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *THREE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *THREE)
                .centerCrop()
                .tag(context)
                .into(result);


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                dialog.setContentView(R.layout.photos_view);

                ImageView imageProfile = (ImageView) dialog.findViewById(R.id.imageDialog);
                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                float density = context.getResources().getDisplayMetrics().density;
                if(density < 3.0){
                    Picasso.with(context)
                            .load(url)
                            .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *6, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *6)
                            .into(imageProfile, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    setSpinner(dialog,position,v);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
                else if(density == 3.0 || density == 4.0){
                    Picasso.with(context)
                            .load(url)
                            .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *10, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *10)
                            .into(imageProfile, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    setSpinner(dialog,position,v);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                }
            }
        });
        if (getCodiceViaggio() != null) {
            result.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(getItem(position).getEmailProfilo().equals(emailProfiloLoggato)){

                        confirmFileDeletion(v, Constants.QUERY_DEL_IMAGE);
                    }

                    return false;
                }
            });
        }

        return result;
    }



    private void setSpinner(Dialog dialog, int position, final View v){

        strings = context.getResources().getStringArray(R.array.PrivacyLevel);
        subs = context.getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;

        final Spinner privacySpinner = (Spinner) dialog.findViewById(R.id.spinnerPrivacyLevel);
        final ContenutoMultimediale contenutoMultimediale = getItem(Integer.parseInt(v.getContentDescription().toString()));
        final PrivacyLevelAdapter adapter = new PrivacyLevelAdapter(context, R.layout.entry_privacy_level, strings);

        if (privacySpinner != null) {
            privacySpinner.setAdapter(adapter);

            final int spinnerPosition = adapter.getPosition(contenutoMultimediale.getLivelloCondivisione());
            privacySpinner.setSelection(spinnerPosition);

            if(!getItem(position).getEmailProfilo().equals(emailProfiloLoggato)) {
                privacySpinner.setEnabled(false);
            }
            else {
                privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Log.i(TAG, "elemento selezionato nello spinner: " + adapter.getItem(position));
                        String livelloCondivisioneContenuto = adapter.getItem(position);

                        cm = getItem(Integer.parseInt(v.getContentDescription().toString()));
                        cm.setLivelloCondivisione(livelloCondivisioneContenuto);

                        //Log.i(TAG, "update livello condivisione del contenuto: " + cm.getUrlContenuto());

                        new UpdateCondivisioneContentTask(context, codiceViaggio, cm.getOrdineTappa(), livelloCondivisioneContenuto, cm.getUrlContenuto()).execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } else {
            Log.e(TAG, "privacySpinner is null");
        }

        dialog.show();
    }



    private class PrivacyLevelAdapter extends ArrayAdapter<String> {

        public PrivacyLevelAdapter(Context context, int textViewResourceId, String[] strings) {
            super(context, textViewResourceId, strings);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= LayoutInflater.from(getContext());
            convertView=inflater.inflate(R.layout.entry_privacy_level, parent, false);
            TextView label=(TextView)convertView.findViewById(R.id.privacyLevel);
            label.setText(strings[position]);

            TextView sub=(TextView)convertView.findViewById(R.id.description);
            sub.setText(subs[position]);

            ImageView icon=(ImageView)convertView.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);
            return convertView;
        }
    }

}

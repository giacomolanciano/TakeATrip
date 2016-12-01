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
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.takeatrip.AsyncTasks.UpdateCondivisioneContentTask;
import com.takeatrip.Classes.ContenutoMultimediale;
import com.takeatrip.R;
import com.takeatrip.Utilities.Constants;
import com.takeatrip.Utilities.SquaredImageView;
import com.takeatrip.Utilities.UtilS3AmazonCustom;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
        adapter = this;

        convertView = new SquaredImageView(context);

            convertView.setContentDescription(position+"");

            final Context context = this.getContext();

            final ContenutoMultimediale contenutoMultimediale = getItem(Integer.parseInt(convertView.getContentDescription().toString()));
            final String url = UtilS3AmazonCustom.getS3FileURL(context,getS3(), Constants.BUCKET_TRAVELS_NAME,
                    contenutoMultimediale.getUrlContenuto());

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.empty_image)
                    .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *THREE, Constants.BASE_DIMENSION_OF_IMAGE_PARTICIPANT *THREE)
                    .centerCrop()
                    .tag(context)
                    .into((SquaredImageView) convertView);


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Dialog dialog = new Dialog(context, R.style.CustomDialog2);
                    dialog.setContentView(R.layout.photos_view);

                    ImageView imageProfile = (ImageView) dialog.findViewById(R.id.imageDialog);
                    imageProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    Picasso.with(context)
                            .load(url)
                            .into(imageProfile, new com.squareup.picasso.Callback() {

                                @Override
                                public void onSuccess() {

                                    setDialog(dialog,position,v);
                                }

                                @Override
                                public void onError() {

                                }
                            });


                }
            });
            if (getCodiceViaggio() != null) {
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(getItem(position).getEmailProfilo().equals(emailProfiloLoggato)){

                            confirmFileDeletion(v, Constants.QUERY_DEL_IMAGE);
                        }

                        return false;
                    }
                });
            }

        return convertView;


    }



    private void setDialog(Dialog dialog, int position, final View v){

        final ContenutoMultimediale contenutoMultimediale = getItem(Integer.parseInt(v.getContentDescription().toString()));


        TextView viewReport = (TextView)dialog.findViewById(R.id.textSegnala);
        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReportFile(v, contenutoMultimediale);
            }
        });

        strings = context.getResources().getStringArray(R.array.PrivacyLevel);
        subs = context.getResources().getStringArray(R.array.PrivacyLevelDescription);
        arr_images = Constants.privacy_images;

        final Spinner privacySpinner = (Spinner) dialog.findViewById(R.id.spinnerPrivacyLevel);
        final PrivacyLevelAdapter adapter = new PrivacyLevelAdapter(context, R.layout.entry_privacy_level, strings);

        if (privacySpinner != null) {
            privacySpinner.setAdapter(adapter);

            privacySpinner.setSelection(Integer.parseInt(contenutoMultimediale.getLivelloCondivisione()));

            if(!getItem(position).getEmailProfilo().equals(emailProfiloLoggato)) {
                privacySpinner.setEnabled(false);
            }
            else {
                privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Log.i(TAG, "elemento selezionato nello spinner: " + adapter.getItem(position));
                        String livelloCondivisioneContenuto = position+"";

                        cm = getItem(Integer.parseInt(v.getContentDescription().toString()));
                        cm.setLivelloCondivisione(livelloCondivisioneContenuto);

                        //Log.i(TAG, "update livello condivisione del contenuto: " + cm.getUrlContenuto());

                        try {
                            boolean result = new UpdateCondivisioneContentTask(context, codiceViaggio, cm.getOrdineTappa(),
                                    livelloCondivisioneContenuto, cm.getUrlContenuto()).execute().get();

                            if(!result){
                                Toast.makeText(context, R.string.error_connection, Toast.LENGTH_LONG).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
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

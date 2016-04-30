package com.example.david.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 17/03/16.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class GridViewAdapter extends BaseAdapter {

    private static final String TAG = "TEST GridViewAdapter";
    private static final int TRIPLE = 3;

    private final Context context;
    private final List<String> urls = new ArrayList<String>();
    private final int tipoContenuti;

    public GridViewAdapter(Context context, String[] URLs, int tipoContenuti) {
        this.context = context;
        this.tipoContenuti = tipoContenuti;

        // Ensure we get a different ordering of images on each run.

        if(URLs != null)
            Collections.addAll(urls, URLs);
        //Collections.shuffle(urls);

        // Triple up the list.
        //ArrayList<String> copy = new ArrayList<String>(urls);
        //urls.addAll(copy);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the URL for the current position (o il testo nel caso delle note).
        final String url = getItem(position);

        //è utile solamente nel caso delle immagini
        view.setContentDescription(url);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context)
                .load(R.drawable.empty_image)
                .resize(Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*TRIPLE, Constants.BASE_DIMENSION_OF_IMAGE_PARTECIPANT*TRIPLE)
                .centerCrop()
                .tag(context)
                .into(view);

        if (tipoContenuti == Constants.VIDEO_FILE) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "content: "+v.getContentDescription());

                    Uri uri = Uri.parse(url);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/*");
                    context.startActivity(intent);

                }
            });
        } else if (tipoContenuti == Constants.AUDIO_FILE) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "content: "+v.getContentDescription());

                    Uri uri = Uri.parse(url);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "audio/*");
                    context.startActivity(intent);

                }
            });
        } else if (tipoContenuti == Constants.NOTE_FILE) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO definire comportamento per visualizzazione nota

                    Log.i(TAG, "content: "+v.getContentDescription());

                    ContextThemeWrapper wrapper = new ContextThemeWrapper(context, android.R.style.Theme_Material_Light_Dialog);

                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(wrapper);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    final View dialogView = inflater.inflate(R.layout.material_edit_text, null);
                    builder.setView(dialogView);

                    TextInputLayout textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.textInputLayout);
//                    if (textInputLayout != null) {
//                        textInputLayout.setCounterEnabled(true);
//                        textInputLayout.setCounterMaxLength(Constants.NOTE_MAX_LENGTH);
//                    }

                    TextInputEditText textInputEditText = (TextInputEditText) dialogView.findViewById(R.id.editText);
                    textInputEditText.setText(url);


                    //TODO per ora neghiamo la possibilità di editare, valutare aggiunta feature in seguito
                    textInputEditText.setEnabled(false);
                    textInputEditText.setFocusable(false);
                    textInputEditText.setFilters(new InputFilter[] {
                            new InputFilter() {
                                public CharSequence filter(CharSequence src, int start,
                                                           int end, Spanned dst, int dstart, int dend) {
                                    return src.length() < 1 ? dst.subSequence(dstart, dend) : "";
                                }
                            }
                    });


                    builder.setNegativeButton(context.getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();


                                    Log.i(TAG, "edit text dialog canceled");
                                }
                            });

//                    builder.setPositiveButton(context.getString(R.string.ok),
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    noteInserite.add(textInputEditText.getText().toString());
//                                    Log.i(TAG, "edit text confirmed");
//                                }
//                            });


                    builder.setTitle(context.getString(R.string.labelNoEditNote));

                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return context;
    }

    public List<String> getUrls() {
        return urls;
    }

    public int getTipoContenuti() {
        return tipoContenuti;
    }
}
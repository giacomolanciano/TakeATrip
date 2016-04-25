package com.example.david.takeatrip.Adapters;

/**
 * Created by lucagiacomelli on 18/03/16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.BitmapCache;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

public class MyExpandableListItemAdapter extends ExpandableListItemAdapter<Integer> {

    private final String TAG = "TEST ExpListItemAdapt";

    private final Context mContext;
    private final BitmapCache mMemoryCache;

    /**
     * Creates a new ExpandableListItemAdapter with the specified list, or an empty list if
     * items == null.
     */
    public MyExpandableListItemAdapter(final Context context) {
        super(context, R.layout.activity_tappa, R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content);
        mContext = context;
        mMemoryCache = new BitmapCache();

        for (int i = 0; i < 100; i++) {
            add(i);
        }
    }

    @NonNull
    @Override
    public View getTitleView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = new TextView(mContext);
        }
        return tv;
    }

    @NonNull
    @Override
    public View getContentView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        int imageResId;
        switch (getItem(position) % 5) {
            case 0:
                imageResId = R.drawable.default_female;
                break;
            case 1:
                imageResId = R.drawable.default_female;
                break;
            case 2:
                imageResId = R.drawable.default_male;
                break;
            case 3:
                imageResId = R.drawable.default_female;
                break;
            default:
                imageResId = R.drawable.default_male;
        }

        Bitmap bitmap = getBitmapFromMemCache(imageResId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
            addBitmapToMemoryCache(imageResId, bitmap);
        }
        imageView.setImageBitmap(bitmap);

        return imageView;
    }

    private void addBitmapToMemoryCache(final int key, final Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(final int key) {
        return mMemoryCache.get(key);
    }
}
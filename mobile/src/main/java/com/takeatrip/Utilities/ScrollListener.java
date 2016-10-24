package com.takeatrip.Utilities;

/**
 * Created by lucagiacomelli on 17/03/16.
 */


import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

public class ScrollListener implements AbsListView.OnScrollListener {

    private static final String TAG = "TEST ScrollListener";

    private final Context context;

    public ScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // Do nothing.
    }
}
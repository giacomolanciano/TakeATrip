package com.takeatrip.Utilities;

/**
 * Created by lucagiacomelli on 17/03/16.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/** An image view which always remains square with respect to its width. */
public final class SquaredVideoView extends VideoView {

    private static final String TAG = "TEST SquaredVideoView";

    public SquaredVideoView(Context context) {
        super(context);
    }

    public SquaredVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
package com.example.david.takeatrip.Utilities;

/**
 * Created by lucagiacomelli on 17/03/16.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** An image view which always remains square with respect to its width. */
public final class SquaredImageView extends ImageView {

    private static final String TAG = "TEST SquaredImageView";

    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
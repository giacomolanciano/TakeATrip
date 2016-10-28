package com.takeatrip.GraphicalComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by lucagiacomelli on 28/10/16.
 */

public class AdaptableListView extends ListView {
    private static final int SHIFT = 2;

    public AdaptableListView(Context context) {
        super(context);
    }

    public AdaptableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdaptableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> SHIFT, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}

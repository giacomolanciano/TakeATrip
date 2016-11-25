package com.takeatrip.GraphicalComponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.takeatrip.R;

public class DrawView extends View {
    Paint paint = new Paint();
    int xSource, ySource, xDest, yDest, xSocialButton, ySocialButton;


    public DrawView(Context context) {
        super(context);
        paint.setColor(getResources().getColor(R.color.blu_scuro));
        paint.setStrokeWidth(10);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(getResources().getColor(R.color.blu_scuro));
        paint.setStrokeWidth(10);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint.setColor(getResources().getColor(R.color.blu_scuro));
        paint.setStrokeWidth(10);
    }



    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(363, 900, 600, 800, paint);
        canvas.drawLine(363, 900, 100, 800, paint);
        canvas.drawLine(363, 900, 363, 250, paint);
        canvas.drawLine(600, 800, 363, 250, paint);
        canvas.drawLine(100, 800, 363, 250, paint);
        canvas.drawLine(100, 800, 600, 800, paint);

        //canvas.drawLine(286, 860, 240, 915, paint);

    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
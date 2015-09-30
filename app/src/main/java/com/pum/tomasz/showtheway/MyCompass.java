package com.pum.tomasz.showtheway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tomasz on 30.09.2015.
 */
public class MyCompass extends View {

    Paint circlePaint = new Paint();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int W = canvas.getWidth();
        final int H = canvas.getHeight();
        final int S = (W<H)?W:H;

        final int R = S/4;
        final int x0 = W/2;
        final int y0 = H/2;

        final int armHourLength = 2*R/3;
        final int armMinuteLength = R-R/5;
        final int armSecondLength = R-R/6;
        final int smallCircleR = R/12;

        canvas.drawCircle(x0, y0, R, circlePaint);
    }

    public MyCompass(Context context,AttributeSet attrs) {
        super(context);

        circlePaint.setColor(0x20000000);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);

    }
}

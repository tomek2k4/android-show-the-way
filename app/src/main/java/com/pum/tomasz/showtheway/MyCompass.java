package com.pum.tomasz.showtheway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.pum.tomasz.showtheway.engine.AzimuthChangeListener;
import com.pum.tomasz.showtheway.engine.AzimuthData;
import com.pum.tomasz.showtheway.engine.GeoAzimuthChangeNotifier;
import com.pum.tomasz.showtheway.engine.LocationAzimuthManager;


/**
 * Created by tomasz on 30.09.2015.
 */
public class MyCompass extends View implements AzimuthChangeListener {

    private Paint circlePaint = new Paint();
    private Paint northTextPaint = new Paint();
    private Paint defaultTextPaint = new Paint();
    private int   canvasHeight;
    private int   canvasWidth;

    private GeoAzimuthChangeNotifier geoAzimuthChangeNotifier;
    private LocationAzimuthManager locationAzimuthManager;


    private float rotateAngle = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasHeight = canvas.getWidth();
        canvasWidth = canvas.getHeight();

        canvas.save();
        canvas.rotate(rotateAngle, canvasHeight /2, canvasWidth /2);
        drawCompassRing(canvas);
        canvas.restore();

    }

    public MyCompass(Context context,AttributeSet attrs) {
        super(context,attrs);

        circlePaint.setColor(0x20000000);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);

        northTextPaint.setColor(0xFFFF0000);
        northTextPaint.setTextSize(30);

        defaultTextPaint.setColor(0xFF000000);
        defaultTextPaint.setTextSize(30);

        geoAzimuthChangeNotifier = new GeoAzimuthChangeNotifier(context);
        locationAzimuthManager = new LocationAzimuthManager(context);
    }

    public void open(){
        Log.d("Tomek", "My compass has been opened");
        geoAzimuthChangeNotifier.registerAzimuthChangeListener(this);
        locationAzimuthManager.activate();
    }


    public void close(){
        Log.d("Tomek", "My compass has been closed");
        if(geoAzimuthChangeNotifier != null){
            geoAzimuthChangeNotifier.unregisterAzimuthChangeListener();
        }
        locationAzimuthManager.deactivate();
    }

    private void drawCompassRing(Canvas canvas){
        final int S = (canvasHeight < canvasWidth)? canvasHeight : canvasWidth;

        final int R = S/3;
        final int x0 = canvasHeight /2;
        final int y0 = canvasWidth /2;

        final int armHourLength = 2*R/3;
        final int armMinuteLength = R-R/5;
        final int armSecondLength = R-R/6;
        final int textMargin = R/12;

        //Draw main circle
        canvas.drawCircle(x0, y0, R, circlePaint);

        //Draw Sides texts
        float deltaAngleWorldSides = 2*(float)Math.PI / 4;

        canvas.drawText("N", x0, y0 - R - textMargin / 2, northTextPaint);
        canvas.drawText("W", (float) (x0 - R - 1.5 * textMargin), y0, defaultTextPaint);
        canvas.drawText("E", x0 + R + textMargin / 2, y0, defaultTextPaint);
        canvas.drawText("S", x0, (float) (y0 + R + 1.5 * textMargin), defaultTextPaint);
    }


    public void rotateCompassRing(float angle){
        rotateAngle = angle;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                rotateAngle = 300;//(rotateAngle += 45) %360;
//                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    @Override
    public synchronized void onAzimuthChange(AzimuthData azimuthData) {
        Log.d("Tomek", "Azimuth from " + azimuthData.getAzimuthSourceEnum().name().toString()
                + " source is:" + new Float(azimuthData.getAzimuth()).toString());
        rotateCompassRing(-azimuthData.getAzimuth());

    }
}

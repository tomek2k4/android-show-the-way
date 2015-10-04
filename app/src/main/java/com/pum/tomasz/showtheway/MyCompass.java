package com.pum.tomasz.showtheway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pum.tomasz.showtheway.data.DestinationLocation;
import com.pum.tomasz.showtheway.engine.AzimuthChangeListener;
import com.pum.tomasz.showtheway.data.AzimuthData;
import com.pum.tomasz.showtheway.engine.GeoAzimuthChangeNotifier;
import com.pum.tomasz.showtheway.engine.LocationAzimuthManager;


/**
 * Created by tomasz on 30.09.2015.
 */
public class MyCompass extends View implements AzimuthChangeListener {

    private Paint circlePaintFilled = new Paint();
    private Paint circlePaintStroke = new Paint();
    private Paint northTextPaint = new Paint();
    private Paint defaultTextPaint = new Paint();
    private Paint directionVectorPaint = new Paint();
    private int   canvasHeight;
    private int   canvasWidth;

    private GeoAzimuthChangeNotifier geoAzimuthChangeNotifier;
    private LocationAzimuthManager locationAzimuthManager;

    private int canvasMaxDim;
    private int circleRadius;
    private int x0;
    private int y0;

    private float rotateAngle = 0;
    private Float directionAngle = null;
    private boolean arrived = false;

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasHeight = canvas.getWidth();
        canvasWidth = canvas.getHeight();
        canvasMaxDim = (canvasHeight < canvasWidth)? canvasHeight : canvasWidth;

        circleRadius = canvasMaxDim /3;
        x0 = canvasHeight /2;
        y0 = canvasWidth /2;

        canvas.save();
        canvas.rotate(rotateAngle, canvasHeight / 2, canvasWidth / 2);
        if(directionAngle!=null) {
            drawDirectionVector(canvas);
        }
        if(arrived){
            circlePaintFilled.setColor(0xFF336600);
        }else
        {
            circlePaintFilled.setColor(0xFFCCCCCC);
        }
        drawCompassRing(canvas);
        canvas.restore();

    }

    public MyCompass(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaintFilled.setColor(0xFFCCCCCC);
        circlePaintFilled.setStyle(Paint.Style.STROKE);
        circlePaintFilled.setStrokeWidth(3);
        circlePaintFilled.setStyle(Paint.Style.FILL_AND_STROKE);

        circlePaintStroke.setColor(0xFF8F8F8F);
        circlePaintStroke.setStyle(Paint.Style.STROKE);
        circlePaintStroke.setStrokeWidth(2);

        northTextPaint.setColor(0xFFFF0000);
        northTextPaint.setTextSize(30);

        defaultTextPaint.setColor(0xFF000000);
        defaultTextPaint.setTextSize(30);

        directionVectorPaint.setColor(0x8F000066);
        directionVectorPaint.setStyle(Paint.Style.FILL);
        directionVectorPaint.setStrokeWidth(2);
        directionVectorPaint.setAntiAlias(true);


        geoAzimuthChangeNotifier = new GeoAzimuthChangeNotifier(context);
        locationAzimuthManager = new LocationAzimuthManager(context);
    }

    public void open(){
        Log.d("Tomek", "My compass has been opened");
        geoAzimuthChangeNotifier.registerAzimuthChangeListener(this);
        locationAzimuthManager.setmAzimuthChangeListener(this);
        locationAzimuthManager.activate();
    }

    public void setDestinationLocation(DestinationLocation destLocation) {
        if(locationAzimuthManager!=null){
            locationAzimuthManager.setDestinationLocation(destLocation);
        }
    }


    public void close(){
        Log.d("Tomek", "My compass has been closed");
        if(geoAzimuthChangeNotifier != null){
            geoAzimuthChangeNotifier.unregisterAzimuthChangeListener();
        }
        locationAzimuthManager.deactivate();
    }

    private void drawCompassRing(Canvas canvas){

        final int textMargin = circleRadius /12;
        final int textSize = (int) getResources().getDimension(R.dimen.edit_text_size);

        //Draw main circle
        canvas.drawCircle(x0, y0, circleRadius, circlePaintFilled);
        canvas.drawCircle(x0, y0, circleRadius, circlePaintStroke);

        //Draw Sides texts
        float deltaAngleWorldSides = 2*(float)Math.PI / 4;

        canvas.drawText("N", x0 - textSize/2, y0 - circleRadius - textMargin / 2, northTextPaint);
        canvas.drawText("W", (float) (x0 - circleRadius - 1.5 * textMargin), y0 + textSize/2, defaultTextPaint);
        canvas.drawText("E", x0 + circleRadius + textMargin / 2, y0 + textSize / 2, defaultTextPaint);
        canvas.drawText("S", x0 - textSize / 2, (float) (y0 + circleRadius + 1.5 * textMargin), defaultTextPaint);
    }


    private void drawDirectionVector(Canvas canvas) {

        final int vectorLength = circleRadius/5;

        float alpha = (float) ( Math.PI * directionAngle/180);

        float delta = (float) (Math.PI/12);
        float beta = (float) (alpha - delta);
        float gamma = (float) (alpha + delta);

        Point a = new Point((int) (x0 + (circleRadius * Math.sin(beta))),(int) (y0 - (circleRadius * Math.cos(beta))));
        Point b = new Point((int) (x0 + (circleRadius * Math.sin(gamma))), (int) (y0 - (circleRadius * Math.cos(gamma))));
        Point c = new Point((int)( x0 + ((circleRadius+vectorLength) * Math.sin(alpha))), (int)(y0 - ((circleRadius+vectorLength) * Math.cos(alpha))));

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, directionVectorPaint);


    }



    public void rotateCompassRing(float angle){
        rotateAngle = angle;
        invalidate();
    }

    private void updateDirection(float angle) {
        directionAngle = angle;
        invalidate();
    }

    private void notifyArrived() {
        Log.d("Tomek","Arrived to destination");
        setArrived(true);
        Toast.makeText(getContext(), R.string.arrived_text,
                Toast.LENGTH_LONG).show();
        invalidate();
    }

    @Override
    public synchronized void onAzimuthChange(AzimuthData azimuthData) {
        Log.d("Tomek", "Azimuth from " + azimuthData.getAzimuthSourceEnum().name().toString()
                + " source is:" + new Float(azimuthData.getAzimuth()).toString());
        switch (azimuthData.getAzimuthSourceEnum()){
            case GEOMAGNETIC:
                rotateCompassRing(-azimuthData.getAzimuth());
                break;
            case LOCATION:
                setArrived(false);
                updateDirection(azimuthData.getAzimuth());
                break;
            case ARRIVED:
                notifyArrived();
                break;
            case NULL:
                setArrived(false);
                directionAngle = null;
                invalidate();
                break;
        }
    }
}

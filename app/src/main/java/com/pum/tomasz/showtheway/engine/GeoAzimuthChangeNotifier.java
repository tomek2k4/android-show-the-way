package com.pum.tomasz.showtheway.engine;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.pum.tomasz.showtheway.data.AzimuthData;
import com.pum.tomasz.showtheway.data.AzimuthSourceEnum;

/**
 * Created by tomasz on 01.10.2015.
 */
public class GeoAzimuthChangeNotifier extends AzimuthNotifier implements SensorEventListener {

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;

   public GeoAzimuthChangeNotifier(Context context) {

        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    public void registerAzimuthChangeListener(AzimuthChangeListener azimuthChangeListener){
        setAzimuthChangeListener(azimuthChangeListener);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterAzimuthChangeListener(){
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = orientation[0]; // orientation contains: azimut, pitch and roll

                if(getAzimuthChangeListener()!=null){
                    //convert to deegrees
                    azimuth = (float) (azimuth*180/Math.PI); // output range is from -180 to 180;
                    getAzimuthChangeListener().onAzimuthChange(new AzimuthData(AzimuthSourceEnum.GEOMAGNETIC,azimuth));
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

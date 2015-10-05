package com.pum.tomasz.showtheway.engine;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.pum.tomasz.showtheway.data.AzimuthData;
import com.pum.tomasz.showtheway.data.AzimuthSourceEnum;


import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tomasz on 03.10.2015.
 */
public class LocationAzimuthManager{

    public static final int LOCATION_UPDTE_MIN_DISTANCE = 1; //in metres
    private final static String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // the id of a message to our response handler
    private static final int CALCULATED_AZIMUTH_MESSAGE_ID = 1;

    private Context context = null;
    LocationManager locationManager = null;
    private MyLocationListener locationListener = null;
    private LocationUpdatesHandlerThread locationUpdatesHandlerThread = null;

    private AzimuthChangeListener mAzimuthChangeListener = null;
    private Location destinationLocation = null;
    private ReentrantLock lock = new ReentrantLock();


    public LocationAzimuthManager(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void activate(){
        // instantiate Listener to receive Location events
        locationListener = new MyLocationListener();

        // instantiate HandlerThread to be responsible for handling new location
        // that way UI thread won't be slowed down by azimuth calculation
        locationUpdatesHandlerThread = new LocationUpdatesHandlerThread("LocationWorkerThread");

        //Start the thread
        locationUpdatesHandlerThread.start();

        // requests location updates triggerred by distance travelled from last point
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, LOCATION_UPDTE_MIN_DISTANCE,
                locationListener, locationUpdatesHandlerThread.getLooper());

    }

    public void deactivate(){
        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
        //stop the location thread
        locationUpdatesHandlerThread.quit();

        //null members
        locationListener = null;
        locationUpdatesHandlerThread = null;
        mAzimuthChangeListener = null;
    }

    public void setmAzimuthChangeListener(AzimuthChangeListener azimuthChangeListener){
        mAzimuthChangeListener = azimuthChangeListener;
    }


    private final Handler azimuthResponseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("Tomek", "Received calculated location azimuth message on thread: " +
                    Long.valueOf(Thread.currentThread().getId()).toString());


            switch (msg.what) {
                case CALCULATED_AZIMUTH_MESSAGE_ID:
                    Log.d("Tomek", "Handling response");
                    mAzimuthChangeListener.onAzimuthChange((AzimuthData) msg.obj);
                    break;
            }


        }
    };

    public void setDestinationLocation(Location destLocation) {

        Location prevDest =  this.destinationLocation;
        lock.lock();
        try{
            this.destinationLocation = destLocation;
        }finally {
            lock.unlock();
        }

        AzimuthData azimuthData = calculateDestinationAzimuth(locationManager.getLastKnownLocation(LOCATION_PROVIDER),destLocation);
        azimuthData.setAzimuthSourceEnum(AzimuthSourceEnum.NEW_DESTINATION);

        Message message = LocationAzimuthManager.this.azimuthResponseHandler
                .obtainMessage(CALCULATED_AZIMUTH_MESSAGE_ID,azimuthData);
        LocationAzimuthManager.this.azimuthResponseHandler.sendMessage(message);

    }


    class LocationUpdatesHandlerThread extends HandlerThread{

        public LocationUpdatesHandlerThread(String name) {
            super(name);
        }
    }

    class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.d("Tomek", "Received new location: " + location.getLatitude() + " " + location.getLongitude() +
                    " on thread: " + Long.valueOf(Thread.currentThread().getId()).toString());

            float azimuth;
            AzimuthData azimuthData;
            lock.lock();
            try {
                if(destinationLocation!=null){
                    azimuthData = calculateDestinationAzimuth(location,destinationLocation);
                }else{
                    azimuthData = new AzimuthData(AzimuthSourceEnum.NULL,0);
                }
            }finally {
                lock.unlock();
            }
            // since we shouldn't update the UI from a non-UI thread,
            // we'll send the result to the azimuthResponseHandler (defined above)
            Message message = LocationAzimuthManager.this.azimuthResponseHandler
                    .obtainMessage(CALCULATED_AZIMUTH_MESSAGE_ID,azimuthData);
            LocationAzimuthManager.this.azimuthResponseHandler.sendMessage(message);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


    private AzimuthData calculateDestinationAzimuth(Location currentLocation,Location destLocation) {

        AzimuthData azimuthData = null;
        float azimuth = 0;


        Location destLoc = new Location("dest");
        destLoc.setLatitude(destLocation.getLatitude());
        destLoc.setLongitude(destLocation.getLongitude());

        float distance = currentLocation.distanceTo(destLoc);
        //Check if we arrived on destination
        if (distance < LOCATION_UPDTE_MIN_DISTANCE) {
            azimuthData = new AzimuthData(AzimuthSourceEnum.ARRIVED, 0);
            return azimuthData;
        }

        // normalize to beginning axis
        float y = (float) (destLocation.getLatitude() - currentLocation.getLatitude());
        float x = (float) (destLocation.getLongitude() - currentLocation.getLongitude());

        // Output of atan2 will return the counter-clock wise angle with respect to X-axis in range -pi to pi
        // In our case longitude is x axis and latitude is y axis.
        // Geo azimuth is angle in respect to north base line and counted clockwise, so we have to
        // negate output to be clockwise and add 90 degrees to get respect to Y-axis
        azimuth = (float)((Math.PI/2)-Math.atan2(y,x));
        //convert to degrees
        azimuth = (float) (180f*(azimuth/Math.PI));

        return new AzimuthData(AzimuthSourceEnum.LOCATION, azimuth);
    }

}

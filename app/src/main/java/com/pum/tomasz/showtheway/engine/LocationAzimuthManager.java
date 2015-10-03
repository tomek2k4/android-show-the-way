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

/**
 * Created by tomasz on 03.10.2015.
 */
public class LocationAzimuthManager{

    public static final int LOCATION_UPDTE_MIN_DISTANCE = 1; //in metres
    private final static String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private Context context = null;
    LocationManager locationManager = null;
    private MyLocationListener locationListener = null;
    private LocationUpdatesHandlerThread locationUpdatesHandlerThread = null;



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


        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, LOCATION_UPDTE_MIN_DISTANCE,
                locationListener, locationUpdatesHandlerThread.getLooper());

    }

    public void deactivate(){
        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
        //stop the location thread
        locationUpdatesHandlerThread.quit();

        //null members for garbage collector
        locationListener = null;
        locationUpdatesHandlerThread = null;
    }


    private final Handler azimuthResponseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("Tomek","Received azimuth message");
        }
    };



    class LocationUpdatesHandlerThread extends HandlerThread{

        public LocationUpdatesHandlerThread(String name) {
            super(name);
        }
    }

    class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.d("Tomek","Received new location");
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

}

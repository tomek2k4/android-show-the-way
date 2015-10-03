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
import com.pum.tomasz.showtheway.data.DestinationLocation;

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
    private DestinationLocation destinationLocation;


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
                    new Long(Thread.currentThread().getId()).toString());


            switch (msg.what) {
                case CALCULATED_AZIMUTH_MESSAGE_ID:
                    Log.d("Tomek", "Handling response");
                    mAzimuthChangeListener.onAzimuthChange((AzimuthData) msg.obj);
                    break;
            }


        }
    };

    public void setDestinationLocation(DestinationLocation destinationLocation) {
        this.destinationLocation = destinationLocation;
    }


    class LocationUpdatesHandlerThread extends HandlerThread{

        public LocationUpdatesHandlerThread(String name) {
            super(name);
        }
    }

    class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.d("Tomek","Received new location: "+ location.getLatitude() + " "+ location.getLongitude() +
            "on thread: " + new Long(Thread.currentThread().getId()).toString());

            float azimuth = 45;

            // since we cannot update the UI from a non-UI thread,
            // we'll send the result to the azimuthResponseHandler (defined above)
            Message message = LocationAzimuthManager.this.azimuthResponseHandler
                    .obtainMessage(CALCULATED_AZIMUTH_MESSAGE_ID,new AzimuthData(AzimuthSourceEnum.LOCATION,azimuth));
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

}

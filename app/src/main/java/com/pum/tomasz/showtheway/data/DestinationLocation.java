package com.pum.tomasz.showtheway.data;

/**
 * Created by tomasz on 03.10.2015.
 */
public class DestinationLocation {

    private String name;
    private float latitude;
    private float longitude;


    public DestinationLocation(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DestinationLocation(float latitude, float longitude) {
        this("",latitude,longitude);
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }
}

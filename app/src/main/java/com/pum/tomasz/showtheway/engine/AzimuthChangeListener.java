package com.pum.tomasz.showtheway.engine;

/**
 * Created by tomasz on 03.10.2015.
 */
public interface AzimuthChangeListener {

    /// Interface will be used for many Azimuth change sources
    /// Make this method synchronized in implementation
    void onAzimuthChange(AzimuthData azimuthData);

}

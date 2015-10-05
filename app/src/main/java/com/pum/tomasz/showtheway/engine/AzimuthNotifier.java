package com.pum.tomasz.showtheway.engine;

/**
 * Created by tomasz on 05.10.2015.
 */
public abstract class AzimuthNotifier {
    private AzimuthChangeListener azimuthChangeListener;

    public AzimuthChangeListener getAzimuthChangeListener() {
        return azimuthChangeListener;
    }

    public void setAzimuthChangeListener(AzimuthChangeListener azimuthChangeListener) {
        this.azimuthChangeListener = azimuthChangeListener;
    }

}

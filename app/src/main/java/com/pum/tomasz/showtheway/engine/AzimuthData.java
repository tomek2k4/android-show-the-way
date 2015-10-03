package com.pum.tomasz.showtheway.engine;

/**
 * Created by tomasz on 03.10.2015.
 */
public class AzimuthData {

    // Source fo azimuth change
    private AzimuthSourceEnum azimuthSourceEnum;

    // azimuth value with deegrees
    private float azimuth;

    public AzimuthData(AzimuthSourceEnum azimuthSourceEnum,float azimuth) {
        this.azimuthSourceEnum = azimuthSourceEnum;
        this.azimuth = azimuth;
    }

    public AzimuthSourceEnum getAzimuthSourceEnum() {
        return azimuthSourceEnum;
    }

    public float getAzimuth() {
        return azimuth;
    }

}

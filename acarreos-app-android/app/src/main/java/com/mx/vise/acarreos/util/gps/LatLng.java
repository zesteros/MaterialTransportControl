package com.mx.vise.acarreos.util.gps;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.util.gps
 * Creado por Angelo el viernes 25 de enero del 2019 a las 04:16 PM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class LatLng implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5395089629335897375L;
    public BigDecimal latitude;
    public BigDecimal longitude;

    public BigDecimal getLatitude() {
        return latitude;
    }

    public LatLng setLatitude(BigDecimal latitude) {
        this.latitude = latitude;return this;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public LatLng setLongitude(BigDecimal longitude) {
        this.longitude = longitude;return this;
    }
}

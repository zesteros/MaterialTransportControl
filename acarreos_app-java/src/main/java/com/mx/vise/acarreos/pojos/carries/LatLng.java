package com.mx.vise.acarreos.pojos.carries;

import java.io.Serializable;
import java.math.BigDecimal;

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
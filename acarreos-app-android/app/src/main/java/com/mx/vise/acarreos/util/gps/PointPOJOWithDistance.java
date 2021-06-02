package com.mx.vise.acarreos.util.gps;

import com.mx.vise.acarreos.pojos.PointPOJO;

class PointPOJOWithDistance {
    private PointPOJO point;
    private  double distance;

    public PointPOJOWithDistance(PointPOJO bank, double distance) {
        this.point = bank;
        this.distance = distance;
    }

    public PointPOJO getPoint() {
        return point;
    }

    public void setPoint(PointPOJO point) {
        this.point = point;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

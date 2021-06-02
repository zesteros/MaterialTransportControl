package com.mx.vise.acarreos.util.gps;

import android.content.Context;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;

import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.singleton.SingletonGlobal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MapCalculator {

    private static final String TAG = "VISE";
    private long actualTime;
    private float actualDistance;

    public MapCalculator() {
        actualTime = System.currentTimeMillis();
    }

    /*Calculates the distance between two locations in KM */
    public static double getDistanceBetweenTwoCoordinates(LatLng latLng1, LatLng latLng2) {
        float[] results = new float[1];
        if (latLng1 != null && latLng2 != null) {
            if(latLng1.latitude != null && latLng1.longitude != null &&
                    latLng2.latitude != null && latLng2.longitude != null)
            Location.distanceBetween(
                    latLng1.latitude.doubleValue(),
                    latLng1.longitude.doubleValue(),
                    latLng2.latitude.doubleValue(),
                    latLng2.longitude.doubleValue(),
                    results
            );
        }
        return results[0];
    }

    public static float getAngleBetweenTwoCoordinates(LatLng latLng1, LatLng latLng2) {

        double lat1 = latLng1.latitude.doubleValue() * Math.PI / 180;
        double lng1 = latLng1.longitude.doubleValue() * Math.PI / 180;
        double lat2 = latLng2.latitude.doubleValue() * Math.PI / 180;
        double lng2 = latLng2.longitude.doubleValue() * Math.PI / 180;

        double distanceBetweenLng = (lng2 - lng1);

        double y = Math.sin(distanceBetweenLng) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(distanceBetweenLng);

        double angle = Math.atan2(y, x);

        angle = Math.toDegrees(angle);
        angle = (angle + 360) % 360;

        return (float) angle;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * @return si el punto esta activado para regalais (tiene que ser banco)
     */
    public static boolean isLocatedInThisPoints(Context context, ArrayList<PointPOJO> points, final OnNearbyPointFoundListener foundListener) {

        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            /*
             * Obtiene la localización actual
             * */
            BigDecimal latitude = SingletonGlobal.getInstance().getGpsTracker().latitude;
            BigDecimal longitude = SingletonGlobal.getInstance().getGpsTracker().longitude;

            LatLng actualLocationLatLng = new LatLng();
            actualLocationLatLng.setLatitude(latitude);
            actualLocationLatLng.setLongitude(longitude);

            /*Saca los bancos actuales sincronizados en esta obra*/
            final ArrayList<PointPOJOWithDistance> pointNearby = new ArrayList<>();
            /*
             * Compara cada banco con los puntos actuales
             * */
            for (final PointPOJO point : points) {

                LatLng bankLocationLatLng = new LatLng();
                bankLocationLatLng.setLatitude(BigDecimal.valueOf(point.getLatitud()));
                bankLocationLatLng.setLongitude(BigDecimal.valueOf(point.getLongitud()));

                final double distance = MapCalculator.getDistanceBetweenTwoCoordinates(actualLocationLatLng, bankLocationLatLng);
                if (distance <= point.getRadio()) {
                    pointNearby.add(new PointPOJOWithDistance(point, distance));
                }
            }
            if (pointNearby.isEmpty()) {
                foundListener.onNearbyLocationNotDetected();
                return false;
            }

            Collections.sort(pointNearby, new Comparator<PointPOJOWithDistance>() {
                @Override
                public int compare(PointPOJOWithDistance p1, PointPOJOWithDistance p2) {
                    return (int) (p1.getDistance() - p2.getDistance()); // Ascending
                }
            });


            if (context instanceof AppCompatActivity)
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PointPOJO pointPOJO = pointNearby.get(0).getPoint();
                        foundListener.onNearbyLocationDetected(pointPOJO, pointNearby.get(0).getDistance());

                    }
                });

            return true;
        }
        foundListener.onNearbyLocationNotDetected();
        return false;
    }

}

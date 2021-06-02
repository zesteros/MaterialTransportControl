package com.mx.vise.acarreos.util.gps;

import com.mx.vise.acarreos.pojos.PointPOJO;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el martes 12 de febrero del 2019 a las 15:31
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public interface OnNearbyPointFoundListener {
    void onNearbyLocationDetected(PointPOJO pointPOJO, double distance);
    void onNearbyLocationNotDetected();
}

package com.mx.vise.acarreos.util.gps;

/**
 * Created by ernestochavez on 23/02/18.
 */

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.math.BigDecimal;

public final class GPSTracker implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location; // location
    public BigDecimal latitude; // latitude
    public BigDecimal longitude; // longitude

    private BigDecimal longitudGps;
    private BigDecimal latitudGps;
    private BigDecimal longitudNetwork;
    private BigDecimal latitudNetwork;
    private BigDecimal longitudBest;
    private BigDecimal latitudBest;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 500; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    protected FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    private LocationCallback locationCallback;


    /**
     * Function to get the user's current location
     *
     * @return
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);



            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            //Log.v("VISE", "GPS ENABLED=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //Log.v("VISE", "NETWORK ENABLED=" + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;
                    if (ContextCompat
                            .checkSelfPermission(
                                    mContext,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat
                            .checkSelfPermission(
                                    mContext,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);




                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            longitudNetwork = new BigDecimal(location.getLongitude());
                            latitudNetwork = new BigDecimal(location.getLatitude());
                        }

                    } else {
                       // Log.d("VISE", "no tienes permiso para leer el gps");
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        longitudGps = new BigDecimal(location.getLongitude());
                        latitudGps = new BigDecimal(location.getLatitude());
                      //  Log.d("gpstra", "Las logitudes las sacaron del GPS_PROVIDER: " + location.getLatitude() + ":" + location.getLongitude());
                    }


                }


                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String provider = locationManager.getBestProvider(criteria, true);
                if (provider != null) {
                    locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    location = locationManager
                            .getLastKnownLocation(provider);
                    if (location != null) {
                        latitudBest = new BigDecimal(location.getLatitude());
                        longitudBest = new BigDecimal(location.getLongitude());
                    }

                }

            }
        } catch (Exception e) {
         //   Log.e("GPST", "Ocurrio un error al leer la ubicacion", e);

        } finally {
            if (latitudNetwork != null && longitudNetwork != null && longitudNetwork.compareTo(BigDecimal.ZERO) != 0 && latitudNetwork.compareTo(BigDecimal.ZERO) != 0) {
                latitude = latitudNetwork;
                longitude = longitudNetwork;
            } else if (latitudBest != null && longitudBest != null && longitudBest.compareTo(BigDecimal.ZERO) != 0 && latitudBest.compareTo(BigDecimal.ZERO) != 0) {
                latitude = latitudBest;
                longitude = longitudBest;
            } else if (latitudGps != null && longitudGps != null && longitudGps.compareTo(BigDecimal.ZERO) != 0 && latitudGps.compareTo(BigDecimal.ZERO) != 0) {
                latitude = latitudGps;
                longitude = longitudGps;
            }
        }
        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public BigDecimal getLatitude() {
        if (location != null) {
            latitude = new BigDecimal(location.getLatitude());
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public BigDecimal getLongitude() {
        if (location != null) {
            longitude = new BigDecimal(location.getLongitude());
        }
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS configuración");

        // Setting Dialog Message
        alertDialog.setMessage("GPS no está habilitado ¿Quieres ir al menú de configuración?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        latitude = BigDecimal.valueOf(location.getLatitude());
        longitude = BigDecimal.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public Context getmContext() {
        return mContext;
    }

    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }

    public boolean isNetworkEnabled() {
        return isNetworkEnabled;
    }


    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public void setCanGetLocation(boolean canGetLocation) {
        this.canGetLocation = canGetLocation;
    }


    public static long getMinDistanceChangeForUpdates() {
        return MIN_DISTANCE_CHANGE_FOR_UPDATES;
    }

    public static long getMinTimeBwUpdates() {
        return MIN_TIME_BW_UPDATES;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}

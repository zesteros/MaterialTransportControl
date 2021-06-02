package com.mx.vise.acarreos.singleton;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.singleton
 * Creado por Angelo el martes 08 de enero del 2019 a las 12:22 PM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class Singleton {
    private static Singleton instance;
    private String imei;
    private boolean isNfcActivated;

    public static synchronized Singleton getInstance(){
        return instance == null ? instance = new Singleton() : instance;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isNfcActivated() {
        return isNfcActivated;
    }

    public void setNfcActivated(boolean nfcActivated) {
        isNfcActivated = nfcActivated;
    }
}

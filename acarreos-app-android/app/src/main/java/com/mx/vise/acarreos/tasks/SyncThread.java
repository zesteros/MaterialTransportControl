package com.mx.vise.acarreos.tasks;

import android.content.Context;
import android.util.Log;

import com.mx.vise.acarreos.singleton.Singleton;
import com.mx.vise.acarreos.singleton.SingletonGlobal;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el miércoles 20 de marzo del 2019 a las 11:38
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class SyncThread extends Thread {
    private static final long SYNC_INTERVAL_TIME = 2000;
    private boolean keepRunning = true;
    private Context mContext;
public static String TAG = "VISE";
    public SyncThread(Context context){
        this.mContext = context;
    }

    @Override
    public void run() {
        String imei = "7475707574616d61";
        while(keepRunning){
            new SyncData(mContext, imei).sync(new SyncStatus());
            try {
                Thread.sleep(SYNC_INTERVAL_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }
}

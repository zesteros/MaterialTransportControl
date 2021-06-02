package com.mx.vise.acarreos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.tasks
 * Creado por Angelo el lunes 07 de enero del 2019 a las 09:30 AM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class WaitTask extends AsyncTask<Void, Void, Void> {

    private final Context mContext;
    private final OnOperationRunning mOnOperationRunning;
    private String mTitle;
    private String mMessage;
    private ProgressDialog mProgressDialog;

    public WaitTask(Context context, OnOperationRunning onOperationRunning){
        this.mContext = context;
        this.mOnOperationRunning = onOperationRunning;
    }
    public WaitTask(Context context, OnOperationRunning onOperationRunning, String title, String message){
        this.mContext = context;
        this.mOnOperationRunning = onOperationRunning;
        this.mTitle = title;
        this.mMessage = message;
    }


    @Override
    protected void onPreExecute() {
        if(mTitle != null & mMessage != null)
            mProgressDialog = ProgressDialog.show(mContext, mTitle, mMessage,true,false);
        else
            mProgressDialog = ProgressDialog.show(mContext, "Espere", "Cargando...",true,false);
        mOnOperationRunning.onOperationStart();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mOnOperationRunning.onOperationRun();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressDialog.dismiss();
        mOnOperationRunning.onOperationFinish();
    }

}

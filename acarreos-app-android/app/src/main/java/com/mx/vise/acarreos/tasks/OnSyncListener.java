package com.mx.vise.acarreos.tasks;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el martes 02 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public interface OnSyncListener {
    void onSyncSuccessful();
    void onSyncFailed(SyncStatus syncStatus);
}

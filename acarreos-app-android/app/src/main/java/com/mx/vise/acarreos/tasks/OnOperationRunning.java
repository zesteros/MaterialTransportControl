package com.mx.vise.acarreos.tasks;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.tasks
 * Creado por Angelo el lunes 07 de enero del 2019 a las 09:31 AM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public interface OnOperationRunning {
    void onOperationStart();
    void onOperationRun();
    void onOperationFinish();
}

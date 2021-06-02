package com.mx.vise.acarreos.util;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el viernes 22 de febrero del 2019 a las 09:44
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class ModuleType {
    private int idModule;
    private int type;

    public ModuleType(int idModule, int type) {
        this.idModule = idModule;
        this.type = type;
    }

    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

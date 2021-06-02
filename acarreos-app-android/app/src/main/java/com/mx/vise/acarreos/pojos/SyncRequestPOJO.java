package com.mx.vise.acarreos.pojos;

import com.mx.vise.nfc.pojos.KeyPOJO;

import java.io.Serializable;
import java.util.List;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 11 de febrero del 2019
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 *
 * Clase a enviar al servidor se envian lso puntos ya registrados, los materiales y los materiales por
 * punto. Para que nos regrese los nuevos.
 */
public class SyncRequestPOJO implements Serializable {

    private static final long serialVersionUID = 9009508709914893739L;

    private List<PointPOJO> pointsAlreadyRegistered;
    private List<MaterialsPOJO> materialsAlreadyRegistered;
    private List<MaterialsByPointPOJO> materialsByPointRegistered;
    private List<DistancePOJO> distancesRegistered;
    private List<KeyPOJO> keysRegistered;
    private String building;

    private SyncPOJO syncData;

    public List<PointPOJO> getPointsAlreadyRegistered() {
        return pointsAlreadyRegistered;
    }
    public void setPointsAlreadyRegistered(List<PointPOJO> pointsAlreadyRegistered) {
        this.pointsAlreadyRegistered = pointsAlreadyRegistered;
    }
    public List<MaterialsPOJO> getMaterialsAlreadyRegistered() {
        return materialsAlreadyRegistered;
    }
    public void setMaterialsAlreadyRegistered(List<MaterialsPOJO> materialsAlreadyRegistered) {
        this.materialsAlreadyRegistered = materialsAlreadyRegistered;
    }
    public List<MaterialsByPointPOJO> getMaterialsByPointRegistered() {
        return materialsByPointRegistered;
    }
    public void setMaterialsByPointRegistered(List<MaterialsByPointPOJO> materialsByPointRegistered) {
        this.materialsByPointRegistered = materialsByPointRegistered;
    }
    public List<DistancePOJO> getDistancesRegistered() {
        return distancesRegistered;
    }
    public void setDistancesRegistered(List<DistancePOJO> distancesRegistered ) {
        this.distancesRegistered = distancesRegistered;
    }

    public SyncPOJO getSyncData() {
        return syncData;
    }

    public void setSyncData(SyncPOJO syncPOJO) {
        this.syncData = syncPOJO;
    }
    public List<KeyPOJO> getKeysRegistered() {
        return keysRegistered;
    }
    public void setKeysRegistered(List<KeyPOJO> keysRegistered) {
        this.keysRegistered = keysRegistered;
    }
    public String getBuilding() {
        return building;
    }
    public void setBuilding(String building) {
        this.building = building;
    }

}
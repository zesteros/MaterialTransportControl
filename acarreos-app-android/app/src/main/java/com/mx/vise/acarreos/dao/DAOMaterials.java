package com.mx.vise.acarreos.dao;

import android.content.Context;
import android.util.Log;

import com.mx.vise.acarreos.App;
import com.mx.vise.acarreos.dao.entities.Materials;
import com.mx.vise.acarreos.dao.entities.MaterialsByPoint;
import com.mx.vise.acarreos.dao.entities.MaterialsByPointDao;
import com.mx.vise.acarreos.dao.entities.MaterialsDao;
import com.mx.vise.acarreos.pojos.MaterialsByPointPOJO;
import com.mx.vise.acarreos.pojos.MaterialsPOJO;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.tasks.Progress;
import com.mx.vise.acarreos.tasks.SyncData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 25 de febrero del 2019 a las 18:56
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class DAOMaterials {

    private static final String TAG = "VISE";

    public static ArrayList<MaterialsPOJO> getAvailableMaterials(Context context, Integer idPoint) {

        ArrayList<MaterialsPOJO> materials = new ArrayList<>();

        MaterialsDao materialsDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsDao();
        MaterialsByPointDao materialsByPointDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsByPointDao();


        List<Long> bankIds = new ArrayList<>();

        if(idPoint == null) {

            int pointType = SingletonGlobal.getInstance().getActualPointType();
            /*
             *
             * Se sacan los bancos de la obra
             *
             * */
            ArrayList<PointPOJO> banks = DAOPoints.getAllPoints(context,
                    SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId(),
                    pointType);
            /*
             * Se extrae su id
             * */
            for (PointPOJO bank : banks)
                bankIds.add(Long.valueOf(bank.getIdPuntoServer()));
        } else {
            bankIds.add(Long.valueOf(idPoint));
        }

        /*
         * Se sacan los materiales por punto
         * */
        List<MaterialsByPoint> materialsByPoints = materialsByPointDao
                .queryBuilder()
                .where(
                        MaterialsByPointDao.Properties.IdPointServer.in(bankIds),
                        MaterialsByPointDao.Properties.EstatusServer.eq("A")
                )
                .list();

        for (MaterialsByPoint materialByPoint : materialsByPoints) {

            Materials materialsEntity = materialsDao
                    .queryBuilder()
                    .where(
                            MaterialsDao.Properties.MaterialIdServer.eq(materialByPoint.getIdMaterialServer()),
                            MaterialsDao.Properties.Building.eq(
                                    SingletonGlobal
                                            .getInstance()
                                            .getSession()
                                            .getAssignedBuilding()
                                            .getBuildingId()
                            ),
                            MaterialsDao.Properties.EstatusServer.eq("A")
                    )
                    .limit(1)
                    .unique();
            MaterialsPOJO material = new MaterialsPOJO();
            try {
                material.setIdMaterialServer(materialsEntity.getMaterialIdServer());

                material.setIdMaterialNavision(materialsEntity.getIdMaterialNavision());
                material.setBuilding(materialsEntity.getBuilding());
                material.setAcronym(materialsEntity.getAcronym());
                material.setAddUser(materialsEntity.getAddUser());
                material.setAddDate(materialsEntity.getAddDate());
                material.setUpdDate(materialsEntity.getUpdDate());
                material.setStatusServer(materialsEntity.getEstatusServer());
                material.setDescription(materialsEntity.getDescription());
                material.setUnitOfMeasure(materialsEntity.getUnitOfMeasure());

                materials.add(material);
            } catch (Exception e) {

            }
        }

        Map<Long, MaterialsPOJO> map = new HashMap<>();

        for (MaterialsPOJO materialsPOJO : materials) {
            if (!map.containsKey(materialsPOJO.getIdMaterialServer())) {
                map.put(materialsPOJO.getIdMaterialServer(), materialsPOJO);
            }
        }

        return new ArrayList<>(map.values());


    }


    public static List<MaterialsPOJO> getActualMaterials(Context context, SyncData syncData) {

        List<MaterialsPOJO> materials = new ArrayList<>();

        String building = SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId();

        MaterialsDao materialsDao = ((App) context.getApplicationContext())
                .getDaoSession()
                .getMaterialsDao();
        List<Materials> materialsList = materialsDao
                .queryBuilder()
                .where(MaterialsDao.Properties.Building.eq(building))
                .list();

        int totalElements = materialsList.size();
        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando materiales..."));
        int i = 0;
        for (Materials materialEntity : materialsList) {
            MaterialsPOJO materialPojo = new MaterialsPOJO();
            materialPojo.setIdMaterialServer(materialEntity.getMaterialIdServer());
            materialPojo.setIdMaterialNavision(materialEntity.getIdMaterialNavision());
            materialPojo.setBuilding(materialEntity.getBuilding());
            materialPojo.setAcronym(materialEntity.getAcronym());
            materialPojo.setAddUser(materialEntity.getAddUser());
            materialPojo.setAddDate(materialEntity.getAddDate());
            materialPojo.setUpdDate(materialEntity.getUpdDate());
            materialPojo.setStatusServer(materialEntity.getEstatusServer());
            materials.add(materialPojo);
            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Procesando materiales..."));
            i++;
        }
        Map<Long, MaterialsPOJO> map = new HashMap<>();


        for (MaterialsPOJO materialsPOJO : materials) {
            if (!map.containsKey(materialsPOJO.getIdMaterialServer())) {
                map.put(materialsPOJO.getIdMaterialServer(), materialsPOJO);
            }
        }

        return new ArrayList<>(map.values());
    }

    public static List<MaterialsByPointPOJO> getActualMaterialsByPoint(Context context, SyncData syncData) {

        List<MaterialsByPointPOJO> materialsByPointsPojo = new ArrayList<>();

        String building = SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId();

        MaterialsDao materialsDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsDao();

        MaterialsByPointDao materialsByPointDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsByPointDao();

        /*
         * Se extraem los materiales de la obra dados de alta y sus ids
         * */
        List<Materials> materialsList = materialsDao
                .queryBuilder()
                .where(MaterialsDao.Properties.Building.eq(building))
                .list();

        List<Long> materialsIds = new ArrayList<>();

        for (Materials materials : materialsList)
            materialsIds.add(materials.getMaterialIdServer());

        /*
         * Forma la lista de materiales por punto a partir de los puntos y
         * materiales disponibles en la obra
         * */

        List<MaterialsByPoint> materialsByPointsList = materialsByPointDao
                .queryBuilder()
                .where(
                        MaterialsByPointDao.Properties.IdMaterialServer.in(materialsIds),
                        MaterialsByPointDao.Properties.IdPointServer.in(DAOPoints.getAvailableBanksByBuilding(context))
                )
                .list();

        int totalElements = materialsList.size();
        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando materiales..."));
        int i = 0;

        for (MaterialsByPoint materialsByPointEntity : materialsByPointsList) {
            MaterialsByPointPOJO materialsByPointPojo = new MaterialsByPointPOJO();
            materialsByPointPojo.setIdMaterialByPointServer(materialsByPointEntity.getIdMaterialByPointServer());
            materialsByPointPojo.setIdMaterialServer(materialsByPointEntity.getIdMaterialServer());
            materialsByPointPojo.setIdPointServer(materialsByPointEntity.getIdPointServer());
            materialsByPointPojo.setAddUser(materialsByPointEntity.getAddUser());
            materialsByPointPojo.setAddDate(materialsByPointEntity.getAddDate());
            materialsByPointPojo.setUpdDate(materialsByPointEntity.getUpdDate());
            materialsByPointPojo.setStatusServer(materialsByPointEntity.getEstatusServer());
            materialsByPointsPojo.add(materialsByPointPojo);
            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Procesando materiales..."));
            i++;
        }

        Map<Long, MaterialsByPointPOJO> map = new HashMap<>();


        for (MaterialsByPointPOJO materialsByPointPOJO : materialsByPointsPojo) {
            if (!map.containsKey(materialsByPointPOJO.getIdMaterialByPointServer())) {
                map.put(materialsByPointPOJO.getIdMaterialByPointServer(), materialsByPointPOJO);
            }
        }

        return new ArrayList<>(map.values());
    }

    public static void addPendingMaterials(ArrayList<MaterialsPOJO> newMaterials, Context context, SyncData syncData) {
        int total = newMaterials.size();

        MaterialsDao materialsDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsDao();


        syncData.publishProgress(new Progress(0, 0, total, "Descargando puntos..."));
        int i = 0;

        for (MaterialsPOJO materialPOJO : newMaterials) {

            Materials materialsEntity = new Materials();

            materialsEntity.setMaterialIdServer(materialPOJO.getIdMaterialServer());
            materialsEntity.setIdMaterialNavision(materialPOJO.getIdMaterialNavision());
            materialsEntity.setBuilding(materialPOJO.getBuilding());
            materialsEntity.setAcronym(materialPOJO.getAcronym());
            materialsEntity.setAddUser(materialPOJO.getAddUser());
            materialsEntity.setAddDate(materialPOJO.getAddDate());
            materialsEntity.setUpdDate(materialPOJO.getUpdDate());
            materialsEntity.setEstatusServer(materialPOJO.getStatusServer());
            materialsEntity.setUploadStatus("B");
            materialsEntity.setDownloadDate(new Date());
            materialsEntity.setDescription(materialPOJO.getDescription());
            materialsEntity.setUnitOfMeasure(materialPOJO.getUnitOfMeasure());

            if (materialsDao.queryBuilder()
                    .where(MaterialsDao.Properties.MaterialIdServer
                            .eq(materialPOJO.getIdMaterialServer())).list().isEmpty())
                materialsDao.insert(materialsEntity);
            else {
                Log.i(TAG, "addPendingDistances: material repetido:" +
                        materialsEntity.getIdMaterialNavision() +
                        "," + materialsEntity.getMaterialIdServer());
            }


            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Descargando materiales..."));

            i++;

        }
    }

    public static void updateMaterials(ArrayList<MaterialsPOJO> updatedMaterials, Context context, SyncData syncData) {

        int total = updatedMaterials.size();

        MaterialsDao materialsDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsDao();


        syncData.publishProgress(new Progress(0, 0, total, "Actualizando materiales..."));

        int i = 0;

        for (MaterialsPOJO materialPOJO : updatedMaterials) {
            // New value for one column
            Materials materialsEntity = new Materials();

            materialsEntity.setMaterialIdServer(materialPOJO.getIdMaterialServer());
            materialsEntity.setIdMaterialNavision(materialPOJO.getIdMaterialNavision());
            materialsEntity.setBuilding(materialPOJO.getBuilding());
            materialsEntity.setAcronym(materialPOJO.getAcronym());
            materialsEntity.setAddUser(materialPOJO.getAddUser());
            materialsEntity.setAddDate(materialPOJO.getAddDate());
            materialsEntity.setUpdDate(materialPOJO.getUpdDate());
            materialsEntity.setEstatusServer(materialPOJO.getStatusServer());
            materialsEntity.setUploadStatus("B");
            materialsEntity.setDownloadDate(new Date());
            materialsEntity.setDescription(materialPOJO.getDescription());
            materialsEntity.setUnitOfMeasure(materialPOJO.getUnitOfMeasure());

            Materials materialsToUpdate = materialsDao.queryBuilder()
                    .where(MaterialsDao.Properties.MaterialIdServer
                            .eq(materialPOJO.getIdMaterialServer())).limit(1).unique();
            materialsEntity.setMaterialIdLocal(materialsToUpdate.getMaterialIdLocal());
            materialsDao.update(materialsEntity);

            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Actualizando materiales..."));

            i++;
        }

    }

    public static void addPendingMaterialsByPoint(ArrayList<MaterialsByPointPOJO> newMaterials, Context context, SyncData syncData) {
        int total = newMaterials.size();

        MaterialsByPointDao materialsByPointDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsByPointDao();


        syncData.publishProgress(new Progress(0, 0, total, "Descargando materiales por punto..."));
        int i = 0;

        for (MaterialsByPointPOJO materialPOJO : newMaterials) {

            MaterialsByPoint materialsByPointEntity = new MaterialsByPoint();
            materialsByPointEntity.setIdMaterialByPointServer(materialPOJO.getIdMaterialByPointServer());
            materialsByPointEntity.setIdMaterialServer(materialPOJO.getIdMaterialServer());
            materialsByPointEntity.setIdPointServer(materialPOJO.getIdPointServer());
            materialsByPointEntity.setAddUser(materialPOJO.getAddUser());
            materialsByPointEntity.setAddDate(materialPOJO.getAddDate());
            materialsByPointEntity.setUpdDate(materialPOJO.getUpdDate());
            materialsByPointEntity.setEstatusServer(materialPOJO.getStatusServer());

            if (materialsByPointDao.queryBuilder()
                    .where(MaterialsByPointDao.Properties.IdMaterialByPointServer
                            .eq(materialPOJO.getIdMaterialByPointServer())).list().isEmpty())
                materialsByPointDao.insert(materialsByPointEntity);

            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Descargando materiales..."));

            i++;

        }

    }

    public static void updateMaterialsByPoint(ArrayList<MaterialsByPointPOJO> updatedMaterials, Context context, SyncData syncData) {
        int total = updatedMaterials.size();

        MaterialsByPointDao materialsDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsByPointDao();


        syncData.publishProgress(new Progress(0, 0, total, "Actualizando materiales por punto..."));

        int i = 0;

        for (MaterialsByPointPOJO materialByPointPOJO : updatedMaterials) {
            // New value for one column


            MaterialsByPoint materialsByPointEntity = new MaterialsByPoint();
            materialsByPointEntity.setIdMaterialByPointServer(materialByPointPOJO.getIdMaterialByPointServer());
            materialsByPointEntity.setIdMaterialServer(materialByPointPOJO.getIdMaterialServer());
            materialsByPointEntity.setIdPointServer(materialByPointPOJO.getIdPointServer());
            materialsByPointEntity.setAddUser(materialByPointPOJO.getAddUser());
            materialsByPointEntity.setAddDate(materialByPointPOJO.getAddDate());
            materialsByPointEntity.setUpdDate(materialByPointPOJO.getUpdDate());
            materialsByPointEntity.setEstatusServer(materialByPointPOJO.getStatusServer());

            MaterialsByPoint materialsByPointToUpdate = materialsDao
                    .queryBuilder()
                    .where(
                            MaterialsByPointDao.Properties.IdMaterialByPointServer
                                    .eq(materialByPointPOJO.getIdMaterialByPointServer()))
                    .limit(1).unique();
            materialsByPointEntity.setIdMaterialByPointLocal(materialsByPointToUpdate.getIdMaterialByPointLocal());
            materialsDao.update(materialsByPointEntity);

            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Actualizando materiales por punto..."));

            i++;
        }

    }

    public static MaterialsPOJO getMaterialByID(Context context, String materialSelectedId) {


        MaterialsDao materialsDao = ((App) context.getApplicationContext()).getDaoSession().getMaterialsDao();

        Materials materialEntity = materialsDao
                .queryBuilder()
                .where(
                        MaterialsDao.Properties.MaterialIdServer
                                .eq(Integer.parseInt(materialSelectedId)))
                .limit(1)
                .unique();
        MaterialsPOJO materialPojo = new MaterialsPOJO();
        materialPojo.setIdMaterialServer(materialEntity.getMaterialIdServer());
        materialPojo.setIdMaterialNavision(materialEntity.getIdMaterialNavision());
        materialPojo.setDescription(materialEntity.getDescription());
        materialPojo.setBuilding(materialEntity.getBuilding());
        materialPojo.setAcronym(materialEntity.getAcronym());
        materialPojo.setAddUser(materialEntity.getAddUser());
        materialPojo.setAddDate(materialEntity.getAddDate());
        materialPojo.setUpdDate(materialPojo.getUpdDate());
        materialPojo.setStatusServer(materialEntity.getEstatusServer());

        return materialPojo;
    }

    public static List<MaterialsPOJO> getMaterialsByPointType(Context context, String building, int pointType) {
        return null;
    }
}

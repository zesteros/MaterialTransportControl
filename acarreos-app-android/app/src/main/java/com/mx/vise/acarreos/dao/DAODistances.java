package com.mx.vise.acarreos.dao;

import android.content.Context;
import android.util.Log;

import com.mx.vise.acarreos.App;
import com.mx.vise.acarreos.dao.entities.DistancesByPoint;
import com.mx.vise.acarreos.dao.entities.DistancesByPointDao;
import com.mx.vise.acarreos.pojos.DistancePOJO;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.tasks.Progress;
import com.mx.vise.acarreos.tasks.SyncData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mx.vise.acarreos.tasks.LocationThread.BANK_AND_WASTE_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.BANK_TYPE;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 25 de febrero del 2019 a las 18:52
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class DAODistances {


    private static final String TAG = "VISE";

    public static List<DistancePOJO> getActualDistances(Context context, SyncData syncData) {

        List<DistancesByPoint> distancesEntity =
                ((App) context.getApplicationContext())
                        .getDaoSession()
                        .getDistancesByPointDao()
                        .queryBuilder()
                        .where(
                                DistancesByPointDao.Properties.IdPointServer.in(
                                        DAOPoints.getAvailableBanksByBuilding(context)
                                )
                        )
                        .list();

        List<DistancePOJO> distancesPojos = new ArrayList<>();

        int totalElements = distancesEntity.size();
        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando distancias..."));
        int i = 0;
        for (DistancesByPoint distancesByPointEntity : distancesEntity) {
            DistancePOJO distancesByPointPojo = new DistancePOJO();
            distancesByPointPojo.setIdDistanceServer(distancesByPointEntity.getIdDistanceServer());
            distancesByPointPojo.setIdPoint(distancesByPointEntity.getIdPointServer());
            distancesByPointPojo.setDistance(distancesByPointEntity.getDistance());
            distancesByPointPojo.setAddUser(distancesByPointEntity.getAddUser());
            distancesByPointPojo.setAddDate(distancesByPointEntity.getAddDate());
            distancesByPointPojo.setUpdDate(distancesByPointEntity.getUpdDate());
            distancesByPointPojo.setEstatus(distancesByPointEntity.getEstatusServer());

            distancesPojos.add(distancesByPointPojo);

            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Procesando distancias..."));
            i++;
        }
        return distancesPojos;
    }


    public static void addPendingDistances(ArrayList<DistancePOJO> newDistances, Context context, SyncData syncData) {
        int total = newDistances.size();

        DistancesByPointDao distancesByPointDao = ((App) context.getApplicationContext())
                .getDaoSession().getDistancesByPointDao();

        syncData.publishProgress(new Progress(0, 0, total, "Agregando distancias pendientes..."));
        int i = 0;

        for (DistancePOJO distancePojo : newDistances) {

            DistancesByPoint distancesByPointEntity = new DistancesByPoint();
            distancesByPointEntity.setAddDate(distancePojo.getAddDate());
            distancesByPointEntity.setAddUser(distancePojo.getAddUser());
            distancesByPointEntity.setDistance(distancePojo.getDistance());
            distancesByPointEntity.setEstatusServer(distancePojo.getEstatus());
            distancesByPointEntity.setIdPointServer(distancePojo.getIdPoint());
            distancesByPointEntity.setIdDistanceServer(distancePojo.getIdDistanceServer());
            distancesByPointEntity.setUpdDate(distancePojo.getUpdDate());
            distancesByPointEntity.setUploadStatus("B");
            distancesByPointEntity.setUploadDate(new Date());

            if (distancesByPointDao
                    .queryBuilder()
                    .where(DistancesByPointDao.Properties.IdDistanceServer.eq(distancePojo.getIdDistanceServer()))
                    .list().isEmpty())
                distancesByPointDao.insert(distancesByPointEntity);
            else {
            }

            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Agregando distancias..."));

            i++;
        }
    }

    public static void updateDistances(ArrayList<DistancePOJO> updatedDistances, Context context, SyncData syncData) {
        int total = updatedDistances.size();


        DistancesByPointDao distancesByPointDao = ((App) context.getApplicationContext())
                .getDaoSession().getDistancesByPointDao();

        syncData.publishProgress(new Progress(0, 0, total, "Actualizando distancias..."));

        int i = 0;

        for (DistancePOJO distancePojo : updatedDistances) {


            DistancesByPoint distancesByPointEntity = new DistancesByPoint();

            distancesByPointEntity.setIdDistanceServer(distancePojo.getIdDistanceServer());
            distancesByPointEntity.setIdPointServer(distancePojo.getIdPoint());
            distancesByPointEntity.setDistance(distancePojo.getDistance());
            distancesByPointEntity.setAddUser(distancePojo.getAddUser());
            distancesByPointEntity.setAddDate(distancePojo.getAddDate());
            distancesByPointEntity.setUpdDate(distancePojo.getUpdDate());
            distancesByPointEntity.setEstatusServer(distancePojo.getEstatus());
            distancesByPointEntity.setUploadStatus("B");
            distancesByPointEntity.setDownloadDate(new Date());

            DistancesByPoint distancesByPointToUpdate =
                    distancesByPointDao
                            .queryBuilder()
                            .where(DistancesByPointDao.Properties.IdDistanceServer.eq(distancePojo.getIdDistanceServer()))
                            .limit(1)
                            .unique();

            distancesByPointEntity.setIdDistanceLocal(distancesByPointToUpdate.getIdDistanceLocal());

            distancesByPointDao.update(distancesByPointEntity);

            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Actualizando distancias..."));

            i++;
        }
    }

    /**
     * @param context el contexto
     * @param idPoint el id del banco
     * @return la lista de distancias
     */
    public static ArrayList<DistancePOJO> getAvailableDistances(Context context, Integer idPoint) {
        ArrayList<DistancePOJO> distances = new ArrayList<>();

        DistancesByPointDao distancesByPointDao = ((App) context.getApplicationContext())
                .getDaoSession().getDistancesByPointDao();

        List<Long> pointIds = new ArrayList<>();
        /*
        * si el id del banco es nulo, saca todas las distancias de todos los bancos
        * */
        if (idPoint == null) {

            int pointType = SingletonGlobal.getInstance().getActualPointType();

            ArrayList<PointPOJO> points = DAOPoints.getAllPoints(
                    context,
                    SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId(),
                    pointType);


            if (!points.isEmpty()) {
                for (PointPOJO point : points) {
                    pointIds.add(Long.valueOf(point.getIdPuntoServer()));
                }
            }
        } else {
            /*
            * agrega el banco que se desea
            * */
            pointIds.add(Long.valueOf(idPoint));
        }


        QueryBuilder<DistancesByPoint> qb = distancesByPointDao.queryBuilder();
        qb.where(DistancesByPointDao.Properties.IdPointServer.in(pointIds)
                , DistancesByPointDao.Properties.EstatusServer.eq("A"))
                .orderDesc(DistancesByPointDao.Properties.Distance);

        List<DistancesByPoint> distancesEntity = qb.list();
        for (DistancesByPoint distancesByPointEntity : distancesEntity) {

            DistancePOJO distancePojo = new DistancePOJO();
            distancePojo.setIdDistanceServer(distancesByPointEntity.getIdDistanceServer());
            distancePojo.setIdPoint(distancesByPointEntity.getIdPointServer());
            distancePojo.setDistance(distancesByPointEntity.getDistance());
            distancePojo.setAddUser(distancesByPointEntity.getAddUser());
            distancePojo.setAddDate(distancesByPointEntity.getAddDate());
            distancePojo.setUpdDate(distancesByPointEntity.getUpdDate());
            distancePojo.setEstatus(distancesByPointEntity.getEstatusServer());

            distances.add(distancePojo);
        }

        Map<Long, DistancePOJO> map = new HashMap<>();


        for (DistancePOJO distancePOJO : distances) {
            if (!map.containsKey(distancePOJO.getIdDistanceServer())) {
                map.put(distancePOJO.getIdDistanceServer(), distancePOJO);
            }
        }

        return new ArrayList<>(map.values());
    }

    public static ArrayList<DistancePOJO> getDistancesByPoint(Context context, int idPointOrigin) {

        return getAvailableDistances(context,idPointOrigin);
    }
}

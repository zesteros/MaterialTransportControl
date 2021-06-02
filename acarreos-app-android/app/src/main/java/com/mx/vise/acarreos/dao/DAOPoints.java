package com.mx.vise.acarreos.dao;

import android.content.Context;
import android.util.Log;

import com.mx.vise.acarreos.App;
import com.mx.vise.acarreos.dao.entities.Points;
import com.mx.vise.acarreos.dao.entities.PointsDao;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.tasks.Progress;
import com.mx.vise.acarreos.tasks.SyncData;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mx.vise.acarreos.tasks.LocationThread.ALL_POINT_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.BANK_AND_WASTE_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.BANK_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.WASTE_TYPE;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 25 de febrero del 2019 a las 18:57
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class DAOPoints {

    private static final String TAG = "VISE";

    public static Points convertPojoToEntity(PointPOJO pointPOJO, String uploadStatus, Date uploadDate) {
        Points pointsEntity = new Points();
        pointsEntity.setAddUser(pointPOJO.getAddUser());
        pointsEntity.setBankName(pointPOJO.getNombreBanco());
        pointsEntity.setBuilding(pointPOJO.getObra());
        pointsEntity.setChainage(pointPOJO.getCadenamiento());
        pointsEntity.setEstatusServer(pointPOJO.getEstatus());
        if (pointPOJO.getIdPuntoServer() != null)
            pointsEntity.setIdPointServer(Long.valueOf(pointPOJO.getIdPuntoServer()));
        pointsEntity.setAuthorized(pointPOJO.getAutorizado());
        pointsEntity.setIsBankToo(pointPOJO.getEsBancoYTiro());
        pointsEntity.setLatitude((float) pointPOJO.getLatitud());
        pointsEntity.setLongitude((float) pointPOJO.getLongitud());
        pointsEntity.setPointType(pointPOJO.getTipoPunto());
        pointsEntity.setRadio((float) pointPOJO.getRadio());
        pointsEntity.setRegDate(pointPOJO.getRegDate());
        pointsEntity.setUpdDateServer(pointPOJO.getUpdDate());
        pointsEntity.setUploadDate(uploadDate);
        pointsEntity.setUploadStatus(uploadStatus);
        return pointsEntity;
    }

    public static PointPOJO convertEntityToPojo(Points pointsEntity, boolean isNewPoint) {
        PointPOJO pointPOJO = new PointPOJO();
        pointPOJO.setAddUser(pointsEntity.getAddUser());
        pointPOJO.setCadenamiento(pointsEntity.getChainage());
        pointPOJO.setEsBancoYTiro(pointsEntity.getIsBankToo());
        pointPOJO.setEstatus(pointsEntity.getEstatusServer());
        if (!isNewPoint) {
            pointPOJO.setIdPuntoServer(pointsEntity.getIdPointServer().intValue());
        }
        pointPOJO.setIdPuntoLocal(pointsEntity.getPointId().intValue());
        pointPOJO.setAutorizado(pointsEntity.getAuthorized());
        pointPOJO.setLatitud(pointsEntity.getLatitude());
        pointPOJO.setLongitud(pointsEntity.getLongitude());
        pointPOJO.setNombreBanco(pointsEntity.getBankName());
        pointPOJO.setObra(pointsEntity.getBuilding());
        pointPOJO.setRadio(pointsEntity.getRadio());
        pointPOJO.setRegDate(pointsEntity.getRegDate());
        pointPOJO.setTipoPunto(pointsEntity.getPointType());
        pointPOJO.setUpdDate(pointsEntity.getUpdDateServer());
        return pointPOJO;
    }

    public static boolean addPoint(Context context, PointPOJO point) {
        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();
        return pointsDao.insert(convertPojoToEntity(point, "A", null)) != -1;
    }

    /**
     * @param pendingPoints los puntos pendientes (que estan en el servidor pero no en la terminal)
     * @param context       el contexto
     * @param syncData      el actualizador de ui
     */
    public static void addPendingPoints(ArrayList<PointPOJO> pendingPoints, Context context, SyncData syncData) {

        int total = pendingPoints.size();


        syncData.publishProgress(new Progress(0, 0, total, "Descargando puntos..."));
        int i = 0;

        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();


        for (PointPOJO pointPOJO : pendingPoints) {

            if (pointsDao
                    .queryBuilder()
                    .where(
                            PointsDao.Properties.IdPointServer
                                    .eq(pointPOJO.getIdPuntoServer()))
                    .list().isEmpty())

                pointsDao.insert(convertPojoToEntity(pointPOJO, "B", new Date()));


            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Descargando puntos..."));

            i++;

        }


    }


    public static void updatePoints(ArrayList<PointPOJO> updatedPoints, Context context, SyncData syncData) {

        int total = updatedPoints.size();

        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();
        syncData.publishProgress(new Progress(0, 0, total, "Actualizando puntos..."));

        int i = 0;
        for (PointPOJO point : updatedPoints) {
            Points pointEntityToUpdate = pointsDao
                    .queryBuilder()
                    .where(PointsDao.Properties.IdPointServer.eq(point.getIdPuntoServer()))
                    .limit(1)
                    .unique();
            Points pointEntityConverted = convertPojoToEntity(point, "B", new Date());
            pointEntityConverted.setPointId(pointEntityToUpdate.getPointId());
            pointsDao.update(pointEntityConverted);
            syncData.publishProgress(new Progress(i * 100 / total, i, total, "Actualizando puntos..."));
            i++;
        }

    }

//    /**
//     * @param context el contexto
//     * @param point   el punto
//     * @return si existe ya el punto
//     */
//    public static boolean bankAlreadyExists(Context context, PointPOJO point) {
//
//        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();
//
//
//        Cursor cursor = DAO.getInstance().getDatabase().query(
//                Contract.SCRIPT.TABLE_NAME_POINTS,   // The table to query
//                null,// The array of columns to return (pass null to get all)
//                Contract.SCRIPT.COLUMN_BANK_NAME + "=?",// The columns for the WHERE clause
//                new String[]{point.getNombreBanco()},          // The values for the WHERE clause
//                null,// don't group the rows
//                null,// don't filter by row groups
//                null// The sort order
//        );
//        boolean exists = cursor.getCount() > 0;
//        return exists;
//    }
//
//
//    /**
//     * @return

//
//    public static String getLastPointUpdateDate() {
//
//        Cursor cursor = DAO.getInstance().getDatabase().query(
//                Contract.SCRIPT.TABLE_NAME_POINTS,   // The table to query
//                new String[]{Contract.SCRIPT.COLUMN_UPLOAD_DATE},// The array of columns to return (pass null to get all)
//                null,// The columns for the WHERE clause
//                null,          // The values for the WHERE clause
//                null,// don't group the rows
//                null,// don't filter by row groups
//                Contract.SCRIPT.COLUMN_UPLOAD_DATE + " desc"// The sort order
//        );
//        String lastUpdateDate = null;
//        if (cursor.moveToFirst())
//            lastUpdateDate = cursor.getString(0);
//        DAO.getInstance().close();
//
//
//        return lastUpdateDate;
//    }

//
//    /**
//     * @param context contexto
//     * @param date    la fecha nueva
//     * @param idPoint el punto
//     * @return si se cambio correctamente
//     */
//    public static boolean changePointUploadDate(Context context, Date date, String idPoint) {
//
//
//        // New value for one column
//        ContentValues values = new ContentValues();
//        values.put(Contract.SCRIPT.COLUMN_UPLOAD_DATE, dateFormat.format(date));
//
//        // Which row to update, based on the title
//        String selection = Contract.SCRIPT.COLUMN_ID_POINT + " = ?";
//        String[] selectionArgs = {idPoint};
//
//        int count = DAO.getInstance().getDatabase().update(
//                Contract.SCRIPT.TABLE_NAME_POINTS,
//                values,
//                selection,
//                selectionArgs);
//        boolean success = count > 0;
//
//        /*if (success) {
//            for (int i = 0; i < cubage.getPhotos().length; i++)
//                if(ImageHelper.deleteImage(cubage.getPhotos()[i]))
//                    Log.i(TAG, "changeCubageStatus: imagen eliminada ="+cubage.getPhotos()[i]);
//        }*/
//
//        return success;
//    }

    /**
     * @param context  el contexto
     * @param syncData el actualizador
     * @return la lista de puntos sin folio pendientes por enviar que aun no estan en el servidor
     */
    public static List<PointPOJO> getPointsToSend(Context context, SyncData syncData) {
        List<PointPOJO> points = new ArrayList();

        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();

        List<Points> pointsToSend = pointsDao.queryBuilder().where(PointsDao.Properties.UploadStatus.eq("A")).list();
        int totalElements = pointsToSend.size();
        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando puntos..."));
        int i = 0;
        for (Points pointsEntity : pointsToSend) {
            points.add(convertEntityToPojo(pointsEntity, true));
            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Obteniendo nuevos puntos..."));
            i++;
        }
        return points;
    }

    /**
     * @param context       el contexto
     * @param status        el estatus q se cambiara a ('B')
     * @param idPoint       el punto a cambiar
     * @param idPointServer el id que viene desde el servidor
     * @return si se ha cambiado correctamente
     */
    public static boolean changePointsStatus(Context context, String status, int idPoint, Long idPointServer) {

        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();

        Points points = pointsDao.load((long) idPoint);

        points.setUploadStatus(status);
        points.setUploadDate(new Date());
        points.setIdPointServer(idPointServer);

        pointsDao.update(points);

        return true;
    }


    /**
     * @param context  el contexto
     * @param building la obra
     * @param type     el tipo de punto (banco/1, tiro/2, desperdicio/3, todos/4)
     * @return la lista de puntos solicitados
     */
    public static ArrayList<PointPOJO> getAllPoints(Context context, String building, int type) {

        ArrayList<PointPOJO> points = new ArrayList();

        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();

        QueryBuilder<Points> queryBuilder = pointsDao.queryBuilder();

        if (type == ALL_POINT_TYPE)
            queryBuilder.where(
                    PointsDao.Properties.Building.eq(building),
                    PointsDao.Properties.EstatusServer.eq("A"),
                    PointsDao.Properties.IdPointServer.isNotNull())
                    .orderDesc(PointsDao.Properties.Chainage);
        else {
            if (type == BANK_AND_WASTE_TYPE) {
                queryBuilder.where(
                        PointsDao.Properties.Building.eq(building),
                        PointsDao.Properties.EstatusServer.eq("A"),
                        PointsDao.Properties.IdPointServer.isNotNull())
                        .whereOr(PointsDao.Properties.PointType.eq(BANK_TYPE),
                                PointsDao.Properties.PointType.eq(WASTE_TYPE))
                        .orderDesc(PointsDao.Properties.Chainage);
            } else {
                queryBuilder.where(
                        PointsDao.Properties.Building.eq(building),
                        PointsDao.Properties.EstatusServer.eq("A"),
                        PointsDao.Properties.PointType.eq(type),
                        PointsDao.Properties.IdPointServer.isNotNull())
                        .orderDesc(PointsDao.Properties.Chainage);
            }
        }

        List<Points> result = queryBuilder.list();
        for (Points pointsEntity : result) {
            points.add(convertEntityToPojo(pointsEntity, false));
        }

        Map<Long, PointPOJO> map = new HashMap<>();


        for (PointPOJO pointPOJO : points) {
            if (!map.containsKey(pointPOJO.getIdPuntoServer())) {
                map.put(Long.valueOf(pointPOJO.getIdPuntoServer()), pointPOJO);
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * @param context el contexto
     * @return los ids de los puntos
     */
    public static List<Long> getAvailableBanksByBuilding(Context context) {
        List<Long> bankIds = new ArrayList<>();
        /*
         *
         * Se sacan los bancos de la obra y sus ids
         *
         * */
        ArrayList<PointPOJO> banks = DAOPoints.getAllPoints(context,
                SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId(),
                BANK_TYPE);
        /*
         * Se extrae su id
         * */
        for (PointPOJO bank : banks)
            bankIds.add(Long.valueOf(bank.getIdPuntoServer()));
        return bankIds;
    }

    /**
     * @param context  el contexto
     * @param syncData el actualizador de la sincronización
     * @return la lista de puntos actuales
     */
    public static List<PointPOJO> getActualPoints(Context context, SyncData syncData) {

        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();

        QueryBuilder<Points> pointsQueryBuilder = pointsDao.queryBuilder();

        List<Points> pointsEntity = pointsQueryBuilder.where(
                PointsDao.Properties.IdPointServer.isNotNull(),
                PointsDao.Properties.Building.eq(
                        SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId()
                )
        ).list();

        List<PointPOJO> pointPojos = new ArrayList();

        for (Points pointEntity : pointsEntity)

            pointPojos.add(convertEntityToPojo(pointEntity, false));

        return pointPojos;
    }

    public static PointPOJO getPointById(Context context, Long id) {
        PointsDao pointsDao = ((App) context.getApplicationContext()).getDaoSession().getPointsDao();

        return convertEntityToPojo(
                pointsDao
                        .queryBuilder()
                        .where(PointsDao.Properties.IdPointServer.eq(id))
                        .limit(1)
                        .unique(),
                false);
    }

}

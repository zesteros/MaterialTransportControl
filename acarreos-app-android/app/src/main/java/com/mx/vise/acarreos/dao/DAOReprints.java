package com.mx.vise.acarreos.dao;

import android.content.ContentValues;
import android.content.Context;

import com.mx.vise.acarreos.App;
import com.mx.vise.acarreos.dao.entities.Reprints;
import com.mx.vise.acarreos.dao.entities.ReprintsDao;
import com.mx.vise.acarreos.pojos.ReprintPOJO;
import com.mx.vise.acarreos.tasks.Progress;
import com.mx.vise.acarreos.tasks.SyncData;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 25 de febrero del 2019 a las 18:53
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class DAOReprints {


    public static boolean addReprint(Context context, ReprintPOJO reprintPOJO) {

        ReprintsDao reprintsDao = ((App) context.getApplicationContext()).getDaoSession().getReprintsDao();

        Reprints reprints = new Reprints();

        reprints.setAddUser(reprintPOJO.getAddUser());
        reprints.setAddDate(new Date());
        reprints.setSheetNumber(reprintPOJO.getSheetNumber());
        reprints.setCoordinates(reprintPOJO.getCoordinates());
        reprints.setUploadStatus("A");

        return reprintsDao.insertOrReplace(reprints) != -1;
    }

    public static List<ReprintPOJO> getReprintsToSend(Context context, SyncData syncData) {
        List<ReprintPOJO> reprints = new ArrayList();


        ReprintsDao reprintsDao = ((App) context.getApplicationContext()).getDaoSession().getReprintsDao();

        List<Reprints> reprintsEntities = reprintsDao
                .queryBuilder()
                .where(ReprintsDao.Properties.UploadStatus.eq("A"))
                .list();

        int totalElements = reprintsEntities.size();
        if (syncData != null)
            syncData.publishProgress(new Progress(0, 0, totalElements, "Procesando reimpresiones..."));
        int i = 0;
        for (Reprints reprintsEntity : reprintsEntities) {

            ReprintPOJO reprint = new ReprintPOJO();
            reprint.setIdReprintLocal(reprintsEntity.getIdReprintLocal().intValue());
            reprint.setAddUser(reprintsEntity.getAddUser());
            reprint.setAddDate(reprintsEntity.getAddDate());
            reprint.setSheetNumber(reprintsEntity.getSheetNumber());
            reprint.setCoordinates(reprintsEntity.getCoordinates());
            reprints.add(reprint);
            if (syncData != null)
                syncData.publishProgress(new Progress(i * 100 / totalElements, i, totalElements, "Obteniendo reimpresiones..."));
            i++;

        }

        return reprints;
    }


    public static boolean changeReprintStatus(Context context, String status, Integer idReprintLocal, Integer idReprintServer) {

        ReprintsDao reprintsDao = ((App) context.getApplicationContext()).getDaoSession().getReprintsDao();

        Reprints reprintsEntity = reprintsDao
                .queryBuilder()
                .where(ReprintsDao.Properties.IdReprintLocal.eq(idReprintLocal))
                .limit(1)
                .unique();

        reprintsEntity.setUploadStatus(status);
        reprintsEntity.setUpdDate(new Date());
        reprintsEntity.setIdReprintServer(idReprintServer);

        reprintsDao.update(reprintsEntity);

        return true;
    }
}

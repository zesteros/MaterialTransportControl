package com.mx.vise.acarreos.dao;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.mx.vise.acarreos.pojos.TagPOJO;
import com.mx.vise.acarreos.tasks.Progress;
import com.mx.vise.acarreos.tasks.SyncData;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 25 de febrero del 2019 a las 18:59
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class DAOTags {

//    /**
//     * @param context the context
//     * @param tid     the tid to verify if exists
//     * @return if tag exists in database
//     */
//    public static boolean tagExists(Context context, String tid) {
//
//
//        Cursor cursor = DAO.getInstance().getDatabase().query(
//                Contract.SCRIPT.TABLE_NAME_TAGS,   // The table to query
//                null,// The array of columns to return (pass null to get all)
//                Contract.SCRIPT.COLUMN_TID_TAG + "=?",// The columns for the WHERE clause
//                new String[]{tid},          // The values for the WHERE clause
//                null,// don't group the rows
//                null,// don't filter by row groups
//                null// The sort order
//        );
//        boolean exists = cursor.getCount() > 0;
//
//
//        return exists;
//    }
//
//    public static void addTags(Context context, ArrayList<TagPOJO> tags, SyncData syncData) {
//
//        if(DAO.getInstance().getDatabaseHelper() != null)
//            Log.i(TAG, "addTags: database helper missing");
//
//        DAO.getInstance().getDatabaseHelper().onUpgradeTagsTable(DAO.getInstance().getDatabase());
//        int i = 0;
//        int totalElements = tags.size();
//        syncData.publishProgress(new Progress(0, 0, totalElements, "Descargando tags..."));
//
//        for (TagPOJO tag : tags) {
//            ContentValues values = new ContentValues();
//            values.put(Contract.SCRIPT.COLUMN_ID_ACTIVO_TAG, tag.getIdActivo());
//            values.put(Contract.SCRIPT.COLUMN_TID_TAG, tag.getTag());
//            values.put(Contract.SCRIPT.COLUMN_ADD_DATE_TAG, dateFormat.format(tag.getAddDate()));
//            values.put(Contract.SCRIPT.COLUMN_SYNC_DATE_TAG, dateFormat.format(new Date()));
//            DAO.getInstance().getDatabase().insert(Contract.SCRIPT.TABLE_NAME_TAGS, null, values);
//            i++;
//            syncData.publishProgress(i * 100 / totalElements, i, totalElements);
//        }
//
//    }
}

package com.mx.vise.acarreos.dao;

import android.content.Context;

import com.mx.vise.acarreos.dao.entities.DaoMaster;
import com.mx.vise.acarreos.dao.entities.TicketsDao;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Used for migrating data from one schema version to another.
 * Created by Pierce Zaifman on 2017-01-28.
 */

public class DatabaseUpgradeHelper extends DaoMaster.OpenHelper {

    public DatabaseUpgradeHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        List<Migration> migrations = getMigrations();

        // Only run migrations past the old version
        for (Migration migration : migrations) {

            if (oldVersion < migration.getVersion()) {
                migration.runMigration(db);
            }
        }
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new MigrationV2());
        //migrations.add(new MigrationV3());

        // Sorting just to be safe, in case other people add migrations in the wrong order.
        Comparator<Migration> migrationComparator = (m1, m2) -> m1.getVersion().compareTo(m2.getVersion());
        Collections.sort(migrations, migrationComparator);

        return migrations;
    }

    private static class MigrationV2 implements Migration {

        @Override
        public Integer getVersion() {
            return 2;
        }

        @Override
        public void runMigration(Database db) {
            //Adding new table
                      db.execSQL("ALTER TABLE " + TicketsDao.TABLENAME + " ADD COLUMN " + TicketsDao.Properties.CancelUploadStatus.columnName + " TEXT");

            //UserDao.createTable(db, false);

        }
    }

//    private static class MigrationV3 implements Migration {
//
//        @Override
//        public Integer getVersion() {
//            return 3;
//        }
//
//        @Override
//        public void runMigration(Database db) {
//            // Add new column to user table
//            db.execSQL("ALTER TABLE " + UserDao.TABLENAME + " ADD COLUMN " + UserDao.Properties.Age.columnName + " INTEGER");
//        }
//    }

    private interface Migration {
        Integer getVersion();

        void runMigration(Database db);
    }
}
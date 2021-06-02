package com.mx.vise.acarreos;

import android.app.Application;

import com.mx.vise.acarreos.dao.Contract;
import com.mx.vise.acarreos.dao.DatabaseUpgradeHelper;
import com.mx.vise.acarreos.dao.entities.DaoMaster;
import com.mx.vise.acarreos.dao.entities.DaoSession;

import org.greenrobot.greendao.database.Database;

public class App extends Application {

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseUpgradeHelper helper = new DatabaseUpgradeHelper(this,"carries-db");
        Database db = helper.getEncryptedReadableDb(Contract.DATABASE_K);
        mDaoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession(){
        return mDaoSession;
    }
}

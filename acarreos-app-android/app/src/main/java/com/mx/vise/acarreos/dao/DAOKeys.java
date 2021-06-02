package com.mx.vise.acarreos.dao;

import android.content.Context;

import com.mx.vise.acarreos.App;
import com.mx.vise.acarreos.dao.entities.Keys;
import com.mx.vise.acarreos.dao.entities.KeysDao;
import com.mx.vise.androidwscon.utils.AESencrp;
import com.mx.vise.nfc.pojos.KeyPOJO;
import com.mx.vise.acarreos.tasks.SyncData;

import java.util.ArrayList;
import java.util.List;

public class DAOKeys {
    private static final String TAG = "VISE";

    public static List<KeyPOJO> getActualKeys(Context context, SyncData syncData) {

        KeysDao daoKeys = ((App) context.getApplicationContext()).getDaoSession().getKeysDao();

        return convertKeyEntityListToPojoList(daoKeys.queryBuilder().where(KeysDao.Properties.Estatus.eq("A")).list());
    }


    private static List<Keys> convertKeyPojoToEntity(List<KeyPOJO> pojos) {
        List<Keys> entities = new ArrayList<Keys>();
        for (KeyPOJO pojo : pojos) {
            Keys entity = new Keys();
            entity.setAddDate(pojo.getAddDate());
            entity.setAddUser(pojo.getAddUser());
            entity.setEstatus(pojo.getEstatus());
            entity.setIdKeyServer(Long.valueOf(pojo.getIdKeyServer()));
            entity.setKeyA(pojo.getKeyA());
            entity.setKeyB(pojo.getKeyB());
            entity.setSector(pojo.getSector());
            entity.setUpdDate(pojo.getUpdDate());
            entity.setUpdUser(pojo.getUpdUser());
            entity.setVersion(pojo.getVersion());
            entities.add(entity);
        }
        return entities;
    }

    public static List<KeyPOJO> getKeysByVersion(Context context, int version) {
        KeysDao daoKeys = ((App) context.getApplicationContext()).getDaoSession().getKeysDao();
        List<KeyPOJO> keys = convertKeyEntityListToPojoList(daoKeys.queryBuilder().where(KeysDao.Properties.Version.eq(version)).list());

        for (KeyPOJO key : keys) {
            try {
                key.setKeyA(AESencrp.decrypt(key.getKeyA()));
                key.setKeyB(AESencrp.decrypt(key.getKeyB()));
                keys.set(keys.indexOf(key), key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return keys;
    }

    public static KeyPOJO getFlagSectorKey(Context context, int keysVersion) {
        KeysDao daoKeys = ((App) context.getApplicationContext()).getDaoSession().getKeysDao();

        Keys entity = daoKeys.queryBuilder().where(
                KeysDao.Properties.Sector.eq(15),
                KeysDao.Properties.Version.eq(keysVersion)
        ).limit(1).unique();


        if (entity != null) {
            KeyPOJO keyPOJO = convertKeyEntityToPojo(entity);
            try {
                keyPOJO.setKeyA(AESencrp.decrypt(keyPOJO.getKeyA()));
                keyPOJO.setKeyB(AESencrp.decrypt(keyPOJO.getKeyB()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return keyPOJO;
        }

        return null;
    }


    private static ArrayList<KeyPOJO> convertKeyEntityListToPojoList(List<Keys> entities) {
        ArrayList<KeyPOJO> pojos = new ArrayList<KeyPOJO>();
        for (Keys entity : entities) {
            pojos.add(convertKeyEntityToPojo(entity));
        }
        return pojos;
    }

    private static KeyPOJO convertKeyEntityToPojo(Keys entity) {
        KeyPOJO pojo = new KeyPOJO();
        pojo.setAddDate(entity.getAddDate());
        pojo.setAddUser(entity.getAddUser());
        pojo.setEstatus(entity.getEstatus());
        pojo.setIdKeyServer(entity.getIdKeyServer().intValue());
        pojo.setKeyA(entity.getKeyA());
        pojo.setKeyB(entity.getKeyB());
        pojo.setSector(entity.getSector());
        pojo.setUpdDate(entity.getUpdDate());
        pojo.setUpdUser(entity.getUpdUser());
        pojo.setVersion(entity.getVersion());
        return pojo;
    }

    public static void addPendingKeys(ArrayList<KeyPOJO> newKeys, Context context, SyncData syncData) {

        KeysDao daoKeys = ((App) context.getApplicationContext()).getDaoSession().getKeysDao();

        List<Keys> keys = convertKeyPojoToEntity(newKeys);

        for (Keys key : keys) {
            if (daoKeys
                    .queryBuilder()
                    .where(KeysDao.Properties.IdKeyServer.eq(key.getIdKeyServer()))
                    .limit(1)
                    .list()
                    .size() == 0)
                daoKeys.insert(key);
        }
    }

    public static void updateKeys(ArrayList<KeyPOJO> updatedKeys, Context context, SyncData syncData) {
        KeysDao daoKeys = ((App) context.getApplicationContext()).getDaoSession().getKeysDao();

        List<Keys> keysInServer = convertKeyPojoToEntity(updatedKeys);

        for (Keys keyInServer : keysInServer) {

            try {
                Keys keyToUpdate = daoKeys
                        .queryBuilder()
                        .where(KeysDao.Properties.IdKeyServer.eq(keyInServer.getIdKeyServer()))
                        .limit(1)
                        .unique();
                if (keyToUpdate != null) {
                    keyInServer.setIdKeyLocal(keyToUpdate.getIdKeyLocal());
                    daoKeys.update(keyInServer);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }
    }
}

package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.OwnerInfoEntity;
import com.juzix.wallet.db.entity.SharedWalletInfoEntity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SharedWalletInfoDao extends BaseDao {

    private SharedWalletInfoDao() {
        super();
    }

    public static SharedWalletInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ArrayList<SharedWalletInfoEntity> getWalletInfoList() {

        ArrayList<SharedWalletInfoEntity> list  = new ArrayList<>();
        Realm                                 realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SharedWalletInfoEntity> results = realm.where(SharedWalletInfoEntity.class).findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp){
            exp.printStackTrace();
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public boolean insertWalletInfo(SharedWalletInfoEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(entity);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean insertWalletInfoList(ArrayList<SharedWalletInfoEntity> list) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(list);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public boolean updateOwnerNameWithUuid(String uuid, ArrayList<OwnerInfoEntity> entityArrayList) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmList<OwnerInfoEntity> entityRealmList = new RealmList<>();
            for (OwnerInfoEntity entity : entityArrayList){
                entityRealmList.add(realm.copyToRealmOrUpdate(entity));
            }
            realm.where(SharedWalletInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst()
                    .setOwner(entityRealmList);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public boolean updateNameWithUuid(String uuid, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(SharedWalletInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst()
                    .setName(name);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public boolean deleteWalletInfo(String uuid) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(SharedWalletInfoEntity.class).equalTo("uuid", uuid).findAll().deleteFirstFromRealm();
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public boolean deleteAll() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.delete(SharedWalletInfoEntity.class);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    private final static class InstanceHolder {
        private final static SharedWalletInfoDao INSTANCE = new SharedWalletInfoDao();
    }
}

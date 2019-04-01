package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.IndividualWalletInfoEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class IndividualWalletInfoDao extends BaseDao {

    private IndividualWalletInfoDao() {
        super();
    }

    public static IndividualWalletInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ArrayList<IndividualWalletInfoEntity> getWalletInfoList() {

        ArrayList<IndividualWalletInfoEntity> list  = new ArrayList<>();
        Realm                                 realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualWalletInfoEntity> results = realm.where(IndividualWalletInfoEntity.class).findAll();
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

    public boolean insertWalletInfo(IndividualWalletInfoEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(entity);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            if (realm != null ) {
                realm.cancelTransaction();
            }
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean insertWalletInfoList(ArrayList<IndividualWalletInfoEntity> list) {
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

    public boolean updateNameWithUuid(String uuid, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(IndividualWalletInfoEntity.class)
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

    public boolean updateMnemonicWithUuid(String uuid, String mnemonic) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(IndividualWalletInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst()
                    .setMnemonic(mnemonic);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public boolean updateUpdateTimeWithUuid(String uuid, long updateTime) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(IndividualWalletInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst()
                    .setUpdateTime(updateTime);
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
            realm.where(IndividualWalletInfoEntity.class).equalTo("uuid", uuid).findAll().deleteFirstFromRealm();
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
            realm.delete(IndividualWalletInfoEntity.class);
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
        private final static IndividualWalletInfoDao INSTANCE = new IndividualWalletInfoDao();
    }
}

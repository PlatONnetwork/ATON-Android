package com.juzix.wallet.db.sqlite;

import android.util.Log;

import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class SharedTransactionInfoDao {

    private final static String TAG = SharedTransactionInfoDao.class.getSimpleName();

    private SharedTransactionInfoDao() {
    }

    public static SharedTransactionInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public boolean insertTransaction(SharedTransactionInfoEntity transactionEntity) {

        Log.e(TAG, "insertTransaction..." + transactionEntity.toString());

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(transactionEntity);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return false;
    }

    public boolean insertTransaction(ArrayList<SharedTransactionInfoEntity> entities) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entities);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public ArrayList<SharedTransactionInfoEntity> getTransactionInfoList() {

        ArrayList<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class).findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public List<SharedTransactionInfoEntity> getTransactionListByContractAddress(String contractAddress) {
        ArrayList<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class).equalTo("contractAddress", contractAddress).findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return list;
    }

    public boolean updateReadWithContractAddress(String contractAddress, boolean read) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SharedTransactionInfoEntity> realmResults = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("contractAddress", contractAddress)
                    .findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                realmResults.get(i).setRead(read);
            }
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean updateReadWithUuid(String uuid, boolean read) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst()
                    .setRead(read);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


    public boolean hasUnRead() {
        long count = 0;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            count = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("read", false).count();
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
        return count > 0;
    }

    private static class InstanceHolder {
        private static final SharedTransactionInfoDao INSTANCE = new SharedTransactionInfoDao();
    }

}

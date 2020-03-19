package com.platon.aton.db.sqlite;

import com.platon.aton.db.entity.TransactionEntity;
import com.platon.aton.engine.NodeManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author matrixelement
 */
public class TransactionDao {

    private TransactionDao() {
    }

    public static boolean insertTransaction(TransactionEntity transactionEntity) {
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

    public static boolean deleteTransaction(String hash) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(TransactionEntity.class)
                    .equalTo("hash", hash)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .findAll()
                    .deleteAllFromRealm();
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

    public static TransactionEntity getTransactionByHash(String hash) {
        Realm realm = null;
        TransactionEntity transactionEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            transactionEntity = realm.where(TransactionEntity.class)
                    .equalTo("hash", hash)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .findFirst();
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return transactionEntity;

    }

    public static List<TransactionEntity> getTransactionList() {

        List<TransactionEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionEntity> results = realm.where(TransactionEntity.class)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .sort("createTime", Sort.DESCENDING)
                    .findAll();
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

    public static List<TransactionEntity> getTransactionList(String address) {

        List<TransactionEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TransactionEntity> results = realm.where(TransactionEntity.class)
                    .beginGroup()
                    .equalTo("from", address.toLowerCase())
                    .or()
                    .equalTo("to", address.toLowerCase())
                    .endGroup()
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .sort("createTime", Sort.DESCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static TransactionEntity getTransaction(String from, String txType, int txReceiptStatus) {
        TransactionEntity transactionEntity = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmObject realmObject = realm.where(TransactionEntity.class)
                    .equalTo("from", from.toLowerCase())
                    .equalTo("txType", txType)
                    .equalTo("txReceiptStatus", txReceiptStatus)
                    .equalTo("chainId", NodeManager.getInstance().getChainId())
                    .findFirst();
            if (realmObject != null) {
                transactionEntity = (TransactionEntity) realm.copyFromRealm(realmObject);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionEntity;
    }
}

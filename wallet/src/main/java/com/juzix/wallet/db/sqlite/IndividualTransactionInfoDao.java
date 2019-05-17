package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.engine.NodeManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author matrixelement
 */
public class IndividualTransactionInfoDao {

    public static boolean insertTransaction(IndividualTransactionInfoEntity transactionEntity) {
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

    public static IndividualTransactionInfoEntity getTransactionByHash(String hash) {
        Realm realm = null;
        IndividualTransactionInfoEntity transactionEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            transactionEntity = realm.where(IndividualTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("hash", hash)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
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

    public static List<IndividualTransactionInfoEntity> getTransactionList() {

        List<IndividualTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualTransactionInfoEntity> results = realm.where(IndividualTransactionInfoEntity.class)
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
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

    /**
     * 分页加载获取交易记录
     * @param walletAddress
     * @param createTime
     * @param size
     * @return
     */
    public static List<IndividualTransactionInfoEntity> getTransactionListByWalletAddress(String walletAddress, long createTime,long size) {
        List<IndividualTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualTransactionInfoEntity> results = realm.where(IndividualTransactionInfoEntity.class)
                    .beginGroup()
                        .equalTo("from", walletAddress)
                        .or()
                        .equalTo("to", walletAddress)
                    .endGroup()
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .lessThan("createTime",createTime)
                    .sort("createTime", Sort.DESCENDING)
                    .limit(size)
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

    public static List<IndividualTransactionInfoEntity> getTransactionList(String address) {

        List<IndividualTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualTransactionInfoEntity> results = realm.where(IndividualTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("from", address)
                    .or()
                    .equalTo("to", address)
                    .endGroup()
                    .beginGroup()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
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
}

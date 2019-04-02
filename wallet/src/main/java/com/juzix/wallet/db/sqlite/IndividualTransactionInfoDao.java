package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class IndividualTransactionInfoDao {

    private IndividualTransactionInfoDao() {

    }

    public static IndividualTransactionInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void insertTransaction(IndividualTransactionInfoEntity transactionEntity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(transactionEntity);
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
    }

    public IndividualTransactionInfoEntity getTransactionByHash(String hash) {
        Realm realm = null;
        IndividualTransactionInfoEntity transactionEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            transactionEntity = realm.where(IndividualTransactionInfoEntity.class).equalTo("hash", hash).findFirst();
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

    public List<IndividualTransactionInfoEntity> getTransactionList() {

        List<IndividualTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualTransactionInfoEntity> results = realm.where(IndividualTransactionInfoEntity.class).findAll();
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

    public List<IndividualTransactionInfoEntity> getTransactionListByWalletAddress(String walletAddress) {

        List<IndividualTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualTransactionInfoEntity> results = realm.where(IndividualTransactionInfoEntity.class).equalTo("creatorAddress", walletAddress).findAll();
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

    public List<IndividualTransactionInfoEntity> getTransactionList(String address) {

        List<IndividualTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualTransactionInfoEntity> results = realm.where(IndividualTransactionInfoEntity.class)
                    .equalTo("from", address)
                    .or()
                    .equalTo("to", address)
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

    public void updateTransactionBlockNumber(String uuid, long blockNumber) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    IndividualTransactionInfoEntity individualTransactionInfoEntity = realm.where(IndividualTransactionInfoEntity.class)
                            .equalTo("uuid", uuid)
                            .findFirst();
                    if (individualTransactionInfoEntity != null) {
                        individualTransactionInfoEntity.setBlockNumber(blockNumber);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }


    }

    private final static class InstanceHolder {
        private final static IndividualTransactionInfoDao INSTANCE = new IndividualTransactionInfoDao();
    }

}

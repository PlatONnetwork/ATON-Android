package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.entity.TicketInfoEntity;
import com.juzix.wallet.entity.SingleVoteEntity;

import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class SingleVoteInfoDao {

    private SingleVoteInfoDao() {

    }

    public static SingleVoteInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void insertTransaction(SingleVoteInfoEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entity);
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

    public SingleVoteInfoEntity getTransactionByUuid(String uuid) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            return realm.where(SingleVoteInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst();
        } catch (Exception e) {
            return null;
        }
    }

    public List<SingleVoteInfoEntity> getTransactionListByWalletAddress(String walletAddress) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .contains("creatorAddress", Numeric.cleanHexPrefix(walletAddress))
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

    public List<TicketInfoEntity> getTicketListByCandidateId(String candidateId) {
        List<TicketInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TicketInfoEntity> results = realm.where(TicketInfoEntity.class)
                    .equalTo("candidateId", candidateId)
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

    public List<SingleVoteInfoEntity> getTransactionListByStatus(int status) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("status", status)
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

    public List<SingleVoteInfoEntity> getTransactionListByCandidateId(String candidateId) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("candidateId", candidateId)
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

    public SingleVoteInfoEntity getTransactionByHash(String transactionHash) {
        Realm realm = null;
        SingleVoteInfoEntity entity = null;
        try {
            realm = Realm.getDefaultInstance();
            SingleVoteInfoEntity singleVoteInfoEntity = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("hash", transactionHash)
                    .findFirst();
            entity = realm.copyFromRealm(singleVoteInfoEntity);
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return entity;
    }


    public List<SingleVoteInfoEntity> getTransactionList() {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class).findAll();
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

    private final static class InstanceHolder {
        private final static SingleVoteInfoDao INSTANCE = new SingleVoteInfoDao();
    }

}

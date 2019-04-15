package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.entity.TicketInfoEntity;
import com.juzix.wallet.engine.NodeManager;

import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class SingleVoteInfoDao {

    public static boolean insertTransaction(SingleVoteInfoEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entity);
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

    public static SingleVoteInfoEntity getTransactionByUuid(String uuid) {
        Realm realm = null;
        SingleVoteInfoEntity singleVoteEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            SingleVoteInfoEntity voteEntity = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (voteEntity != null) {
                singleVoteEntity = realm.copyFromRealm(voteEntity);
            }
            realm.commitTransaction();
            return singleVoteEntity;
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return null;
    }

    public static List<SingleVoteInfoEntity> getTransactionListByWalletAddress(String walletAddress) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .contains("walletAddress", Numeric.cleanHexPrefix(walletAddress))
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
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

    public static List<SingleVoteInfoEntity> getTransactionListByWalletAddress(String[] walletAddress) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .in("walletAddress", walletAddress)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
            realm.commitTransaction();
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

    public static List<TicketInfoEntity> getTicketListByCandidateId(String candidateId) {
        List<TicketInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<TicketInfoEntity> results = realm.where(TicketInfoEntity.class)
                    .equalTo("candidateId", candidateId)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
            realm.commitTransaction();
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

    public static List<SingleVoteInfoEntity> getTransactionListByStatus(int status) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("status", status)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list.addAll(realm.copyFromRealm(results));
            }
            realm.commitTransaction();
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

    public static List<SingleVoteInfoEntity> getTransactionListByCandidateId(String candidateId) {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("candidateId", candidateId)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
            realm.commitTransaction();
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

    public static String getCandidateNameByCandidateId(String candidateId) {
        Realm realm = null;
        String candidateName = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            SingleVoteInfoEntity entity = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("candidateId", candidateId)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (entity != null) {
                candidateName = entity.getCandidateName();
            }
            realm.commitTransaction();
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
        return candidateName;
    }

    public static SingleVoteInfoEntity getTransactionByHash(String transactionHash) {
        Realm realm = null;
        SingleVoteInfoEntity entity = null;
        try {
            realm = Realm.getDefaultInstance();
            SingleVoteInfoEntity singleVoteInfoEntity = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("hash", transactionHash)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (singleVoteInfoEntity != null) {
                entity = realm.copyFromRealm(singleVoteInfoEntity);
            }
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
        return entity;
    }


    public static List<SingleVoteInfoEntity> getTransactionList() {
        List<SingleVoteInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SingleVoteInfoEntity> results = realm.where(SingleVoteInfoEntity.class)
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
            realm.commitTransaction();
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

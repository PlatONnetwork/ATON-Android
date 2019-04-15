package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.SharedTransactionEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author matrixelement
 */
public class SharedTransactionInfoDao {

    public static boolean insertTransaction(SharedTransactionInfoEntity transactionEntity) {

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

    public static boolean insertTransaction(List<SharedTransactionInfoEntity> entities) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(entities);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.close();
            }
            return false;
        }
    }

    public static List<SharedTransactionInfoEntity> getSharedTransactionListByTransactionType() {
        Realm realm = null;
        List<SharedTransactionInfoEntity> sharedTransactionInfoEntityList = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            List<SharedTransactionInfoEntity> list = realm.where(SharedTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("transactionType", SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue())
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findAll();
            if (list != null) {
                sharedTransactionInfoEntityList = realm.copyFromRealm(list);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return sharedTransactionInfoEntityList;
    }

    public static SharedTransactionInfoEntity getSharedTransaction(String contractAddress, String transactionId, SharedTransactionEntity.TransactionType transactionType) {
        Realm realm = null;
        SharedTransactionInfoEntity transactionInfoEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("contractAddress", contractAddress)
                    .and()
                    .equalTo("transactionId", transactionId)
                    .and()
                    .equalTo("transactionType", transactionType.getValue())
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                transactionInfoEntity = realm.copyFromRealm(sharedTransactionInfoEntity);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionInfoEntity;
    }

    public static String getSharedTransactionUUID(String contractAddress, String transactionId, SharedTransactionEntity.TransactionType transactionType) {
        Realm realm = null;
        String uuid = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("contractAddress", contractAddress)
                    .and()
                    .equalTo("transactionId", transactionId)
                    .and()
                    .equalTo("transactionType", transactionType.getValue())
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                uuid = realm.copyFromRealm(sharedTransactionInfoEntity).getUuid();
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return uuid;
    }

    public static List<SharedTransactionInfoEntity> getTransactionInfoList() {

        List<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class)
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


    public static List<SharedTransactionInfoEntity> getTransactionListByContractAddress(String[] contractAddressArray) {
        List<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class)
                    .beginGroup()
                    .in("contractAddress", contractAddressArray)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .sort("createTime", Sort.DESCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
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

    public static List<SharedTransactionInfoEntity> getTransactionListByContractAddress(String contractAddress) {
        List<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("contractAddress", contractAddress)
                    .or()
                    .equalTo("toAddress", contractAddress)
                    .endGroup()
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static SharedTransactionInfoEntity getTransactionByTransactionId(String transactionId) {
        Realm realm = null;
        SharedTransactionInfoEntity transactionInfoEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("transactionId", transactionId)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                transactionInfoEntity = realm.copyFromRealm(sharedTransactionInfoEntity);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionInfoEntity;
    }

    public static String getSharedTransactionHashByUUID(String uuid) {
        Realm realm = null;
        String transactionHash = null;
        try {
            realm = Realm.getDefaultInstance();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                transactionHash = realm.copyFromRealm(sharedTransactionInfoEntity).getHash();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionHash;
    }

    public static SharedTransactionInfoEntity getTransactionByUUID(String uuid) {
        Realm realm = null;
        SharedTransactionInfoEntity transactionInfoEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                transactionInfoEntity = realm.copyFromRealm(sharedTransactionInfoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return transactionInfoEntity;
    }

    public static boolean updateReadWithContractAddress(String contractAddress, boolean read) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<SharedTransactionInfoEntity> realmResults = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("contractAddress", contractAddress)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                realmResults.get(i).setRead(read);
            }
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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

    public static SharedTransactionInfoEntity updateReadWithUUID(String uuid, boolean read) {
        Realm realm = null;
        SharedTransactionInfoEntity sharedTransactionInfoEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            SharedTransactionInfoEntity transactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .findFirst();
            if (transactionInfoEntity != null) {
                transactionInfoEntity.setRead(read);
                sharedTransactionInfoEntity = realm.copyFromRealm(transactionInfoEntity);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return sharedTransactionInfoEntity;
    }


    public static boolean hasUnRead() {
        long count = 0;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            count = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("read", false)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .count();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return count > 0;
    }

    /**
     * 共享钱包是否有未读消息
     *
     * @return
     */
    public static boolean hasUnreadTransactionByContractAddress(String contractAddress) {
        Realm realm = null;
        long unreadCount = 0;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            unreadCount = realm.where(SharedTransactionInfoEntity.class)
                    .beginGroup()
                    .equalTo("fromAddress", contractAddress)
                    .or()
                    .equalTo("toAddress", contractAddress)
                    .endGroup()
                    .beginGroup()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .and()
                    .equalTo("read", false)
                    .endGroup()
                    .count();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return unreadCount > 0;
    }
}

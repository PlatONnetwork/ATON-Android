package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
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

    private final static String TAG = SharedTransactionInfoDao.class.getSimpleName();

    private SharedTransactionInfoDao() {
    }

    public static SharedTransactionInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public boolean insertTransaction(SharedTransactionInfoEntity transactionEntity) {

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

    public boolean insertTransaction(List<SharedTransactionInfoEntity> entities) {
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

    public List<SharedTransactionInfoEntity> getSharedTransactionListByTransactionType() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            List<SharedTransactionInfoEntity> list = realm.where(SharedTransactionInfoEntity.class).equalTo("transactionType", SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue()).findAll();
            List<SharedTransactionInfoEntity> sharedTransactionInfoEntityList = realm.copyFromRealm(list);
            realm.commitTransaction();
            return sharedTransactionInfoEntityList;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.close();
            }
            return new ArrayList<>();
        }
    }

    public SharedTransactionInfoEntity getSharedTransaction(String contractAddress, String transactionId, SharedTransactionEntity.TransactionType transactionType) {
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
                    .endGroup()
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                transactionInfoEntity = realm.copyFromRealm(sharedTransactionInfoEntity);
            }
            realm.commitTransaction();
            return transactionInfoEntity;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.close();
            }
        }
        return transactionInfoEntity;
    }

    public String getSharedTransactionUUID(String contractAddress, String transactionId, SharedTransactionEntity.TransactionType transactionType) {
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
                    .endGroup()
                    .findFirst();
            if (sharedTransactionInfoEntity != null) {
                uuid = realm.copyFromRealm(sharedTransactionInfoEntity).getUuid();
            }
            realm.commitTransaction();
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.close();
            }
        }
        return uuid;
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


    public List<SharedTransactionInfoEntity> getTransactionListByContractAddress(String[] contractAddressArray) {
        ArrayList<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class)
                    .in("contractAddress", contractAddressArray)
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

    public List<SharedTransactionInfoEntity> getTransactionListByContractAddress(String contractAddress) {
        ArrayList<SharedTransactionInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<SharedTransactionInfoEntity> results = realm.where(SharedTransactionInfoEntity.class)
                    .equalTo("contractAddress", contractAddress)
                    .or()
                    .equalTo("toAddress", contractAddress)
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

    public SharedTransactionInfoEntity getTransactionByTransactionId(String transactionId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class).equalTo("transactionId", transactionId).findFirst();
            if (sharedTransactionInfoEntity != null) {
                return realm.copyFromRealm(sharedTransactionInfoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return null;
    }

    public String getSharedTransactionHashByUUID(String uuid) {
        Realm realm = null;
        String transactionHash = null;
        try {
            realm = Realm.getDefaultInstance();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class).equalTo("uuid", uuid).findFirst();
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

    public SharedTransactionInfoEntity getTransactionByUUID(String uuid) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            SharedTransactionInfoEntity sharedTransactionInfoEntity = realm.where(SharedTransactionInfoEntity.class).equalTo("uuid", uuid).findFirst();
            if (sharedTransactionInfoEntity != null) {
                return realm.copyFromRealm(sharedTransactionInfoEntity);
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
        return null;
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
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    SharedTransactionInfoEntity transactionInfoEntity = realm.where(SharedTransactionInfoEntity.class)
                            .equalTo("uuid", uuid)
                            .findFirst();
                    transactionInfoEntity.setRead(read);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (realm != null) {
                realm.close();
            }
            return false;
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

    /**
     * 共享钱包是否有未读消息
     *
     * @return
     */
    public boolean hasUnreadTransactionByContractAddress(String contractAddress) {
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
        }
        return unreadCount > 0;
    }

    private static class InstanceHolder {
        private static final SharedTransactionInfoDao INSTANCE = new SharedTransactionInfoDao();
    }

}

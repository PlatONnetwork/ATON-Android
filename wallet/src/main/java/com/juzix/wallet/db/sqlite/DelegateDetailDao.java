package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.DelegateDetailEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DelegateDetailDao {

    public static List<DelegateDetailEntity> getDelegateAddressInfoList() {
        List<DelegateDetailEntity> list = new ArrayList<>();
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            RealmResults<DelegateDetailEntity> results = realm.where(DelegateDetailEntity.class)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
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


        return list;
    }



    /**
     * 获取数据通过(钱包地址和节点地址,块高)
     */
    public static DelegateDetailEntity getEntityWithAddressAndNodeId(String walletAddress, String nodeId, String stakingBlockNum) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            DelegateDetailEntity entity = realm.where(DelegateDetailEntity.class)
                    .beginGroup()
                    .equalTo("address", walletAddress)
                    .and()
                    .equalTo("nodeId", nodeId)
                    .and()
                    .equalTo("stakingBlockNum", stakingBlockNum)
                    .endGroup()
                    .findFirst();
            return realm.copyFromRealm(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return null;

    }


    public static boolean insertDelegateNodeAddressInfo(DelegateDetailEntity entity) {
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
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return false;

    }

    //根据字段更新对象
    public static boolean updateNodeIdAndBlockHeight(String walletAddress, String stakingBlockNum, String nodeId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(DelegateDetailEntity.class)
                    .equalTo("address", walletAddress)
                    .findFirst()
                    .setStakingBlockNum(stakingBlockNum);

            realm.where(DelegateDetailEntity.class)
                    .equalTo("address", walletAddress)
                    .findFirst()
                    .setNodeId(nodeId);

            realm.commitTransaction();

        } catch (Exception e) {
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

}

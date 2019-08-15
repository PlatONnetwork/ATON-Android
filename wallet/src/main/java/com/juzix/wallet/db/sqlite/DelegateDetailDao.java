package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.AddressEntity;
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


    //删除数据



    /**
     * 获取数据通过(钱包地址和节点地址)
     */
    public static DelegateDetailEntity getEntityWithAddressAndNodeId(String walletAddress, String nodeId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            DelegateDetailEntity entity = realm.where(DelegateDetailEntity.class)
                    .equalTo("address", walletAddress)
                    .and()
                    .equalTo("nodeId", nodeId)
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

}

package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.DelegateAddressEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DelegateDao {

    public static List<DelegateAddressEntity> getDelegateAddressInfoList() {
        List<DelegateAddressEntity> list = new ArrayList<>();
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            RealmResults<DelegateAddressEntity> results = realm.where(DelegateAddressEntity.class)
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


    public static boolean insertDelegateNodeAddressInfo(DelegateAddressEntity entity) {
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

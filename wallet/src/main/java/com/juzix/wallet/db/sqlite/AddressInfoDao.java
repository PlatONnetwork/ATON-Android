package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.AddressInfoEntity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class AddressInfoDao extends BaseDao {

    private AddressInfoDao() {
        super();
    }

    public static AddressInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ArrayList<AddressInfoEntity> getAddressInfoList() {
        ArrayList<AddressInfoEntity> list  = new ArrayList<>();
        Realm                       realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AddressInfoEntity> results = realm.where(AddressInfoEntity.class).findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp){
            exp.printStackTrace();
            if (realm != null) {
                realm.cancelTransaction();
            }
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public boolean insertAddressInfo(AddressInfoEntity entity) {
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
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean updateAddressInfo(AddressInfoEntity oldAddressInfo, AddressInfoEntity newAddressInfo) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            if (oldAddressInfo.getAddress().equals(newAddressInfo.getAddress())) {
                //update
                realm.where(AddressInfoEntity.class)
                        .equalTo("address", oldAddressInfo.getAddress())
                        .findFirst()
                        .setName(newAddressInfo.getName());
            } else {
                //delete
                realm.where(AddressInfoEntity.class).equalTo("address", oldAddressInfo.getAddress()).findAll().deleteFirstFromRealm();
                //insert
                realm.copyToRealmOrUpdate(newAddressInfo);

            }
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }

    }

    public boolean updateNameWithAddress(String address, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(AddressInfoEntity.class)
                    .equalTo("address", address)
                    .findFirst()
                    .setName(name);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public AddressInfoEntity getEntityWithAddress(String address) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            return realm.where(AddressInfoEntity.class)
                    .equalTo("address", address).findFirst();
        } catch (Exception e) {
            return null;
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean deleteAddressInfo(String address) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(AddressInfoEntity.class).equalTo("address", address).findAll().deleteFirstFromRealm();
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    public boolean deleteAll() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.delete(AddressInfoEntity.class);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    private final static class InstanceHolder {
        private final static AddressInfoDao INSTANCE = new AddressInfoDao();
    }
}

package com.platon.aton.db.sqlite;

import com.platon.aton.db.entity.AddressEntity;
import com.platon.framework.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddressDao {

    private AddressDao() {

    }

    public static List<AddressEntity> getAddressInfoList() {
        List<AddressEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AddressEntity> results = realm.where(AddressEntity.class)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
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

    public static String getAddressNameByAddress(String address) {
        String addressName = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            AddressEntity addressInfoEntity = realm.where(AddressEntity.class)
                    .equalTo("address", address, Case.INSENSITIVE)
                    .findFirst();
            if (addressInfoEntity != null) {
                addressName = realm.copyFromRealm(addressInfoEntity).getName();
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return addressName;
    }

    public static boolean insertAddressInfo(AddressEntity entity) {
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

    public static boolean updateAddressInfo(AddressEntity oldAddressInfo, AddressEntity newAddressInfo) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            if (oldAddressInfo.getAddress().equals(newAddressInfo.getAddress())) {
                //update
                realm.where(AddressEntity.class)
                        .equalTo("address", oldAddressInfo.getAddress(), Case.INSENSITIVE)
                        .findFirst()
                        .setName(newAddressInfo.getName());
            } else {
                //delete
                realm.where(AddressEntity.class)
                        .equalTo("address", oldAddressInfo.getAddress(), Case.INSENSITIVE)
                        .findAll()
                        .deleteFirstFromRealm();
                //insert
                realm.copyToRealmOrUpdate(newAddressInfo);

            }
            realm.commitTransaction();
            return true;
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

    public static boolean updateNameWithAddress(String address, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(AddressEntity.class)
                    .equalTo("address", address, Case.INSENSITIVE)
                    .findFirst()
                    .setName(name);
            realm.commitTransaction();
            return true;
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

    public static boolean isExist(String address) {
        return getEntityWithAddress(address) != null;
    }

    public static AddressEntity getEntityWithAddress(String address) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            AddressEntity entity = realm.where(AddressEntity.class)
                    .equalTo("address", address, Case.INSENSITIVE)
                    .findFirst();
            if (entity != null) {
                return realm.copyFromRealm(entity);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return null;
    }

    public static boolean deleteAddressInfo(String address) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(AddressEntity.class)
                    .equalTo("address", address, Case.INSENSITIVE)
                    .findAll()
                    .deleteFirstFromRealm();
            realm.commitTransaction();
            return true;
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

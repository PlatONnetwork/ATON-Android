package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.RegionInfoEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RegionInfoDao extends BaseDao {

    private RegionInfoDao() {
        super();
    }

    public static RegionInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ArrayList<RegionInfoEntity> getRegionInfoListWithIpList(List<String> ipList) {
        ArrayList<RegionInfoEntity> list  = new ArrayList<>();
        Realm                       realm = null;
        try {
            String[] array = ipList.toArray(new String[ipList.size()]);
            realm = Realm.getDefaultInstance();
            RealmResults<RegionInfoEntity> results = realm.where(RegionInfoEntity.class).in("ip", array).findAll();
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

    public RegionInfoEntity getRegionInfoEntityWithIp(String ip) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RegionInfoEntity regionInfoEntity = realm.where(RegionInfoEntity.class).equalTo("ip", ip).findFirst();
            if (regionInfoEntity != null) {
                return realm.copyFromRealm(regionInfoEntity);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return null;
    }

    public RegionInfoEntity getRegionInfoWithIp(String ip) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RegionInfoEntity entity = realm.where(RegionInfoEntity.class)
                    .equalTo("ip", ip)
                    .findFirst();
           return realm.copyFromRealm(entity);
        } catch (Exception exp) {
            return null;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean insertRegionInfo(RegionInfoEntity entity) {
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

    public boolean insertBatchRegionInfo(List<RegionInfoEntity> entities) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entities);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public boolean deleteAll() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.delete(RegionInfoEntity.class);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
            return false;
        }
    }

    private final static class InstanceHolder {
        private final static RegionInfoDao INSTANCE = new RegionInfoDao();
    }
}

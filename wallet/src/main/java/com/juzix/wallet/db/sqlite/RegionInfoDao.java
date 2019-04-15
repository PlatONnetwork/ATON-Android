package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.RegionInfoEntity;
import com.juzix.wallet.engine.NodeManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RegionInfoDao {


    public static List<RegionInfoEntity> getRegionInfoListWithIpList(List<String> ipList) {
        List<RegionInfoEntity> list = new ArrayList<>();
        String[] array = ipList.toArray(new String[ipList.size()]);
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<RegionInfoEntity> results = realm.where(RegionInfoEntity.class)
                    .beginGroup()
                    .in("ip", array)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static RegionInfoEntity getRegionInfoEntityWithIp(String ip) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RegionInfoEntity regionInfoEntity = realm.where(RegionInfoEntity.class)
                    .beginGroup()
                    .equalTo("ip", ip)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findFirst();
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

    public static RegionInfoEntity getRegionInfoWithIp(String ip) {
        Realm realm = null;
        RegionInfoEntity regionInfoEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            RegionInfoEntity entity = realm.where(RegionInfoEntity.class)
                    .beginGroup()
                    .equalTo("ip", ip)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findFirst();
            if (entity != null) {
                regionInfoEntity = realm.copyFromRealm(entity);
            }
        } catch (Exception exp) {
            return null;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return regionInfoEntity;
    }

    public static boolean insertBatchRegionInfo(List<RegionInfoEntity> entities) {
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
}

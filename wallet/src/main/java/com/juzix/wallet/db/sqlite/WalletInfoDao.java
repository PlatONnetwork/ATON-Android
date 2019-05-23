package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.WalletEntity;
import com.juzix.wallet.engine.NodeManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class WalletInfoDao {

    /**
     * 获取钱包列表，根据updateTime升序
     * updateTime是指钱更新信息的时间
     * @return
     */
    public static List<WalletEntity> getWalletInfoList() {

        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .sort("updateTime", Sort.ASCENDING)
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

    public static boolean insertWalletInfo(WalletEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(entity);
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

    public static boolean updateNameWithUuid(String uuid, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
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

    public static boolean updateMnemonicWithUuid(String uuid, String mnemonic) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findFirst()
                    .setMnemonic(mnemonic);
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

    public static boolean updateUpdateTimeWithUuid(String uuid, long updateTime) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
                    .findFirst()
                    .setUpdateTime(updateTime);
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

    public static boolean deleteWalletInfo(String uuid) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
                    .endGroup()
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

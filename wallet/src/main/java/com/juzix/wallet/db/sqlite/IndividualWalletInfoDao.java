package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.IndividualWalletInfoEntity;
import com.juzix.wallet.engine.NodeManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class IndividualWalletInfoDao {

    public static List<IndividualWalletInfoEntity> getWalletInfoList() {

        List<IndividualWalletInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<IndividualWalletInfoEntity> results = realm.where(IndividualWalletInfoEntity.class)
                    .equalTo("nodeAddress", NodeManager.getInstance().getCurNodeAddress())
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

    public static boolean insertWalletInfo(IndividualWalletInfoEntity entity) {
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
            realm.where(IndividualWalletInfoEntity.class)
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
            realm.where(IndividualWalletInfoEntity.class)
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
        }finally {
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
            realm.where(IndividualWalletInfoEntity.class)
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
        }finally {
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
            realm.where(IndividualWalletInfoEntity.class)
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
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
        return false;
    }

}

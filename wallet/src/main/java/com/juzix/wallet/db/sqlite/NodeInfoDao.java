package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.NodeInfoEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class NodeInfoDao {

    public static boolean insertNode(NodeInfoEntity nodeInfoEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(nodeInfoEntity);
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

    public static boolean insertNodeList(List<NodeInfoEntity> nodeInfoEntityList) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(nodeInfoEntityList);
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

    public static boolean deleteNode(long id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(NodeInfoEntity.class).equalTo("id", id).findAll().deleteFirstFromRealm();
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

    public static boolean deleteNode(List<Long> idList) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(NodeInfoEntity.class).in("id", idList.toArray(new Long[0])).findAll().deleteAllFromRealm();
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

    public static boolean updateNode(long id, String nodeAddress) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(NodeInfoEntity.class).equalTo("id", id).findFirst().setNodeAddress(nodeAddress);
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

    public static boolean updateNode(long id, boolean isChecked) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(NodeInfoEntity.class).equalTo("id", id).findFirst().setChecked(isChecked);
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

    public static List<NodeInfoEntity> getNodeList() {

        List<NodeInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeInfoEntity> results = realm.where(NodeInfoEntity.class).findAll();
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

    public static List<NodeInfoEntity> getNode(boolean isChecked) {

        List<NodeInfoEntity> nodeInfoEntityList = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeInfoEntity> results = realm.where(NodeInfoEntity.class)
                    .equalTo("isChecked", isChecked)
                    .findAll();
            if (results != null) {
                nodeInfoEntityList = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return nodeInfoEntityList;
    }
}

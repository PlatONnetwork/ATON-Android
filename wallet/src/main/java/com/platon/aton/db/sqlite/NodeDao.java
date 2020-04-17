package com.platon.aton.db.sqlite;

import com.platon.aton.db.entity.NodeEntity;
import com.platon.framework.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author matrixelement
 */
public class NodeDao {

    private NodeDao() {
    }

    public static boolean insertNode(NodeEntity nodeInfoEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(nodeInfoEntity);
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

    public static boolean insertNodeList(List<NodeEntity> nodeInfoEntityList) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(nodeInfoEntityList);
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
            realm.where(NodeEntity.class).equalTo("id", id).findAll().deleteFirstFromRealm();
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
            realm.where(NodeEntity.class).in("id", idList.toArray(new Long[0])).findAll().deleteAllFromRealm();
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
            realm.where(NodeEntity.class).equalTo("id", id).findFirst().setNodeAddress(nodeAddress);
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
            realm.where(NodeEntity.class).equalTo("id", id).findFirst().setChecked(isChecked);
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

    public static List<NodeEntity> getNodeList() {

        List<NodeEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeEntity> results = realm.where(NodeEntity.class).findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static List<NodeEntity> getNode(boolean isChecked) {

        List<NodeEntity> nodeInfoEntityList = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeEntity> results = realm.where(NodeEntity.class)
                    .equalTo("isChecked", isChecked)
                    .findAll();
            if (results != null) {
                nodeInfoEntityList = realm.copyFromRealm(results);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return nodeInfoEntityList;
    }

    public static List<NodeEntity> getNode(String nodeAddress) {

        List<NodeEntity> infoEntityList = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            List<NodeEntity> nodeInfoEntityList = realm.where(NodeEntity.class)
                    .equalTo("nodeAddress", nodeAddress)
                    .findAll();
            if (nodeInfoEntityList != null) {
                infoEntityList = realm.copyFromRealm(nodeInfoEntityList);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return infoEntityList;
    }
}

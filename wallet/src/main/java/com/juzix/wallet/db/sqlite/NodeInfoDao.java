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

    private NodeInfoDao() {

    }

    public static NodeInfoDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public boolean insertNode(NodeInfoEntity nodeInfoEntity) {

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
            return false;
        }
    }

    public boolean insertNodeList(List<NodeInfoEntity> nodeInfoEntityList) {

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
            return false;
        }
    }

    public boolean deleteNode(long id) {
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

    public boolean deleteNode(List<Long> idList) {
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
            return false;
        }
    }

    public boolean updateNode(long id, String nodeAddress) {
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
            return false;
        }
    }

    public boolean updateNode(long id, boolean isChecked) {
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

    public List<NodeInfoEntity> getNodeList() {

        List<NodeInfoEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeInfoEntity> results = realm.where(NodeInfoEntity.class).findAll();
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

    public List<NodeInfoEntity> getNode(boolean isChecked) {

        List<NodeInfoEntity> nodeInfoEntityList = new ArrayList<>();

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<NodeInfoEntity> results = realm.where(NodeInfoEntity.class).equalTo("isChecked", isChecked).findAll();
            nodeInfoEntityList.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return nodeInfoEntityList;
    }

    private final static class InstanceHolder {
        private final static NodeInfoDao INSTANCE = new NodeInfoDao();
    }
}

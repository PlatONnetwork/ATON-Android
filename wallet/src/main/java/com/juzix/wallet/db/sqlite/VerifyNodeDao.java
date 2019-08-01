package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.VerifyNodeEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class VerifyNodeDao {

    public static List<VerifyNodeEntity> getVerifyNodeDataByState(String state, int ranking) {
        List<VerifyNodeEntity> list = new ArrayList<>();
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<VerifyNodeEntity> result = null;

            if (ranking < 0) {
                result = realm.where(VerifyNodeEntity.class)
                        .equalTo("nodeStatus", state)
                        .sort("ranking", Sort.ASCENDING)
                        .limit(10)
                        .findAll();
            } else {
                result = realm.where(VerifyNodeEntity.class)
                        .equalTo("nodeStatus", state)
                        .and()
                        .greaterThan("ranking", ranking)
                        .sort("ranking", Sort.DESCENDING)
                        .limit(10)
                        .findAll();
            }

            if (null != result) {
                list = realm.copyFromRealm(result);
            }

        } catch (Exception e) {

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

    public static List<VerifyNodeEntity> getVerifyNodeByAll(int rank) {
        List<VerifyNodeEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<VerifyNodeEntity> result = null;
            if (rank < 0) {
                result = realm.where(VerifyNodeEntity.class)
                        .sort("ranking", Sort.ASCENDING)
                        .limit(10)
                        .findAll();
            } else {
                result = realm.where(VerifyNodeEntity.class)
                        .greaterThan("ranking", rank)
                        .sort("ranking", Sort.ASCENDING)
                        .limit(10)
                        .findAll();
            }
            if (null != result) {
                list = realm.copyFromRealm(result);
            }

        } catch (Exception e) {
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


    //查询所有数据
    public static List<VerifyNodeEntity> getVerifyNodeList() {
        List<VerifyNodeEntity> list = new ArrayList<>();
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            RealmResults<VerifyNodeEntity> results = realm.where(VerifyNodeEntity.class)
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


    //删除所有数据
    public static boolean deleteVerifyNode() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<VerifyNodeEntity> all = realm.where(VerifyNodeEntity.class)
                    .findAll();
            if (all != null) {
                realm.deleteAll();
            }
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


    //插入数据到表中
    public static boolean insertVerifyNode(VerifyNodeEntity entity) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entity);
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


    //插入list到表中
    public static boolean insertVerifyNodeList(List<VerifyNodeEntity> entityList) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entityList);
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

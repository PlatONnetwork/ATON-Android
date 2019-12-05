package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.VerifyNodeEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class VerifyNodeDao {

    private VerifyNodeDao() {
    }

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
                        .findAllAsync();
            } else {
                result = realm.where(VerifyNodeEntity.class)
                        .equalTo("nodeStatus", state)
                        .and()
                        .greaterThan("ranking", ranking)
                        .sort("ranking", Sort.ASCENDING)
                        .limit(10)
                        .findAllAsync();
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


    //获取数据通过年化率和状态
    public static List<VerifyNodeEntity> getVerifyNodeByStateAndRate(String state, int ratePA) {
        List<VerifyNodeEntity> list = new ArrayList<>();
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<VerifyNodeEntity> result = null;

            if (ratePA < 0) {
                result = realm.where(VerifyNodeEntity.class)
                        .equalTo("nodeStatus", state)
                        .sort("ratePA", Sort.DESCENDING)
                        .limit(10)
                        .findAllAsync();
            } else {
                result = realm.where(VerifyNodeEntity.class)
                        .equalTo("nodeStatus", state)
                        .and()
                        .lessThan("ratePA", ratePA)
                        .sort("ratePA", Sort.DESCENDING)
                        .limit(10)
                        .findAllAsync();
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

    /**
     * 获取所有通过rank
     *
     * @param rank
     * @return
     */
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
                        .findAllAsync();
            } else {
                result = realm.where(VerifyNodeEntity.class)
                        .greaterThan("ranking", rank)
                        .sort("ranking", Sort.ASCENDING)
                        .limit(10)
                        .findAllAsync();
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


    //获取(所有)按年化率查询
    public static List<VerifyNodeEntity> getVerifyNodeAllByRate(int ratePAing) {
        List<VerifyNodeEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<VerifyNodeEntity> result = null;
            if (ratePAing < 0) {
                result = realm.where(VerifyNodeEntity.class)
                        .sort("ratePA", Sort.DESCENDING)
                        .limit(10)
                        .findAllAsync();
            } else {
                result = realm.where(VerifyNodeEntity.class)
                        .lessThan("ratePA", ratePAing)
                        .sort("ratePA", Sort.DESCENDING)
                        .limit(10)
                        .findAllAsync();
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
            realm.beginTransaction();
            realm.where(VerifyNodeEntity.class)
                    .findAll().deleteAllFromRealm();
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

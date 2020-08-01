package com.platon.aton.db.sqlite;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.WalletSelectedIndex;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.framework.utils.LogUtils;

import org.web3j.crypto.bech32.AddressBech32;
import org.web3j.crypto.bech32.AddressManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class WalletDao {

    private WalletDao() {
    }

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
                    .equalTo("isHD",false)
                    .or()
                    .equalTo("isShow",true)
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    /**
     * 查询所有钱包中普通及HD钱包中子钱包
     * @return
     */
    public static List<WalletEntity> getWalletInfoListByOrdinaryAndSubWallet() {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("isHD",false)
                    .or()
                    .equalTo("isHD",true)
                    .and()
                    .equalTo("depth",1)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    /**
     * 查询所有钱包中普通及HD母钱包
     * @return
     */
    public static List<WalletEntity> getWalletInfoListByOrdinaryAndHD() {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("depth",0)
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }


    /**
     * 查询所有HD钱包之(子钱包)根据prendId
     * @return
     */
    public static List<WalletEntity> getHDWalletListByParentId(String parentId) {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("parentId",parentId)
                    .and()
                    .equalTo("isHD",true)
                    .sort("updateTime", Sort.ASCENDING)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    /**
     * 查询wallet根据Uuid
     * @param uuid
     * @return
     */
    public static WalletEntity getWalletByUuid(String uuid){

        WalletEntity walletEntity = new WalletEntity();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity result = realm.where(WalletEntity.class)
                                .equalTo("uuid",uuid)
                                .findFirst();
            if(result != null){
                walletEntity = realm.copyFromRealm(result);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletEntity;
    }



    /**
     * 查询wallet根据wallet name
     * @param name
     * @return
     */
    public static WalletEntity getWalletByName(String name){

        WalletEntity walletEntity = new WalletEntity();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity result = realm.where(WalletEntity.class)
                    .equalTo("name",name)
                    .findFirst();
            if(result != null){
                walletEntity = realm.copyFromRealm(result);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletEntity;
    }


    /**
     * 查询所有HD钱包之(母钱包)
     * @return
     */
    public static List<WalletEntity> getHDParentWalletList() {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .equalTo("depth",0)
                    .and()
                    .equalTo("isHD",true)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }



    /**
     * 查询所有钱包
     * @return
     */
    public static List<WalletEntity> getAllWalletList() {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = realm.where(WalletEntity.class)
                    .findAll();
            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }


    /**
     * 查询钱包集合，根据(walletType、walletAddress、WalletName)
     * @return
     */
    public static List<WalletEntity> getWalletListByAddressAndNameAndType(@WalletTypeSearch int walletType,String walletName,String walletAddress) {
        List<WalletEntity> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> results = null;
            RealmQuery<WalletEntity> realmQuery = realm.where(WalletEntity.class);

            if(walletType == WalletTypeSearch.WALLET_ALL){

                realmQuery.equalTo("isHD",false)
                          .or()
                          .equalTo("isHD",true)
                          .and()
                          .equalTo("depth",1)
                          .and();
            }else if(walletType == WalletTypeSearch.HD_WALLET){

                realmQuery.equalTo("isHD",true)
                          .and()
                          .equalTo("depth",1)
                          .and();
            }else if(walletType == WalletTypeSearch.ORDINARY_WALLET){

                realmQuery.equalTo("isHD",false)
                          .and();
            }

            //关键字搜索
            if(!TextUtils.isEmpty(walletName)){
                //realmQuery.like("name",walletName);
                realmQuery.contains("name",walletName);
            }
            if(!TextUtils.isEmpty(walletAddress)){

                if(WalletManager.getInstance().isMainNetWalletAddress()){
                    realmQuery.equalTo("mainNetAddress",walletAddress);
                }else{
                    realmQuery.equalTo("testNetAddress",walletAddress);
                }
            }
            results = realmQuery.findAll();

            if (results != null) {
                list = realm.copyFromRealm(results);
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }




    public static String getWalletNameByAddress(String prefixAddress) {

        String fieldName = "";
        if(WalletManager.getInstance().isMainNetWalletAddress()){
            fieldName = "mainNetAddress";
        }else{
            fieldName = "testNetAddress";
        }

        String walletName = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity walletEntity = realm.where(WalletEntity.class)
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
                    .equalTo(fieldName, prefixAddress, Case.INSENSITIVE)
                    .findFirst();
            if (walletEntity != null) {
                walletName = walletEntity.getName();
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletName;
    }

    public static String getWalletAvatarByAddress(String prefixAddress) {

        String fieldName = "";
        if(WalletManager.getInstance().isMainNetWalletAddress()){
            fieldName = "mainNetAddress";
        }else{
            fieldName = "testNetAddress";
        }

        String walletAvatar = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity walletEntity = realm.where(WalletEntity.class)
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
                    .equalTo(fieldName, prefixAddress, Case.INSENSITIVE)
                    .findFirst();
            if (walletEntity != null) {
                walletAvatar = walletEntity.getAvatar();
            }
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return walletAvatar;
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


    public static boolean insertWalletInfoList(List<WalletEntity> walletEntities) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(walletEntities);
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


    /**
     * 重置所有钱包选中索引selectedIndex=0
     * @return
     */
    public static boolean resetAllWalletSelectedIndex() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<WalletEntity> WalletEntitys = realm.where(WalletEntity.class)
                                                            .findAll();
            realm.beginTransaction();
            for (int i = 0; i < WalletEntitys.size(); i++) {
                WalletEntitys.get(i).setSelectedIndex(WalletSelectedIndex.UNSELECTED);
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

    /**
     * 更新钱包选中钱包的selectedIndex
     * @param uuid
     * @param selectedIndex
     * @return
     */
    public static boolean updateSubWalletSelectedIndexByUuid(String uuid,@WalletSelectedIndex int selectedIndex) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity walletEntity = realm.where(WalletEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst();
            realm.beginTransaction();
            walletEntity.setSelectedIndex(selectedIndex);
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


    /**
     * 更新钱包是否首页显示isShow
     * @param uuid
     * @return
     */
    public static boolean updateSubWalletIsShowByUuid(String uuid,boolean isShow) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            WalletEntity walletEntity = realm.where(WalletEntity.class)
                    .equalTo("uuid", uuid)
                    .findFirst();
            realm.beginTransaction();
            walletEntity.setShow(isShow);
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


    /**
     * 修改单个钱包排序根据uuid
     * @param uuid
     * @param sortIndex
     * @return
     */
    public static boolean updateWalletSortIndexWithUuid(String uuid, int sortIndex) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst()
                    .setSortIndex(sortIndex);

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



    /**
     * 批量修改子钱包钱包排序根据parentId
     * @param parentId
     * @param sortIndex
     * @return
     */
    public static boolean updateBatchWalletSortIndexWithParentId(String parentId, int sortIndex) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("parentId", parentId)
                    .endGroup()
                    .findAll()
                    .setInt("sortIndex",sortIndex);
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


    public static boolean updateNameWithUuid(String uuid, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
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

    public static boolean updateBackedUpWithUuid(String uuid, boolean backedUp) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(WalletEntity.class)
                    .beginGroup()
                    .equalTo("uuid", uuid)
                    .and()
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
                    .endGroup()
                    .findFirst()
                    .setBackedUp(backedUp);
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
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
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
                    //.equalTo("chainId", NodeManager.getInstance().getChainId())
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


    public static boolean updateBetch32AddressWithWallet() {

        boolean updateWalletFlag = false;//默认钱包未做转换标识
        List<WalletEntity> walletEntities = getWalletInfoList();
        LogUtils.e("---walletEntities:" + walletEntities.size());
        LogUtils.e("---walletEntities:" + walletEntities.toString());
        for (int i = 0; i < walletEntities.size(); i++) {
            WalletEntity walletEntity = walletEntities.get(i);
            String walletAddress = walletEntity.getAddress();
            String mainNetAddress = walletEntity.getMainNetAddress();
            String testNetAddress = walletEntity.getTestNetAddress();
            String keyJson = walletEntity.getKeyJson();
            boolean isHD = walletEntity.isHD();
            LogUtils.e("---walletEntities  isHD:"  + isHD + ",id:" + i);
            LogUtils.e("---walletEntities  walletAddress:"  + walletAddress + ",id:" + i);
            LogUtils.e("---walletEntities  mainNetAddress:" + mainNetAddress + ",id:" + i);
            LogUtils.e("---walletEntities  testNetAddress:" + testNetAddress +",id:" + i);
            LogUtils.e("---walletEntities  keyJson:" + keyJson + ",id:" + i);


            if(!isHD){//判断(普通钱包)是否需要转换地址
                if((mainNetAddress != null && !mainNetAddress.equals("")) && (testNetAddress != null && !testNetAddress.equals(""))){
                    continue;
                }
            }else{
                continue;
            }

            LogUtils.e("-------walletEntities  开始转换-------");
            //1、转换address
            AddressBech32 addressBech32 = AddressManager.getInstance().executeEncodeAddress(walletAddress);
            LogUtils.e("---walletEntities  addressBech32.getMainnet:" + addressBech32.getMainnet());
            LogUtils.e("---walletEntities  addressBech32.getTestnet:" + addressBech32.getTestnet());
            walletEntities.get(i).setMainNetAddress(addressBech32.getMainnet());
            walletEntities.get(i).setTestNetAddress(addressBech32.getTestnet());
            //2、keyStore进行转换
            JSONObject keystoreJSON = JSON.parseObject(keyJson);
            LogUtils.e("---walletEntities keystoreJSON:" + keystoreJSON.toJSONString());
            if (keystoreJSON.containsKey("address")) {
                Object addressObj = keystoreJSON.get("address");
                if(addressObj instanceof String){
                    keystoreJSON.remove("address");
                    keystoreJSON.put("address", addressBech32);
                    walletEntities.get(i).setKeyJson(keystoreJSON.toString());
                }
            }
            //3、更新钱包转换标识
            updateWalletFlag = true;
            LogUtils.e("-------walletEntities  结束转换-------");
        }

        if((walletEntities == null || walletEntities.size() == 0) || !updateWalletFlag){
            LogUtils.e("---walletEntities 钱包为空/钱包未做转换，无需更新DB");
            return false;
        }

           Realm realm = null;
           try {
               realm = Realm.getDefaultInstance();
               LogUtils.e("---walletEntities  insertOrUpdate");
               realm.beginTransaction();
               realm.insertOrUpdate(walletEntities);
               realm.commitTransaction();
               LogUtils.e("---walletEntities  commitTransaction");

               List<WalletEntity> walletEntitiesNew = getWalletInfoList();
               LogUtils.e("---walletEntitiesNew 转换结束查询结果:" + walletEntitiesNew.size());
               LogUtils.e("---walletEntitiesNew 转换结束查询结果:" + walletEntitiesNew.toString());


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
                   // .equalTo("chainId", NodeManager.getInstance().getChainId())
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

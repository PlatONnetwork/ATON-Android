package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.entity.IndividualWalletInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualWalletInfoDao;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.ArrayList;

public class IndividualWalletManager {

    public static final int                   CODE_OK                  = 0;
    public static final int                   CODE_ERROR_NAME          = -1;
    public static final int                   CODE_ERROR_PASSWORD      = -2;
    public static final int                   CODE_ERROR_KEYSTORE      = -3;
    public static final int                   CODE_ERROR_PRIVATEKEY    = -4;
    public static final int                   CODE_ERROR_MNEMONIC      = -5;
    public static final int                   CODE_ERROR_WALLET_EXISTS = -200;
    public static final int                   CODE_ERROR_UNKNOW        = -999;
    private ArrayList<IndividualWalletEntity> mWalletList              = new ArrayList<>();


    private IndividualWalletManager() {
    }

    public static IndividualWalletManager getInstance() {
        return IndividualWalletManager.InstanceHolder.INSTANCE;
    }

    public void init() {
        if (!mWalletList.isEmpty()) {
            mWalletList.clear();
        }

        ArrayList<IndividualWalletInfoEntity> walletInfoList = IndividualWalletInfoDao.getInstance().getWalletInfoList();
        for (IndividualWalletInfoEntity walletInfoEntity : walletInfoList) {
            try {
                mWalletList.add(walletInfoEntity.buildWalletEntity());
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    public ArrayList<IndividualWalletEntity> getWalletList() {
        return mWalletList;
    }

    public String generateMnemonic() {
        return IndividualWalletService.getInstance().generateMnemonic();
    }

    public int importKeystore(IndividualWalletEntity walletEntity, String store, String name, String password) {
        if (!JZWalletUtil.isValidKeystore(store)){
            return CODE_ERROR_KEYSTORE;
        }
        if (TextUtils.isEmpty(name)){
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)){
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importKeystore(store, name, password);
            if (entity == null){
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList){
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())){
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            walletEntity.setWalletEntity(entity);
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.getInstance().insertWalletInfo(walletEntity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

    public int importPrivateKey(IndividualWalletEntity walletEntity, String privateKey, String name, String password) {
        if (!JZWalletUtil.isValidPrivateKey(privateKey)) {
            return CODE_ERROR_PRIVATEKEY;
        }
        if (TextUtils.isEmpty(name)){
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)){
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importPrivateKey(privateKey, name, password);
            if (entity == null){
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList){
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())){
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            walletEntity.setWalletEntity(entity);
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.getInstance().insertWalletInfo(walletEntity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

    public int importMnemonic(IndividualWalletEntity walletEntity, String mnemonic, String name, String password) {
        if (!JZWalletUtil.isValidMnemonic(mnemonic)){
            return CODE_ERROR_MNEMONIC;
        }
        if (TextUtils.isEmpty(name)){
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)){
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importMnemonic(mnemonic, name, password);
            if (entity == null){
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList){
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())){
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            walletEntity.setWalletEntity(entity);
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.getInstance().insertWalletInfo(walletEntity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

    public String exportKeystore(IndividualWalletEntity wallet, String password) {
        return IndividualWalletService.getInstance().exportKeystore(wallet, password);
    }

    public String exportPrivateKey(IndividualWalletEntity wallet, String password) {
        return IndividualWalletService.getInstance().exportPrivateKey(wallet, password);
    }

    public boolean updateWalletName(IndividualWalletEntity wallet, String newName) {
        for (IndividualWalletEntity walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                walletEntity.setName(newName);
                break;
            }
        }
        return IndividualWalletInfoDao.getInstance().updateNameWithUuid(wallet.getUuid(), newName);
    }

    public IndividualWalletEntity getWalletByAddress(String address){
        if (TextUtils.isEmpty(address)){
            return null;
        }
        for (IndividualWalletEntity walletEntity : mWalletList){
            if (walletEntity.getPrefixAddress().contains(address)){
                return walletEntity;
            }
        }
        return null;
    }

    public boolean deleteWallet(IndividualWalletEntity wallet) {
        for (IndividualWalletEntity walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                mWalletList.remove(walletEntity);
                break;
            }
        }
        return IndividualWalletInfoDao.getInstance().deleteWalletInfo(wallet.getUuid());
    }

    public boolean isValidWallet(IndividualWalletEntity walletEntity, String password) {
        try {
            return JZWalletUtil.decrypt(walletEntity.getKey(), password) != null;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    private static class InstanceHolder {
        private static volatile IndividualWalletManager INSTANCE = new IndividualWalletManager();
    }
}

package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.entity.IndividualWalletInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualWalletInfoDao;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;

public class IndividualWalletManager {

    public static final int CODE_OK = 0;
    public static final int CODE_ERROR_NAME = -1;
    public static final int CODE_ERROR_PASSWORD = -2;
    public static final int CODE_ERROR_KEYSTORE = -3;
    public static final int CODE_ERROR_PRIVATEKEY = -4;
    public static final int CODE_ERROR_MNEMONIC = -5;
    public static final int CODE_ERROR_WALLET_EXISTS = -200;
    public static final int CODE_ERROR_UNKNOW = -999;
    private ArrayList<IndividualWalletEntity> mWalletList = new ArrayList<>();


    private IndividualWalletManager() {
    }

    public static IndividualWalletManager getInstance() {
        return IndividualWalletManager.InstanceHolder.INSTANCE;
    }

    public void init() {
        if (!mWalletList.isEmpty()) {
            mWalletList.clear();
        }

        List<IndividualWalletInfoEntity> walletInfoList = IndividualWalletInfoDao.getWalletInfoList();
        for (IndividualWalletInfoEntity walletInfoEntity : walletInfoList) {
            try {
                mWalletList.add(walletInfoEntity.buildWalletEntity());
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    public List<String> getAddressList() {
        return Flowable.fromIterable(mWalletList)
                .map(new Function<IndividualWalletEntity, String>() {
                    @Override
                    public String apply(IndividualWalletEntity individualWalletEntity) throws Exception {
                        return individualWalletEntity.getPrefixAddress();
                    }
                }).collect(new Callable<List<String>>() {
                    @Override
                    public List<String> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<String>, String>() {
                    @Override
                    public void accept(List<String> strings, String s) throws Exception {
                        strings.add(s);
                    }
                }).blockingGet();
    }

    public String getWalletNameByWalletAddress(String walletAddress) {
        if (!mWalletList.isEmpty()) {
            for (IndividualWalletEntity walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equals(walletEntity.getPrefixAddress())) {
                    return walletEntity.getName();
                }
            }
        }

        return null;
    }

    /**
     * 获取第一个有效的(余额大于0)个人钱包，
     *
     * @return
     */
    public IndividualWalletEntity getFirstValidIndividualWalletBalance() {

        if (!mWalletList.isEmpty()) {
            for (int i = 0; i < mWalletList.size(); i++) {
                IndividualWalletEntity walletEntity = mWalletList.get(i);
                if (walletEntity.getBalance() > 0) {
                    return walletEntity;
                }
            }
        }

        return null;

    }

    public ArrayList<IndividualWalletEntity> getWalletList() {
        return mWalletList;
    }

    public String generateMnemonic() {
        return IndividualWalletService.getInstance().generateMnemonic();
    }

    public int createWalletWithMnemonic(IndividualWalletEntity walletEntity, String mnemonic, String name, String password) {
        if (!JZWalletUtil.isValidMnemonic(mnemonic)) {
            return CODE_ERROR_MNEMONIC;
        }
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importMnemonic(mnemonic, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic(JZWalletUtil.encryptMnemonic(entity.getKey(), mnemonic, password));
            walletEntity.setWalletEntity(entity);
            walletEntity.setNodeAddress(NodeManager.getInstance().getCurNodeAddress());
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

    public int importKeystore(IndividualWalletEntity walletEntity, String store, String name, String password) {
        if (!JZWalletUtil.isValidKeystore(store)) {
            return CODE_ERROR_KEYSTORE;
        }
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importKeystore(store, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic("");
            walletEntity.setWalletEntity(entity);
            walletEntity.setNodeAddress(NodeManager.getInstance().getCurNodeAddress());
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
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
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importPrivateKey(privateKey, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic("");
            walletEntity.setWalletEntity(entity);
            walletEntity.setNodeAddress(NodeManager.getInstance().getCurNodeAddress());
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

    public int importMnemonic(IndividualWalletEntity walletEntity, String mnemonic, String name, String password) {
        if (!JZWalletUtil.isValidMnemonic(mnemonic)) {
            return CODE_ERROR_MNEMONIC;
        }
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            IndividualWalletEntity entity = IndividualWalletService.getInstance().importMnemonic(mnemonic, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (IndividualWalletEntity param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic("");
            walletEntity.setWalletEntity(entity);
            walletEntity.setNodeAddress(NodeManager.getInstance().getCurNodeAddress());
            mWalletList.add(walletEntity);
            IndividualWalletInfoDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
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

    public boolean emptyMnemonic(String mnenonic) {
        String uuid = "";
        for (IndividualWalletEntity walletEntity : mWalletList) {
            if (mnenonic.equals(walletEntity.getMnemonic())) {
                walletEntity.setMnemonic("");
                uuid = walletEntity.getUuid();
                break;
            }
        }
        if (TextUtils.isEmpty(uuid)) {
            return false;
        }
        return IndividualWalletInfoDao.updateMnemonicWithUuid(uuid, "");
    }

    public boolean updateWalletName(IndividualWalletEntity wallet, String newName) {
        for (IndividualWalletEntity walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                walletEntity.setName(newName);
                return true;
            }
        }
        return false;
    }

    public void updateWalletBalance(String address, double balance) {
        int position = -1;
        for (int i = 0; i < mWalletList.size(); i++) {
            WalletEntity walletEntity = mWalletList.get(i);
            if (address.equals(walletEntity.getPrefixAddress())) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            mWalletList.get(position).setBalance(balance);
        }
    }

    public IndividualWalletEntity getWalletByAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        for (IndividualWalletEntity walletEntity : mWalletList) {
            if (walletEntity.getPrefixAddress().contains(address)) {
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
        return IndividualWalletInfoDao.deleteWalletInfo(wallet.getUuid());
    }

    public boolean isValidWallet(IndividualWalletEntity walletEntity, String password) {
        try {
            return JZWalletUtil.decrypt(walletEntity.getKey(), password) != null;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    public boolean walletNameExists(String walletName) {
        try {
            for (IndividualWalletEntity walletEntity : mWalletList) {
                if (walletName.equals(walletEntity.getName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    private static class InstanceHolder {
        private static volatile IndividualWalletManager INSTANCE = new IndividualWalletManager();
    }
}

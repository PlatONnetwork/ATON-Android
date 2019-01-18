package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.db.entity.OwnerInfoEntity;
import com.juzix.wallet.db.entity.SharedWalletInfoEntity;
import com.juzix.wallet.db.sqlite.SharedWalletInfoDao;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class SharedWalletManager {

    private ArrayList<SharedWalletEntity> mWalletList = new ArrayList<>();


    private SharedWalletManager() {
    }

    public static SharedWalletManager getInstance() {
        return SharedWalletManager.InstanceHolder.INSTANCE;
    }

    public void init() {
        if (!mWalletList.isEmpty()) {
            mWalletList.clear();
        }
        ArrayList<SharedWalletInfoEntity> walletInfoList = SharedWalletInfoDao.getInstance().getWalletInfoList();
        for (SharedWalletInfoEntity walletInfoEntity : walletInfoList) {
            try {
                mWalletList.add(walletInfoEntity.buildWalletEntity());
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    public void addWallet(SharedWalletEntity sharedWalletEntity) {
        mWalletList.add(sharedWalletEntity);
    }

    public ArrayList<SharedWalletEntity> getWalletList() {
        return mWalletList;
    }

    public boolean createWallet(String walletName, String contractAddress, String individualWalletAddress, int requiredSignNumber,
                                ArrayList<OwnerEntity> members) {
        try {
            SharedWalletEntity sharedWalletEntity = new SharedWalletEntity.Builder()
                    .uuid(UUID.randomUUID().toString())
                    .name(walletName)
                    .contractAddress(contractAddress)
                    .walletAddress(individualWalletAddress)
                    .requiredSignNumber(requiredSignNumber)
                    .owner(members)
                    .avatar(getWalletAvatar())
                    .finished(true)
                    .build();
            if (SharedWalletInfoDao.getInstance().insertWalletInfo(sharedWalletEntity.buildWalletInfoEntity())){
                mWalletList.add(sharedWalletEntity);
                return true;
            }
            return false;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    public boolean saveShareWallet(SharedWalletEntity sharedWalletEntity) {

        if (SharedWalletInfoDao.getInstance().insertWalletInfo(sharedWalletEntity.buildWalletInfoEntity())) {
            if (!mWalletList.contains(sharedWalletEntity)) {
                mWalletList.add(sharedWalletEntity);
            }
            return true;
        }
        return false;
    }

    public boolean updateWalletName(String walletUuid, String newName) {
        for (SharedWalletEntity walletEntity : mWalletList) {
            if (walletUuid.equals(walletEntity.getUuid())) {
                walletEntity.setName(newName);
                break;
            }
        }
        return SharedWalletInfoDao.getInstance().updateNameWithUuid(walletUuid, newName);
    }

    public boolean updateOwner(String walletUuid, ArrayList<OwnerEntity> newAddressEntityList) {
        ArrayList<OwnerInfoEntity> addressInfoEntityArrayList = new ArrayList<>();
        for (OwnerEntity entity : newAddressEntityList){
            OwnerInfoEntity addressInfoEntity = new OwnerInfoEntity.Builder()
//                    .uuid(entity.getUuid())
                    .uuid(UUID.randomUUID().toString())
                    .address(entity.getAddress())
                    .name(entity.getName())
                    .build();
            addressInfoEntityArrayList.add(addressInfoEntity);
        }
        if (!SharedWalletInfoDao.getInstance().updateOwnerNameWithUuid(walletUuid, addressInfoEntityArrayList)){
            return false;
        }
        for (SharedWalletEntity walletEntity : mWalletList) {
            if (walletUuid.equals(walletEntity.getUuid())) {
                walletEntity.setOwner(newAddressEntityList);
                break;
            }
        }
        return true;
    }

    public boolean deleteWallet(String walletUuid) {
        if (!SharedWalletInfoDao.getInstance().deleteWalletInfo(walletUuid)){
            return false;
        }
        for (SharedWalletEntity walletEntity : mWalletList) {
            try {
                if (walletUuid.equals(walletEntity.getUuid())) {
                    SharedWalletTransactionManager.getInstance().updateTransactionForRead(walletEntity);
                    mWalletList.remove(walletEntity);
                    break;
                }
            }catch (Exception exp){

            }
        }
        return true;
    }

    public SharedWalletEntity getWalletByUuid(String uuid){
        for (SharedWalletEntity walletEntity : mWalletList) {
            if (uuid.equals(walletEntity.getUuid())) {
                return walletEntity;
            }
        }
        return null;
    }

    public SharedWalletEntity getWalletByWalletAddress(String walletAddress){
        for (SharedWalletEntity walletEntity : mWalletList) {
            if (walletEntity.getPrefixAddress().contains(walletAddress)) {
                return walletEntity;
            }
        }
        return null;
    }

    public SharedWalletEntity getWalletByContractAddress(String contractAddress){
        for (SharedWalletEntity walletEntity : mWalletList) {
            if (!TextUtils.isEmpty(walletEntity.getPrefixContractAddress()) && walletEntity.getPrefixContractAddress().contains(contractAddress)) {
                return walletEntity;
            }
        }
        return null;
    }

    public String getWalletAvatar() {
        String[] avatarArray = App.getContext().getResources().getStringArray(R.array.wallet_avatar);
        return avatarArray[new Random().nextInt(avatarArray.length)];
    }

    private static class InstanceHolder {
        private static volatile SharedWalletManager INSTANCE = new SharedWalletManager();
    }
}

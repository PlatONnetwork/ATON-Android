package com.platon.aton.component.ui.presenter;


import com.platon.aton.component.ui.contract.VerificationMnemonicContract;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.base.BasePresenter;

import java.util.ArrayList;
import java.util.Collections;

public class VerificationMnemonicPresenter extends BasePresenter<VerificationMnemonicContract.View> implements VerificationMnemonicContract.Presenter {

    private ArrayList<VerificationMnemonicContract.DataEntity> mAllList = new ArrayList<>();
    private VerificationMnemonicContract.DataEntity[] mTopList;
    private String mPassword;
    private Wallet mWalletEntity;
    private String mMnemonic;

    @Override
    public void init() {
        mAllList.clear();
        mPassword = getView().getPasswordFromIntent();
        mWalletEntity = getView().getWalletFromIntent();
        mMnemonic = JZWalletUtil.decryptMnenonic(mWalletEntity.getKey(), mWalletEntity.getMnemonic(), mPassword);
        String[] worlds = mMnemonic.split(" ");
        for (String world : worlds) {
            VerificationMnemonicContract.DataEntity dataEntity = generateDataEntity(world);
            mAllList.add(dataEntity);
        }
        mTopList = new VerificationMnemonicContract.DataEntity[worlds.length];
        Collections.shuffle(mAllList);
        getView().showTopList(mTopList);
        getView().showBottomList(mAllList);
        getView().setCompletedBtnEnable(false);
        getView().setClearBtnEnable(false);
    }

    @Override
    public void checkTopListItem(int index) {
        VerificationMnemonicContract.DataEntity dataEntity = mTopList[index];
        if (dataEntity != null) {
            setBottomItemChecked(dataEntity.getMnemonic(), false);
            mTopList[index] = null;
        }
        getView().showTopList(mTopList);
        getView().showBottomList(mAllList);
        getView().setCompletedBtnEnable(enableBackup());
        getView().setClearBtnEnable(enableClear());
    }

    @Override
    public void checkBottomListItem(int index) {
        VerificationMnemonicContract.DataEntity dataEntity = mAllList.get(index);
        if (dataEntity.isChecked()) {
            dataEntity.setChecked(false);
            removeTopItem(dataEntity);
        } else {
            dataEntity.setChecked(true);
            addTopItem(dataEntity);
        }
        getView().showTopList(mTopList);
        getView().showBottomList(mAllList);
        getView().setCompletedBtnEnable(enableBackup());
        getView().setClearBtnEnable(enableClear());
    }


    @Override
    public void emptyChecked() {
        for (VerificationMnemonicContract.DataEntity dataEntity : mAllList) {
            dataEntity.setChecked(false);
        }
        for (int i = 0; i < mTopList.length; i++) {
            mTopList[i] = null;
        }
        getView().showTopList(mTopList);
        getView().showBottomList(mAllList);
        getView().setCompletedBtnEnable(enableBackup());
        getView().setClearBtnEnable(enableClear());
    }

    @Override
    public void submit() {
        StringBuilder builder = new StringBuilder();
        int len = mTopList.length;
        for (int i = 0; i < len; i++) {
            builder.append(mTopList[i].getMnemonic());
            if (i != len - 1) {
                builder.append(" ");
            }
        }
        VerificationMnemonicContract.View view = getView();
        if (view != null) {
            if (mMnemonic.equals(builder.toString())) {
                //备份成功
                view.showDisclaimerDialog();
                WalletManager.getInstance().updateBackedUpWithUuid(mWalletEntity.getUuid(), true);
                EventPublisher.getInstance().sendBackedUpWalletSuccessedEvent(mWalletEntity.getUuid());
            } else {
                //备份失败
                view.showBackupFailedDialog();
            }
        }
    }

    private VerificationMnemonicContract.DataEntity generateDataEntity(String mnemonic) {
        return new VerificationMnemonicContract.DataEntity.Builder()
                .mnemonic(mnemonic)
                .checked(false)
                .build();
    }

    private boolean enableClear() {
        if (mTopList.length != 12) {
            return false;
        }
        for (int i = 0; i < mTopList.length; i++) {
            if (mTopList[i] != null) {
                return true;
            }
        }
        return false;
    }

    private boolean enableBackup() {
        if (mTopList.length != 12) {
            return false;
        }
        for (int i = 0; i < mTopList.length; i++) {
            if (mTopList[i] == null) {
                return false;
            }
        }
        return true;
    }

    private void setBottomItemChecked(String mnemonic, boolean checked) {
        for (VerificationMnemonicContract.DataEntity item : mAllList) {
            if (mnemonic.equals(item.getMnemonic())) {
                item.setChecked(checked);
                break;
            }
        }
    }

    private void addTopItem(VerificationMnemonicContract.DataEntity dataEntity) {
        for (int i = 0; i < mTopList.length; i++) {
            if (mTopList[i] == null) {
                mTopList[i] = dataEntity;
                break;
            }
        }
    }

    private void removeTopItem(VerificationMnemonicContract.DataEntity dataEntity) {
        for (int i = 0; i < mTopList.length; i++) {
            if (mTopList[i] != null && mTopList[i].getMnemonic().equals(dataEntity.getMnemonic())) {
                mTopList[i] = null;
                break;
            }
        }
    }
}

package com.juzix.wallet.component.ui.presenter;


import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VerificationMnemonicContract;

import java.util.ArrayList;
import java.util.Collections;

public class VerificationMnemonicPresenter extends BasePresenter<VerificationMnemonicContract.View> implements VerificationMnemonicContract.Presenter {

    private ArrayList<VerificationMnemonicContract.DataEntity> mAllList     = new ArrayList<>();
    private ArrayList<VerificationMnemonicContract.DataEntity> mCheckedList = new ArrayList<>();

    public VerificationMnemonicPresenter(VerificationMnemonicContract.View view) {
        super(view);
    }

    public void init() {
        mAllList.clear();
        mCheckedList.clear();
        String   mnemonic = getView().getMnemonicFromIntent();
        String[] worlds   = mnemonic.split(" ");
        for (String world : worlds) {
            mAllList.add(generateDataEntity(world));
        }
        Collections.shuffle(mAllList);
        getView().showAllList(mAllList);
        getView().showCheckedList(mCheckedList);
    }

    @Override
    public void checkAllListItem(int index) {
        VerificationMnemonicContract.DataEntity dataEntity = mAllList.get(index);
        if (dataEntity.isChecked()) {
            return;
        }
        dataEntity.setChecked(true);
        mCheckedList.add(dataEntity);
        getView().showAllList(mAllList);
        getView().showCheckedList(mCheckedList);
        getView().setCompletedBtnEnable(mCheckedList.size() == mAllList.size());
        getView().setClearBtnEnable(!mCheckedList.isEmpty());
    }

    @Override
    public void uncheckItem(int index) {
        VerificationMnemonicContract.DataEntity dataEntity = mCheckedList.get(index);
        mCheckedList.remove(dataEntity);
        dataEntity.setChecked(false);
        getView().showAllList(mAllList);
        getView().showCheckedList(mCheckedList);
        getView().setCompletedBtnEnable(mCheckedList.size() == mAllList.size());
        getView().setClearBtnEnable(!mCheckedList.isEmpty());
    }

    @Override
    public void emptyChecked() {
        for (VerificationMnemonicContract.DataEntity dataEntity : mAllList) {
            dataEntity.setChecked(false);
        }
        mCheckedList.clear();
        getView().showAllList(mAllList);
        getView().showCheckedList(mCheckedList);
        getView().setCompletedBtnEnable(false);
        getView().setClearBtnEnable(false);
    }

    @Override
    public void submit() {
        StringBuilder builder = new StringBuilder();
        int           len     = mCheckedList.size();
        for (int i = 0; i < len; i++) {
            builder.append(mCheckedList.get(i).getMnemonic());
            if (i != len - 1) {
                builder.append(" ");
            }
        }
        VerificationMnemonicContract.View view = getView();
        if (view != null){
            String mnemonic = view.getMnemonicFromIntent();
            if (mnemonic.equals(builder.toString())) {
                view.showDisclaimerDialog();
            } else {
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
}

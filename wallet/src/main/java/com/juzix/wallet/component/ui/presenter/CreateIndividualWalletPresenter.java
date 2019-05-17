package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.CreateIndividualWalletContract;
import com.juzix.wallet.component.ui.view.BackupWalletActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

public class CreateIndividualWalletPresenter extends BasePresenter<CreateIndividualWalletContract.View> implements CreateIndividualWalletContract.Presenter {

    public CreateIndividualWalletPresenter(CreateIndividualWalletContract.View view) {
        super(view);
    }

    @Override
    public void createWallet(String name, String password, String repeatePassword) {
        if (name.length() > 12){
            getView().showNameError(string(R.string.validWalletNameTips), true);
            return;
        }
        if (!password.equals(repeatePassword)){
            getView().showPasswordError(string(R.string.passwordTips), true);
            return;
        }
        if (isExists(name)){
            return;
        }

        showLoadingDialog();
        new Thread(){
            @Override
            public void run() {
                String                 mnemonic     = IndividualWalletManager.getInstance().generateMnemonic();
                IndividualWalletEntity walletEntity = new IndividualWalletEntity.Builder().nodeAddress(NodeManager.getInstance().getCurNodeAddress()).build();
                int                    code         = IndividualWalletManager.getInstance().createWalletWithMnemonic(walletEntity, mnemonic, name, password);
                switch (code) {
                    case IndividualWalletManager.CODE_OK:
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.Extra.EXTRA_WALLET, walletEntity);
                        bundle.putString(Constants.Extra.EXTRA_MNEMONIC, mnemonic);
                        Message msg = mHandler.obtainMessage();
                        msg.what = MSG_OK;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        break;
                    case IndividualWalletManager.CODE_ERROR_MNEMONIC:
                        mHandler.sendEmptyMessage(MSG_MNEMONIC_ERROR);
                        break;
                    case IndividualWalletManager.CODE_ERROR_NAME:
                        break;
                    case IndividualWalletManager.CODE_ERROR_PASSWORD:
                        mHandler.sendEmptyMessage(MSG_PASSWORD_FAILED);
                        break;
                    case IndividualWalletManager.CODE_ERROR_WALLET_EXISTS:
                        mHandler.sendEmptyMessage(MSG_WALLET_EXISTS);
                        break;
                    case IndividualWalletManager.CODE_ERROR_UNKNOW:
                        mHandler.sendEmptyMessage(MSG_PASSWORD_FAILED);
                        break;
                }
            }
        }.start();
    }

    @Override
    public boolean isExists(String walletName) {
        return IndividualWalletManager.getInstance().walletNameExists(walletName);
    }

    private static final int MSG_OK = 1;
    private static final int MSG_PASSWORD_FAILED = -1;
    private static final int MSG_MNEMONIC_ERROR = -2;
    private static final int MSG_WALLET_EXISTS = -3;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_OK:
                    dismissLoadingDialogImmediately();
                    BaseActivity activity = currentActivity();
                    Bundle bundle = msg.getData();
                    BackupWalletActivity.actionStart(activity, bundle.getParcelable(Constants.Extra.EXTRA_WALLET));
                    activity.finish();
                    break;
                case MSG_PASSWORD_FAILED:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.createWalletFailed));
                    break;
                case MSG_MNEMONIC_ERROR:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.createWalletFailed));
                    break;
                case MSG_WALLET_EXISTS:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.createWalletFailed));
                    break;
            }
        }
    };
}

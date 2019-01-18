package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.view.ExportIndividualKeystoreActivity;
import com.juzix.wallet.component.ui.view.ExportIndividualPrivateKeyActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

public class ManageIndividualWalletPresenter extends BasePresenter<ManageIndividualWalletContract.View> implements ManageIndividualWalletContract.Presenter{

    public ManageIndividualWalletPresenter(ManageIndividualWalletContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        ManageIndividualWalletContract.View view = getView();
        if (view != null){
            IndividualWalletEntity walletEntity = view.getWalletEntityFromIntent();
            view.showWalletName(walletEntity.getName());
            view.showWalletAddress(walletEntity.getPrefixAddress());
            view.showWalletAvatar(walletEntity.getAvatar());
        }
    }

    @Override
    public void validPassword(int viewType, String password) {
        ManageIndividualWalletContract.View view = getView();
        if (viewType == ManageIndividualWalletContract.View.TYPE_MODIFY_NAME
                || viewType == ManageIndividualWalletContract.View.TYPE_DELETE_WALLET) {
            showLoadingDialog();
            new Thread() {
                @Override
                public void run() {
                    if (view != null) {
                        Message msg = mHandler.obtainMessage();
                        msg.arg1 = viewType;
                        msg.what = IndividualWalletManager.getInstance().isValidWallet(view.getWalletEntityFromIntent(), password) ? MSG_VALID_PWD_OK : MSG_VALID_PWD_FAILED;
                        mHandler.sendMessage(msg);
                    }
                }
            }.start();
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY) {
            showLoadingDialog();
            new Thread() {
                @Override
                public void run() {
                    if (view != null) {
                        String privateKey = IndividualWalletManager.getInstance().exportPrivateKey(view.getWalletEntityFromIntent(), password);
                        if (TextUtils.isEmpty(privateKey)) {
                            mHandler.sendEmptyMessage(MSG_VALID_PWD_FAILED);
                        } else {
                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = viewType;
                            msg.what = MSG_VALID_PWD_OK;
                            msg.obj = privateKey;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }.start();
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_KEYSTORE) {
            showLoadingDialog();
            new Thread() {
                @Override
                public void run() {
                    if (view != null) {
                        String keystore = IndividualWalletManager.getInstance().exportKeystore(view.getWalletEntityFromIntent(), password);
                        if (TextUtils.isEmpty(keystore)) {
                            mHandler.sendEmptyMessage(MSG_VALID_PWD_FAILED);
                        } else {
                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = viewType;
                            msg.what = MSG_VALID_PWD_OK;
                            msg.obj = keystore;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void deleteWallet() {
        ManageIndividualWalletContract.View view = getView();
        if (view != null) {
            new Thread(){
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(IndividualWalletManager.getInstance().deleteWallet(view.getWalletEntityFromIntent()) ? MSG_DELETE_WALLET_OK : MSG_DELETE_WALLET_FAILED);
                }
            }.start();
        }
    }

    @Override
    public void modifyName(String name) {
        showLoadingDialog();
        ManageIndividualWalletContract.View view = getView();
        if (view != null) {
            new Thread(){
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(IndividualWalletManager.getInstance().updateWalletName(view.getWalletEntityFromIntent(), name) ? MSG_MODIFY_NAME_OK : MSG_MODIFY_NAME_FAILED);
                }
            }.start();
        }
    }

    @Override
    public void exportPrivateKey() {
    }

    @Override
    public void exportKeystore() {

    }

    private static final int MSG_VALID_PWD_OK = 0;
    private static final int MSG_VALID_PWD_FAILED = 1;
    private static final int MSG_DELETE_WALLET_OK = 2;
    private static final int MSG_DELETE_WALLET_FAILED = 3;
    private static final int MSG_MODIFY_NAME_OK = 4;
    private static final int MSG_MODIFY_NAME_FAILED = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ManageIndividualWalletContract.View view = getView();
            switch (msg.what){
                case MSG_VALID_PWD_OK:
                    int viewType = msg.arg1;
                    switch (viewType) {
                        case ManageIndividualWalletContract.View.TYPE_MODIFY_NAME:
                            if (view != null) {
                                view.dimissPasswordDialog();
                                view.showModifyNameDialog();
                            }
                            dismissLoadingDialogImmediately();
                            break;
                        case ManageIndividualWalletContract.View.TYPE_DELETE_WALLET:
                            if (view != null) {
                                view.dimissPasswordDialog();
                            }
                            deleteWallet();
                            break;
                        case ManageIndividualWalletContract.View.TYPE_EXPORT_KEYSTORE:
                            ExportIndividualKeystoreActivity.actionStart(getContext(), (String)msg.obj);
                            dismissLoadingDialogImmediately();
                            if (view != null) {
                                view.dimissPasswordDialog();
                            }
                            break;
                        case ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY:
                            ExportIndividualPrivateKeyActivity.actionStart(getContext(), (String)msg.obj);
                            dismissLoadingDialogImmediately();
                            if (view != null) {
                                view.dimissPasswordDialog();
                            }
                            break;
                    }
                    break;
                case MSG_VALID_PWD_FAILED:
                    if (view != null){
                        view.showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips));
                    }
                    dismissLoadingDialogImmediately();
                    break;

                case MSG_DELETE_WALLET_OK:
                    dismissLoadingDialogImmediately();
                    currentActivity().finish();
                    break;

                case MSG_DELETE_WALLET_FAILED:
                    dismissLoadingDialogImmediately();
                    break;

                case MSG_MODIFY_NAME_OK:
                    dismissLoadingDialogImmediately();
                    currentActivity().finish();
                    break;

                case MSG_MODIFY_NAME_FAILED:
                    dismissLoadingDialogImmediately();
                    break;
            }
        }
    };
}

package com.platon.aton.component.ui.presenter;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.ImportKeystoreContract;
import com.platon.aton.component.ui.view.MainActivity;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.utils.CommonUtil;
import com.platon.framework.base.BasePresenter;

public class ImportKeystorePresenter extends BasePresenter<ImportKeystoreContract.View> implements ImportKeystoreContract.Presenter {

    @Override
    public void init() {
        if (isViewAttached()) {
            getView().showQRCode(getView().getKeystoreFromIntent());
        }
    }

    @Override
    public void checkPaste() {
        String text = CommonUtil.getTextFromClipboard(getContext());
        if (isViewAttached()) {
            getView().enablePaste(!TextUtils.isEmpty(text));
        }
    }

    @Override
    public void parseQRCode(String QRCode) {
        if (!TextUtils.isEmpty(QRCode)) {
            getView().showQRCode(QRCode);
        }
    }

    @Override
    public void importKeystore(String keystore, String name, String password) {

        if (isExists(name)) {
            return;
        }

        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                int code = WalletManager.getInstance().importKeystore(keystore, name, password);
                switch (code) {
                    case WalletManager.CODE_OK:
                        mHandler.sendEmptyMessage(MSG_OK);
                        break;
                    case WalletManager.CODE_ERROR_KEYSTORE:
                        mHandler.sendEmptyMessage(MSG_KEYSTORE_ERROR);
                        break;
                    case WalletManager.CODE_ERROR_NAME:
                        break;
                    case WalletManager.CODE_ERROR_PASSWORD:
                        mHandler.sendEmptyMessage(MSG_PASSWORD_FAILED);
                        break;
                    case WalletManager.CODE_ERROR_WALLET_EXISTS:
                        mHandler.sendEmptyMessage(MSG_WALLET_EXISTS);
                        break;
                    case WalletManager.CODE_ERROR_UNKNOW:
                        mHandler.sendEmptyMessage(MSG_PASSWORD_FAILED);
                        break;
                    default:
                        break;
                }
            }
        }.start();
    }

    @Override
    public boolean isExists(String walletName) {
        return WalletManager.getInstance().isWalletNameExists(walletName);
    }

    private static final int MSG_OK = 1;
    private static final int MSG_PASSWORD_FAILED = -1;
    private static final int MSG_KEYSTORE_ERROR = -2;
    private static final int MSG_WALLET_EXISTS = -3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    dismissLoadingDialogImmediately();
                    MainActivity.actionStart(currentActivity());
                    currentActivity().finish();
                    break;
                case MSG_PASSWORD_FAILED:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.validPasswordError));
                    break;
                case MSG_KEYSTORE_ERROR:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.parsedError, string(R.string.keystore)));
                    break;
                case MSG_WALLET_EXISTS:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.walletExists));
                    break;
                default:
                    break;
            }
        }
    };

}

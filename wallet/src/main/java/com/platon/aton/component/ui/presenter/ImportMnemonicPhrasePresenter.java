package com.platon.aton.component.ui.presenter;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.ImportMnemonicPhraseContract;
import com.platon.aton.component.ui.view.MainActivity;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletType;
import com.platon.aton.event.EventPublisher;
import com.platon.framework.base.BasePresenter;

import java.util.Arrays;
import java.util.List;

public class ImportMnemonicPhrasePresenter extends BasePresenter<ImportMnemonicPhraseContract.View> implements ImportMnemonicPhraseContract.Presenter {

    @Override
    public void init() {
        if (isViewAttached()) {
            String mnemonic = getView().getKeystoreFromIntent();
            if (TextUtils.isEmpty(mnemonic)) {
                return;
            }
            List<String> words = Arrays.asList(mnemonic.split(" "));
            if (words != null && words.size() == 12) {
                getView().showMnemonicWords(words);
            }
        }
    }

    @Override
    public void parseQRCode(String QRCode) {
        if (isViewAttached()) {
            if (TextUtils.isEmpty(QRCode)) {
                return;
            }
            List<String> words = Arrays.asList(QRCode.split(" "));
            if (words != null && words.size() == 12) {
                getView().showMnemonicWords(words);
            }
        }
    }

    @Override
    public void importMnemonic(String phrase, String name, String password, String repeatPassword, @WalletType int walletType) {

        if (isExists(name)) {
            return;
        }
        if (!password.equals(repeatPassword)) {
            showShortToast(string(R.string.passwordTips));
            return;
        }



        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                int code = WalletManager.getInstance().importMnemonic(phrase, name, password,walletType);
                switch (code) {
                    case WalletManager.CODE_OK:
                        mHandler.sendEmptyMessage(MSG_OK);
                        break;
                    case WalletManager.CODE_ERROR_MNEMONIC:
                        mHandler.sendEmptyMessage(MSG_MNEMONIC_ERROR);
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

    @Override
    public void loadDBWalletNumber() {
        getView().showWalletNumber(WalletManager.getInstance().getWalletInfoListByOrdinaryAndSubWalletNum());
    }

    private static final int MSG_OK = 1;
    private static final int MSG_PASSWORD_FAILED = -1;
    private static final int MSG_MNEMONIC_ERROR = -2;
    private static final int MSG_WALLET_EXISTS = -3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    //EventPublisher.getInstance().sendWalletNumberChangeEvent();
                    dismissLoadingDialogImmediately();
                    MainActivity.actionStart(currentActivity());
                    currentActivity().finish();
                    break;
                case MSG_PASSWORD_FAILED:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.validPasswordError));
                    break;
                case MSG_MNEMONIC_ERROR:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.parsedError, string(R.string.mnemonicPhrase)));
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

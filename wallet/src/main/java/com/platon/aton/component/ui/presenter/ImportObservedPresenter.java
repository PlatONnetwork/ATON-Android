package com.platon.aton.component.ui.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.ImportObservedContract;
import com.platon.aton.component.ui.view.MainActivity;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.CommonUtil;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.utils.PreferenceTool;


public class ImportObservedPresenter extends BasePresenter<ImportObservedContract.View> implements ImportObservedContract.Presenter {

    @Override
    public void parseQRCode(String QRCode) {
        getView().showQRCode(QRCode);
    }

    @Override
    public void IsImportObservedWallet(String content) {
        if (!TextUtils.isEmpty(content)) {
            getView().enableImportObservedWallet(true);
        } else {
            getView().enableImportObservedWallet(false);
        }
    }

    @Override
    public void checkPaste() {
        String text = CommonUtil.getTextFromClipboard(getContext());
        if (isViewAttached()) {
            getView().enablePaste(!TextUtils.isEmpty(text));
        }
    }

    //验证钱包地址是否合法，完成导入
    @Override
    public void importWalletAddress(String walletAddress) {
        showLoadingDialog();
        int code = WalletManager.getInstance().importWalletAddress(walletAddress);
        switch (code) {
            case WalletManager.CODE_OK:
                mHandler.sendEmptyMessage(MSG_OK);
                break;
            case WalletManager.CODE_ERROR_INVALIA_ADDRESS:
                mHandler.sendEmptyMessage(MSG_INVALID_ADDRESS);
                break;
            case WalletManager.CODE_ERROR_WALLET_EXISTS:
                mHandler.sendEmptyMessage(MSG_WALLET_EXISTS);
                break;
            default:
                break;
        }
    }

    private static final int MSG_OK = 1;
    private static final int MSG_WALLET_EXISTS = -1;
    private static final int MSG_INVALID_ADDRESS = -2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    EventPublisher.getInstance().sendWalletNumberChangeEvent();
                    dismissLoadingDialogImmediately();
                    PreferenceTool.putInt(NodeManager.getInstance().getChainId(), PreferenceTool.getInt(NodeManager.getInstance().getChainId(), 1) + 1);
                    MainActivity.actionStart(currentActivity());
                    currentActivity().finish();
                    break;
                case MSG_INVALID_ADDRESS:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.observed_invalid_address));
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

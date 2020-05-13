package com.platon.aton.component.ui.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.UnlockWithPasswordContract;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.biometric.BiometricPromptCompat;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.utils.PreferenceTool;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class UnlockWithPasswordPresenter extends BasePresenter<UnlockWithPasswordContract.View> implements UnlockWithPasswordContract.Presenter {

    private Wallet mWallet;

    @Override
    public void setSelectWallet(Wallet wallet) {
        mWallet = wallet;
        getView().updateWalletInfo(wallet);
    }

    @Override
    public Wallet getSelectedWallet() {
        return mWallet;
    }

    @Override
    public void init() {

        List<Wallet> walletList = WalletManager.getInstance().getWalletList();

        if (walletList == null || walletList.isEmpty()) {
            return;
        }

        mWallet = getFirstWalletExceptObserverWallet(walletList);

        if (!mWallet.isNull()) {
            setSelectWallet(mWallet);
        }

    }


    @Override
    public void unlock(String password) {
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                if (!WalletManager.getInstance().isValidWallet(mWallet, password)) {
                    mHandler.sendEmptyMessage(MSG_PASSWORD_FAILED);
                    return;
                } else {
                    mHandler.sendEmptyMessage(MSG_OK);
                }
            }
        }.start();
    }

    private Wallet getFirstWalletExceptObserverWallet(List<Wallet> walletList) {
        return Flowable.fromIterable(walletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return !wallet.isObservedWallet();
                    }
                })
                .firstElement()
                .defaultIfEmpty(Wallet.getNullInstance())
                .onErrorReturnItem(Wallet.getNullInstance())
                .blockingGet();
    }

    private static final int MSG_PASSWORD_FAILED = -1;
    private static final int MSG_OK = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PASSWORD_FAILED:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.validPasswordError));
                    break;
                case MSG_OK:
                    if (!BiometricPromptCompat.supportBiometricPromptCompat(currentActivity())) {
                        PreferenceTool.putBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
                    }
                    dismissLoadingDialogImmediately();
                    BaseActivity activity = currentActivity();
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    };
}

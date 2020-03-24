package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.utils.LogUtils;
import com.platon.aton.R;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.CreateWalletContract;
import com.platon.aton.component.ui.view.BackupWalletActivity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.utils.PreferenceTool;

import io.reactivex.functions.Consumer;

public class CreateWalletPresenter extends BasePresenter<CreateWalletContract.View> implements CreateWalletContract.Presenter {

    @SuppressLint("CheckResult")
    @Override
    public void createWallet(String name, String password, String repeatPassword) {
        if (name.length() > 20) {
            getView().showNameError(string(R.string.validWalletNameTips), true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            getView().showPasswordError(string(R.string.validPasswordEmptyTips), true);
            return;
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            getView().showPasswordError(string(R.string.validRepeatPasswordEmptyTips), true);
            return;
        }

        if (!password.equals(repeatPassword)) {
            getView().showPasswordError(string(R.string.passwordTips), true);
            return;
        }
        if (WalletManager.getInstance().isWalletNameExists(name)) {
            showLongToast(string(R.string.walletExists));
            return;
        }

        WalletManager.getInstance()
                .createWallet(name, password)
                .doOnSuccess(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        WalletManager.getInstance().addWallet(walletEntity);
                        WalletDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
                        PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG,false);
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        LogUtils.e("accept " + System.currentTimeMillis() + " " + Thread.currentThread().getName());
                        if (isViewAttached()) {
                            BackupWalletActivity.actionStart(currentActivity(), walletEntity);
                            currentActivity().finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached() && throwable instanceof CustomThrowable) {
                            CustomThrowable customThrowable = (CustomThrowable) throwable;
                            if (customThrowable.getDetailMsgRes() != -1) {
                                showLongToast(customThrowable.getDetailMsgRes());
                            }
                        }
                    }
                });
    }


}

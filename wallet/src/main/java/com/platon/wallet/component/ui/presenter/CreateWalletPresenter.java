package com.platon.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.platon.framework.util.LogUtils;
import com.platon.wallet.R;
import com.platon.wallet.app.CustomThrowable;
import com.platon.wallet.app.LoadingTransformer;
import com.platon.wallet.component.ui.base.BasePresenter;
import com.platon.wallet.component.ui.contract.CreateWalletContract;
import com.platon.wallet.component.ui.view.BackupWalletActivity;
import com.platon.wallet.config.AppSettings;
import com.platon.wallet.db.sqlite.WalletDao;
import com.platon.wallet.engine.WalletManager;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.utils.RxUtils;

import io.reactivex.functions.Consumer;

public class CreateWalletPresenter extends BasePresenter<CreateWalletContract.View> implements CreateWalletContract.Presenter {

    public CreateWalletPresenter(CreateWalletContract.View view) {
        super(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void createWallet(String name, String password, String repeatPassword) {
        if (name.length() > 12) {
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
                        AppSettings.getInstance().setOperateMenuFlag(false);
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

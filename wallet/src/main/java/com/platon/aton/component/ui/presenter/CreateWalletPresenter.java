package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.platon.aton.R;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.CreateWalletContract;
import com.platon.aton.component.ui.view.BackupWalletActivity;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletType;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.utils.PreferenceTool;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class CreateWalletPresenter extends BasePresenter<CreateWalletContract.View> implements CreateWalletContract.Presenter {

    @Override
    public void loadDBWalletNumber() {
        getView().showWalletNumber(WalletManager.getInstance().getWalletInfoListByOrdinaryAndSubWalletNum());
    }

    @SuppressLint("CheckResult")
    @Override
    public void createWallet(String name, String password, String repeatPassword,  @WalletType int walletType) {
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

        if(walletType <= 0){
            showLongToast(string(R.string.wallet_type_error));
            return;
        }

        if(walletType == WalletType.ORDINARY_WALLET){
            createOrdinaryWallet(name,password);

        }else if(walletType == WalletType.HD_WALLET){
            createHDWallet(name,password);
        }else{
            //异常
        }

    }

    private void createOrdinaryWallet(String name,String password){

        WalletManager.getInstance()
                .createWallet(name, password)
                .doOnSuccess(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        //walletEntity.setBackedUpPrompt(true);

                        WalletDao.insertWalletInfo(walletEntity.buildWalletInfoEntity());
                        WalletManager.getInstance().addAndSelectedWalletStatusNotice(walletEntity);
                        //WalletManager.getInstance().addWallet(walletEntity);

                        PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        if (isViewAttached()) {
                            //EventPublisher.getInstance().sendWalletNumberChangeEvent();
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


    private void createHDWallet(String name,String password){

        WalletManager.getInstance()
                .createWalletList(name, password)
                .doOnSuccess(new Consumer<List<Wallet>>() {
                    @Override
                    public void accept(List<Wallet> walletList) throws Exception {

                        //save db
                        List<WalletEntity> walletEntities = new ArrayList<>();
                        for(int i = 0; i < walletList.size(); i++) {
                            Wallet wallet = walletList.get(i);
                            WalletEntity walletEntity = wallet.buildWalletInfoEntity();
                            if(i == 1){
                                walletEntity.setShow(true);
                            }
                            walletEntities.add(walletEntity);
                        }
                        WalletDao.insertWalletInfoList(walletEntities);

                        //save cache
                        Wallet subFirstWallet = walletList.get(1);
                        //subFirstWallet.setBackedUpPrompt(true);
                        subFirstWallet.setShow(true);
                        WalletManager.getInstance().addAndSelectedWalletStatusNotice(subFirstWallet);
                        //WalletManager.getInstance().addWallet(subFirstWallet);


                        PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<List<Wallet>>() {
                    @Override
                    public void accept(List<Wallet> walletList) throws Exception {
                        if (isViewAttached()) {
                            //EventPublisher.getInstance().sendWalletNumberChangeEvent();
                            BackupWalletActivity.actionStart(currentActivity(), walletList.get(1));
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

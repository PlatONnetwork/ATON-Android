package com.platon.aton.component.ui.presenter;

import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.ManageWalletContract;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.ObservedWalletDialogFragment;
import com.platon.aton.component.ui.view.BackupMnemonicPhraseActivity;
import com.platon.aton.component.ui.view.ExportKeystoreActivity;
import com.platon.aton.component.ui.view.ExportPrivateKeyActivity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.SchedulersTransformer;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class ManageWalletPresenter extends BasePresenter<ManageWalletContract.View> implements ManageWalletContract.Presenter {

    private Wallet mWalletEntity;


    @Override
    public void init(Wallet wallet) {
        this.mWalletEntity = wallet;
    }

    @Override
    public Wallet getWalletData() {
        return this.mWalletEntity;
    }


    @Override
    public void showWalletInfo() {
        if (mWalletEntity != null && isViewAttached()) {

            if(!mWalletEntity.isHD()){//普通钱包
                List<Wallet> walletList = WalletManager.getInstance().getWalletList();
                for (Wallet walletEntity : walletList) {
                    if (mWalletEntity.getUuid().equals(walletEntity.getUuid())) {
                        mWalletEntity = walletEntity;
                        break;
                    }
                }
            }else{//HD钱包

                //查询HD母钱包信息组装到子钱包
                Wallet rootWallet = WalletManager.getInstance().getWalletInfoByUuid(mWalletEntity.getParentId());
                mWalletEntity.setMnemonic(rootWallet.getMnemonic());
                mWalletEntity.setKey(rootWallet.getKey());
            }

            getView().showWalletInfo(mWalletEntity);
        }
    }

    @Override
    public void validPassword(int viewType, Credentials credentials) {
        if (viewType == ManageWalletContract.View.TYPE_MODIFY_NAME
                || viewType == ManageWalletContract.View.TYPE_DELETE_WALLET) {
            deleteWallet();
        } else if (viewType == ManageWalletContract.View.TYPE_EXPORT_PRIVATE_KEY) {
            String privateKey = Numeric.toHexStringNoPrefixZeroPadded(credentials.getEcKeyPair().getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
            ExportPrivateKeyActivity.actionStart(getContext(), privateKey);
        } else if (viewType == ManageWalletContract.View.TYPE_EXPORT_KEYSTORE) {
            ExportKeystoreActivity.actionStart(getContext(), mWalletEntity.getKey());
        }
    }

    //删除观察钱包
    @Override
    public void deleteObservedWallet() {
        ObservedWalletDialogFragment.newInstance().setmConfirmListener(new ObservedWalletDialogFragment.ConfirmListener() {
            @Override
            public void confirm() {
                deleteWallet();
            }
        }).show(currentActivity().getSupportFragmentManager(), "deleteObserverWallet");
    }

    @Override
    public void deleteWallet() {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return WalletManager.getInstance().deleteWallet(mWalletEntity);
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            EventPublisher.getInstance().sendWalletNumberChangeEvent();
                            currentActivity().finish();
                        }
                    }
                });
    }

    @Override
    public void modifyName(String name) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return WalletDao.updateNameWithUuid(mWalletEntity.getUuid(), name);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        return WalletManager.getInstance().updateWalletName(mWalletEntity, name);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })
                .toSingle()
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().showWalletName(name);
                            EventPublisher.getInstance().sendWalletNumberChangeEvent();
                        }
                    }
                });
    }

    @Override
    public void modifyHDName(String name) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return WalletDao.updateNameWithUuid(mWalletEntity.getUuid(), name);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })
                .toSingle()
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().showWalletName(name);
                            EventPublisher.getInstance().sendWalletNumberChangeEvent();
                        }
                    }
                });
    }

    @Override
    public void backup() {
        InputWalletPasswordDialogFragment.newInstance(mWalletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password, Wallet wallet) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, mWalletEntity, BackupMnemonicPhraseActivity.BackupMnemonicExport.MAIN_ACTIVITY);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public boolean isExists(String walletName) {
        return WalletManager.getInstance().isWalletNameExistsFromDB(walletName);
    }



}

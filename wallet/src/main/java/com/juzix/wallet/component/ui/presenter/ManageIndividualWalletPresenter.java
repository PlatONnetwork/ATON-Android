package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.view.ExportIndividualKeystoreActivity;
import com.juzix.wallet.component.ui.view.ExportIndividualPrivateKeyActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class ManageIndividualWalletPresenter extends BasePresenter<ManageIndividualWalletContract.View> implements ManageIndividualWalletContract.Presenter {

    private IndividualWalletEntity mWalletEntity;


    public ManageIndividualWalletPresenter(ManageIndividualWalletContract.View view) {
        super(view);
        mWalletEntity = view.getWalletEntityFromIntent();
    }

    @Override
    public void showIndividualWalletInfo() {
        if (mWalletEntity != null && isViewAttached()) {
            getView().showWalletName(mWalletEntity.getName());
            getView().showWalletAddress(mWalletEntity.getPrefixAddress());
            getView().showWalletAvatar(mWalletEntity.getAvatar());
        }
    }

    @Override
    public void validPassword(int viewType, String password) {
        if (viewType == ManageIndividualWalletContract.View.TYPE_MODIFY_NAME
                || viewType == ManageIndividualWalletContract.View.TYPE_DELETE_WALLET) {
            checkValidWallet(mWalletEntity, password);
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY) {
            exportPrivateKey(mWalletEntity, password);
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_KEYSTORE) {
            exportKeystore(mWalletEntity, password);
        }
    }

    @Override
    public void deleteWallet() {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return IndividualWalletManager.getInstance().deleteWallet(mWalletEntity);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
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
                        return IndividualWalletManager.getInstance().updateWalletName(mWalletEntity, name);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().showWalletName(name);
                        }
                    }
                });
    }

    private void checkValidWallet(IndividualWalletEntity walletEntity, String password) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return IndividualWalletManager.getInstance().isValidWallet(walletEntity, password);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isViewAttached()) {
                            if (isSuccess) {
                                deleteWallet();
                            } else {
                                getView().showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips), password, ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY);
                            }
                        }
                    }
                });
    }

    private void exportPrivateKey(IndividualWalletEntity walletEntity, String password) {
        Single
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return IndividualWalletManager.getInstance().exportPrivateKey(walletEntity, password);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String privateKey) throws Exception {
                        if (isViewAttached()) {
                            if (TextUtils.isEmpty(privateKey)) {
                                getView().showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips), password, ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY);
                            } else {
                                ExportIndividualPrivateKeyActivity.actionStart(getContext(), privateKey);
                            }
                        }
                    }
                });
    }

    private void exportKeystore(IndividualWalletEntity walletEntity, String password) {
        Single
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return IndividualWalletManager.getInstance().exportKeystore(walletEntity, password);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String keyStore) throws Exception {
                        if (isViewAttached()) {
                            if (TextUtils.isEmpty(keyStore)) {
                                getView().showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips), password, ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY);
                            } else {
                                ExportIndividualKeystoreActivity.actionStart(getContext(), keyStore);
                            }
                        }
                    }
                });
    }
}

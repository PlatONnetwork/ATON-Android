package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.view.BackupMnemonicPhraseActivity;
import com.juzix.wallet.component.ui.view.BackupWalletActivity;
import com.juzix.wallet.component.ui.view.ExportIndividualKeystoreActivity;
import com.juzix.wallet.component.ui.view.ExportIndividualPrivateKeyActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.event.EventPublisher;

import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class ManageIndividualWalletPresenter extends BasePresenter<ManageIndividualWalletContract.View> implements ManageIndividualWalletContract.Presenter {

    private IndividualWalletEntity mWalletEntity;


    public ManageIndividualWalletPresenter(ManageIndividualWalletContract.View view) {
        super(view);
        mWalletEntity = getView().getWalletEntityFromIntent();
    }

    @Override
    public void showIndividualWalletInfo() {
        if (mWalletEntity != null && isViewAttached()) {
            ArrayList<IndividualWalletEntity> walletList = IndividualWalletManager.getInstance().getWalletList();
            for (IndividualWalletEntity walletEntity : walletList){
                if (mWalletEntity.getUuid().equals(walletEntity.getUuid())) {
                    mWalletEntity = walletEntity;
                }
            }
            getView().showWalletName(mWalletEntity.getName());
            getView().showWalletAddress(mWalletEntity.getPrefixAddress());
            getView().showWalletAvatar(mWalletEntity.getAvatar());
            boolean hasBackup = TextUtils.isEmpty(mWalletEntity.getMnemonic());
            getView().enableBackup(!hasBackup);
            getView().enableDelete(hasBackup);
        }
    }

    @Override
    public void validPassword(int viewType, Credentials credentials) {
        if (viewType == ManageIndividualWalletContract.View.TYPE_MODIFY_NAME
                || viewType == ManageIndividualWalletContract.View.TYPE_DELETE_WALLET) {
            deleteWallet();
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_PRIVATE_KEY) {
            ExportIndividualPrivateKeyActivity.actionStart(getContext(), Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey()));
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_KEYSTORE) {
            ExportIndividualKeystoreActivity.actionStart(getContext(), mWalletEntity.getKey());
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
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            EventPublisher.getInstance().sendUpdateWalletListEvent();
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
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().showWalletName(name);
                            EventPublisher.getInstance().sendUpdateWalletListEvent();
                        }
                    }
                });
    }

    @Override
    public void backup() { 
        InputWalletPasswordDialogFragment.newInstance((IndividualWalletEntity) mWalletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, mWalletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public boolean isExists(String walletName) {
        return IndividualWalletManager.getInstance().walletNameExists(walletName) ? true : SharedWalletManager.getInstance().walletNameExists(walletName);
    }

}

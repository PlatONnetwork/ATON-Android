package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.view.BackupMnemonicPhraseActivity;
import com.juzix.wallet.component.ui.view.ExportKeystoreActivity;
import com.juzix.wallet.component.ui.view.ExportPrivateKeyActivity;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class ManageIndividualWalletPresenter extends BasePresenter<ManageIndividualWalletContract.View> implements ManageIndividualWalletContract.Presenter {

    private Wallet mWalletEntity;

    public ManageIndividualWalletPresenter(ManageIndividualWalletContract.View view) {
        super(view);
        mWalletEntity = getView().getWalletEntityFromIntent();
    }

    @Override
    public void showIndividualWalletInfo() {
        if (mWalletEntity != null && isViewAttached()) {
            List<Wallet> walletList = WalletManager.getInstance().getWalletList();
            for (Wallet walletEntity : walletList) {
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
            String privateKey = Numeric.toHexStringNoPrefixZeroPadded(credentials.getEcKeyPair().getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
            ExportPrivateKeyActivity.actionStart(getContext(), privateKey);
        } else if (viewType == ManageIndividualWalletContract.View.TYPE_EXPORT_KEYSTORE) {
            ExportKeystoreActivity.actionStart(getContext(), mWalletEntity.getKey());
        }
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
                .compose(RxUtils.bindToLifecycle(currentActivity()))
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
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
                            EventPublisher.getInstance().sendUpdateWalletListEvent();
                        }
                    }
                });
    }

    @Override
    public void backup() {
        InputWalletPasswordDialogFragment.newInstance(mWalletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, mWalletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public boolean isExists(String walletName) {
        return WalletManager.getInstance().isWalletNameExists(walletName);
    }

}

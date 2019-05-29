package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.WalletManagerContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.view.BackupMnemonicPhraseActivity;
import com.juzix.wallet.component.ui.view.ManageIndividualWalletActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Wallet;

import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class WalletManagerPresenter extends BasePresenter<WalletManagerContract.View> implements WalletManagerContract.Presenter {

    private final static String TAG = WalletManagerPresenter.class.getSimpleName();

    private ArrayList<Wallet> mWalletList = new ArrayList<>();

    public WalletManagerPresenter(WalletManagerContract.View view) {
        super(view);
    }

    @Override
    public void fetchWalletList() {
        if (isViewAttached()) {
            List<Wallet> walletList = WalletManager.getInstance().getWalletList();
            if (!mWalletList.isEmpty()) {
                mWalletList.clear();
            }
            if (!walletList.isEmpty()) {
                mWalletList.addAll(walletList);
            }
            if (mWalletList.isEmpty()) {
                AppSettings.getInstance().setOperateMenuFlag(true);
                AppSettings.getInstance().setFaceTouchIdFlag(false);
                getView().showEmpty();
                return;
            }
            getView().showWalletList();
            getView().notifyWalletListChanged();
        }

    }

    @Override
    public void sortWalletList() {
        if (!mWalletList.isEmpty()) {
            Observable.fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    long updateTime = System.currentTimeMillis();
                    for (int i = 0; i < mWalletList.size(); i++) {
                        Wallet walletEntity = mWalletList.get(i);
                        updateTime += 10;
                        walletEntity.setUpdateTime(updateTime);
                        WalletDao.updateUpdateTimeWithUuid(walletEntity.getUuid(), updateTime);
                    }
                    return null;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aVoid) throws Exception {

                }
            });
        }
    }

    @Override
    public void backupWallet(int position) {
        Wallet walletEntity = mWalletList.get(position);
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void startAction(int position) {
        ManageIndividualWalletActivity.actionStart(currentActivity(), mWalletList.get(position));
    }

    @Override
    public ArrayList<Wallet> getDataSource() {
        return mWalletList;
    }
}

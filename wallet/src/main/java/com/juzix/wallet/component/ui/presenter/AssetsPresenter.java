package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.AssetsContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.view.BackupMnemonicPhraseActivity;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.web3j.crypto.Credentials;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class AssetsPresenter extends BasePresenter<AssetsContract.View> implements AssetsContract.Presenter {

    private static final String TAG = AssetsPresenter.class.getSimpleName();

    private List<Wallet> mWalletList;

    public AssetsPresenter(AssetsContract.View view) {
        super(view);
        mWalletList = WalletManager.getInstance().getWalletList();
    }

    @Override
    public void fetchWalletList() {
        if (!isViewAttached()) {
            return;
        }
        show();
    }

    @Override
    public List<Wallet> getRecycleViewDataSource() {
        return mWalletList;
    }

    @Override
    public void clickRecycleViewItem(Wallet walletEntity) {
        WalletManager.getInstance().setSelectedWallet(walletEntity);
        getView().showWalletList(walletEntity);
        getView().showWalletInfo(walletEntity);
        getView().setArgument(walletEntity);
    }

    @Override
    public void backupWallet() {
        Wallet walletEntity = WalletManager.getInstance().getSelectedWallet();
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public boolean needBackup(Wallet walletEntity) {
        if (walletEntity == null) {
            return false;
        }
        if (!TextUtils.isEmpty(walletEntity.getMnemonic())) {
            return true;
        }
        return false;
    }

    @Override
    public void fetchWalletsBalance() {

        Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Double>() {
                    @Override
                    public Double apply(Wallet walletEntity) throws Exception {
                        double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                        walletEntity.setBalance(balance == 0 ? walletEntity.getBalance() : balance);
                        return balance == 0 ? walletEntity.getBalance() : balance;
                    }
                })
                .reduce(new BiFunction<Double, Double, Double>() {
                    @Override
                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
                        return aDouble + aDouble2;
                    }
                })
                .toSingle()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        if (isViewAttached()) {
                            getView().showTotalBalance(balance);
                            Wallet walletEntity = WalletManager.getInstance().getSelectedWallet();
                            if (walletEntity != null) {
                                getView().showBalance(walletEntity.getBalance());
                            }
                            getView().finishRefresh();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getView().finishRefresh();
                        }
                    }
                });
    }


    private boolean isSelected(Wallet selectedWallet) {
        if (selectedWallet == null) {
            return false;
        }
        for (int i = 0; i < mWalletList.size(); i++) {
            if (mWalletList.get(i) == selectedWallet) {
                return true;
            }
        }
        return false;
    }

    private Wallet getSelectedWallet() {
        for (int i = 0; i < mWalletList.size(); i++) {
            Wallet walletEntity = mWalletList.get(i);
            return mWalletList.get(i);
        }
        return null;
    }

    private void show() {
        if (!isViewAttached()) {
            return;
        }
        if (mWalletList.isEmpty()) {
            getView().showTotalBalance(0);
            getView().showEmptyView(true);
            return;
        }
        getView().showEmptyView(false);
        Wallet walletEntity = WalletManager.getInstance().getSelectedWallet();
        if (isSelected(walletEntity)) {
            getView().showWalletList(walletEntity);
            getView().showWalletInfo(walletEntity);
        } else {
            //挑选一个当前选中的钱包
            walletEntity = getSelectedWallet();
            WalletManager.getInstance().setSelectedWallet(walletEntity);
            getView().showWalletList(walletEntity);
            getView().showWalletInfo(walletEntity);
            getView().setArgument(walletEntity);
        }
    }

    private void refreshWalletList() {//联名钱包相关的先屏蔽掉
        List<Wallet> walletList = WalletManager.getInstance().getWalletList();
        if (!mWalletList.isEmpty()) {
            mWalletList.clear();
        }
        if (!walletList.isEmpty()) {
            mWalletList.addAll(walletList);
        }
        if (mWalletList.isEmpty()) {
            return;
        }
        Collections.sort(mWalletList, new Comparator<Wallet>() {
            @Override
            public int compare(Wallet o1, Wallet o2) {
                return Long.compare(o1.getUpdateTime(), o2.getUpdateTime());
            }
        });
    }
}

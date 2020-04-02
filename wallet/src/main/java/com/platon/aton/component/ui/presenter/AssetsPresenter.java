package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.util.Log;

import com.platon.aton.component.ui.contract.AssetsContract;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.view.BackupMnemonicPhraseActivity;
import com.platon.aton.component.ui.view.TransactionDetailActivity;
import com.platon.aton.db.entity.TransactionRecordEntity;
import com.platon.aton.db.sqlite.TransactionRecordDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.utils.PreferenceTool;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AssetsPresenter extends BasePresenter<AssetsContract.View> implements AssetsContract.Presenter {

    private List<Wallet> mWalletList;
    private Disposable mDisposable;

    public AssetsPresenter() {
        mWalletList = WalletManager.getInstance().getWalletList();
    }

    @Override
    public void fetchWalletList() {
        if (isViewAttached()) {
            show();
        }
    }

    @Override
    public List<Wallet> getRecycleViewDataSource() {
        return mWalletList;
    }

    @Override
    public void clickRecycleViewItem(Wallet walletEntity) {
//        WalletManager.getInstance().setSelectedWallet(walletEntity);
        getView().setSelectedWallet(walletEntity);
        getView().showWalletInfo(walletEntity);
        getView().setArgument(walletEntity);
    }

    @Override
    public void backupWallet() {
        Wallet walletEntity = WalletManager.getInstance().getSelectedWallet();
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity, BackupMnemonicPhraseActivity.BackupMnemonicExport.MAIN_ACTIVITY);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void fetchWalletsBalance() {

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mDisposable = WalletManager.getInstance().getAccountBalance()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new Consumer<BigDecimal>() {
                    @Override
                    public void accept(BigDecimal balance) {
                        if (isViewAttached()) {
                            getView().showTotalBalance(balance.toPlainString());
                            Wallet wallet = WalletManager.getInstance().getSelectedWallet();
                            if (wallet != null) {
                                getView().showFreeBalance(wallet.getFreeBalance());
                                getView().showLockBalance(wallet.getLockBalance());
                            }
                            getView().finishRefresh();
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (isViewAttached()) {
                            getView().finishRefresh();
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().finishRefresh();
                        }
                    }
                });
    }

    @Override
    public void afterSendTransactionSucceed(Transaction transaction) {
        if (transaction.getTxType() == TransactionType.TRANSFER) {
            insertAndDeleteTransactionRecord(transaction.buildTransactionRecordEntity());
            backToTransactionListWithDelay();
        } else {
            TransactionDetailActivity.actionStart(getContext(), transaction, Collections.singletonList(transaction.getFrom()));
        }
    }

    /**
     * 延迟指定时间后返回交易列表页
     */
    @SuppressLint("CheckResult")
    private void backToTransactionListWithDelay() {
        Single
                .timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(aLong -> {
                    if (isViewAttached()) {
                        getView().resetView();
//                        getView().showTab(AssetsFragment.MainTab.TRANSACTION_LIST);
                    }
                });
    }

    private void insertAndDeleteTransactionRecord(TransactionRecordEntity transactionRecordEntity) {
        if (PreferenceTool.getBoolean(Constants.Preference.KEY_RESEND_REMINDER,true)) {
            Single
                    .fromCallable(() -> TransactionRecordDao.insertTransactionRecord(transactionRecordEntity) && TransactionRecordDao.deleteTimeoutTransactionRecord())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
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
        if (mWalletList != null && !mWalletList.isEmpty()) {
            return mWalletList.get(0);
        }
        return null;
    }

    private void show() {
        Log.e("AssetsFragment", "钱包列表是否为空：" + (mWalletList == null || mWalletList.isEmpty()));
        mWalletList = WalletManager.getInstance().getWalletList();
        if (mWalletList.isEmpty()) {
            getView().showTotalBalance("0");
            getView().showContent(true);
            return;
        }
        Collections.sort(mWalletList);
        Wallet walletEntity = WalletManager.getInstance().getSelectedWallet();
        if (!isSelected(walletEntity)) {
            //挑选一个当前选中的钱包
            walletEntity = getSelectedWallet();
//            WalletManager.getInstance().setSelectedWallet(walletEntity);
            getView().setArgument(walletEntity);
        }
        getView().showWalletList(mWalletList, walletEntity);
        getView().showWalletInfo(walletEntity);
        getView().showContent(false);
    }
}

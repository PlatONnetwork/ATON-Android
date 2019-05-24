package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualTransactionDetailContract;
import com.juzix.wallet.db.sqlite.AddressDao;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.utils.RxUtils;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * @author matrixelement
 */
public class TransactionDetailPresenter extends BasePresenter<IndividualTransactionDetailContract.View> implements IndividualTransactionDetailContract.Presenter {

    private Transaction mTransaction;
    private String mQueryAddress;

    public TransactionDetailPresenter(IndividualTransactionDetailContract.View view) {
        super(view);
        mTransaction = view.getTransactionFromIntent();
        mQueryAddress = view.getAddressFromIntent();
    }


    @SuppressLint("CheckResult")
    @Override
    public void loadData() {

        if (mTransaction != null) {

            getWalletNameByAddressFromWalletDB(mTransaction.getFrom())
                    .flatMap(new Function<String, SingleSource<String>>() {
                        @Override
                        public SingleSource<String> apply(String s) throws Exception {
                            if (TextUtils.isEmpty(s)) {
                                return getWalletNameByAddressFromAddressDB(mTransaction.getFrom());
                            } else {
                                return Single.just(s);
                            }
                        }
                    })
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(RxUtils.bindToLifecycle(getView()))
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String walletName) throws Exception {
                            if (isViewAttached()) {
                                getView().setTransactionDetailInfo(mTransaction, mQueryAddress, walletName);
                            }
                        }
                    });

        }


    }

    @SuppressLint("CheckResult")
    @Override
    public void updateTransactionDetailInfo(Transaction transaction) {
        if (transaction.equals(mTransaction)) {
            getWalletNameByAddressFromWalletDB(transaction.getFrom())
                    .flatMap(new Function<String, SingleSource<String>>() {
                        @Override
                        public SingleSource<String> apply(String s) throws Exception {
                            if (TextUtils.isEmpty(s)) {
                                return getWalletNameByAddressFromAddressDB(transaction.getFrom());
                            } else {
                                return Single.just(s);
                            }
                        }
                    })
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(RxUtils.bindToLifecycle(getView()))
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String walletName) throws Exception {
                            if (isViewAttached()) {
                                getView().setTransactionDetailInfo(transaction, mQueryAddress, walletName);
                            }
                        }
                    });
        }
    }

    private Single<String> getWalletNameByAddressFromWalletDB(String address) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return WalletDao.getWalletNameByAddress(address);
            }
        }).onErrorReturnItem("");
    }

    private Single<String> getWalletNameByAddressFromAddressDB(String address) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return AddressDao.getAddressNameByAddress(address);
            }
        }).onErrorReturnItem("");
    }
}

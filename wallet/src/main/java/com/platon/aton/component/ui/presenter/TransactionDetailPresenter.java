package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.TransactionDetailContract;
import com.platon.aton.db.sqlite.AddressDao;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.entity.Transaction;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BasePresenter;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * @author matrixelement
 */
public class TransactionDetailPresenter extends BasePresenter<TransactionDetailContract.View> implements TransactionDetailContract.Presenter {

    private Transaction mTransaction;
    private List<String> mQueryAddressList;

    @Override
    public void init() {
        if (isViewAttached()) {
            mTransaction = getView().getTransactionFromIntent();
            mQueryAddressList = getView().getAddressListFromIntent();
        }
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
                                getView().setTransactionDetailInfo(mTransaction, mQueryAddressList, walletName);
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
                                getView().setTransactionDetailInfo(transaction, mQueryAddressList, walletName);
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

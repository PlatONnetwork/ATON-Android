package com.juzix.wallet.component.ui.presenter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.LogUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.component.ui.view.AssetsFragment;
import com.juzix.wallet.component.ui.view.TransactionsFragment;
import com.juzix.wallet.db.entity.TransactionEntity;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


/**
 * @author matrixelement
 */
public class TransactionsPresenter extends BasePresenter<TransactionsContract.View> implements TransactionsContract.Presenter {

    private static final String TAG = TransactionsPresenter.class.getSimpleName();

    public final static String DIRECTION_OLD = "old";
    public final static String DIRECTION_NEW = "new";

    private List<Transaction> mTransactionList = new ArrayList<>();
    private Disposable mAutoRefreshDisposable;
    private Disposable mLoadLatestDisposable;
    private String mWalletAddress;

    public TransactionsPresenter(TransactionsContract.View view) {
        super(view);
        mAutoRefreshDisposable = new CompositeDisposable();
        mLoadLatestDisposable = new CompositeDisposable();
    }


    /**
     * 加载最新的数据，切换钱包时
     */
    @SuppressLint("CheckResult")
    @Override
    public void loadLatestData() {

        mWalletAddress = WalletManager.getInstance().getSelectedWalletAddress();

        if (TextUtils.isEmpty(mWalletAddress)) {
            return;
        }

        if (!mLoadLatestDisposable.isDisposed()) {
            mLoadLatestDisposable.dispose();
        }

        if (!mAutoRefreshDisposable.isDisposed()) {
            mAutoRefreshDisposable.dispose();
        }

        mLoadLatestDisposable = getTransactionList(mWalletAddress)
                .toObservable()
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<List<Transaction>>() {
                    @Override
                    public void accept(List<Transaction> transactionList) throws Exception {
                        //存在数据就刷新余额
                        if (isViewAttached()) {
                            ((AssetsFragment) (((TransactionsFragment) getView()).getParentFragment())).fetchWalletsBalance();
                        }
                    }
                })
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new Consumer<List<Transaction>>() {
                    @Override
                    public void accept(List<Transaction> transactionList) {
                        if (isViewAttached()) {
                            //先进行排序
                            List<Transaction> newTransactionList = getNewTransactionList(mTransactionList, transactionList, false);
                            Collections.sort(newTransactionList);
                            getView().notifyDataSetChanged(mTransactionList, newTransactionList, mWalletAddress, true);
                            mTransactionList = newTransactionList;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "loadLatestData onApiFailure");
                        if (isViewAttached()) {
                            getView().notifyDataSetChanged(mTransactionList, null, mWalletAddress, true);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadNew(String direction) {

        mWalletAddress = WalletManager.getInstance().getSelectedWalletAddress();

        if (TextUtils.isEmpty(mWalletAddress)) {
            return;
        }

        if (!mAutoRefreshDisposable.isDisposed()) {
            mAutoRefreshDisposable.dispose();
        }

        mAutoRefreshDisposable = getTransactionListWithTime(mWalletAddress, direction)
                .toObservable()
                .doOnNext(new Consumer<List<Transaction>>() {
                    @Override
                    public void accept(List<Transaction> transactionList) throws Exception {
                        //存在数据就刷新余额
                        if (isViewAttached()) {
                            ((AssetsFragment) (((TransactionsFragment) getView()).getParentFragment())).fetchWalletsBalance();
                        }
                    }
                })
                .compose(RxUtils.getSchedulerTransformer())
                .compose(RxUtils.bindToParentLifecycleUtilEvent(getView(), FragmentEvent.STOP))
                .subscribe(new Consumer<List<Transaction>>() {
                    @Override
                    public void accept(List<Transaction> transactionList) throws Exception {
                        if (isViewAttached() && !transactionList.isEmpty()) {
                            //先进行排序
                            List<Transaction> newTransactionList = getNewTransactionList(mTransactionList, transactionList, true);
                            Collections.sort(newTransactionList);
                            getView().notifyDataSetChanged(mTransactionList, newTransactionList, mWalletAddress, false);
                            mTransactionList = newTransactionList;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "onApiFailure");
                    }
                });
    }

    @Override
    public void loadMore() {

        if (TextUtils.isEmpty(mWalletAddress)) {
            return;
        }

        if (!mAutoRefreshDisposable.isDisposed()) {
            mAutoRefreshDisposable.dispose();
        }

        getTransactionList(mWalletAddress, DIRECTION_OLD, getBeginSequenceByDirection(DIRECTION_OLD))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribe(new ApiSingleObserver<List<Transaction>>() {
                    @Override
                    public void onApiSuccess(List<Transaction> transactions) {
                        Log.e(TAG, "onApiSuccess");
                        if (isViewAttached()) {
                            //先进行排序
                            Collections.sort(transactions);
                            List<Transaction> newTransactionList = getNewTransactionList(mTransactionList, transactions, true);
                            Collections.sort(newTransactionList);
                            //累加,mTransactionList不可能为null,放在最后面
                            getView().notifyDataSetChanged(mTransactionList, newTransactionList, mWalletAddress, false);

                            mTransactionList = newTransactionList;
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
//                            getView().finishLoadMore();
                        }
                    }
                });
    }

    @Override
    public synchronized void addNewTransaction(Transaction transaction) {
        if (isViewAttached()) {
            if (mTransactionList.contains(transaction)) {
                //更新
                List<Transaction> newTransactionList = new ArrayList<>(mTransactionList);
                int index = newTransactionList.indexOf(transaction);
                newTransactionList.set(index, transaction);
                Collections.sort(newTransactionList);
                getView().notifyDataSetChanged(mTransactionList, newTransactionList, mWalletAddress, false);
                mTransactionList = newTransactionList;
            } else {
                //添加
                List<Transaction> newTransactionList = getNewTransactionList(mTransactionList, Arrays.asList(transaction), true);
                Collections.sort(newTransactionList);
                getView().notifyDataSetChanged(mTransactionList, newTransactionList, mWalletAddress, false);
                mTransactionList = newTransactionList;
            }
        }
    }

    private List<Transaction> getNewTransactionList(List<Transaction> oldTransactionList, List<Transaction> curTransactionList, boolean isLoadMore) {
        List<Transaction> oldList = oldTransactionList == null ? new ArrayList<Transaction>() : oldTransactionList;
        List<Transaction> curList = curTransactionList;
        List<Transaction> newList = new ArrayList<>();
        if (isLoadMore) {
            newList.addAll(oldList);
            newList.removeAll(curList);
            newList.addAll(curList);
        } else {
            newList = curList;
        }
        return newList;
    }


    /**
     * 定时刷新交易记录
     *
     * @param walletAddress
     * @param direction
     * @return
     */
    private Flowable<List<Transaction>> getTransactionListWithTime(String walletAddress, String direction) {
        return getTransactionList(walletAddress, DIRECTION_NEW, getBeginSequenceByDirection(direction))
                .flatMap(new Function<Response<ApiResponse<List<Transaction>>>, SingleSource<List<Transaction>>>() {
                    @Override
                    public SingleSource<List<Transaction>> apply(Response<ApiResponse<List<Transaction>>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                            return Single.just(apiResponseResponse.body().getData());
                        } else {
                            return Single.error(new Throwable());
                        }
                    }
                })
                .toFlowable();
    }


    /**
     * 切换钱包，每次都是获取最新的数据
     *
     * @param walletAddress
     * @return
     */
    private Single<List<Transaction>> getTransactionList(String walletAddress) {

        return getTransactionListFromDB(walletAddress).flatMap(new Function<List<Transaction>, SingleSource<List<Transaction>>>() {
            @Override
            public SingleSource<List<Transaction>> apply(List<Transaction> transactionList) throws Exception {
                LogUtils.d("getTransactionListFromDB success" + transactionList);
                return getTransactionList(walletAddress, DIRECTION_NEW, -1)
                        .flatMap(new Function<Response<ApiResponse<List<Transaction>>>, SingleSource<List<Transaction>>>() {
                            @Override
                            public SingleSource<List<Transaction>> apply(Response<ApiResponse<List<Transaction>>> apiResponseResponse) throws Exception {
                                LogUtils.d("getTransactionList success" + apiResponseResponse.toString());
                                if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                                    transactionList.addAll(apiResponseResponse.body().getData());
                                }
                                return Single.just(transactionList);
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.d(throwable.getMessage());
                            }
                        });
            }
        });
    }

    /**
     * 从数据中获取未完成的交易记录
     *
     * @param walletAddress
     * @return
     */
    private Single<List<Transaction>> getTransactionListFromDB(String walletAddress) {

        LogUtils.d("getTransactionListFromDB" + walletAddress);

        return Flowable.fromCallable(new Callable<List<TransactionEntity>>() {
            @Override
            public List<TransactionEntity> call() throws Exception {
                return TransactionDao.getTransactionList(walletAddress);
            }
        }).flatMap(new Function<List<TransactionEntity>, Publisher<TransactionEntity>>() {
            @Override
            public Publisher<TransactionEntity> apply(List<TransactionEntity> transactionEntities) throws Exception {
                return Flowable.fromIterable(transactionEntities);
            }
        }).map(new Function<TransactionEntity, Transaction>() {
            @Override
            public Transaction apply(TransactionEntity transactionEntity) throws Exception {
                return transactionEntity.toTransaction();
            }
        }).map(new Function<Transaction, Transaction>() {
            @Override
            public Transaction apply(Transaction transaction) throws Exception {
                LogUtils.e("发送交易完成开始轮询。。" + transaction.toString());
                if (transaction.getTxReceiptStatus() == TransactionStatus.PENDING) {
                    TransactionManager.getInstance().getTransactionByLoop(transaction);
                }
                return transaction;
            }
        })
                .toList()
                .onErrorReturnItem(new ArrayList<>());
    }

    private List<Transaction> addAll(List<Transaction> transactionList, String direction) {
        return Flowable
                .fromIterable(transactionList)
                .collectInto(mTransactionList, new BiConsumer<List<Transaction>, Transaction>() {
                    @Override
                    public void accept(List<Transaction> transactionList, Transaction transaction) throws Exception {
                        //如果本地与服务器存在相同记录，以服务器记录为主
                        if (mTransactionList.contains(transaction)) {
                            int index = mTransactionList.indexOf(transaction);
                            mTransactionList.set(index, transaction);
                        } else {
                            if (DIRECTION_NEW.equals(direction)) {
                                transactionList.add(0, transaction);
                            } else {
                                transactionList.add(transactionList.size(), transaction);
                            }
                        }
                    }
                })
                .blockingGet();
    }

    private Single<Response<ApiResponse<List<Transaction>>>> getTransactionList(String walletAddress, String direction, long beginSequence) {
        return ServerUtils
                .getCommonApi()
                .getTransactionList(ApiRequestBody.newBuilder()
                        .put("walletAddrs", new String[]{walletAddress})
                        .put("beginSequence", beginSequence)
                        .put("listSize", 20)
                        .put("direction", direction)
                        .build());
    }

    private long getBeginSequenceByDirection(String direction) {
        //拉最新的
        if (mTransactionList == null || mTransactionList.isEmpty()) {
            return -1;
        }
        //拉比当前最新的还大的
        if (DIRECTION_NEW.equals(direction)) {
            return getValidBiggerSequence(mTransactionList);
        } else {
            return getValidSmallerSequence(mTransactionList);
        }
    }

    private long getValidBiggerSequence(List<Transaction> transactionList) {

        return Flowable
                .range(0, transactionList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return transactionList.get(integer).getSequence() != 0;
                    }
                })
                .map(new Function<Integer, Long>() {
                    @Override
                    public Long apply(Integer integer) throws Exception {
                        return transactionList.get(integer).getSequence();
                    }
                })
                .firstElement()
                .defaultIfEmpty(-1L)
                .onErrorReturnItem(-1L)
                .blockingGet();
    }

    private long getValidSmallerSequence(List<Transaction> transactionList) {

        return Flowable
                .range(0, transactionList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return transactionList.get(integer).getSequence() != 0;
                    }
                })
                .lastElement()
                .map(new Function<Integer, Long>() {
                    @Override
                    public Long apply(Integer integer) throws Exception {
                        return transactionList.get(integer).getSequence();
                    }
                })
                .blockingGet();
    }
}

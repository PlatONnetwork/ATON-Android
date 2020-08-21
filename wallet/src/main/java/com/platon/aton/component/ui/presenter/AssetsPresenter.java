package com.platon.aton.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.platon.aton.component.ui.contract.AssetsContract;
import com.platon.aton.component.ui.view.TransactionDetailActivity;
import com.platon.aton.db.entity.TransactionEntity;
import com.platon.aton.db.sqlite.TransactionDao;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiErrorCode;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.utils.LogUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author ziv
 * date On 2020-03-25
 */
public class AssetsPresenter extends BasePresenter<AssetsContract.View> implements AssetsContract.Presenter {

    private static final String TAG = TransactionsPresenter.class.getSimpleName();

    private Map<String, List<Transaction>> mTransactionMap = new HashMap<>();

    /**
     * 自动刷新交易列表
     */
    private Disposable mAutoRefreshDisposable;
    /**
     * 加载最新的交易列表
     */
    private Disposable mLoadLatestDisposable;
    /**
     * 获取钱包余额
     */
    private Disposable mFetchWalletBalanceDisposable;

    private String mWalletAddress;

    public AssetsPresenter() {
        mAutoRefreshDisposable = new CompositeDisposable();
        mLoadLatestDisposable = new CompositeDisposable();
        mFetchWalletBalanceDisposable = new CompositeDisposable();
    }


    @Override
    public void fetchWalletBalance() {

        if (!mFetchWalletBalanceDisposable.isDisposed()) {
            mFetchWalletBalanceDisposable.dispose();
        }

        mFetchWalletBalanceDisposable = WalletManager.getInstance().getAccountBalance()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new Consumer<BigDecimal>() {
                    @Override
                    public void accept(BigDecimal balance) throws Exception {
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
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getView().finishRefresh();
                        }
                    }
                });
    }

    @Override
    public void fetchWalletBalanbceBySelected(String address) {
        ServerUtils
                .getCommonApi()
                .getAccountBalance(ApiRequestBody.newBuilder()
                        .put("addrs", Arrays.asList(address))
                        .build())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {

                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        if (isViewAttached() && accountBalances != null && !accountBalances.isEmpty()) {
                            AccountBalance balance = accountBalances.get(0);
                            getView().showFreeBalance(balance.getFree());
                            getView().showLockBalance(balance.getLock());
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });

    }

    /**
     * 切换钱包时，获取全部数据
     */
    @Override
    @SuppressLint("CheckResult")
    public void loadData() {

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
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new Consumer<Pair<List<Transaction>, List<Transaction>>>() {
                    @Override
                    public void accept(Pair<List<Transaction>, List<Transaction>> pair) {
                        if (isViewAttached()) {
                            List<Transaction> dbTransactionList = pair.first;
                            List<Transaction> netTransactionList = pair.second;
                            List<Transaction> transactionList = new ArrayList<>();
                            transactionList.addAll(netTransactionList);
                            transactionList.removeAll(dbTransactionList);
                            transactionList.addAll(dbTransactionList);
                            //先进行排序
                            List<Transaction> transactions = get(mWalletAddress);
                            List<Transaction> newTransactionList = getNewTransactionList(transactions, transactionList, true);
                            Collections.sort(newTransactionList);
                            getView().notifyTransactionSetChanged(transactions, newTransactionList, mWalletAddress, true);

                            put(mWalletAddress, newTransactionList);

                            deleteExceptionalTransaction(transactions, netTransactionList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "loadLatestData onApiFailure");
                        if (isViewAttached()) {
                            getView().notifyTransactionSetChanged(get(mWalletAddress), get(mWalletAddress), mWalletAddress, true);
                        }
                    }
                });
    }

    /**
     * 根据方向加载最新的数据
     *
     * @param direction
     */
    @Override
    @SuppressLint("CheckResult")
    public void loadNewData(String direction) {

        mWalletAddress = WalletManager.getInstance().getSelectedWalletAddress();

        if (TextUtils.isEmpty(mWalletAddress)) {
            return;
        }

        if (!mAutoRefreshDisposable.isDisposed()) {
            mAutoRefreshDisposable.dispose();
        }

        mAutoRefreshDisposable = getTransactionListWithTime(mWalletAddress, direction)
                .toObservable()
                .compose(RxUtils.getSchedulerTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Pair<List<Transaction>, List<Transaction>>>() {
                    @Override
                    public void accept(Pair<List<Transaction>, List<Transaction>> pair) throws Exception {
                        if (isViewAttached()) {
                            //先进行排序
                            List<Transaction> transactions = get(mWalletAddress);
                            List<Transaction> dbTransactionList = pair.first;
                            List<Transaction> netTransactionList = pair.second;
                            List<Transaction> transactionList = new ArrayList<>();
                            transactionList.addAll(netTransactionList);
                            transactionList.removeAll(dbTransactionList);
                            transactionList.addAll(dbTransactionList);

                            List<Transaction> newTransactionList = getNewTransactionList(transactions, transactionList, true);
                            Collections.sort(newTransactionList);
                            getView().notifyTransactionSetChanged(transactions, newTransactionList, mWalletAddress, false);

                            put(mWalletAddress, newTransactionList);

                            deleteExceptionalTransaction(transactions, netTransactionList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "onApiFailure");
                        if (isViewAttached()) {
                            getView().notifyTransactionSetChanged(get(mWalletAddress), get(mWalletAddress), mWalletAddress, true);
                        }
                    }
                });
    }

    @Override
    public void afterSendTransactionSucceed(Transaction transaction) {
        TransactionDetailActivity.actionStart(getContext(), transaction, Collections.singletonList(transaction.getFrom()));
    }


    public void deleteTransaction(Transaction transaction) {

        List<Transaction> transactionList = get(mWalletAddress);
        if (transactionList != null && !transactionList.isEmpty()) {
            List<Transaction> newTransactionList = new ArrayList<>(transactionList);
            newTransactionList.remove(transaction);
            Collections.sort(newTransactionList);
            getView().notifyTransactionSetChanged(transactionList, newTransactionList, mWalletAddress, false);

            put(mWalletAddress, newTransactionList);
        }
    }

    public synchronized void addNewTransaction(Transaction transaction) {
        if (isViewAttached()) {
            String walletAddress = getWalletAddressFromTransaction(transaction);
            List<Transaction> transactionList = get(walletAddress);
            List<Transaction> newTransactionList = new ArrayList<>();
            if (transactionList != null) {
                if (transactionList.contains(transaction)) {
                    //更新
                    LogUtils.e("-------更新transaction：" + transaction.getValue() + "walletName:" +transaction.getWalletName());
                    newTransactionList = new ArrayList<>(transactionList);
                    int index = newTransactionList.indexOf(transaction);
                    newTransactionList.set(index, transaction);
                    Collections.sort(newTransactionList);
                } else {
                    //添加
                    LogUtils.e("-------新增transaction：" + transaction.getValue() + "walletName:" +transaction.getWalletName());
                    newTransactionList = getNewTransactionList(transactionList, Arrays.asList(transaction), true);
                    Collections.sort(newTransactionList);
                }
                if (mWalletAddress != null && mWalletAddress.equalsIgnoreCase(walletAddress)) {
                    getView().notifyTransactionSetChanged(transactionList, newTransactionList, walletAddress, false);
                }
                put(walletAddress, newTransactionList);

            }

        }
    }

    private List<Transaction> get(String address) {
        List<Transaction> transactionList = mTransactionMap.get(address.toLowerCase());
        return transactionList == null ? new ArrayList<>() : transactionList;
    }

    private void put(String address, List<Transaction> transactionList) {
        mTransactionMap.put(address.toLowerCase(), transactionList);
    }

    private String getWalletAddressFromTransaction(Transaction transaction) {
        return !TextUtils.isEmpty(transaction.getFrom()) ? transaction.getFrom() : transaction.getTo();
    }

    /**
     * 当前交易
     *
     * @param transaction
     * @return
     */
    private boolean isCurrentSelectedWallet(Transaction transaction) {
        return transaction.getFrom().equalsIgnoreCase(mWalletAddress) || transaction.getTo().equalsIgnoreCase(mWalletAddress);
    }

    /**
     * 删除异常的交易记录，删除的前提就是，服务端返回了与本地异常(交易状态为pending或者timeout)交易记录相同的记录
     * 如果服务端
     *
     * @param oldTransactionList
     * @param curTransactionList
     */
    private void deleteExceptionalTransaction(List<Transaction> oldTransactionList, List<Transaction> curTransactionList) {

        if (oldTransactionList == null || oldTransactionList.isEmpty() || curTransactionList.isEmpty()) {
            return;
        }

        for (int i = 0; i < oldTransactionList.size(); i++) {
            Transaction oldTransaction = oldTransactionList.get(i);
            boolean isOldTransactionStatusException = oldTransaction.getTxReceiptStatus() == TransactionStatus.PENDING || oldTransaction.getTxReceiptStatus() == TransactionStatus.TIMEOUT;
            if (curTransactionList.contains(oldTransaction) && isOldTransactionStatusException) {
                Transaction newTransaction = curTransactionList.get(i);
                if (newTransaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED || newTransaction.getTxReceiptStatus() == TransactionStatus.FAILED || newTransaction.getTxReceiptStatus() == TransactionStatus.TIMEOUT) {
                    //删除掉
                    TransactionManager.getInstance().removePendingTransaction(newTransaction.getFrom());
                    deleteTransaction(oldTransactionList.get(i).getHash());
                }
            }
        }

    }

    private void deleteTransaction(String hash) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return TransactionDao.deleteTransaction(hash);
                    }
                })
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .doOnSuccess(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtils.e("deleteTransaction cancelTaskByHash " + hash);
                        TransactionManager.getInstance().cancelTaskByHash(hash);
                    }
                })
                .subscribe();
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
    private Flowable<Pair<List<Transaction>, List<Transaction>>> getTransactionListWithTime(String walletAddress, String direction) {
        long beginSequence = getBeginSequenceByDirection(direction);
        if (beginSequence == -1) {
            return getTransactionList(walletAddress)
                    .toFlowable()
                    .flatMap(new Function<Pair<List<Transaction>, List<Transaction>>, Publisher<Pair<List<Transaction>, List<Transaction>>>>() {
                        @Override
                        public Publisher<Pair<List<Transaction>, List<Transaction>>> apply(Pair<List<Transaction>, List<Transaction>> pair) throws Exception {
                            return Flowable.just(pair);
                        }
                    });
        } else {
            return getTransactionList(walletAddress, direction, beginSequence)
                    .flatMap(new Function<Response<ApiResponse<List<Transaction>>>, SingleSource<Pair<List<Transaction>, List<Transaction>>>>() {
                        @Override
                        public SingleSource<Pair<List<Transaction>, List<Transaction>>> apply(Response<ApiResponse<List<Transaction>>> apiResponseResponse) throws Exception {
                            if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                                return Single.just(new Pair<List<Transaction>, List<Transaction>>(new ArrayList<>(), apiResponseResponse.body().getData()));
                            } else {
                                return Single.error(new Throwable());
                            }
                        }
                    })
                    .toFlowable();
        }
    }


    /**
     * 切换钱包，每次都是获取最新的数据
     *
     * @param walletAddress
     * @return
     */
    private Single<Pair<List<Transaction>, List<Transaction>>> getTransactionList(String walletAddress) {

        return getTransactionListFromDB(walletAddress).flatMap(new Function<List<Transaction>, SingleSource<Pair<List<Transaction>, List<Transaction>>>>() {
            @Override
            public SingleSource<Pair<List<Transaction>, List<Transaction>>> apply(List<Transaction> transactionList) throws Exception {
                LogUtils.d("getTransactionListFromDB success" + transactionList);
                return getTransactionList(walletAddress, Direction.DIRECTION_NEW, getBeginSequenceByDirection(Direction.DIRECTION_NEW))
                        .flatMap(new Function<Response<ApiResponse<List<Transaction>>>, SingleSource<Pair<List<Transaction>, List<Transaction>>>>() {
                            @Override
                            public SingleSource<Pair<List<Transaction>, List<Transaction>>> apply(Response<ApiResponse<List<Transaction>>> apiResponseResponse) throws Exception {
                                LogUtils.d("getTransactionList success" + apiResponseResponse.toString());
                                List<Transaction> netTransactionList = new ArrayList<>();
                                if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                                    netTransactionList = apiResponseResponse.body().getData();
                                }
                                return Single.just(new Pair<>(transactionList, netTransactionList));
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
        List<Transaction> transactionList = get(mWalletAddress);
        //拉最新的
        if (transactionList == null || transactionList.isEmpty()) {
            return -1;
        }
        //拉比当前最新的还大的
        if (Direction.DIRECTION_NEW.equals(direction)) {
            return getValidBiggerSequence(transactionList);
        } else {
            return getValidSmallerSequence(transactionList);
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

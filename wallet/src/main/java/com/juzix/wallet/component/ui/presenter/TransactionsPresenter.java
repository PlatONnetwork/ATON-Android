package com.juzix.wallet.component.ui.presenter;

import android.util.Log;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.component.ui.view.IndividualTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.IndividualVoteDetailActivity;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.db.sqlite.SharedTransactionInfoDao;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.VoteTransactionEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class TransactionsPresenter extends BasePresenter<TransactionsContract.View> implements TransactionsContract.Presenter {

    private static final String TAG = TransactionsPresenter.class.getSimpleName();
    private WalletEntity mWalletEntity;
    private Disposable mDisposable;

    public TransactionsPresenter(TransactionsContract.View view) {
        super(view);
    }

    @Override
    public void updateWalletEntity() {
        mWalletEntity = WalletManager.getInstance().getSelectedWallet();
    }

    @Override
    public void fetchWalletTransactionList() {
        if (!isViewAttached() || mWalletEntity == null) {
            return;
        }
        if (mWalletEntity instanceof IndividualWalletEntity) {
            fetchIndividualWalletTransactionList();
        } else {
            fetchSharedWalletTransactionList();
        }
    }

    @Override
    public void enterTransactionDetailActivity(TransactionEntity transactionEntity) {
        if (isViewAttached() && mWalletEntity != null) {
            if (mWalletEntity instanceof IndividualWalletEntity) {
                enterIndividualWalletRelevantTransactionDetailActivity(transactionEntity);
            } else {
                enterSharedWalletRelevantTransactionDetailActivity(transactionEntity);
            }
        }
    }

    private void fetchIndividualWalletTransactionList() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mDisposable = Single.fromCallable(new Callable<List<TransactionEntity>>() {
            @Override
            public List<TransactionEntity> call() {
                return getIndividualRelevantTransactionEntityList(mWalletEntity.getPrefixAddress()).blockingGet();
            }
        })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<List<TransactionEntity>>() {
                    @Override
                    public void accept(List<TransactionEntity> transactionEntityList) throws Exception {
                        if (isViewAttached() && mWalletEntity != null) {
                            Collections.sort(transactionEntityList);
                            getView().notifyTransactionListChanged(transactionEntityList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });
    }

    private void fetchSharedWalletTransactionList() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = Single.fromCallable(new Callable<List<TransactionEntity>>() {
            @Override
            public List<TransactionEntity> call() {
                if (mWalletEntity == null) {
                    return null;
                }
                return getSharedRelevantTransactionEntityList(mWalletEntity.getPrefixAddress()).blockingGet();
            }
        })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<List<TransactionEntity>>() {
                    @Override
                    public void accept(List<TransactionEntity> transactionEntityList) throws Exception {
                        if (isViewAttached() && mWalletEntity != null) {
                            Collections.sort(transactionEntityList);
                            getView().notifyTransactionListChanged(transactionEntityList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 获取普通钱包相关的交易
     *
     * @param address
     * @return
     */
    private Single<List<TransactionEntity>> getIndividualRelevantTransactionEntityList(String address) {
        return getIndividualRelevantSharedTransactionEntityList(address)
                .zipWith(getIndividualTransactionEntityList(address), new BiFunction<List<SharedTransactionEntity>, List<IndividualTransactionEntity>, List<TransactionEntity>>() {
                    @Override
                    public List<TransactionEntity> apply(List<SharedTransactionEntity> sharedTransactionEntities, List<IndividualTransactionEntity> individualTransactionEntities) throws Exception {
                        List<TransactionEntity> transactionEntityList = new ArrayList<>();
                        transactionEntityList.addAll(sharedTransactionEntities);
                        transactionEntityList.addAll(individualTransactionEntities);
                        Collections.sort(transactionEntityList);
                        return transactionEntityList;
                    }
                })
                .zipWith(getVoteTransactionEntityList(address), new BiFunction<List<TransactionEntity>, List<? extends TransactionEntity>, List<TransactionEntity>>() {
                    @Override
                    public List<TransactionEntity> apply(List<TransactionEntity> transactionEntities, List<? extends TransactionEntity> transactionEntities2) throws Exception {
                        transactionEntities.addAll(transactionEntities2);
                        Collections.sort(transactionEntities);
                        return transactionEntities;
                    }
                });
    }

    /**
     * 获取共享钱包相关的交易
     *
     * @param contractAddress
     * @return
     */
    private Single<List<TransactionEntity>> getSharedRelevantTransactionEntityList(String contractAddress) {
        return getSharedTransactionEntityList(contractAddress)
                .zipWith(getIndividualTransactionEntityList(contractAddress), new BiFunction<List<SharedTransactionEntity>, List<IndividualTransactionEntity>, List<TransactionEntity>>() {
                    @Override
                    public List<TransactionEntity> apply(List<SharedTransactionEntity> sharedTransactionEntities, List<IndividualTransactionEntity> individualTransactionEntities) throws Exception {
                        List<TransactionEntity> transactionEntityList = new ArrayList<>();
                        transactionEntityList.addAll(sharedTransactionEntities);
                        transactionEntityList.addAll(individualTransactionEntities);
                        Collections.sort(transactionEntityList);
                        return transactionEntityList;
                    }
                });
    }

    private Single<List<IndividualTransactionEntity>> getIndividualTransactionEntityList(String contractAddress) {

        return Flowable.fromCallable(new Callable<List<IndividualTransactionInfoEntity>>() {
            @Override
            public List<IndividualTransactionInfoEntity> call() throws Exception {
                return IndividualTransactionInfoDao.getTransactionList(contractAddress);
            }
        }).flatMap(new Function<List<IndividualTransactionInfoEntity>, Publisher<IndividualTransactionInfoEntity>>() {
            @Override
            public Publisher<IndividualTransactionInfoEntity> apply(List<IndividualTransactionInfoEntity> individualTransactionInfoEntities) throws Exception {
                return Flowable.fromIterable(individualTransactionInfoEntities);
            }
        }).map(new Function<IndividualTransactionInfoEntity, IndividualTransactionEntity>() {
            @Override
            public IndividualTransactionEntity apply(IndividualTransactionInfoEntity individualTransactionInfoEntity) throws Exception {
                return individualTransactionInfoEntity.buildIndividualTransactionEntity();
            }
        }).flatMap(new Function<IndividualTransactionEntity, Publisher<IndividualTransactionEntity>>() {
            @Override
            public Publisher<IndividualTransactionEntity> apply(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                if (!individualTransactionEntity.isCompleted()) {
                    IndividualWalletTransactionManager.getInstance().getIndividualTransactionByLoop(individualTransactionEntity);
                }
                return Flowable.just(individualTransactionEntity);
            }
        }).toList().onErrorReturnItem(new ArrayList<>());
    }

    /**
     * 获取普通相关的共享钱包交易
     *
     * @param address
     * @return
     */
    private Single<List<SharedTransactionEntity>> getIndividualRelevantSharedTransactionEntityList(String address) {

        return Single.fromCallable(new Callable<List<SharedWalletEntity>>() {
            @Override
            public List<SharedWalletEntity> call() throws Exception {
                return SharedWalletManager.getInstance().getWalletListByWalletAddress(address);
            }
        })
                .filter(new Predicate<List<SharedWalletEntity>>() {
                    @Override
                    public boolean test(List<SharedWalletEntity> sharedWalletEntityList) throws Exception {
                        return !sharedWalletEntityList.isEmpty();
                    }
                })
                .defaultIfEmpty(new ArrayList<>())
                .map(new Function<List<SharedWalletEntity>, String[]>() {
                    @Override
                    public String[] apply(List<SharedWalletEntity> sharedWalletEntityList) throws Exception {
                        return getContractAddressArray(sharedWalletEntityList);
                    }
                })
                .flatMapSingle(new Function<String[], SingleSource<List<SharedTransactionEntity>>>() {
                    @Override
                    public SingleSource<List<SharedTransactionEntity>> apply(String[] contractaddressArray) throws Exception {

                        return Flowable.fromCallable(new Callable<List<SharedTransactionInfoEntity>>() {
                            @Override
                            public List<SharedTransactionInfoEntity> call() throws Exception {
                                return SharedTransactionInfoDao.getTransactionListByContractAddress(contractaddressArray);
                            }
                        })
                                .flatMap(new Function<List<SharedTransactionInfoEntity>, Publisher<SharedTransactionInfoEntity>>() {
                                    @Override
                                    public Publisher<SharedTransactionInfoEntity> apply(List<SharedTransactionInfoEntity> sharedTransactionInfoEntities) throws Exception {
                                        return Flowable.fromIterable(sharedTransactionInfoEntities);
                                    }
                                })
                                .map(new Function<SharedTransactionInfoEntity, SharedTransactionEntity>() {
                                    @Override
                                    public SharedTransactionEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                                        return sharedTransactionInfoEntity.buildSharedTransactionEntity();
                                    }
                                })
                                .filter(new Predicate<SharedTransactionEntity>() {
                                    @Override
                                    public boolean test(SharedTransactionEntity sharedTransactionEntity) throws Exception {
                                        SharedTransactionEntity.TransactionType transactionType = SharedTransactionEntity.TransactionType.getTransactionType(sharedTransactionEntity.getTransactionType());
                                        if (transactionType != SharedTransactionEntity.TransactionType.SEND_TRANSACTION && !mWalletEntity.getPrefixAddress().equals(sharedTransactionEntity.getFromAddress())) {
                                            return false;
                                        }
                                        return true;
                                    }
                                })
                                .toList();
                    }
                })
                .doOnSuccess(new Consumer<List<SharedTransactionEntity>>() {
                    @Override
                    public void accept(List<SharedTransactionEntity> sharedTransactionEntities) throws Exception {
                        Log.e(TAG, "sharedWalletTransaction: " + sharedTransactionEntities.size());
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })
                .onErrorReturnItem(new ArrayList<SharedTransactionEntity>());
    }

    /**
     * 获取共享钱包相关的共享钱包交易
     *
     * @param contractAddress
     * @return
     */
    private Single<List<SharedTransactionEntity>> getSharedTransactionEntityList(String contractAddress) {

        return Flowable.fromCallable(new Callable<List<SharedTransactionInfoEntity>>() {
            @Override
            public List<SharedTransactionInfoEntity> call() throws Exception {
                return SharedTransactionInfoDao.getTransactionListByContractAddress(contractAddress);
            }
        }).flatMap(new Function<List<SharedTransactionInfoEntity>, Publisher<SharedTransactionInfoEntity>>() {
            @Override
            public Publisher<SharedTransactionInfoEntity> apply(List<SharedTransactionInfoEntity> sharedTransactionInfoEntityList) throws Exception {
                return Flowable.fromIterable(sharedTransactionInfoEntityList);
            }
        })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionEntity>() {
                    @Override
                    public SharedTransactionEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        return sharedTransactionInfoEntity.buildSharedTransactionEntity();
                    }
                })
                .filter(new Predicate<SharedTransactionEntity>() {
                    @Override
                    public boolean test(SharedTransactionEntity sharedTransactionEntity) throws Exception {
                        return SharedTransactionEntity.TransactionType.getTransactionType(sharedTransactionEntity.getTransactionType()) == SharedTransactionEntity.TransactionType.SEND_TRANSACTION;
                    }
                })
                .toList()
                .onErrorReturnItem(new ArrayList<>());

    }

    /**
     * 获取投票交易
     *
     * @param address
     * @return
     */
    private Single<List<VoteTransactionEntity>> getVoteTransactionEntityList(String address) {

        return Single.fromCallable(new Callable<List<VoteTransactionEntity>>() {
            @Override
            public List<VoteTransactionEntity> call() throws Exception {
                List<SingleVoteInfoEntity> singleVoteInfoEntities = SingleVoteInfoDao.getTransactionListByWalletAddress(address);
                List<VoteTransactionEntity> transactionEntityList = new ArrayList<>();
                for (SingleVoteInfoEntity voteInfoEntity : singleVoteInfoEntities) {
                    VoteTransactionEntity entity = new VoteTransactionEntity.Builder(voteInfoEntity.getUuid(), voteInfoEntity.getCreateTime(), voteInfoEntity.getWalletName())
                            .hash(voteInfoEntity.getHash())
                            .fromAddress(voteInfoEntity.getWalletAddress())
                            .toAddress(voteInfoEntity.getContractAddress())
                            .value(voteInfoEntity.getValue())
                            .blockNumber(voteInfoEntity.getBlockNumber())
                            .latestBlockNumber(voteInfoEntity.getLatestBlockNumber())
                            .energonPrice(voteInfoEntity.getEnergonPrice())
                            .memo("")
                            .status(voteInfoEntity.getStatus())
                            .build();
                    transactionEntityList.add(entity);
                }
                return transactionEntityList;
            }
        })
                .toFlowable()
                .flatMap(new Function<List<VoteTransactionEntity>, Publisher<VoteTransactionEntity>>() {
                    @Override
                    public Publisher<VoteTransactionEntity> apply(List<VoteTransactionEntity> singleVoteInfoEntities) throws Exception {
                        return Flowable.fromIterable(singleVoteInfoEntities);
                    }
                })
                .toList();

    }

    /**
     * 拼接共享钱包的地址
     *
     * @param sharedWalletEntityList
     * @return
     */
    private String[] getContractAddressArray(List<SharedWalletEntity> sharedWalletEntityList) {
        return Flowable
                .fromIterable(sharedWalletEntityList)
                .map(new Function<SharedWalletEntity, String>() {
                    @Override
                    public String apply(SharedWalletEntity sharedWalletEntity) throws Exception {
                        return sharedWalletEntity.getPrefixAddress();
                    }
                })
                .toList()
                .map(new Function<List<String>, String[]>() {
                    @Override
                    public String[] apply(List<String> contractaddressList) throws Exception {
                        return contractaddressList.toArray(new String[contractaddressList.size()]);
                    }
                })
                .onErrorReturnItem(new String[0])
                .blockingGet();
    }

    /**
     * 进入普通钱包相关的交易详情
     *
     * @param transactionEntity
     */
    private void enterIndividualWalletRelevantTransactionDetailActivity(TransactionEntity transactionEntity) {
        if (isViewAttached() && mWalletEntity != null) {
            if (transactionEntity instanceof IndividualTransactionEntity) {
                IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) transactionEntity, mWalletEntity.getPrefixAddress());
            } else if (transactionEntity instanceof VoteTransactionEntity) {
                IndividualVoteDetailActivity.actionStart(currentActivity(), transactionEntity.getUuid());
            } else if (transactionEntity instanceof SharedTransactionEntity) {
                SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
                if (!sharedTransactionEntity.isRead()) {
                    sharedTransactionEntity.setRead(true);
                    getView().notifyItem(sharedTransactionEntity);
                    SharedWalletTransactionManager.getInstance().updateTransactionForRead(SharedWalletManager.getInstance().getWalletByContractAddress(sharedTransactionEntity.getContractAddress()), sharedTransactionEntity);
                }
//                if (sharedTransactionEntity.getTransactionStatus() == TransactionEntity.TransactionStatus.SIGNING) {
//                    SigningActivity.actionStart(currentActivity(), sharedTransactionEntity, (IndividualWalletEntity) mWalletEntity);
//                } else {
//                    SharedTransactionDetailActivity.actionStart(currentActivity(), sharedTransactionEntity, mWalletEntity.getPrefixAddress());
//                }
            }
        }
    }

    /**
     * 进入共享钱包相关的交易详情
     *
     * @param transactionEntity
     */
    private void enterSharedWalletRelevantTransactionDetailActivity(TransactionEntity transactionEntity) {
        if (isViewAttached() && mWalletEntity != null) {
            SharedWalletEntity walletEntity = (SharedWalletEntity) mWalletEntity;
            if (transactionEntity instanceof SharedTransactionEntity) {
                SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
                if (!sharedTransactionEntity.isRead()) {
                    sharedTransactionEntity.setRead(true);
                    getView().notifyItem(sharedTransactionEntity);
                    SharedWalletTransactionManager.getInstance().updateTransactionForRead(walletEntity, sharedTransactionEntity);
                }
//                if (sharedTransactionEntity.getTransactionStatus() == TransactionEntity.TransactionStatus.SIGNING) {
//                    SigningActivity.actionStart(currentActivity(), sharedTransactionEntity, IndividualWalletManager.getInstance().getWalletByAddress(sharedTransactionEntity.getOwnerWalletAddress()));
//                } else {
//                    SharedTransactionDetailActivity.actionStart(currentActivity(), sharedTransactionEntity, walletEntity.getPrefixAddress());
//                }
            } else {
                IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) transactionEntity, walletEntity.getPrefixAddress());
            }
        }
    }

}

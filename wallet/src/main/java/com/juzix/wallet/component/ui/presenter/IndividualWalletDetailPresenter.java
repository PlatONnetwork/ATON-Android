package com.juzix.wallet.component.ui.presenter;

import android.util.Log;

import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualWalletDetailContract;
import com.juzix.wallet.component.ui.view.IndividualReceiveTransationActivity;
import com.juzix.wallet.component.ui.view.IndividualTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.SharedTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.SigningActivity;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.db.sqlite.SharedTransactionInfoDao;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class IndividualWalletDetailPresenter extends BasePresenter<IndividualWalletDetailContract.View> implements IndividualWalletDetailContract.Presenter {

    private final static String TAG = IndividualWalletDetailPresenter.class.getSimpleName();

    private IndividualWalletEntity mWalletEntity;
    private SharedWalletEntity mSharedWalletEntity;
    private Disposable mDisposable;

    public IndividualWalletDetailPresenter(IndividualWalletDetailContract.View view) {
        super(view);
        mWalletEntity = view.getWalletEntityFromIntent();
    }

    @Override
    public void fetchWalletDetail() {
        if (!isViewAttached() || mWalletEntity == null) {
            return;
        }

        getView().updateWalletInfo(mWalletEntity);

        fetchWalletBalance();

        fetchWalletTransactionList();

    }

    private void fetchWalletBalance() {
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return Web3jManager.getInstance().getBalance(mWalletEntity.getPrefixAddress());
            }
        })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        mWalletEntity.setBalance(balance);
                        if (isViewAttached()) {
                            getView().updateWalletInfo(mWalletEntity);
                        }
                    }
                });
    }

    @Override
    public void fetchWalletTransactionList() {

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mDisposable = getTransactionEntityList()
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<List<TransactionEntity>>() {
                    @Override
                    public void accept(List<TransactionEntity> transactionEntityList) throws Exception {
                        if (isViewAttached()) {
                            getView().notifyTransactionListChanged(transactionEntityList, mWalletEntity.getPrefixAddress());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });
    }

    @Override
    public void enterTransactionDetailActivity(TransactionEntity transactionEntity) {
        if (!isViewAttached() || mWalletEntity == null) {
            return;
        }
        if (transactionEntity instanceof IndividualTransactionEntity) {
            IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) transactionEntity, mWalletEntity.getPrefixAddress());
        } else if (transactionEntity instanceof SharedTransactionEntity) {
            SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
            if (!sharedTransactionEntity.isRead()) {
                sharedTransactionEntity.setRead(true);
                SharedWalletTransactionManager.getInstance().updateTransactionForRead(mSharedWalletEntity, sharedTransactionEntity);
            }
            BaseActivity activity = currentActivity();
            if (sharedTransactionEntity.isTransactionFinished()) {
                SharedTransactionDetailActivity.actionStart(activity, sharedTransactionEntity);
            } else {
                SigningActivity.actionStart(activity, sharedTransactionEntity);
            }
        }

    }

    @Override
    public void enterReceiveTransactionActivity() {
        if (isViewAttached()) {
            IndividualReceiveTransationActivity.actionStart(currentActivity(), mWalletEntity);
        }
    }

    private Single<List<TransactionEntity>> getTransactionEntityList() {
        return getSharedTransactionEntityList(mWalletEntity.getPrefixAddress())
                .zipWith(getIndividualTransactionEntityList(mWalletEntity.getPrefixAddress()), new BiFunction<List<SharedTransactionEntity>, List<IndividualTransactionEntity>, List<TransactionEntity>>() {
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

    private Single<List<SharedTransactionEntity>> getSharedTransactionEntityList(String address) {

        return Single.fromCallable(new Callable<SharedWalletEntity>() {
            @Override
            public SharedWalletEntity call() throws Exception {
                mSharedWalletEntity = SharedWalletManager.getInstance().getWalletByWalletAddress(address);
                return mSharedWalletEntity;
            }
        })
                .flatMap(new Function<SharedWalletEntity, SingleSource<List<SharedTransactionEntity>>>() {
                    @Override
                    public SingleSource<List<SharedTransactionEntity>> apply(SharedWalletEntity sharedWalletEntity) throws Exception {

                        return Flowable.fromCallable(new Callable<List<SharedTransactionInfoEntity>>() {
                            @Override
                            public List<SharedTransactionInfoEntity> call() throws Exception {
                                return SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(sharedWalletEntity.getPrefixContractAddress());
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

    private Single<List<IndividualTransactionEntity>> getIndividualTransactionEntityList(String contractAddress) {

        List<IndividualTransactionInfoEntity> individualTransactionInfoEntityList = IndividualTransactionInfoDao.getInstance().getTransactionList(contractAddress);

        return Flowable
                .fromIterable(individualTransactionInfoEntityList)
                .map(new Function<IndividualTransactionInfoEntity, IndividualTransactionEntity>() {
                    @Override
                    public IndividualTransactionEntity apply(IndividualTransactionInfoEntity transactionInfoEntity) throws Exception {
                        return transactionInfoEntity.buildIndividualTransactionEntity();
                    }
                })
                .flatMap(new Function<IndividualTransactionEntity, Publisher<IndividualTransactionEntity>>() {
                    @Override
                    public Publisher<IndividualTransactionEntity> apply(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        return getIndividualTransactionEntity(individualTransactionEntity);
                    }
                })
                .toList()
                .doOnSuccess(new Consumer<List<IndividualTransactionEntity>>() {
                    @Override
                    public void accept(List<IndividualTransactionEntity> individualTransactionEntities) throws Exception {
                        Log.e(TAG, "IndividualWalletTransaction: " + individualTransactionEntities.size());
                    }
                })
                .onErrorReturnItem(new ArrayList<>());
    }

    private Flowable<IndividualTransactionEntity> getIndividualTransactionEntity(IndividualTransactionEntity individualTransactionEntity) {

        Single<Long> getLatestBlockNumber = Single.just(Web3jManager.getInstance().getLatestBlockNumber());
        Single<IndividualTransactionEntity> getIndividualTransactionEntity = Single.just(IndividualWalletTransactionManager.getInstance().getTransactionByHash(individualTransactionEntity.getHash(), individualTransactionEntity.getCreateTime(), individualTransactionEntity.getWalletName(), individualTransactionEntity.getMemo()));
        Single<IndividualTransactionEntity> getTransactionDetail = getIndividualTransactionEntity.zipWith(getLatestBlockNumber, new BiFunction<IndividualTransactionEntity, Long, IndividualTransactionEntity>() {
            @Override
            public IndividualTransactionEntity apply(IndividualTransactionEntity individualTransactionEntity, Long latestBlockNumber) throws Exception {
                individualTransactionEntity.setLatestBlockNumber(latestBlockNumber);
                return individualTransactionEntity;
            }
        });

        return Single
                .just(individualTransactionEntity)
                .flatMap(new Function<IndividualTransactionEntity, SingleSource<IndividualTransactionEntity>>() {
                    @Override
                    public SingleSource<IndividualTransactionEntity> apply(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        if (individualTransactionEntity.getBlockNumber() == 0) {
                            return getTransactionDetail.doOnSuccess(new Consumer<IndividualTransactionEntity>() {
                                @Override
                                public void accept(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                                    IndividualTransactionInfoDao.getInstance().updateTransactionBlockNumber(individualTransactionEntity.getUuid(), individualTransactionEntity.getBlockNumber());
                                }
                            });
                        } else {
                            return getLatestBlockNumber.map(new Function<Long, IndividualTransactionEntity>() {
                                @Override
                                public IndividualTransactionEntity apply(Long latestBlockNumber) throws Exception {
                                    individualTransactionEntity.setLatestBlockNumber(latestBlockNumber);
                                    return individualTransactionEntity;
                                }
                            });
                        }
                    }
                })
                .toFlowable();
    }
}

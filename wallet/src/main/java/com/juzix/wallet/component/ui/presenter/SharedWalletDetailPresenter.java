package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.app.FlowableSchedulersTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedWalletDetailContract;
import com.juzix.wallet.component.ui.view.IndividualTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.SharedReceiveTransationActivity;
import com.juzix.wallet.component.ui.view.SharedTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.SigningActivity;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.db.sqlite.SharedTransactionInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
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
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class SharedWalletDetailPresenter extends BasePresenter<SharedWalletDetailContract.View> implements SharedWalletDetailContract.Presenter {

    private static final String TAG = SharedWalletDetailPresenter.class.getSimpleName();
    private SharedWalletEntity walletEntity;
    private Disposable mDisposable;

    public SharedWalletDetailPresenter(SharedWalletDetailContract.View view) {
        super(view);
        walletEntity = view.getSharedWalletFromIntent();
    }

    @Override
    public void fetchWalletDetail() {

        if (!isViewAttached() || walletEntity == null) {
            return;
        }

        IndividualWalletEntity individualWalletEntity = IndividualWalletManager.getInstance().getWalletByAddress(walletEntity.getAddress());
        if (individualWalletEntity == null || !walletEntity.isOwner()) {
            getView().hideSendButton();
        }
        getView().updateWalletInfo(walletEntity);

        fetchWalletBalance();

        fetchWalletTransactionList();
    }

    private void fetchWalletBalance() {

        if (walletEntity == null) {
            return;
        }

        Flowable
                .fromCallable(new Callable<Double>() {
                    @Override
                    public Double call() throws Exception {
                        return Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                    }
                })
                .compose(bindToLifecycle())
                .compose(new FlowableSchedulersTransformer())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        walletEntity.setBalance(balance);
                        if (isViewAttached()) {
                            getView().updateWalletInfo(walletEntity);
                        }
                    }
                });
    }

    @Override
    public void fetchWalletTransactionList() {

        if (walletEntity == null) {
            return;
        }

        String contractAddress = walletEntity.getPrefixAddress();

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mDisposable = getTransactionEntityList(contractAddress)
                .compose(bindToLifecycle())
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<List<TransactionEntity>>() {
                    @Override
                    public void accept(List<TransactionEntity> transactionEntityList) throws Exception {
                        if (isViewAttached()) {
                            getView().notifyTransactionListChanged(transactionEntityList, contractAddress);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public void enterTransactionDetailActivity(TransactionEntity transactionEntity) {

        if (isViewAttached() && walletEntity != null) {
            if (transactionEntity instanceof SharedTransactionEntity) {
                SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
                if (!sharedTransactionEntity.isRead()) {
                    sharedTransactionEntity.setRead(true);
                    getView().notifyTransactionChanged(sharedTransactionEntity, walletEntity.getPrefixAddress());
                    SharedWalletTransactionManager.getInstance().updateTransactionForRead(walletEntity, sharedTransactionEntity);
                }
                if (sharedTransactionEntity.transfered()) {
                    SharedTransactionDetailActivity.actionStart(currentActivity(), sharedTransactionEntity,walletEntity.getPrefixAddress());
                } else {
                    SigningActivity.actionStart(currentActivity(), sharedTransactionEntity,IndividualWalletManager.getInstance().getWalletByAddress(sharedTransactionEntity.getOwnerWalletAddress()));
                }
            } else {
                IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) transactionEntity, walletEntity.getPrefixAddress());
            }
        }

    }

    @Override
    public void enterReceiveTransactionActivity() {
        if (isViewAttached()) {
            SharedReceiveTransationActivity.actionStart(currentActivity(), walletEntity);
        }
    }

    private Single<List<TransactionEntity>> getTransactionEntityList(String contractAddress) {
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

    private Single<List<SharedTransactionEntity>> getSharedTransactionEntityList(String contractAddress) {

        List<SharedTransactionInfoEntity> sharedTransactionInfoEntityList = SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(contractAddress);

        return Flowable
                .fromIterable(sharedTransactionInfoEntityList)
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

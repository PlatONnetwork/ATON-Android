package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.VoteTransactionEntity;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class TransactionRecordsPresenter extends BasePresenter<TransactionRecordsContract.View> implements TransactionRecordsContract.Presenter {

    private ArrayList<TransactionEntity> transactionEntityList = new ArrayList<>();

    public TransactionRecordsPresenter(TransactionRecordsContract.View view) {
        super(view);
    }

    @Override
    public void fetchTransactions() {

        getTransactionEntityList()
                .zipWith(getSharedTransactionEntityList(), new BiFunction<List<TransactionEntity>, List<? extends TransactionEntity>, List<TransactionEntity>>() {
                    @Override
                    public List<TransactionEntity> apply(List<TransactionEntity> transactionEntities, List<? extends TransactionEntity> transactionEntities2) throws Exception {
                        transactionEntities.addAll(transactionEntities2);
                        return transactionEntities;
                    }
                }).zipWith(getVoteTransactionEntityList(), new BiFunction<List<TransactionEntity>, List<? extends TransactionEntity>, List<TransactionEntity>>() {
                    @Override
                    public List<TransactionEntity> apply(List<TransactionEntity> transactionEntities, List<? extends TransactionEntity> transactionEntities2) throws Exception {
                        transactionEntities.addAll(transactionEntities2);
                        return transactionEntities;
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<List<TransactionEntity>>() {
                    @Override
                    public void accept(List<TransactionEntity> transactionEntityList) throws Exception {
                        if (isViewAttached()) {
                            Collections.sort(transactionEntityList);
                            getView().showTransactions(transactionEntityList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("******" + throwable.getMessage());
                    }
                });
    }


    private Single<List<TransactionEntity>> getTransactionEntityList() {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return Web3jManager.getInstance().getLatestBlockNumber();
            }
        })
                .toFlowable()
                .flatMap(new Function<Long, Publisher<TransactionEntity>>() {
                    @Override
                    public Publisher<TransactionEntity> apply(Long latestBlockNumbe) throws Exception {
                        return Flowable
                                .just(IndividualTransactionInfoDao.getTransactionList())
                                .flatMap(new Function<List<IndividualTransactionInfoEntity>, Publisher<IndividualTransactionInfoEntity>>() {
                                    @Override
                                    public Publisher<IndividualTransactionInfoEntity> apply(List<IndividualTransactionInfoEntity> individualTransactionInfoEntities) throws Exception {
                                        return Flowable.fromIterable(individualTransactionInfoEntities);
                                    }
                                })
                                .map(new Function<IndividualTransactionInfoEntity, IndividualTransactionEntity>() {
                                    @Override
                                    public IndividualTransactionEntity apply(IndividualTransactionInfoEntity entity) throws Exception {
                                        IndividualTransactionEntity transactionInfoEntity = IndividualWalletTransactionManager.getInstance().getTransactionByHash(entity.buildIndividualTransactionEntity());
                                        transactionInfoEntity.setLatestBlockNumber(latestBlockNumbe);
                                        return transactionInfoEntity;
                                    }
                                })
                                .filter(new Predicate<IndividualTransactionEntity>() {
                                    @Override
                                    public boolean test(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                                        return individualTransactionEntity.getTransactionStatus() == TransactionEntity.TransactionStatus.SUCCEED;
                                    }
                                })
                                .cast(TransactionEntity.class);
                    }
                })
                .toList();
    }

    private Single<List<SharedTransactionEntity>> getSharedTransactionEntityList() {

        return Single.fromCallable(new Callable<List<SharedTransactionEntity>>() {
            @Override
            public List<SharedTransactionEntity> call() throws Exception {
                return SharedWalletTransactionManager.getInstance().getAllTransactionList();
            }
        })
                .toFlowable()
                .flatMap(new Function<List<SharedTransactionEntity>, Publisher<SharedTransactionEntity>>() {
                    @Override
                    public Publisher<SharedTransactionEntity> apply(List<SharedTransactionEntity> transactionEntities) throws Exception {
                        return Flowable.fromIterable(transactionEntities);
                    }
                })
                .toList();

    }

    private Single<List<VoteTransactionEntity>> getVoteTransactionEntityList() {

        return Single.fromCallable(new Callable<List<VoteTransactionEntity>>() {
            @Override
            public List<VoteTransactionEntity> call() throws Exception {
                List<SingleVoteInfoEntity> singleVoteInfoEntities = SingleVoteInfoDao.getTransactionList();
                List<VoteTransactionEntity> transactionEntityList = new ArrayList<>();
                for (SingleVoteInfoEntity voteInfoEntity : singleVoteInfoEntities){
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

}

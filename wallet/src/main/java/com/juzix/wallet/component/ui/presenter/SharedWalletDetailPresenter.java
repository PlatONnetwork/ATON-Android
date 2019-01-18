package com.juzix.wallet.component.ui.presenter;

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

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class SharedWalletDetailPresenter extends BasePresenter<SharedWalletDetailContract.View> implements SharedWalletDetailContract.Presenter {

    private static final String TAG = SharedWalletDetailPresenter.class.getSimpleName();

    private SharedWalletEntity walletEntity;

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


        String contractAddress = walletEntity.getPrefixContractAddress();


        getSharedTransactionEntityList(contractAddress)
                .zipWith(getIndividualTransactionEntityList(contractAddress), new BiFunction<List<SharedTransactionEntity>, List<IndividualTransactionEntity>, List<TransactionEntity>>() {
                    @Override
                    public List<TransactionEntity> apply(List<SharedTransactionEntity> sharedTransactionEntities, List<IndividualTransactionEntity> individualTransactionEntities) throws Exception {
                        List<TransactionEntity> transactionEntityList = new ArrayList<>();
                        transactionEntityList.addAll(sharedTransactionEntities);
                        transactionEntityList.addAll(individualTransactionEntities);
                        Collections.sort(transactionEntityList);
                        return transactionEntityList;
                    }
                })
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
                    getView().notifyTransactionChanged(sharedTransactionEntity, walletEntity.getPrefixContractAddress());
                    SharedWalletTransactionManager.getInstance().updateTransactionForRead(walletEntity, sharedTransactionEntity);
                }
                if (sharedTransactionEntity.transfered()) {
                    SharedTransactionDetailActivity.actionStart(currentActivity(), sharedTransactionEntity);
                } else {
                    SigningActivity.actionStart(currentActivity(), sharedTransactionEntity);
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

    private Single<List<SharedTransactionEntity>> getSharedTransactionEntityList(String contractAddress) {

        return Flowable
                .fromIterable(SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(contractAddress))
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionEntity>() {
                    @Override
                    public SharedTransactionEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        return sharedTransactionInfoEntity.buildSharedTransactionEntity();
                    }
                })
                .collect(new Callable<List<SharedTransactionEntity>>() {

                    @Override
                    public List<SharedTransactionEntity> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<SharedTransactionEntity>, SharedTransactionEntity>() {
                    @Override
                    public void accept(List<SharedTransactionEntity> sharedTransactionEntities, SharedTransactionEntity sharedTransactionEntity) throws Exception {
                        sharedTransactionEntities.add(sharedTransactionEntity);
                    }
                })
                .onErrorReturnItem(new ArrayList<>());

    }

    private Single<List<IndividualTransactionEntity>> getIndividualTransactionEntityList(String contractAddress) {

        return Flowable
                .fromIterable(IndividualTransactionInfoDao.getInstance().getTransactionList(contractAddress))
                .flatMap(new Function<IndividualTransactionInfoEntity, Publisher<IndividualTransactionEntity>>() {
                    @Override
                    public Publisher<IndividualTransactionEntity> apply(IndividualTransactionInfoEntity individualTransactionInfoEntity) throws Exception {
                        return getIndividualTransactionEntity(individualTransactionInfoEntity);
                    }
                })
                .collect(new Callable<List<IndividualTransactionEntity>>() {
                    @Override
                    public List<IndividualTransactionEntity> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<IndividualTransactionEntity>, IndividualTransactionEntity>() {
                    @Override
                    public void accept(List<IndividualTransactionEntity> individualTransactionEntities, IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        individualTransactionEntities.add(individualTransactionEntity);
                    }
                })
                .onErrorReturnItem(new ArrayList<>());
    }

    private Flowable<IndividualTransactionEntity> getIndividualTransactionEntity(IndividualTransactionInfoEntity individualTransactionInfoEntity) {

        Single<Long> getLatestBlockNumber = Single.just(Web3jManager.getInstance().getLatestBlockNumber());
        Single<IndividualTransactionEntity> getIndividualTransactionEntity = Single.just(IndividualWalletTransactionManager.getInstance().getTransactionByHash(individualTransactionInfoEntity.getHash(), individualTransactionInfoEntity.getCreateTime(), individualTransactionInfoEntity.getWalletName(), individualTransactionInfoEntity.getMemo()));

        return getIndividualTransactionEntity.zipWith(getLatestBlockNumber, new BiFunction<IndividualTransactionEntity, Long, IndividualTransactionEntity>() {
            @Override
            public IndividualTransactionEntity apply(IndividualTransactionEntity individualTransactionEntity, Long latestBlockNumber) throws Exception {
                individualTransactionEntity.setLatestBlockNumber(latestBlockNumber);
                return individualTransactionEntity;
            }
        })
                .toFlowable();
    }
}

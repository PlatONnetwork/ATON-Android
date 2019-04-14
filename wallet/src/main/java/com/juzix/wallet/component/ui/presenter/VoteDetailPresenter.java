package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;
import com.juzix.wallet.entity.BatchVoteTransactionWrapEntity;
import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.VoteDetailItemEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DateUtil;

import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class VoteDetailPresenter extends BasePresenter<VoteDetailContract.View> implements VoteDetailContract.Presenter {

    private BatchVoteTransactionWrapEntity mBatchVoteTransactionEntity;

    public VoteDetailPresenter(VoteDetailContract.View view) {
        super(view);
        mBatchVoteTransactionEntity = view.getBatchVoteWrapTransactionFromIntent();
    }

    @Override
    public void loadData() {

        if (mBatchVoteTransactionEntity != null) {

            getView().showNodeDetailInfo(mBatchVoteTransactionEntity.getBatchVoteTransactionEntity());

            if (mBatchVoteTransactionEntity.getBatchVoteTransactionEntityList() == null || mBatchVoteTransactionEntity.getBatchVoteTransactionEntityList().isEmpty()) {
                loadVoteTicketListFromDB(mBatchVoteTransactionEntity.getBatchVoteTransactionEntity().getCandidateId());
            } else {
                loadVoteTicketListFromMemory(mBatchVoteTransactionEntity.getBatchVoteTransactionEntityList());
            }
        }
    }

    private void loadVoteTicketListFromMemory(List<BatchVoteTransactionEntity> batchVoteTransactionEntityList) {
        Flowable
                .fromIterable(batchVoteTransactionEntityList)
                .map(new Function<BatchVoteTransactionEntity, VoteDetailItemEntity>() {
                    @Override
                    public VoteDetailItemEntity apply(BatchVoteTransactionEntity batchVoteTransactionEntity) throws Exception {
                        return new VoteDetailItemEntity.Builder()
                                .candidateId(batchVoteTransactionEntity.getCandidateId())
                                .ticketPrice(BigDecimalUtil.div(NumberParserUtils.parseDouble(batchVoteTransactionEntity.getDeposit()), 1E18))
                                .voteStaked(batchVoteTransactionEntity.getVoteStaked())
                                .validVoteNum(NumberParserUtils.parseDouble(batchVoteTransactionEntity.getValidNum()))
                                .invalidVoteNum(NumberParserUtils.parseDouble(batchVoteTransactionEntity.getInvalidVoteNum()))
                                .walletAddress(batchVoteTransactionEntity.getOwner())
                                .createTime(batchVoteTransactionEntity.getTransactiontime())
                                .walletName(IndividualWalletManager.getInstance().getWalletNameByWalletAddress(batchVoteTransactionEntity.getOwner()))
                                .profit(batchVoteTransactionEntity.getEarnings())
                                .voteUnStaked(batchVoteTransactionEntity.getVoteUnStaked())
                                .build();
                    }
                })
                .toList()
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<List<VoteDetailItemEntity>>() {
                    @Override
                    public void accept(List<VoteDetailItemEntity> voteDetailItemEntityList) {
                        if (isViewAttached()) {
                            Collections.sort(voteDetailItemEntityList);
                            getView().notifyDataSetChanged(voteDetailItemEntityList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        ;
    }

    private void loadVoteTicketListFromDB(String candidateid) {

        Flowable.fromCallable(new Callable<List<SingleVoteInfoEntity>>() {

            @Override
            public List<SingleVoteInfoEntity> call() throws Exception {
                return SingleVoteInfoDao.getTransactionListByCandidateId(candidateid);
            }
        })
                .flatMap(new Function<List<SingleVoteInfoEntity>, Publisher<SingleVoteInfoEntity>>() {
                    @Override
                    public Publisher<SingleVoteInfoEntity> apply(List<SingleVoteInfoEntity> singleVoteInfoEntityList) throws Exception {
                        return Flowable.fromIterable(singleVoteInfoEntityList);
                    }
                })
                .map(new Function<SingleVoteInfoEntity, SingleVoteEntity>() {
                    @Override
                    public SingleVoteEntity apply(SingleVoteInfoEntity singleVoteInfoEntity) throws Exception {
                        return singleVoteInfoEntity.buildSingleVoteEntity();
                    }
                })
                .map(new Function<SingleVoteEntity, VoteDetailItemEntity>() {
                    @Override
                    public VoteDetailItemEntity apply(SingleVoteEntity singleVoteEntity) throws Exception {
                        return new VoteDetailItemEntity.Builder()
                                .candidateId(singleVoteEntity.getCandidateId())
                                .createTime(DateUtil.format(singleVoteEntity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND))
                                .walletName(singleVoteEntity.getWalletName())
                                .walletAddress(singleVoteEntity.getWalletAddress())
                                .transactionId(singleVoteEntity.getTransactionId())
                                .ticketPrice(BigDecimalUtil.div(NumberParserUtils.parseDouble(singleVoteEntity.getTicketPrice()), 1E18))
                                .voteStaked(singleVoteEntity.getVoteStaked())
                                .validVoteNum(singleVoteEntity.getValidVoteNum())
                                .invalidVoteNum(singleVoteEntity.getInvalidVoteNum())
                                .build();
                    }
                })
                .toList()
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<List<VoteDetailItemEntity>>() {
                    @Override
                    public void accept(List<VoteDetailItemEntity> voteDetailItemEntityList) {
                        if (isViewAttached()) {
                            Collections.sort(voteDetailItemEntityList);
                            getView().notifyDataSetChanged(voteDetailItemEntityList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}

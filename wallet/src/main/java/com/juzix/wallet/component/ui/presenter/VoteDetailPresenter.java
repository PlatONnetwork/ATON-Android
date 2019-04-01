package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.VoteDetailItemEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

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

    private static final long EXPIRE_BLOCKNUMBER = 1536000;

    private String mCandidateId;
    private String mCandidateName;

    public VoteDetailPresenter(VoteDetailContract.View view) {
        super(view);
        mCandidateId = view.getCandidateIdFromIntent();
        mCandidateName = view.getCandidateNameFromIntent();
    }

    @Override
    public void loadData() {

        showNodeDetailInfo();

        loadVoteTicketList();
    }

    private void showNodeDetailInfo() {
        if (isViewAttached()) {
            getView().showNodeDetailInfo(mCandidateId, mCandidateName);
        }
    }

    private void loadVoteTicketList() {

        Flowable.fromCallable(new Callable<List<SingleVoteInfoEntity>>() {

            @Override
            public List<SingleVoteInfoEntity> call() throws Exception {
                return SingleVoteInfoDao.getInstance().getTransactionListByCandidateId(mCandidateId);
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
                .map(new Function<SingleVoteEntity, SingleVoteEntity>() {
                    @Override
                    public SingleVoteEntity apply(SingleVoteEntity singleVoteEntity) throws Exception {
                        singleVoteEntity.setTickets(VoteManager.getInstance()
                                .getTicketDetail(singleVoteEntity.getTransactionId()).blockingGet());
                        return singleVoteEntity;
                    }
                })
                .map(new Function<SingleVoteEntity, VoteDetailItemEntity>() {
                    @Override
                    public VoteDetailItemEntity apply(SingleVoteEntity singleVoteEntity) throws Exception {
                        return new VoteDetailItemEntity.Builder()
                                .candidateId(singleVoteEntity.getCandidateId())
                                .createTime(singleVoteEntity.getCreateTime())
                                .walletName(singleVoteEntity.getWalletName())
                                .walletAddress(singleVoteEntity.getWalletAddress())
                                .transactionId(singleVoteEntity.getTransactionId())
                                .expireTime(singleVoteEntity.getCreateTime() + EXPIRE_BLOCKNUMBER)
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

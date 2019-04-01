package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.R;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.NodeDetailContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class NodeDetailPresenter extends BasePresenter<NodeDetailContract.View> implements NodeDetailContract.Presenter {

    private CandidateEntity mCandidateEntity;

    public NodeDetailPresenter(NodeDetailContract.View view) {
        super(view);
        mCandidateEntity = getView().getCandidateEntityFromIntent();
    }

    @Override
    public void getNodeDetailInfo() {
        if (isViewAttached()) {

            if (mCandidateEntity != null) {

                getView().showNodeDetailInfo(mCandidateEntity);

                VoteManager
                        .getInstance()
                        .getCandidateEpoch(mCandidateEntity.getCandidateId())
                        .compose(bindToLifecycle())
                        .compose(new SchedulersTransformer())
                        .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long epoch) throws Exception {
                                if (isViewAttached()) {
                                    getView().showEpoch(epoch);
                                }
                            }
                        });

            }
        }
    }

    @Override
    public void voteTicket() {
        if (isViewAttached()) {
            ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
            if (walletEntityList.isEmpty()) {
                showLongToast(R.string.voteTicketCreateWalletTips);
                return;
            }

            Flowable
                    .fromIterable(walletEntityList)
                    .map(new Function<IndividualWalletEntity, Double>() {

                        @Override
                        public Double apply(IndividualWalletEntity individualWalletEntity) throws Exception {
                            return individualWalletEntity.getBalance();
                        }
                    })
                    .reduce(new BiFunction<Double, Double, Double>() {
                        @Override
                        public Double apply(Double aDouble, Double aDouble2) throws Exception {
                            return BigDecimalUtil.add(aDouble, aDouble2);
                        }
                    })
                    .subscribe(new Consumer<Double>() {
                        @Override
                        public void accept(Double totalBalance) throws Exception {
                            if (totalBalance <= 0) {
                                showLongToast(R.string.voteTicketInsufficientBalanceTips);
                            } else {
                                SubmitVoteActivity.actionStart(currentActivity(), mCandidateEntity);
                            }
                        }
                    });
        }
    }
}

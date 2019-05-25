package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.NodeDetailContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Candidate;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class NodeDetailPresenter extends BasePresenter<NodeDetailContract.View> implements NodeDetailContract.Presenter {

    private Candidate mCandidateEntity;

    public NodeDetailPresenter(NodeDetailContract.View view) {
        super(view);
        mCandidateEntity = getView().getCandidateFromIntent();
    }

    @Override
    public void getNodeDetailInfo() {
        if (isViewAttached()) {
            getView().showNodeDetailInfo(mCandidateEntity);
        }
    }

    @Override
    public void voteTicket() {
        if (isViewAttached()) {
            List<Wallet> walletEntityList = WalletManager.getInstance().getWalletList();
            if (walletEntityList.isEmpty()) {
                showLongToast(R.string.voteTicketCreateWalletTips);
                return;
            }

            Flowable
                    .fromIterable(walletEntityList)
                    .map(new Function<Wallet, Double>() {

                        @Override
                        public Double apply(Wallet individualWalletEntity) throws Exception {
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
                                if (mCandidateEntity != null){
                                    SubmitVoteActivity.actionStart(currentActivity(), mCandidateEntity.getNodeId(),mCandidateEntity.getName(),mCandidateEntity.getDeposit());
                                }
                            }
                        }
                    });
        }
    }
}

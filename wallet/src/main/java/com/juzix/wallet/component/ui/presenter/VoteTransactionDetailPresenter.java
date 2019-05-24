package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualVoteDetailContract;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteDao;
import com.juzix.wallet.entity.SingleVoteEntity;

/**
 * @author matrixelement
 */
public class VoteTransactionDetailPresenter extends BasePresenter<IndividualVoteDetailContract.View> implements IndividualVoteDetailContract.Presenter {


    public VoteTransactionDetailPresenter(IndividualVoteDetailContract.View view) {
        super(view);
    }

    @Override
    public void fetchTransactionDetail() {
        if (isViewAttached()) {
            showLoadingDialog();
            new Thread() {
                @Override
                public void run() {
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_UPDATE_TRANSACTIONS;
                    SingleVoteInfoEntity voteInfoEntity = SingleVoteDao.getTransactionByUuid(getView().getTransactionUuidFromIntent());
                    if (voteInfoEntity != null) {
                        String candidateId = voteInfoEntity.getCandidateId();
                        if (!candidateId.startsWith("0x")) {
                            candidateId = "0x" + candidateId;
                        }
                        SingleVoteEntity voteEntity = new SingleVoteEntity.Builder()
                                .uuid(voteInfoEntity.getUuid())
                                .hash(voteInfoEntity.getHash())
                                .candidateId(candidateId)
                                .candidateName(voteInfoEntity.getCandidateName())
                                .host(voteInfoEntity.getHost())
                                .contractAddress(voteInfoEntity.getContractAddress())
                                .walletName(voteInfoEntity.getWalletName())
                                .walletAddress(voteInfoEntity.getWalletAddress())
                                .createTime(voteInfoEntity.getCreateTime())
                                .value(voteInfoEntity.getValue())
                                .ticketNumber(voteInfoEntity.getTicketNumber())
                                .ticketPrice(voteInfoEntity.getTicketPrice())
                                .blockNumber(voteInfoEntity.getBlockNumber())
                                .latestBlockNumber(voteInfoEntity.getLatestBlockNumber())
                                .energonPrice(voteInfoEntity.getEnergonPrice())
                                .status(voteInfoEntity.getStatus())
                                .tickets(null)
                                .build();
                        msg.obj = voteEntity;
                    }
                    mHandler.sendMessage(msg);
                }
            }.start();
        }
    }

    private static final int MSG_UPDATE_TRANSACTIONS = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TRANSACTIONS:
                    dismissLoadingDialogImmediately();
                    if (isViewAttached()) {
                        if (msg.obj != null) {
                            getView().setTransactionDetailInfo((SingleVoteEntity) msg.obj);
                        }
                    }
                    break;
            }
        }
    };

}

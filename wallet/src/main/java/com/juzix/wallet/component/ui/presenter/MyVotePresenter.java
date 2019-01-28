package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyVoteContract;
import com.juzix.wallet.component.ui.view.CreateSharedWalletActivity;
import com.juzix.wallet.component.ui.view.VoteActivity;
import com.juzix.wallet.component.ui.view.VoteDetailActivity;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.entity.TicketInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.TicketManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.TicketEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author matrixelement
 */
public class MyVotePresenter extends BasePresenter<MyVoteContract.View> implements MyVoteContract.Presenter {

    private double                      mVoteStaked;
    private long                        mValidVotes;
    private long                        mInvalidVotes;
    private double                      mProfit;
    private List<MyVoteContract.Entity> mEntities;
    private static final String         SPERATOR = ":";

    public MyVotePresenter(MyVoteContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        showLoadingDialog();
    }

    @Override
    public void refresh() {
        mVoteStaked = 0;
        mValidVotes = 0;
        mInvalidVotes = 0;
        mProfit = 0;
        mEntities = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                Map<String, MyVoteContract.Entity> entityMap                        = new HashMap<>();
                List<TicketInfoEntity>             ticketInfoEntityAllList          = new ArrayList<>();
                StringBuilder                      builder                          = new StringBuilder();
                List<SingleVoteInfoEntity>         singleVoteInfoEntityAllList = SingleVoteInfoDao.getInstance().getTransactionList();
                for (SingleVoteInfoEntity voteInfoEntity : singleVoteInfoEntityAllList) {
                    String transactionId = voteInfoEntity.getTransactionId();
                    String  candidateId = voteInfoEntity.getCandidateId();
                    if (!entityMap.containsKey(candidateId)) {
                        MyVoteContract.Entity entity = new MyVoteContract.Entity();
                        entity.avatar = voteInfoEntity.getAvatar();
                        entity.candidateName = voteInfoEntity.getCandidateName();
                        entity.candidateId = candidateId;
                        entity.region = voteInfoEntity.getRegion();
                        entityMap.put(candidateId, entity);
                    }
                    ticketInfoEntityAllList.addAll(voteInfoEntity.getTicketInfoEntityArrayList());
                    builder.append(transactionId).append(SPERATOR);
                }
                Map<String, TicketEntity> ticketEntityMap = TicketManager.getInstance().getTicketBatchDetail(builder.toString());
                for (TicketInfoEntity ticketInfoEntity : ticketInfoEntityAllList){
                    TicketEntity          ticketEntity = ticketEntityMap.get(ticketInfoEntity.getTicketId());
                    MyVoteContract.Entity entity       = entityMap.get(ticketInfoEntity.getCandidateId());
                    if (ticketEntity == null || entity == null){
                        continue;
                    }
                    long validVotes = 0;
                    long invalidVotes = 0;
                    switch (ticketEntity.getState()) {
                        case TicketEntity.NORMAL:
                            validVotes ++;
                            break;
                        default:
                            invalidVotes ++;
                    }

//                    long ticketPrice = Long.parseLong(ticketEntity.getDeposit());
                    double ticketPrice = BigDecimalUtil.div(ticketEntity.getDeposit(), "1E18");
                    entity.validVotes += validVotes;
                    entity.invalidVotes += invalidVotes;
                    entity.voteStaked += validVotes * ticketPrice;
                    entity.profit = 0;

                    mValidVotes += validVotes;
                    mInvalidVotes += invalidVotes;
                    mVoteStaked += validVotes * ticketPrice;
                    mProfit += entity.profit;
                }
                for (String candidateId : entityMap.keySet()){
                    mEntities.add(entityMap.get(candidateId));
                }
                mHandler.sendEmptyMessage(MSG_SUCCESS);
            }
        }.start();
    }


    @Override
    public void enterVoteDetailActivity(MyVoteContract.Entity entity) {
        VoteDetailActivity.actionStart(currentActivity(), entity.candidateId, entity.candidateName, entity.avatar);
    }

    @Override
    public void enterVoteActivity(MyVoteContract.Entity entity) {
        showLoadingDialog();
        new Thread(){
            @Override
            public void run() {
                Bundle data = new Bundle();
                data.putParcelable(Constants.Extra.EXTRA_CANDIDATE, CandidateManager.getInstance().getCandidateDetail(entity.candidateId));
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_VOTE;
                msg.setData(data);
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void voteTicket(CandidateEntity candidateEntity){
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.voteTicketCreateWalletTips);
            return;
        }
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (totalBalance <= 0){
            showLongToast(R.string.voteTicketInsufficientBalanceTips);
            return;
        }
        VoteActivity.actionStart(currentActivity(), candidateEntity);
    }

    private static final int MSG_SUCCESS = 0;
    private static final int MSG_VOTE = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SUCCESS:
                    if (isViewAttached()){
                        dismissLoadingDialogImmediately();
                        getView().showTicketInfo(mVoteStaked, mValidVotes, mInvalidVotes, mProfit);
                        getView().updateTickets(mEntities);
                    }
                    break;
                case MSG_VOTE:
                    if (isViewAttached()){
                        dismissLoadingDialogImmediately();
                        CandidateEntity candidateEntity = msg.getData().getParcelable(Constants.Extra.EXTRA_CANDIDATE);
                        if (candidateEntity != null) {
                            voteTicket(candidateEntity);
                        }
                    }
                    break;
            }
        }
    };
}

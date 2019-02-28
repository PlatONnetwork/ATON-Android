package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.entity.TicketInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.TicketManager;
import com.juzix.wallet.entity.TicketEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author matrixelement
 */
public class VoteDetailPresenter extends BasePresenter<VoteDetailContract.View> implements VoteDetailContract.Presenter {
    private List<VoteDetailContract.Entity> mEntities = new ArrayList<>();
    private static final String             SPERATOR  = ":";
    private static final long EXPIRE_BLOCKNUMBER = 1536000;

    public VoteDetailPresenter(VoteDetailContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        showLoadingDialog();
        new Thread(){
            @Override
            public void run() {
                mEntities.clear();
                List<SingleVoteInfoEntity>             singleVoteInfoEntityAllList = SingleVoteInfoDao.getInstance().getTransactionListByCandidateId(getView().getCandidateIdFromIntent());
                List<TicketInfoEntity>                 ticketInfoEntityAllList          = new ArrayList<>();
                StringBuilder                          builder                          = new StringBuilder();
                Map<String, VoteDetailContract.Entity> entityMap                        = new HashMap<>();
                for (SingleVoteInfoEntity voteInfoEntity : singleVoteInfoEntityAllList){
                    String transactionId = voteInfoEntity.getTransactionId();
                    builder.append(transactionId).append(SPERATOR);
                    ticketInfoEntityAllList.addAll(voteInfoEntity.getTicketInfoEntityArrayList());

                    VoteDetailContract.Entity entity = new VoteDetailContract.Entity();
                    entity.candidateId = voteInfoEntity.getCandidateId();
                    entity.createTime = voteInfoEntity.getCreateTime();
                    entity.walletName = voteInfoEntity.getWalletName();
                    entity.walletAddress = voteInfoEntity.getWalletAddress();
                    entityMap.put(transactionId, entity);
                }
                Map<String, TicketEntity> ticketEntityMap = TicketManager.getInstance().getTicketBatchDetail(builder.toString());
                for (TicketInfoEntity ticketInfoEntity : ticketInfoEntityAllList){
                    TicketEntity              ticketEntity = ticketEntityMap.get(ticketInfoEntity.getTicketId());
                    VoteDetailContract.Entity entity       = entityMap.get(ticketInfoEntity.getTransactionId());
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
                    double ticketPrice = BigDecimalUtil.div(ticketEntity.getDeposit(), "1E18");
                    entity.validVotes += validVotes;
                    entity.invalidVotes += invalidVotes;
                    entity.ticketPrice = String.valueOf(ticketPrice);
                    entity.voteStaked += validVotes * ticketPrice;
                    entity.voteUnstaked += invalidVotes * ticketPrice;
                    entity.profit = 0;
                    entity.expirTime = entity.createTime + EXPIRE_BLOCKNUMBER;
                }
                for (String transactionId : entityMap.keySet()){
                    mEntities.add(entityMap.get(transactionId));
                }
                Collections.sort(mEntities, new Comparator<VoteDetailContract.Entity>() {
                    @Override
                    public int compare(VoteDetailContract.Entity o1, VoteDetailContract.Entity o2) {
                        return Long.compare(o2.createTime, o1.createTime);
                    }
                });
                mHandler.sendEmptyMessage(MSG_UPDATE);
            }
        }.start();
    }

    private static final int MSG_UPDATE = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE:
                    if (isViewAttached()){
                        dismissLoadingDialogImmediately();
                        String candidateId = getView().getCandidateIdFromIntent();
                        if (!candidateId.startsWith("0x")){
                            candidateId = "0x" + candidateId;
                        }
                        getView().showCandidateInfo(CandidateManager.getInstance().getNodeIcon(getView().getCandidateIconFromIntent()), getView().getCandidateNameFromIntent(), candidateId);
                        getView().updateTickets(mEntities);
                    }
                    break;
            }
        }
    };
}

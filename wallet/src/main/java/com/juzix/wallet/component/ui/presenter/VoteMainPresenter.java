package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteMainContract;
import com.juzix.wallet.component.ui.view.VoteActivity;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.TicketManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author matrixelement
 */
public class VoteMainPresenter extends BasePresenter<VoteMainContract.View> implements VoteMainContract.Presenter {

    private final static int                        DEFAULT_MAX_TICKET_POOL_SIZE = 51200;
    private final static int                        DEFAULT_VOTE_NUM             = 100;
    private final static int                        DEFAULT_DEPOSIT_RANKING      = 100;
    private static final int                        REFRESH_TIME                 = 5000;
    private              int                        mSortType;
    private              String                     mKeword;
    private              boolean                    mFinished;
    private              ArrayList<CandidateEntity> mCandidateEntiyList          = new ArrayList<>();

    public VoteMainPresenter(VoteMainContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        mSortType = SORT_DEFAULT;
        mFinished = false;
//        updateTicketInfo();
//        updateCandidateList();
        mMonitor.start();
    }

    @Override
    public void destroy() {
        mFinished = true;
        try {
            mMonitor.interrupt();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void sort(int type) {
        mSortType = type;
        showCandidateList();
    }

    @Override
    public void search(String keyword) {
        mKeword = keyword;
        showCandidateList();
    }

    @Override
    public void voteTicket(CandidateEntity candidateEntity){
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

    private void showCandidateList() {
        if (mCandidateEntiyList.isEmpty()) {
            return;
        }
        ArrayList<CandidateEntity> candidateEntities = new ArrayList<>();
        if (mKeword == null || TextUtils.isEmpty(mKeword.trim())) {
            candidateEntities.addAll(mCandidateEntiyList);
        } else {
            for (CandidateEntity candidateEntity : mCandidateEntiyList) {
                CandidateExtraEntity candidateExtraEntity = candidateEntity.getCandidateExtraEntity();
                if (candidateExtraEntity != null) {
                    String nodeName = candidateExtraEntity.getNodeName();
                    if ((!TextUtils.isEmpty(nodeName)) && nodeName.toLowerCase().contains(mKeword.toLowerCase())) {
                        candidateEntities.add(candidateEntity);
                    }
                }
            }
        }
        switch (mSortType) {
            case SORT_DEFAULT:
                sortedByDefault(candidateEntities);
                break;
            case SORT_REWARD:
                sortedByReward(candidateEntities);
                break;
            case SORT_LOCATION:
                sortedByRegion(candidateEntities);
                break;
        }
        if (isViewAttached()) {
            getView().notifyDataSetChanged(candidateEntities);
        }
    }

    private void updateTicketInfo() {
        new Thread() {
            @Override
            public void run() {
                long   tickets     = TicketManager.getInstance().getPoolRemainder();
                String   ticketPrice = TicketManager.getInstance().getTicketPrice();
                long   votedNum    = DEFAULT_MAX_TICKET_POOL_SIZE - tickets;
                double voteRate    = BigDecimalUtil.div(votedNum * 100, DEFAULT_MAX_TICKET_POOL_SIZE);
                Bundle data        = new Bundle();
                data.putDouble("voteRatio", voteRate);
                data.putLong("votedNum", votedNum);
                data.putString("ticketPrice", ticketPrice);
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_UPDATE_TICKET_INFO;
                msg.setData(data);
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void updateCandidateList() {
        new Thread() {
            @Override
            public void run() {
                ArrayList<CandidateEntity> candidateEntities = CandidateManager.getInstance().getCandidateList();
                sortedByDefault(candidateEntities);
                if (!mCandidateEntiyList.isEmpty()) {
                    mCandidateEntiyList.clear();
                }
                mCandidateEntiyList.addAll(candidateEntities);
                mHandler.sendEmptyMessage(MSG_UPDATE_LIST);
            }
        }.start();
    }

    private void sortedByDefault(List<CandidateEntity> entityList){
        if (entityList.isEmpty() || entityList.size() == 1){
            return;
        }
        Collections.sort(entityList, new Comparator<CandidateEntity>() {
            @Override
            public int compare(CandidateEntity o1, CandidateEntity o2) {
                return Double.compare(NumberParserUtils.parseDouble(o2.getDeposit()), NumberParserUtils.parseDouble(o1.getDeposit()));
            }
        });
        List<CandidateEntity> candidateList = new ArrayList<>();
        List<CandidateEntity> alternativeList = new ArrayList<>();
        int len = entityList.size();
        for (int i = 0; i < len; i++){
            //剔除排名200后的节点
            if (i == 2 * DEFAULT_DEPOSIT_RANKING - 1){
                break;
            }
            CandidateEntity entity = entityList.get(i);
            entity.setStakedRanking(i + 1);
            if (i < DEFAULT_DEPOSIT_RANKING && entity.getVotedNum() >= DEFAULT_VOTE_NUM){
                entity.setStatus(CandidateEntity.STATUS_CANDIDATE);
                candidateList.add(entity);
            }else {
                entity.setStatus(CandidateEntity.STATUS_RESERVE);
                alternativeList.add(entity);
            }
        }
        entityList.clear();
        entityList.addAll(candidateList);
        entityList.addAll(alternativeList);
    }

    private void sortedByReward(List<CandidateEntity> entityList){
        if (entityList.isEmpty() || entityList.size() == 1){
            return;
        }
        Collections.sort(entityList, new Comparator<CandidateEntity>() {
            @Override
            public int compare(CandidateEntity o1, CandidateEntity o2) {
                int compare = Integer.compare(o2.getFee(), o1.getFee());
                if (compare != 0){
                    return compare;
                }
                compare = Integer.compare(o1.getStatus(), o2.getStatus());
                if (compare != 0){
                    return compare;
                }
                compare = Double.compare(NumberParserUtils.parseDouble(o2.getDeposit()), NumberParserUtils.parseDouble(o1.getDeposit()));
                if (compare != 0){
                    return compare;
                }
                compare = Long.compare(o2.getVotedNum(), o1.getVotedNum());
                if (compare != 0){
                    return compare;
                }
                return Long.compare(o1.getCandidateExtraEntity().getTime(), o2.getCandidateExtraEntity().getTime());
            }
        });
    }

    private void sortedByRegion(List<CandidateEntity> entityList){
        if (entityList.isEmpty() || entityList.size() == 1){
            return;
        }
        Collections.sort(entityList, new Comparator<CandidateEntity>() {
            @Override
            public int compare(CandidateEntity o1, CandidateEntity o2) {
                return o1.getRegionPinyin().compareToIgnoreCase(o2.getRegionPinyin());
            }
        });
    }

    private Thread mMonitor = new Thread() {
        @Override
        public void run() {
            while (!mFinished) {
                updateTicketInfo();
                updateCandidateList();
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
    };

    private static final int     MSG_UPDATE_TICKET_INFO = 1;
    private static final int     MSG_UPDATE_LIST        = 2;
    private              Handler mHandler               = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TICKET_INFO:
                    if (isViewAttached() && !mFinished) {
                        Bundle data = msg.getData();
                        getView().setTicketInfo(data.getDouble("voteRatio"), data.getLong("votedNum"), String.valueOf(BigDecimalUtil.div(data.getString("ticketPrice"), "1E18")));
                    }
                    break;

                case MSG_UPDATE_LIST:
                    if (isViewAttached() && !mFinished) {
                        showCandidateList();
                    }
                    break;
            }
        }
    };
}

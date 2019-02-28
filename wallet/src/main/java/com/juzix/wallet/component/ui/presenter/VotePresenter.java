package com.juzix.wallet.component.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletBalanceDialogFragment;
import com.juzix.wallet.component.ui.dialog.VoteConfirmationDialogFragment;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.TicketManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class VotePresenter extends BasePresenter<VoteContract.View> implements VoteContract.Presenter {

    private              IndividualWalletEntity mWalletEntity;
    private              long                   mVotes;
    private static final int                    TYPE_UPDATE = 1;
    private static final int                    TYPE_SUBMIT = 2;

    public VotePresenter(VoteContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        mVotes = 1;
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        IndividualWalletEntity            walletEntity     = null;
        for (IndividualWalletEntity entity : walletEntityList) {
            if (entity.getBalance() > 0) {
                walletEntity = entity;
                break;
            }
        }
        if (!walletEntityList.isEmpty()) {
            updateSelectOwner(walletEntity);
        }
        CandidateEntity entity = getView().getCandidateFromIntent();
        String candidateName = "";
        int iconRes = -1;
        CandidateExtraEntity extraEntity = entity.getCandidateExtraEntity();
        if (extraEntity != null){
            candidateName = extraEntity.getNodeName();
            iconRes = CandidateManager.getInstance().getNodeIcon(extraEntity.getNodePortrait());
        }
        getView().showVoteInfo(iconRes, candidateName, Numeric.prependHexPrefix(entity.getCandidateId()));
        updateVoteInfo(TYPE_UPDATE);
    }

    @Override
    public void submit() {
        updateVoteInfo(TYPE_SUBMIT);
    }

    @Override
    public void showSelectWalletDialogFragment() {
        if (isViewAttached()) {
            SelectIndividualWalletBalanceDialogFragment.newInstance(mWalletEntity == null ? "" : mWalletEntity.getUuid()).show(currentActivity().getSupportFragmentManager(), "selectWallet");
        }
    }

    @Override
    public void updateSelectOwner(IndividualWalletEntity walletEntity) {
        this.mWalletEntity = walletEntity;
        if (isViewAttached()) {
            getView().updateSelectOwner(walletEntity);
            getView().setNextButtonEnable(walletEntity != null);
        }
    }

    @Override
    public void setVotes(String text) {
        if (TextUtils.isEmpty(text)){
            mVotes = 0;
        }else {
            mVotes = Long.parseLong(text);
        }
        updateVoteInfo(TYPE_UPDATE);
        getView().setNextButtonEnable(mWalletEntity != null && mVotes > 0);
    }

    @Override
    public void addVotes() {
        if (isViewAttached()) {
            getView().showVotes(++mVotes);
            updateVoteInfo(TYPE_UPDATE);
        }
    }

    @Override
    public void subVotes() {
        if (isViewAttached() && mVotes > 1) {
            getView().showVotes(--mVotes);
            updateVoteInfo(TYPE_UPDATE);
        }
    }

    private void showVoteConfirmationDialogFragment(double amount) {
        double feeAmount = BigDecimalUtil.mul(TicketManager.GAS_PRICE.doubleValue(), TicketManager.GAS_LIMIT.doubleValue());
        VoteConfirmationDialogFragment.newInstance(BigDecimalUtil.div(String.valueOf(feeAmount), "1E18"), amount).setOnSubmitClickListener(new VoteConfirmationDialogFragment.OnSubmitClickListener() {
            @Override
            public void onSubmitClick() {
                showInputWalletPasswordDialogFragment("");
            }
        }).show(currentActivity().getSupportFragmentManager(), "voteConfirmation");
    }

    private void showInputWalletPasswordDialogFragment(String password) {
        InputWalletPasswordDialogFragment.newInstance(password).setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String password) {
                voteTicket(password);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    private void updateVoteInfo(int type) {
        new Thread() {
            @Override
            public void run() {
                String    ticketPrice = TicketManager.getInstance().getTicketPrice();
                Bundle bundle = new Bundle();
                bundle.putInt("type", type);
                bundle.putLong("poolRemainder", TicketManager.getInstance().getPoolRemainder());
                bundle.putString("ticketPrice", ticketPrice);
                bundle.putDouble("amount", BigDecimalUtil.div(BigDecimalUtil.mul(Double.parseDouble(ticketPrice), mVotes), 1E18));
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_UPDATE_VOTE_INFO_SUCCESS;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void voteTicket(String password) {
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                CandidateEntity entity = getView().getCandidateFromIntent();
                int code = TicketManager.getInstance().submitVoteTicket(password, mVotes,
                        TicketManager.getInstance().getTicketPrice(), mWalletEntity, entity);
                switch (code) {
                    case TicketManager.CODE_OK:
                        mHandler.sendEmptyMessage(MSG_VOTE_TICKET_SUCCESS);
                        break;
                    case TicketManager.CODE_ERROR_PASSWORD:
                        Message msg = mHandler.obtainMessage();
                        msg.obj = password;
                        msg.what = MSG_PASSWORD_ERROR;
                        mHandler.sendMessage(msg);
                        break;
                    case TicketManager.CODE_ERROR_VOTE_TICKET:
                        mHandler.sendEmptyMessage(MSG_VOTE_TICKET_ERROR);
                        break;
                }
            }
        }.start();
    }

    private static final int MSG_UPDATE_VOTE_INFO_SUCCESS = 0;
    private static final int MSG_VOTE_TICKET_SUCCESS      = 2;
    private static final int MSG_PASSWORD_ERROR           = -1;
    private static final int MSG_VOTE_TICKET_ERROR        = -2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case MSG_VOTE_TICKET_SUCCESS:
                    if (isViewAttached()) {
                        dismissLoadingDialogImmediately();
                        BaseActivity activity = currentActivity();
                        activity.finish();
                    }
                    break;
                case MSG_VOTE_TICKET_ERROR:
                    if (isViewAttached()) {
                        dismissLoadingDialogImmediately();
                    }
                    break;
                case MSG_PASSWORD_ERROR:
                    if (isViewAttached()) {
                        dismissLoadingDialogImmediately();
                        CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
                            @Override
                            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                showInputWalletPasswordDialogFragment((String)msg.obj);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
                    }
                    break;
                case MSG_UPDATE_VOTE_INFO_SUCCESS:
                    int type = data.getInt("type");
                    String ticketPrice = data.getString("ticketPrice");
                    double amount = data.getDouble("amount");
                    long poolRemainder = data.getLong("poolRemainder");
                    if (type == TYPE_UPDATE){
                        if (isViewAttached()) {
//                            getView().showPayInfo(ticketPrice, amount);
                            getView().showPayInfo(BigDecimalUtil.div(ticketPrice, "1E18"), amount);
                        }
                    }else if (type == TYPE_SUBMIT){
                        if (amount >= mWalletEntity.getBalance()){
                            showLongToast(R.string.voteTicketInsufficientBalanceTips);
                            return;
                        }
                        if (mVotes > poolRemainder){
                            showLongToast(R.string.voteLimitFailed);
                            return;
                        }
                        showVoteConfirmationDialogFragment(amount);
                    }
                    break;
            }
        }
    };
}

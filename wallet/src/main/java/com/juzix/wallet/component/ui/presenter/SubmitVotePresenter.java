package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SubmitVoteContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.component.ui.dialog.SendTransactionDialogFragment;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class SubmitVotePresenter extends BasePresenter<SubmitVoteContract.View> implements SubmitVoteContract.Presenter {

    private String mCandidateId;
    private String mCandidateName;
    private String mTicketPrice;
    private IndividualWalletEntity mIndividualWalletEntity;

    public SubmitVotePresenter(SubmitVoteContract.View view) {
        super(view);
        mCandidateId = view.getCandidateIdFromIntent();
        mCandidateName = view.getCandidateNameFromIntent();
    }

    @Override
    public void showVoteInfo() {
        getView().showNodeInfo(mCandidateName, mCandidateId);
        showSelectedWalletInfo();
        showVotePayInfo();
    }

    @SuppressLint("CheckResult")
    @Override
    public void showVotePayInfo() {

        VoteManager
                .getInstance()
                .getTicketPrice()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String tp) throws Exception {

                        mTicketPrice = tp;

                        updateVotePayInfo();
                    }
                });
    }


    @SuppressLint("CheckResult")
    @Override
    public void submitVote() {

        VoteManager
                .getInstance()
                .getPoolRemainder()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long poolRemainder) throws Exception {
                        if (isViewAttached()) {
                            String ticketNum = getView().getTicketNum();
                            double ticketAmount = BigDecimalUtil.div(BigDecimalUtil.mul(Double.parseDouble(mTicketPrice), NumberParserUtils.parseInt(ticketNum)), 1E18);
                            if (ticketAmount >= mIndividualWalletEntity.getBalance()) {
                                showLongToast(R.string.voteTicketInsufficientBalanceTips);
                                return;
                            }

                            if (NumberParserUtils.parseInt(ticketNum) > NumberParserUtils.parseInt(poolRemainder)) {
                                showLongToast(R.string.voteLimitFailed);
                                return;
                            }

                            double feeAmount = BigDecimalUtil.div(BigDecimalUtil.mul(VoteManager.GAS_PRICE.doubleValue(), VoteManager.GAS_LIMIT.doubleValue()), 1E18);

                            SendTransactionDialogFragment
                                    .newInstance(NumberParserUtils.getPrettyNumber(ticketAmount, 0), buildTransactionInfo(mIndividualWalletEntity.getName(), feeAmount))
                                    .setOnConfirmBtnClickListener(new SendTransactionDialogFragment.OnConfirmBtnClickListener() {
                                        @Override
                                        public void onConfirmBtnClick() {
                                            InputWalletPasswordDialogFragment
                                                    .newInstance(mIndividualWalletEntity)
                                                    .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                                                        @Override
                                                        public void onWalletPasswordCorrect(Credentials credentials) {
                                                            submitVote(credentials, ticketNum, mTicketPrice);
                                                        }
                                                    })
                                                    .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "sendTransaction");

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                showLongToast(customThrowable.getDetailMsgRes());
                            } else {
                                showLongToast(R.string.vote_failed);
                            }
                        }
                    }
                });
    }

    @Override
    public void showSelectWalletDialogFragment() {
        SelectWalletDialogFragment.newInstance(mIndividualWalletEntity != null ? mIndividualWalletEntity.getUuid() : "", true)
                .setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(IndividualWalletEntity walletEntity) {
                        if (isViewAttached()) {
                            mIndividualWalletEntity = walletEntity;
                            getView().showSelectedWalletInfo(walletEntity);
                        }
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "showSelectWalletDialog");
    }

    @Override
    public void updateVotePayInfo() {
        if (isViewAttached()) {
            int ticketNum = NumberParserUtils.parseInt(getView().getTicketNum());
            double ticketPrice = BigDecimalUtil.div(NumberParserUtils.parseDouble(mTicketPrice), 1E18);
            double ticketAmount = BigDecimalUtil.mul(ticketPrice, ticketNum);
            getView().showVotePayInfo(ticketPrice, ticketAmount);
        }
    }

    private void submitVote(Credentials credentials, String ticketNum, String ticketPrice) {
        VoteManager
                .getInstance()
                .submitVote(credentials, mIndividualWalletEntity, mCandidateId, mCandidateName, ticketNum, ticketPrice)
                .compose(bindToLifecycle())
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<SingleVoteInfoEntity>() {
                    @Override
                    public void accept(SingleVoteInfoEntity voteInfoEntity) throws Exception {
                        if (isViewAttached()) {
                            showLongToast(R.string.vote_success);
                            currentActivity().finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            showLongToast(R.string.vote_failed);
                        }
                    }
                });
    }

    private Map<String, String> buildTransactionInfo(String walletName, double fee) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(string(R.string.type), string(R.string.voting));
        map.put(string(R.string.pay_wallet), walletName);
        map.put(string(R.string.fee), string(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(fee, 8)));
        return map;
    }

    private void showSelectedWalletInfo() {
        mIndividualWalletEntity = IndividualWalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mIndividualWalletEntity != null) {
            getView().showSelectedWalletInfo(mIndividualWalletEntity);
        }
    }
}

package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

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
import com.juzix.wallet.component.ui.view.AssetsFragment;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class SubmitVotePresenter extends BasePresenter<SubmitVoteContract.View> implements SubmitVoteContract.Presenter {

    private String mCandidateId;
    private String mCandidateName;
    private String mCandidateDeposit;
    private String mTicketPrice;
    private Wallet mWallet;

    public SubmitVotePresenter(SubmitVoteContract.View view) {
        super(view);
        mCandidateId = view.getCandidateIdFromIntent();
        mCandidateName = view.getCandidateNameFromIntent();
        mCandidateDeposit = view.getCandidateDepositFromIntent();
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
                .zipWith(Web3jManager.getInstance().getGasPrice(), new BiFunction<Long, Long, String>() {
                    @Override
                    public String apply(Long poolRemainder, Long gasPrice) throws Exception {
                        return poolRemainder + "&" + gasPrice;
                    }
                })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        if (isViewAttached()) {
                            long poolRemainder = NumberParserUtils.parseLong(result.split("&", 2)[0]);
                            long gasPrice = NumberParserUtils.parseLong(result.split("&", 2)[1]);
                            String ticketNum = getView().getTicketNum();
                            double ticketAmount = BigDecimalUtil.div(BigDecimalUtil.mul(Double.parseDouble(mTicketPrice), NumberParserUtils.parseInt(ticketNum)), 1E18);
                            if (ticketAmount >= mWallet.getBalance()) {
                                showLongToast(R.string.voteTicketInsufficientBalanceTips);
                                return;
                            }

                            if (NumberParserUtils.parseInt(ticketNum) > NumberParserUtils.parseInt(poolRemainder)) {
                                showLongToast(R.string.voteLimitFailed);
                                return;
                            }

                            double feeAmount = BigDecimalUtil.mul(VoteManager.GAS_DEPLOY_CONTRACT.doubleValue(), gasPrice);

                            SendTransactionDialogFragment
                                    .newInstance(string(R.string.vote_transaction),NumberParserUtils.getPrettyNumber(ticketAmount, 0), buildTransactionInfo(mWallet.getName(), feeAmount))
                                    .setOnConfirmBtnClickListener(new SendTransactionDialogFragment.OnConfirmBtnClickListener() {
                                        @Override
                                        public void onConfirmBtnClick() {
                                            InputWalletPasswordDialogFragment
                                                    .newInstance(mWallet)
                                                    .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                                                        @Override
                                                        public void onWalletPasswordCorrect(Credentials credentials) {
                                                            submitVote(credentials, ticketNum, mTicketPrice, mCandidateDeposit, String.valueOf(feeAmount));
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
        SelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", true)
                .setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet walletEntity) {
                        if (isViewAttached()) {
                            mWallet = walletEntity;
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

    @SuppressLint("CheckResult")
    private void submitVote(Credentials credentials, String ticketNum, String ticketPrice, String deposit, String feeAmount) {
        VoteManager
                .getInstance()
                .submitVote(credentials, mWallet, mCandidateId, mCandidateName, ticketNum, ticketPrice, deposit, feeAmount)
                .compose(bindToLifecycle())
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
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
        map.put(string(R.string.fee), string(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(fee, 1E18), 8)));
        return map;
    }

    private void showSelectedWalletInfo() {
        mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mWallet != null) {
            getView().showSelectedWalletInfo(mWallet);
        }
    }
}

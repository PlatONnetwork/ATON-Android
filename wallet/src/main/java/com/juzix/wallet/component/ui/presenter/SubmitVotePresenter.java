package com.juzix.wallet.component.ui.presenter;

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
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import org.reactivestreams.Publisher;
import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class SubmitVotePresenter extends BasePresenter<SubmitVoteContract.View> implements SubmitVoteContract.Presenter {

    private final static int MAX_TICKET_POOL_SIZE = 51200;
    private final static int DEFAULT_VOTE_NUM = 512;
    private final static int DEFAULT_DEPOSIT_RANKING = 100;

    private CandidateEntity mCandidateEntity;
    private IndividualWalletEntity mIndividualWalletEntity;
    private String mTicketPrice;

    public SubmitVotePresenter(SubmitVoteContract.View view) {
        super(view);
        mCandidateEntity = view.getCandidateFromIntent();
    }

    @Override
    public void showVoteInfo() {
//        showNodeInfo(mCandidateEntity);
//        showSelectedWalletInfo();
//        showVotePayInfo();
    }

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

    @Override
    public void submitVote() {

//        getVerifiersList()
//                .contains(mCandidateEntity.getCandidateId())
//                .filter(new Predicate<Boolean>() {
//                    @Override
//                    public boolean test(Boolean aBoolean) throws Exception {
//                        return !aBoolean;
//                    }
//                })
//                .switchIfEmpty(new SingleSource<Boolean>() {
//                    @Override
//                    public void subscribe(SingleObserver<? super Boolean> observer) {
//                        observer.onError(new CustomThrowable(CustomThrowable.CODE_NODE_EXIT_CONSENSUS));
//                    }
//                })
//                .flatMap(new Function<Boolean, SingleSource<String>>() {
//                    @Override
//                    public SingleSource<String> apply(Boolean aBoolean) throws Exception {
//                        return Single
//                                .zip(VoteManager.getInstance().getPoolRemainder(), VoteManager.getInstance().getTicketPrice(), new BiFunction<Long, String, String>() {
//                                    @Override
//                                    public String apply(Long poolRemainder, String ticketPrice) throws Exception {
//                                        return poolRemainder + ":" + ticketPrice;
//                                    }
//                                });
//                    }
//                })
//                .compose(new SchedulersTransformer())
//                .compose(bindToLifecycle())
//                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String result) throws Exception {
//                        if (isViewAttached()) {
//                            String ticketNum = getView().getTicketNum();
//                            String poolRemainder = result.split(":", 2)[0];
//                            String ticketPrice = result.split(":", 2)[1];
//                            double ticketAmount = BigDecimalUtil.div(BigDecimalUtil.mul(Double.parseDouble(ticketPrice), NumberParserUtils.parseInt(ticketNum)), 1E18);
//                            if (ticketAmount >= mIndividualWalletEntity.getBalance()) {
//                                showLongToast(R.string.voteTicketInsufficientBalanceTips);
//                                return;
//                            }
//
//                            if (NumberParserUtils.parseInt(ticketNum) > NumberParserUtils.parseInt(poolRemainder)) {
//                                showLongToast(R.string.voteLimitFailed);
//                                return;
//                            }
//
//                            double feeAmount = BigDecimalUtil.div(BigDecimalUtil.mul(VoteManager.GAS_PRICE.doubleValue(), VoteManager.GAS_LIMIT.doubleValue()), 1E18);
//
//                            SendTransactionDialogFragment
//                                    .newInstance(NumberParserUtils.getPrettyNumber(ticketAmount, 0), buildTransactionInfo(mIndividualWalletEntity.getName(), feeAmount))
//                                    .setOnConfirmBtnClickListener(new SendTransactionDialogFragment.OnConfirmBtnClickListener() {
//                                        @Override
//                                        public void onConfirmBtnClick() {
//                                            InputWalletPasswordDialogFragment
//                                                    .newInstance(mIndividualWalletEntity)
//                                                    .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
//                                                        @Override
//                                                        public void onWalletPasswordCorrect(Credentials credentials) {
//                                                            submitVote(credentials, ticketNum, ticketPrice);
//                                                        }
//                                                    })
//                                                    .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
//                                        }
//                                    })
//                                    .show(currentActivity().getSupportFragmentManager(), "sendTransaction");
//
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        if (isViewAttached()) {
//                            if (throwable instanceof CustomThrowable) {
//                                CustomThrowable customThrowable = (CustomThrowable) throwable;
//                                showLongToast(customThrowable.getDetailMsgRes());
//                            } else {
//                                showLongToast(R.string.vote_failed);
//                            }
//                        }
//                    }
//                });
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

    /**
     * 获取不在池子中的验证节点列表，要放在提名节点的后面
     *
     * @param candidateList
     * @param reserveList
     * @param verifyList
     * @return
     */
    private List<CandidateEntity> getAbsentVerifiersList(List<CandidateEntity> candidateList, List<CandidateEntity> reserveList, List<CandidateEntity> verifyList) {
        List<CandidateEntity> absentVerifiersList = new ArrayList<>();
        if (verifyList == null || verifyList.isEmpty()) {
            return absentVerifiersList;
        }
        for (CandidateEntity candidateEntity : verifyList) {
            if (candidateList.contains(candidateEntity) || reserveList.contains(candidateEntity)) {
                continue;
            }
            absentVerifiersList.add(candidateEntity);
        }
        return absentVerifiersList;
    }

    private void submitVote(Credentials credentials, String ticketNum, String ticketPrice) {
        VoteManager
                .getInstance()
                .submitVote(credentials, mIndividualWalletEntity, mCandidateEntity, ticketNum, ticketPrice)
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

//    private void showNodeInfo(CandidateEntity candidateEntity) {
//        if (candidateEntity == null) {
//            return;
//        }
//        CandidateExtraEntity candidateExtraEntity = candidateEntity.getCandidateExtraEntity();
//        if (candidateExtraEntity != null) {
//            getView().showNodeInfo(candidateExtraEntity.getNodeName(), candidateEntity.getCandidateIdWithPrefix());
//        }
//    }

    private void showSelectedWalletInfo() {
        mIndividualWalletEntity = IndividualWalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mIndividualWalletEntity != null) {
            getView().showSelectedWalletInfo(mIndividualWalletEntity);
        }
    }
}

package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyDelegateContract;
import com.juzix.wallet.component.ui.dialog.ClaimRewardsDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.juzix.wallet.component.ui.dialog.TransactionSignatureDialogFragment;
import com.juzix.wallet.db.entity.TransactionEntity;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.Optional;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.ClaimRewardInfo;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.entity.GasProvider;
import com.juzix.wallet.entity.RPCErrorCode;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionAuthorizationBaseData;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

public class MyDelegatePresenter extends BasePresenter<MyDelegateContract.View> implements MyDelegateContract.Presenter {

    private Disposable mDisposable;

    public MyDelegatePresenter(MyDelegateContract.View view) {
        super(view);
    }

    @Override
    public void loadMyDelegateData() {

        mDisposable = ServerUtils.getCommonApi().getMyDelegateList(ApiRequestBody.newBuilder().
                put("walletAddrs", WalletManager.getInstance().getAddressList())
                .build())
                .map(new Function<Response<ApiResponse<List<DelegateInfo>>>, Optional<List<DelegateInfo>>>() {
                    @Override
                    public Optional<List<DelegateInfo>> apply(Response<ApiResponse<List<DelegateInfo>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body() != null ? new Optional<List<DelegateInfo>>(apiResponseResponse.body().getData()) : new Optional<List<DelegateInfo>>(null);
                    }
                })
                .toFlowable()
                .filter(new Predicate<Optional<List<DelegateInfo>>>() {
                    @Override
                    public boolean test(Optional<List<DelegateInfo>> listOptional) throws Exception {
                        return !listOptional.isEmpty();
                    }
                })
                .switchIfEmpty(new Publisher<Optional<List<DelegateInfo>>>() {
                    @Override
                    public void subscribe(Subscriber<? super Optional<List<DelegateInfo>>> s) {
                        s.onError(new Throwable());
                    }
                })
                .flatMap(new Function<Optional<List<DelegateInfo>>, Publisher<DelegateInfo>>() {
                    @Override
                    public Publisher<DelegateInfo> apply(Optional<List<DelegateInfo>> listOptional) throws Exception {
                        return Flowable.fromIterable(listOptional.get());
                    }
                })
                .map(new Function<DelegateInfo, DelegateInfo>() {
                    @Override
                    public DelegateInfo apply(DelegateInfo delegateInfo) throws Exception {
                        delegateInfo.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(delegateInfo.getWalletAddress()));
                        delegateInfo.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(delegateInfo.getWalletAddress()));
                        delegateInfo.setObservedWallet(WalletManager.getInstance().isObservedWallet(delegateInfo.getWalletAddress()));
                        TransactionEntity transactionEntity = TransactionDao.getTransaction(delegateInfo.getWalletAddress(), String.valueOf(TransactionType.CLAIM_REWARDS.getTxTypeValue()));
                        if (transactionEntity != null) {
                            TransactionStatus transactionStatus = TransactionStatus.getTransactionStatusByIndex(transactionEntity.getTxReceiptStatus());
                            if (transactionStatus == TransactionStatus.PENDING) {
                                delegateInfo.setPending(true);
                                TransactionManager.getInstance().getTransactionByLoop(transactionEntity.toTransaction());
                            }
                        }
                        return delegateInfo;
                    }
                })
                .toList()
                .compose(RxUtils.bindToParentLifecycleUtilEvent(getView(), FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<List<DelegateInfo>>() {
                    @Override
                    public void accept(List<DelegateInfo> delegateInfos) throws Exception {
                        if (isViewAttached()) {
                            getView().showMyDelegateData(delegateInfos);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getView().showMyDelegateData(null);
                        }
                    }
                });

    }

    @Override
    public void withdrawDelegateReward(DelegateInfo delegateInfo, int position) {
        ServerUtils.getCommonApi().getGasProvider(ApiRequestBody.newBuilder()
                .put("from", delegateInfo.getWalletAddress())
                .put("txType", FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE)
                .build())
                .compose(RxUtils.bindToLifecycle(currentActivity()))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<GasProvider>() {
                    @Override
                    public void onApiSuccess(GasProvider gasProvider) {
                        if (isViewAttached()) {
                            showClaimRewardsDialogFragment(delegateInfo, gasProvider.toSdkGasProvider(), position);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        super.onApiFailure(response);
                        if (isViewAttached()) {
                            showLongToast(R.string.msg_connect_timeout);
                        }
                    }
                });
    }

    public void showClaimRewardsDialogFragment(DelegateInfo delegateInfo, org.web3j.tx.gas.GasProvider gasProvider, int position) {
        ClaimRewardInfo claimRewardInfo = new ClaimRewardInfo.Builder()
                .setFeeAmount(BigDecimalUtil.mul(gasProvider.getGasPrice().toString(10), gasProvider.getGasLimit().toString(10)).toPlainString())
                .setClaimRewardAmount(delegateInfo.getWithdrawReward())
                .setFromWalletName(delegateInfo.getWalletName())
                .setFromWalletAddress(delegateInfo.getWalletAddress())
                .setAvaliableBalanceAmount(WalletManager.getInstance().getWalletByAddress(delegateInfo.getWalletAddress()).getFreeBalance())
                .build();
        ClaimRewardsDialogFragment.newInstance(claimRewardInfo).setOnConfirmBtnClickListener(new ClaimRewardsDialogFragment.OnConfirmBtnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                if (delegateInfo.isObservedWallet()) {
                    showTransactionAuthorizationDialogFragment(delegateInfo.getWithdrawReward(), delegateInfo.getWalletAddress(), gasProvider, position);
                } else {
                    InputWalletPasswordDialogFragment.newInstance(WalletManager.getInstance().getWalletByAddress(delegateInfo.getWalletAddress())).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                        @Override
                        public void onWalletPasswordCorrect(Credentials credentials) {
                            DelegateManager.getInstance()
                                    .withdrawDelegateReward(credentials, claimRewardInfo.getFeeAmount(), AmountUtil.convertVonToLat(delegateInfo.getWithdrawReward()), gasProvider)
                                    .compose(RxUtils.bindToLifecycle(currentActivity()))
                                    .compose(RxUtils.getSchedulerTransformer())
                                    .compose(RxUtils.getLoadingTransformer(currentActivity()))
                                    .subscribe(new CustomObserver<Transaction>() {
                                        @Override
                                        public void accept(Transaction transaction) {
                                            if (isViewAttached()) {
                                                getView().notifyItemChanged(true, position);
                                            }
                                        }

                                        @Override
                                        public void accept(Throwable throwable) {
                                            super.accept(throwable);
                                            if (throwable instanceof CustomThrowable && ((CustomThrowable) throwable).getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                                showLongToast(R.string.msg_connect_timeout);
                                            } else {
                                                showLongToast(R.string.claim_reward_failed);
                                            }
                                        }
                                    });

                        }
                    }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
                }

            }
        }).show(currentActivity().getSupportFragmentManager(), "showClaimRewardsDialog");
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    private void showTransactionAuthorizationDialogFragment(String amount, String from, org.web3j.tx.gas.GasProvider gasProvider, int position) {

        Observable
                .fromCallable(new Callable<BigInteger>() {
                    @Override
                    public BigInteger call() throws Exception {
                        return Web3jManager.getInstance().getNonce(from);
                    }
                })
                .compose(RxUtils.getSchedulerTransformer())
                .compose(bindToLifecycle())
                .compose(RxUtils.getLoadingTransformer(currentActivity()))
                .subscribe(new CustomObserver<BigInteger>() {
                    @Override
                    public void accept(BigInteger nonce) {
                        if (isViewAttached()) {
                            TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE)
                                    .setAmount(amount)
                                    .setChainId(NodeManager.getInstance().getChainId())
                                    .setNonce(nonce.toString(10))
                                    .setFrom(from)
                                    .setTo(ContractAddress.REWARD_CONTRACT_ADDRESS)
                                    .setGasLimit(gasProvider.getGasLimit().toString(10))
                                    .setGasPrice(gasProvider.getGasPrice().toString(10))
                                    .build()), System.currentTimeMillis() / 1000);
                            TransactionAuthorizationDialogFragment.newInstance(transactionAuthorizationData)
                                    .setOnNextBtnClickListener(new TransactionAuthorizationDialogFragment.OnNextBtnClickListener() {
                                        @Override
                                        public void onNextBtnClick() {
                                            TransactionSignatureDialogFragment.newInstance(transactionAuthorizationData)
                                                    .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                                        @Override
                                                        public void onSendTransactionSucceed(Transaction transaction) {
                                                            if (isViewAttached()) {
                                                                getView().notifyItemChanged(true, position);
                                                            }
                                                        }
                                                    })
                                                    .show(currentActivity().getSupportFragmentManager(), TransactionSignatureDialogFragment.TAG);
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "showTransactionAuthorizationDialog");
                        }
                    }

                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (isViewAttached()) {
                            if (!TextUtils.isEmpty(throwable.getMessage())) {
                                ToastUtil.showLongToast(currentActivity(), throwable.getMessage());
                            }
                        }
                    }
                });
    }


}

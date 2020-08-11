package com.platon.aton.component.ui.presenter;

import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.MyDelegateContract;
import com.platon.aton.component.ui.dialog.ClaimRewardsDialogFragment;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionAuthorizationDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.db.entity.TransactionEntity;
import com.platon.aton.db.sqlite.TransactionDao;
import com.platon.aton.engine.ContractAddressManager;
import com.platon.aton.engine.DelegateManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.Optional;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.ClaimRewardInfo;
import com.platon.aton.entity.DelegateInfo;
import com.platon.aton.entity.EstimateGasResult;
import com.platon.aton.entity.InputWalletPasswordFromType;
import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionAuthorizationBaseData;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.web3j.crypto.Credentials;
import org.web3j.platon.FunctionType;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

public class MyDelegatePresenter extends BasePresenter<MyDelegateContract.View> implements MyDelegateContract.Presenter {

    private Disposable mDisposable;

    @Override
    public void loadMyDelegateData() {

        //WalletManager.getInstance().getAddressList()
        mDisposable = ServerUtils.getCommonApi().getMyDelegateList(ApiRequestBody.newBuilder().
                put("walletAddrs", WalletManager.getInstance().getAddressListFromDB())
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
                        TransactionEntity transactionEntity = TransactionDao.getTransaction(delegateInfo.getWalletAddress(), String.valueOf(TransactionType.CLAIM_REWARDS.getTxTypeValue()), TransactionStatus.PENDING.ordinal());
                        if (transactionEntity != null) {
                            TransactionStatus transactionStatus = TransactionStatus.getTransactionStatusByIndex(transactionEntity.getTxReceiptStatus());
                            if (transactionStatus == TransactionStatus.PENDING) {
                                delegateInfo.setPending(true);
                                TransactionManager.getInstance().getTransactionByLoop(transactionEntity.toTransaction());
                            } else {
                                delegateInfo.setPending(false);
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

        ServerUtils.getCommonApi().estimateGas(ApiRequestBody.newBuilder()
                .put("from", delegateInfo.getWalletAddress())
                .put("txType", FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE)
                .build())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<EstimateGasResult>() {
                    @Override
                    public void onApiSuccess(EstimateGasResult estimateGasResult) {
                        if (isViewAttached()) {
                            showClaimRewardsDialogFragment(delegateInfo, estimateGasResult, position);
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

    public void showClaimRewardsDialogFragment(DelegateInfo delegateInfo, EstimateGasResult estimateGasResult,int position) {

        org.web3j.tx.gas.GasProvider gasProvider = estimateGasResult.getGasProvider().toSdkGasProvider();
        String nonce = estimateGasResult.getNonce();
        ClaimRewardInfo claimRewardInfo = new ClaimRewardInfo.Builder()
                .setFeeAmount(BigDecimalUtil.mul(gasProvider.getGasPrice().toString(10), gasProvider.getGasLimit().toString(10)).toPlainString())
                .setClaimRewardAmount(delegateInfo.getWithdrawReward())
                .setFromWalletName(delegateInfo.getWalletName())
                .setFromWalletAddress(delegateInfo.getWalletAddress())
                //.setAvaliableBalanceAmount(WalletManager.getInstance().getWalletByAddress(delegateInfo.getWalletAddress()).getFreeBalance())
                .setAvaliableBalanceAmount(estimateGasResult.getFree())
                .build();
        ClaimRewardsDialogFragment.newInstance(claimRewardInfo).setOnConfirmBtnClickListener(new ClaimRewardsDialogFragment.OnConfirmBtnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                if (delegateInfo.isObservedWallet()) {
                    showTransactionAuthorizationDialogFragment(delegateInfo.getWithdrawReward(), delegateInfo.getWalletAddress(), gasProvider, position);
                } else {
                    InputWalletPasswordDialogFragment.newInstance(WalletManager.getInstance().getWalletByAddress(delegateInfo.getWalletAddress()), InputWalletPasswordFromType.TRANSACTION).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                        @Override
                        public void onWalletPasswordCorrect(Credentials credentials) {
                            DelegateManager.getInstance()
                                    .withdrawDelegateReward(credentials, claimRewardInfo.getFeeAmount(), AmountUtil.convertVonToLat(delegateInfo.getWithdrawReward()), gasProvider, nonce)
                                    .compose(bindToLifecycle())
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
                                            if (throwable instanceof CustomThrowable) {
                                                CustomThrowable customThrowable = (CustomThrowable) throwable;
                                                if (customThrowable.getErrCode() == RPCErrorCode.CONNECT_TIMEOUT) {
                                                    showLongToast(R.string.msg_connect_timeout);
                                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_KNOWN_TX) {
                                                    showLongToast(R.string.msg_transaction_repeatedly_exception);
                                                } else if (customThrowable.getErrCode() == CustomThrowable.CODE_TX_NONCE_TOO_LOW ||
                                                        customThrowable.getErrCode() == CustomThrowable.CODE_TX_GAS_LOW) {
                                                    showLongToast(string(R.string.msg_transaction_exception, customThrowable.getErrCode()));
                                                } else {
                                                    showLongToast(string(R.string.msg_server_exception, customThrowable.getErrCode()));
                                                }
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

        ServerUtils.getCommonApi().estimateGas(ApiRequestBody.newBuilder()
                .put("from", from)
                .put("txType", FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE)
                .build())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new ApiSingleObserver<EstimateGasResult>() {
                    @Override
                    public void onApiSuccess(EstimateGasResult estimateGasResult) {
                        if (isViewAttached()) {
                            if (isViewAttached()) {
                                String toAddress = ContractAddressManager.getInstance().getPlanContractAddress(ContractAddressManager.REWARD_CONTRACT_ADDRESS);
                                TransactionAuthorizationData transactionAuthorizationData = new TransactionAuthorizationData(Arrays.asList(new TransactionAuthorizationBaseData.Builder(FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE)
                                        .setAmount(amount)
                                        .setChainId(NodeManager.getInstance().getChainId())
                                        .setNonce(estimateGasResult.getNonce())
                                        .setFrom(from)
                                        .setTo(toAddress)
                                        .setGasLimit(gasProvider.getGasLimit().toString(10))
                                        .setGasPrice(gasProvider.getGasPrice().toString(10))
                                        .setRemark("")
                                        .build()), System.currentTimeMillis() / 1000, BuildConfig.QRCODE_VERSION_CODE);
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
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        super.onApiFailure(response);
                        super.onApiFailure(response);
                        if (isViewAttached()) {
                            showLongToast(R.string.msg_connect_timeout);
                        }
                    }
                });
    }


}

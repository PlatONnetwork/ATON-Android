package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.component.ui.popwindow.DelegatePopWindow;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.entity.NodeStates;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class DelegatePresenter extends BasePresenter<DelegateContract.View> implements DelegateContract.Presenter {
    private Wallet mWallet;
    private String mNodeAddress;
    private String mNodeName;
    private String mNodeIcon;
    private int tag;//获取是从哪个页面跳转到委托页

    public DelegatePresenter(DelegateContract.View view) {
        super(view);
        mNodeAddress = view.getNodeAddressFromIntent();
        mNodeName = view.getNodeNameFromIntent();
        mNodeIcon = view.getNodeIconFromIntent();
        tag = view.getJumpTagFromIntent();
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

    //点击选中余额类型
    @Override
    public void getAmountType(DelegatePopWindow view) {
        view.setListener(new DelegatePopWindow.OnPopItemClickListener() {
            @Override
            public void onPopItemClick(View view, int position, DelegateType bean) {
                if (isViewAttached()) {
                    getView().showWalletType(bean);
                }
            }
        });
    }

    @Override
    public void showWalletInfo() {
        getView().showNodeInfo(mNodeAddress, mNodeName, mNodeIcon);
        showSelectedWalletInfo();

    }

    @Override
    public String checkDelegateAmount(String delegateAmount) {
        double amount = NumberParserUtils.parseDouble(delegateAmount);
        //检查委托的数量
        String errMsg = null;
        if (TextUtils.isEmpty(delegateAmount)) {
            errMsg = string(R.string.transfer_amount_cannot_be_empty);
        } else if (amount < 10) {
            //按钮不可点击,并且下方提示
            getView().showTips(true);
            updateDelegateButtonState();
        } else {
            getView().showTips(false);
            updateDelegateButtonState();
        }
        return delegateAmount;

    }

    @Override
    public void updateDelegateButtonState() {
        if (isViewAttached()) {
            String withdrawAmount = getView().getDelegateAmount(); //获取输入的委托数量
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) > 10;
            getView().setDelegateButtonState(isAmountValid);
        }

    }

    private void showSelectedWalletInfo() {
        mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mWallet != null) {
            getView().showSelectedWalletInfo(mWallet);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void submitDelegate(String type) {
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                //输入数量+手续费  (todo 因为手续费还没定)
                return 0.0;
            }
        }).zipWith(ServerUtils.getCommonApi().getNodeStatus(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder().put("nodeId", mNodeAddress).build()),
                new BiFunction<Double, Response<ApiResponse<NodeStates>>, String>() {
                    @Override
                    public String apply(Double amount, Response<ApiResponse<NodeStates>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return "";
                        } else {
                            NodeStates nodeStates = apiResponseResponse.body().getData();
                            int status = nodeStates.getStatus();
                            return amount + "&" + status;
                        }

                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
//                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        if (isViewAttached()) {
                            double amount = NumberParserUtils.parseDouble(result.split("&", 2)[0]);
                            int nodeStatus = NumberParserUtils.parseInt(result.split("&", 2)[1]);
                            double chooseAmount = NumberParserUtils.parseDouble(getView().getChooseBalance()); //选择的余额
                            if (chooseAmount < amount) {
                                //余额不足
                                showLongToast(R.string.insufficient_balance_unable_to_delegate);
                                return;
                            }

                            if (nodeStatus != 1) {
                                //节点已退出或退出中
                                showLongToast(R.string.the_Validator_has_exited_and_cannot_be_delegated);
                                return;
                            }

                            String inputAmount = getView().getDelegateAmount();

                            InputWalletPasswordDialogFragment
                                    .newInstance(mWallet)
                                    .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                                        @Override
                                        public void onWalletPasswordCorrect(Credentials credentials) {
                                            delegate(credentials, inputAmount, mNodeAddress, type);
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");

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
                                showLongToast(R.string.delegate_failed);
                            }
                        }
                    }
                });

    }


    @SuppressLint("CheckResult")
    private void delegate(Credentials credentials, String inputAmount, String address, String type) {

        DelegateManager.getInstance().delegate(credentials, inputAmount, address, type)
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse baseResponse) throws Exception {
                        if (isViewAttached()) {
                            if (baseResponse != null && baseResponse.isStatusOk()) {
                                //委托成功
                                showLongToast(R.string.delegate_success);
                                //发送一个eventbus
                                if (tag == 0) {
                                    EventPublisher.getInstance().sendUpdateDelegateEvent();
                                } else {
                                    EventPublisher.getInstance().sendUpdateValidatorsDetailEvent();
                                }
                                currentActivity().finish();
                            }

                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            showLongToast(R.string.delegate_failed);
                        }
                    }
                });


    }


}

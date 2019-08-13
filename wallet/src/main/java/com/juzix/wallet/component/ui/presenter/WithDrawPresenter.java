package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.WithDrawContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawBalance;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.TransactionCallback;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class WithDrawPresenter extends BasePresenter<WithDrawContract.View> implements WithDrawContract.Presenter {
    private Wallet mWallet;
    private String mNodeAddress;
    private String mNodeName;
    private String mNodeIcon;
    private String mBlockNum;
    private String mWalletAddress;

    Map<String, String> stringMap = new LinkedHashMap<>();

    public WithDrawPresenter(WithDrawContract.View view) {
        super(view);
        mNodeAddress = view.getNodeAddressFromIntent();
        mNodeName = view.getNodeNameFromIntent();
        mNodeIcon = view.getNodeIconFromIntent();
        mBlockNum = view.getBlockNumFromIntent();
        mWalletAddress = view.getWalletAddress();
    }


    @Override
    public void showWalletInfo() {
        getView().showNodeInfo(mNodeAddress, mNodeName, mNodeIcon);
        showSelectedWalletInfo();
    }

//    @Override
//    public void showSelectWalletDialogFragment() {
//
//        SelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", true)
//                .setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(Wallet walletEntity) {
//                        if (isViewAttached()) {
//                            mWallet = walletEntity;
//                            getView().showSelectedWalletInfo(walletEntity);
//                        }
//                    }
//                })
//                .show(currentActivity().getSupportFragmentManager(), "showSelectWalletDialog");
//    }


    private void showSelectedWalletInfo() {
//        mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        mWallet = WalletManager.getInstance().getWalletEntityByWalletAddress(mWalletAddress);//通过钱包地址获取钱包对象
        if (isViewAttached() && mWallet != null) {
            getView().showSelectedWalletInfo(mWallet);
        }
    }


    @Override
    public boolean checkWithDrawAmount(String withdrawAmount) {
        //检查赎回的数量
        double amount = NumberParserUtils.parseDouble(withdrawAmount);
        String errMsg = null;
        if (TextUtils.isEmpty(withdrawAmount)) {
            errMsg = string(R.string.transfer_amount_cannot_be_empty);
        } else if (amount < 10) {
            //按钮不可点击,并且下方提示
            getView().showTips(true);
            updateWithDrawButtonState();
        } else {
            getView().showTips(false);
            updateWithDrawButtonState();
        }

        getView().showAmountError(errMsg);

        return TextUtils.isEmpty(errMsg);


    }

    @Override
    public void getBalanceType(String addr, String stakingBlockNum) {
        ServerUtils.getCommonApi().getWithDrawBalance(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("addr", addr)
                .put("stakingBlockNum", stakingBlockNum)
                .build()).compose(RxUtils.getSingleSchedulerTransformer())
                .compose(bindToLifecycle())
                .subscribe(new ApiSingleObserver<WithDrawBalance>() {
                    @Override
                    public void onApiSuccess(WithDrawBalance withDrawBalance) {

                        if (isViewAttached()) {
                            if (withDrawBalance != null) {
                                double locked = NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(withDrawBalance.getLocked(), "1E18"))); //已锁定
                                double unLocked = NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(withDrawBalance.getUnLocked(), "1E18")));//未锁定
                                double released = NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(withDrawBalance.getReleased(), "1E18")));//已解除
                                double delegated = locked + unLocked; //已委托 = 已锁定+未锁定

                                stringMap.put("tag_delegated", String.valueOf(delegated));
                                stringMap.put("tag_unlocked", String.valueOf(unLocked));
                                stringMap.put("tag_released", String.valueOf(released));
                                getView().showBalanceType(withDrawBalance, stringMap);

                            }
                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void submitWithDraw() {
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
//                return null; //获取输入数量
                return 100.00;
            }
        }).map(new Function<Double, Double>() {
            @Override
            public Double apply(Double aDouble) throws Exception {
                return aDouble;
            }
        }).compose(bindToLifecycle())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double aDouble) throws Exception {
                        //把输入的数量和选择的数量做判断
                        if (isViewAttached()) {
                            double chooseNum = NumberParserUtils.parseDouble(getView().getChooseType());
                            if (chooseNum < aDouble) {
                                showLongToast(R.string.withdraw_operation_tips);
                                return;
                            }

                            String inputAmount = getView().getInputAmount();

                            InputWalletPasswordDialogFragment
                                    .newInstance(mWallet)
                                    .setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
                                        @Override
                                        public void onWalletPasswordCorrect(Credentials credentials) {
                                            withdraw(credentials, mNodeAddress, mBlockNum, inputAmount);
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "inputWalletPasssword");
                        }
                    }
                });

    }


    //操作赎回
    public void withdraw(Credentials credentials, String nodeId, String blockNum, String withdrawAmount) {

        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        String chainId = NodeManager.getInstance().getChainId();
        DelegateContract delegateContract = DelegateContract.load(web3j, credentials, new DefaultGasProvider(), chainId);
        delegateContract.asyncUnDelegate(nodeId, new BigInteger(blockNum), new BigInteger(withdrawAmount), new TransactionCallback<BaseResponse>() {

            @Override
            public void onTransactionStart() {

            }

            @Override
            public void onTransaction(PlatonSendTransaction sendTransaction) {

            }

            @Override
            public void onTransactionSucceed(BaseResponse baseResponse) {
                if (baseResponse != null && baseResponse.isStatusOk()) {
                    //todo 交易成功，关闭当前页面，并返回到交易详情


                }

            }

            @Override
            public void onTransactionFailed(BaseResponse baseResponse) {

            }
        });
    }


    @Override
    public void updateWithDrawButtonState() {
        if (isViewAttached()) {
            String withdrawAmount = getView().getWithDrawAmount();
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) > 10;
            getView().setWithDrawButtonState(isAmountValid);
        }
    }


}

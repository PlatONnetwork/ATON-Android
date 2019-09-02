package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;

public class ValidatorsDetailPresenter extends BasePresenter<ValidatorsDetailContract.View> implements ValidatorsDetailContract.Presenter {
    private String mNodeId;

    public ValidatorsDetailPresenter(ValidatorsDetailContract.View view) {
        super(view);
        mNodeId = getView().getNodeIdFromIntent();
    }


    @Override
    public void loadValidatorsDetailData() {
        getValidatorsDetailData(mNodeId);
    }

    @Override
    public void getWalletBalance() {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        if (walletAddressList.size() == 0) { //当前没有创建钱包,不能进行委托
            getView().showIsCanDelegate(false);
            return;
        } else {
            ServerUtils.getCommonApi().getAccountBalance(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                    .put("addrs", walletAddressList.toArray(new String[walletAddressList.size()]))
                    .build())
                    .compose(RxUtils.bindToLifecycle(getView()))
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                        @Override
                        public void onApiSuccess(List<AccountBalance> accountBalances) {
                            if (isViewAttached()) {
                                for (AccountBalance balance : accountBalances) {
                                    if (!TextUtils.equals(balance.getLock(), "0") || !TextUtils.equals(balance.getFree(), "0")) {
                                        getView().showIsCanDelegate(true);
                                        return;
                                    }
                                }

                                getView().showIsCanDelegate(false);
                            }

                        }

                        @Override
                        public void onApiFailure(ApiResponse response) {

                        }
                    });


        }

    }

    private void getValidatorsDetailData(String nodeId) {
        ServerUtils.getCommonApi().getNodeCandidateDetail(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("nodeId", nodeId)
                .build())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<VerifyNodeDetail>() {
                    @Override
                    public void onApiSuccess(VerifyNodeDetail verifyNodeDetail) {
                        if (isViewAttached()) {
                            if (verifyNodeDetail != null) {
                                getView().showValidatorsDetailData(verifyNodeDetail);
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showValidatorsDetailFailed();
                    }
                });

    }


}

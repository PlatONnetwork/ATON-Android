package com.platon.aton.component.ui.presenter;

import com.platon.aton.component.ui.contract.DelegateRecordContract;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Transaction;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class DelegateRecordPresenter extends BasePresenter<DelegateRecordContract.View> implements DelegateRecordContract.Presenter {

    @Override
    public void loadDelegateRecordData(long beginSequence, String direction, String type) {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        getDelegateRecordData(beginSequence, Constants.VoteConstants.LIST_SIZE, direction, type, walletAddressList.toArray(new String[walletAddressList.size()]));
    }

    private void getDelegateRecordData(long beginSequence, int listSize, String direction, String type, String[] walletAddress) {
        if (isViewAttached()) {
            ServerUtils.getCommonApi().getDelegateRecordList(ApiRequestBody.newBuilder()
                    .put("beginSequence", beginSequence)
                    .put("listSize", listSize)
                    .put("direction", direction)
                    .put("type", type)
                    .put("walletAddrs", walletAddress)
                    .build())
                    .compose(RxUtils.bindToLifecycle(getView()))
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .subscribe(new ApiSingleObserver<List<Transaction>>() {
                        @Override
                        public void onApiSuccess(List<Transaction> recordList) {
                            if (isViewAttached()) {
                                if (recordList != null && recordList.size() > 0) {
                                    getView().showDelegateRecordData(getWalletNameAndIconByaddress(recordList));
                                } else {
                                    getView().showDelegateRecordData(recordList);
                                }
                            }
                        }

                        @Override
                        public void onApiFailure(ApiResponse response) {
                            getView().showDelegateRecordFailed();
                        }
                    });
        }

    }

    //根据钱包地址获取钱包的头像和名称并赋值
    public List<Transaction> getWalletNameAndIconByaddress(List<Transaction> recordList) {
        return Flowable.fromIterable(recordList)
                .map(new Function<Transaction, Transaction>() {
                    @Override
                    public Transaction apply(Transaction transaction) throws Exception {
                        transaction.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(transaction.getFrom()));
                        transaction.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(transaction.getFrom()));
                        return transaction;
                    }
                }).toList().blockingGet();

    }
}

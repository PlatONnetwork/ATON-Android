package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateRecordContract;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class DelegateRecordPresenter extends BasePresenter<DelegateRecordContract.View> implements DelegateRecordContract.Presenter {
    public DelegateRecordPresenter(DelegateRecordContract.View view) {
        super(view);
    }

    @Override
    public void loadDelegateRecordData(long beginSequence, String direction, String type) {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        getDelegateRecordData(beginSequence, Constants.VoteConstants.LIST_SIZE, direction, type, walletAddressList.toArray(new String[walletAddressList.size()]));
    }

    private void getDelegateRecordData(long beginSequence, int listSize, String direction, String type, String[] walletAddress) {
        ServerUtils.getCommonApi().getDelegateRecordList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("beginSequence", beginSequence)
                .put("listSize", listSize)
                .put("direction", direction)
                .put("type", type)
                .put("walletAddrs", walletAddress)
                .build())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<Transaction>>() {
                    @Override
                    public void onApiSuccess(List<Transaction> recordList) {
                        if (isViewAttached()) {
                            if (recordList != null && recordList.size() > 0) {
                                getView().showDelegateRecordData(getWalletNameAndIconByaddress(recordList));
                            } else {
                                getView().showDelegateReCordNoData();
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showDelegateRecordFailed();
                    }
                });

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

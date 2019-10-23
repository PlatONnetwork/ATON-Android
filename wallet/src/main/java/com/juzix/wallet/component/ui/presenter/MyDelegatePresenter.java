package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyDelegateContract;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MyDelegatePresenter extends BasePresenter<MyDelegateContract.View> implements MyDelegateContract.Presenter {
    public MyDelegatePresenter(MyDelegateContract.View view) {
        super(view);
    }


    @Override
    public void loadMyDelegateData() {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        getMyDelegateData(walletAddressList.toArray(new String[walletAddressList.size()]));

    }

    private void getMyDelegateData(String[] addressList) {
        ServerUtils.getCommonApi().getMyDelegateList(ApiRequestBody.newBuilder().
                put("walletAddrs", addressList)
                .build())
                .compose(RxUtils.bindToParentLifecycleUtilEvent(getView(), FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<DelegateInfo>>() {
                    @Override
                    public void onApiSuccess(List<DelegateInfo> infoList) {
                        if (isViewAttached()) {
                            getView().showMyDelegateData(getWalletIconByAddress(infoList));
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showMyDelegateDataFailed();
                        }
                    }
                });

    }


    //根据钱包地址获取钱包的头像和名称并赋值
    public List<DelegateInfo> getWalletIconByAddress(List<DelegateInfo> infoList) {

        if (infoList != null && infoList.size() > 0) {
            return Flowable.fromIterable(infoList)
                    .map(new Function<DelegateInfo, DelegateInfo>() {
                        @Override
                        public DelegateInfo apply(DelegateInfo delegateInfo) throws Exception {
                            delegateInfo.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(delegateInfo.getWalletAddress()));
                            delegateInfo.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(delegateInfo.getWalletAddress()));
                            return delegateInfo;
                        }
                    }).toList().blockingGet();
        }
        return new ArrayList<>();
    }

}

package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.DelegateInfo;

import java.util.List;

public class DelegateDetailContract {
    public interface View extends IView {

        void showWalletInfo(DelegateInfo delegateInfo);

        void showDelegateDetailData(List<DelegateItemInfo> oldDelegateItemInfoList, List<DelegateItemInfo> newDelegateItemInfoList);

        void showWalletDelegatedInfo(String availableDelegationBalance, String totalDelegatedAmount);

        void showIsCanDelegate(String nodeAddress, String nodeName, String nodeIcon, String walletAddress, boolean isCanDelegate);

        DelegateInfo getDelegateInfoFromIntent();
    }

    public interface Presenter extends IPresenter<View> {

        void loadDelegateDetailData();

    }
}

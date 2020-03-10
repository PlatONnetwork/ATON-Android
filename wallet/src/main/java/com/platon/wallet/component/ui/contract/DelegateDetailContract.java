package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;
import com.platon.wallet.entity.DelegateItemInfo;
import com.platon.wallet.entity.DelegateInfo;

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

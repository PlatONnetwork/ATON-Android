package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateDetail;

import java.util.List;

public class DelegateDetailContract {
    public interface View extends IView {

        String getWalletNameFromIntent();

        String getWalletAddressFromIntent();

        String getWalletIconFromIntent();

        void showWalletInfo(String walletAddress, String walletName, String walletIcon);


        void showDelegateDetailData(List<DelegateDetail> detailList);

        void showDelegateDetailFailed();

    }

    public interface Presenter extends IPresenter<View> {

        void loadDelegateDetailData();

        void MoveOut(DelegateDetail detail);

    }
}

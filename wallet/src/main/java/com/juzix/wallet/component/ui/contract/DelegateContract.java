package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.component.ui.popwindow.DelegatePopWindow;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.entity.Wallet;

public class DelegateContract {

    public interface View extends IView {

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setDelegateButtonState(boolean isClickable);

        String getDelegateAmount();

        void showAmountError(String errMsg);

        void showTips(boolean isShowTips);

        void showWalletType(DelegateType delegateType);

        String getNodeAddressFromIntent();

        String getNodeNameFromIntent();

        String getNodeIconFromIntent();

        int getJumpTagFromIntent();

        void showNodeInfo(String nodeAddress, String nodeName, String nodeIcon);

        String getChooseBalance();
    }

    public interface Presenter extends IPresenter<View> {
        void showSelectWalletDialogFragment();

        void showWalletInfo();

        void updateDelegateButtonState();

        String checkDelegateAmount(String delegateAmount);


        void getAmountType(DelegatePopWindow view);

        void submitDelegate(String type);

    }
}

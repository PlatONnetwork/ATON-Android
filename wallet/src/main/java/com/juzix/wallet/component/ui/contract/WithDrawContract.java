package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawBalance;

import java.util.Map;

public class WithDrawContract {
    public interface View extends IView {

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setWithDrawButtonState(boolean isClickable);

        String getWithDrawAmount();

        void showAmountError(String errMsg);

        void showTips(boolean isShow);

        String getNodeAddressFromIntent();

        String getNodeNameFromIntent();

        String getNodeIconFromIntent();

        String getBlockNumFromIntent();

        String getWalletAddress();

        void showNodeInfo(String nodeAddress, String nodeName, String nodeIcon);

        void showBalanceType(WithDrawBalance drawBalance, Map<String, String> map);

        String getInputAmount();

        String getChooseType();


    }

    public interface Presenter extends IPresenter<View> {

        //选择钱包
//        void showSelectWalletDialogFragment();

        void showWalletInfo();


        void updateWithDrawButtonState();

        boolean checkWithDrawAmount(String withdrawAmount);

        void getBalanceType(String addr, String stakingBlockNum);


        void submitWithDraw();

    }

}

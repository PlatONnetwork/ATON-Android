package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;

import java.math.BigInteger;

public class WithDrawContract {
    public interface View extends IView {

        DelegateItemInfo getDelegateDetailFromIntent();

        String getInputAmount();

        String getChooseType();

        String getWithDrawAmount();

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setWithDrawButtonState(boolean isClickable);

        void showTips(boolean isShow,String minDelegationAmount);

        void showNodeInfo(DelegateItemInfo delegateDetail);

        /**
         * @param delegated 已委托
         * @param released  待赎回
         */
        void showBalanceType(double delegated, double released,String minDelegationAmount);

        /**
         * @param transaction
         */
        void withDrawSuccessInfo(Transaction transaction);

        /**
         * 显示手续费
         */

        void showWithDrawGasPrice(String gas);

        void showGas(BigInteger bigInteger);

        void finishDelayed();

    }

    public interface Presenter extends IPresenter<View> {

        void showWalletInfo();

        void updateWithDrawButtonState();

        void checkWithDrawAmount(String withdrawAmount);

        void getBalanceType();

        void submitWithDraw(String chooseType);

        void getWithDrawGasPrice(String gasPrice);

        void getGas();

    }

}

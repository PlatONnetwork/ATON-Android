package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawBalance;

import org.web3j.tx.gas.GasProvider;

import java.math.BigInteger;

public class WithDrawContract {
    public interface View extends IView {

        DelegateItemInfo getDelegateDetailFromIntent();

        String getInputAmount();

        String getWithDrawAmount();

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setWithDrawButtonState(boolean isClickable);

        void showTips(boolean isShow, String minDelegationAmount);

        void showNodeInfo(DelegateItemInfo delegateDetail);

        void showMinDelegationInfo(String minDelegationAmount);

        /**
         * @param transaction
         */
        void withDrawSuccessInfo(Transaction transaction);

        /**
         * 显示手续费
         */

        void showWithDrawGasPrice(String gas);

        void showGas(GasProvider gasProvider);

        void finishDelayed();

        void showWithdrawBalance(WithDrawBalance withDrawBalance);

    }

    public interface Presenter extends IPresenter<View> {

        void showWalletInfo();

        void updateWithDrawButtonState();

        void checkWithDrawAmount(String withdrawAmount);

        void getBalanceType();

        void submitWithDraw();

        void getWithDrawGasPrice(String gasPrice);

        void showSelectDelegationsDialogFragment();

    }

}

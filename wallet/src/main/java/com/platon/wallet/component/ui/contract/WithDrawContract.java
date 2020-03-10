package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;
import com.platon.wallet.entity.DelegateItemInfo;
import com.platon.wallet.entity.Transaction;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.entity.WithDrawBalance;

import org.web3j.tx.gas.GasProvider;

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

        void showsSelectDelegationsBtnVisibility(int visibility);

        void setAllAmountDelegate();

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

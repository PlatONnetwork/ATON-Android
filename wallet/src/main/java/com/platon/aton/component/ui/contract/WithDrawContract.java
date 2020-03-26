package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WithDrawBalance;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import org.web3j.tx.gas.GasProvider;

public class WithDrawContract {

    public interface View extends BaseViewImp {

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

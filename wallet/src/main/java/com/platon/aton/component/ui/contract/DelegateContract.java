package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.DelegateHandle;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;

import org.web3j.platon.StakingAmountType;

public class DelegateContract {

    public interface View extends IContext {

        DelegateItemInfo getDelegateDetailFromIntent();

        String getDelegateAmount();

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setDelegateButtonState(boolean isClickable);

        void showTips(boolean isShowTips, String minDelegation);

        void showNodeInfo(DelegateItemInfo delegateDetail);

        void showIsCanDelegate(DelegateHandle bean);

        /**
         * @param transaction
         */
        void showTransactionSuccessInfo(Transaction transaction);

        /**
         * 显示手续费
         */
        void showFeeAmount(String gas);

        /**
         * 显示全部的手续费
         */
        void showAllFeeAmount(StakingAmountType stakingAmountType, String delegateAmount, String feeAmount);

        /**
         * 获取手续费
         */
        String getFeeAmount();

        /**
         * 清除输入的委托金额
         */
        void clearInputDelegateAmount();
    }

    public interface Presenter extends IPresenter<View> {
        void showSelectWalletDialogFragment();

        void showWalletInfo();

        void updateDelegateButtonState();

        void checkDelegateAmount(String delegateAmount);

        void submitDelegate(StakingAmountType type);

        void checkIsCanDelegate(String walletAddress, String nodeAddress);

        void getGasProvider(StakingAmountType stakingAmountType);

        void getGas();

    }
}

package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;

import org.web3j.platon.StakingAmountType;

import java.math.BigInteger;

public class DelegateContract {

    public interface View extends IView {

        DelegateDetail getDelegateDetailFromIntent();

        String getDelegateAmount();

        String getChooseBalance();

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setDelegateButtonState(boolean isClickable);

        void showTips(boolean isShowTips);

        void showNodeInfo(DelegateDetail delegateDetail);


        void showIsCanDelegate(DelegateHandle bean);

        /**
         * @param transaction
         */
        void transactionSuccessInfo(Transaction transaction);


        /**
         * 显示手续费
         */
        void showGasPrice(String gas);


        void showGas(BigInteger integer);

        /**
         * 显示全部的手续费
         */
        void showAllGasPrice(StakingAmountType stakingAmountType,String allPrice);

        /**
         * 获取手续费
         */
        String getFeeAmount();
    }

    public interface Presenter extends IPresenter<View> {
        void showSelectWalletDialogFragment();

        void showWalletInfo();

        void updateDelegateButtonState();

        void checkDelegateAmount(String delegateAmount);

        void submitDelegate(StakingAmountType type);

        void checkIsCanDelegate(String walletAddress,String nodeAddress);

        void getGasPrice(StakingAmountType stakingAmountType);

        void getGas();

    }
}

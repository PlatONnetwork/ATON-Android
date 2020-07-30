package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.EstimateGasResult;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import org.web3j.platon.StakingAmountType;

import java.util.ArrayList;
import java.util.List;

public class DelegateContract {

    public interface View extends IContext, BaseViewImp {

        DelegateItemInfo getDelegateDetailFromIntent();

        String getDelegateAmount();

        void showSelectedWalletInfo(Wallet wallet);

        void setDelegateButtonState(boolean isClickable);

        void showTips(boolean isShowTips, String minDelegation);

        void showNodeInfo(DelegateItemInfo delegateDetail);

        void showIsCanDelegate(EstimateGasResult estimateGasResult);

        void showDelegateException(int errorCode);

        void showDelegateResult(String minDelegation);

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

        void notifyDataSetChanged();
    }

    public interface Presenter extends IPresenter<View> {

        void init(DelegateItemInfo delegateItemInfo);

        void showSelectWalletDialogFragment();

        void showWalletInfo();

        void updateDelegateButtonState();

        void checkDelegateAmount(String delegateAmount);

        void submitDelegate(StakingAmountType type);

        void getGasProvider(StakingAmountType stakingAmountType);

        //Sidebar
        void loadData(@WalletTypeSearch int walletTypeSearch, String keywords);

        ArrayList<Wallet> getDataSource();

        void updateSelectedWalletnotifyData(Wallet selectedWallet);

    }
}

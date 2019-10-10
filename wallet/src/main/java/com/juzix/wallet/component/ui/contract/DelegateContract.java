package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.Wallet;

import org.web3j.platon.StakingAmountType;

import java.math.BigInteger;
import java.util.List;

public class DelegateContract {

    public interface View extends IView {

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setDelegateButtonState(boolean isClickable);

        String getDelegateAmount();

        String getChooseBalance();

        void showAmountError(String errMsg);

        void showTips(boolean isShowTips);

        String getNodeAddressFromIntent();

        String getNodeNameFromIntent();

        String getNodeIconFromIntent();

        String getWalletAddressFromIntent();

        int getJumpTagFromIntent();

        void showNodeInfo(String nodeAddress, String nodeName, String nodeIcon);

        void getWalletBalanceList(List<AccountBalance> accountBalances);

        void showIsCanDelegate(DelegateHandle bean);

        /**
         * @param from            发起的钱包地址
         * @param to              接收的钱包地址
         * @param txType          交易类型
         * @param value           交易数量
         * @param actualTxCost    交易手续费
         * @param nodeName
         * @param nodeId
         * @param txReceiptStatus "2" 表示确认中
         */
        void transactionSuccessInfo(String hash, String from, String to,  String txType, String value, String actualTxCost, String nodeName, String nodeId, int txReceiptStatus);


        /**
         * 显示手续费
         */
        void showGasPrice(String gas);

        /**
         * 拿到手续费
         *
         * @return
         */
        String getGas();


        void showGas(BigInteger integer);

        /**
         * 显示全部的手续费
         */
        void showAllGasPrice(String allPrice);

        /**
         * 获取手续费
         */
        String getGasPrice();
    }

    public interface Presenter extends IPresenter<View> {
        void showSelectWalletDialogFragment();

        void showWalletInfo();

        void updateDelegateButtonState();

        String checkDelegateAmount(String delegateAmount);

        void submitDelegate(StakingAmountType type);

        void checkIsCanDelegate();


        void getGasPrice(StakingAmountType stakingAmountType);

        void getGas();

    }
}

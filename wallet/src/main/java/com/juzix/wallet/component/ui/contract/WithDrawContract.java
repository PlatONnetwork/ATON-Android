package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawBalance;

import java.math.BigInteger;
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

        String getWalletAddressFromIntent();

        void showNodeInfo(String nodeAddress, String nodeName, String nodeIcon);

        //        void showBalanceType(WithDrawBalance drawBalance, Map<String, String> map);
        void showBalanceType(double delegated, double unlocked, double released);


        String getInputAmount();

        String getChooseType();


        /**
         * @param from            发起的钱包地址
         * @param to              接收的钱包地址
         * @param time            交易时间
         * @param txType          交易类型
         * @param value           交易数量
         * @param actualTxCost    交易手续费
         * @param nodeName
         * @param nodeId
         * @param txReceiptStatus "2" 表示确认中
         */
        void withDrawSuccessInfo(String hash, String from, String to, long time, String txType, String value, String actualTxCost, String nodeName, String nodeId, int txReceiptStatus);


        /**
         * 显示手续费
         */

        void showWithDrawGasPrice(String gas);

        //拿到手续费

        String getGas();


        void showGas(BigInteger bigInteger);

    }

    public interface Presenter extends IPresenter<View> {

        //选择钱包
//        void showSelectWalletDialogFragment();

        void showWalletInfo();


        void updateWithDrawButtonState();

        boolean checkWithDrawAmount(String withdrawAmount);

        void getBalanceType();


        void submitWithDraw(String chooseType);


        void getWithDrawGasPrice(String gasPrice);

        void getGas();

    }

}

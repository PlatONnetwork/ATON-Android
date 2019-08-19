package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.Wallet;

import java.util.List;

public class DelegateContract {

    public interface View extends IView {

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setDelegateButtonState(boolean isClickable);

        String getDelegateAmount();

        void showAmountError(String errMsg);

        void showTips(boolean isShowTips);

        String getNodeAddressFromIntent();

        String getNodeNameFromIntent();

        String getNodeIconFromIntent();

        int getJumpTagFromIntent();

        void showNodeInfo(String nodeAddress, String nodeName, String nodeIcon);

        String getChooseBalance();

        void getWalletBalanceList(List<AccountBalance> accountBalances);

        void showIsCanDelegate(DelegateHandle bean);

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
        void transactionSuccessInfo(String from, String to, long time, String txType, String value, String actualTxCost, String nodeName, String nodeId, int txReceiptStatus);
    }

    public interface Presenter extends IPresenter<View> {
        void showSelectWalletDialogFragment();

        void showWalletInfo();

        void updateDelegateButtonState();

        String checkDelegateAmount(String delegateAmount);


        void submitDelegate(String type);

        void checkIsCanDelegate();
    }
}

package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawBalance;

import java.math.BigInteger;
import java.util.Map;

public class WithDrawContract {
    public interface View extends IView {

        DelegateDetail getDelegateDetailFromIntent();

        String getInputAmount();

        String getChooseType();

        String getWithDrawAmount();

        void showSelectedWalletInfo(Wallet individualWalletEntity);

        void setWithDrawButtonState(boolean isClickable);

        void showTips(boolean isShow);

        void showNodeInfo(DelegateDetail delegateDetail);

        /**
         * @param delegated 已委托
         * @param released  待赎回
         */
        void showBalanceType(double delegated,double released);

        /**
         * @param transaction
         */
        void withDrawSuccessInfo(Transaction transaction);

        /**
         * 显示手续费
         */

        void showWithDrawGasPrice(String gas);

        void showGas(BigInteger bigInteger);

    }

    public interface Presenter extends IPresenter<View> {


        void showWalletInfo();

        void updateWithDrawButtonState();

        void  checkWithDrawAmount(String withdrawAmount);

        void getBalanceType();

        void submitWithDraw(String chooseType);

        void getWithDrawGasPrice(String gasPrice);

        void getGas();

    }

}

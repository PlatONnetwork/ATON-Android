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

        void showAmountError(String errMsg);

        void showTips(boolean isShow);

        void showNodeInfo(DelegateDetail delegateDetail);

        void showBalanceType(double delegated, double unlocked, double released);

        /**
         * @param transaction
         */
        void withDrawSuccessInfo(Transaction transaction);

        /**
         * 显示手续费
         */

        void showWithDrawGasPrice(String gas);

        //拿到手续费

        String getGas();


        void showGas(BigInteger bigInteger);

    }

    public interface Presenter extends IPresenter<View> {


        void showWalletInfo();

        void updateWithDrawButtonState();

        boolean checkWithDrawAmount(String withdrawAmount);

        void getBalanceType();

        void submitWithDraw(String chooseType);

        void getWithDrawGasPrice(String gasPrice);

        void getGas();

    }

}

package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Transaction;

import org.web3j.platon.BaseResponse;

/**
 * @author matrixelement
 */
public class IndividualTransactionDetailContract {

    public interface View extends IView {

        Transaction getTransactionFromIntent();

        String getAddressFromIntent();

        String getDelegateHash();

        String getWithDrawHash();

        void setTransactionDetailInfo(Transaction transaction, String queryAddress, String senderWalletName);

        void showDelegateResponse(BaseResponse response);

        void showWithDrawResponse(BaseResponse response);

    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void updateTransactionDetailInfo(Transaction transaction);

        void getDelegateResult();

        void getWithDrawResult();

    }
}

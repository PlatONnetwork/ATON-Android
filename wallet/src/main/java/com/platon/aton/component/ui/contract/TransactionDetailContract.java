package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.Transaction;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionDetailContract {

    public interface View extends BaseViewImp {

        Transaction getTransactionFromIntent();

        List<String> getAddressListFromIntent();

        void setTransactionDetailInfo(Transaction transaction, List<String> queryAddressList, String senderWalletName);

    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        void updateTransactionDetailInfo(Transaction transaction);

    }
}

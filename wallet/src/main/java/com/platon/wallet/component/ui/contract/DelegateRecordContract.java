package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;
import com.platon.wallet.entity.Transaction;

import java.util.List;

public class DelegateRecordContract {

    public interface View extends IView {
        void showDelegateRecordData(List<Transaction> recordList);

        void showDelegateRecordFailed();

    }

    public interface Presenter extends IPresenter<View> {

        void loadDelegateRecordData(long beginSequence, String direction, String type);

    }

}

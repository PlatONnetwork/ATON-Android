package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateRecord;
import com.juzix.wallet.entity.Transaction;

import java.util.List;

public class DelegateRecordContract {

    public interface View extends IView {
        void showDelegateRecordData(List<Transaction> recordList);

        void showDelegateReCordNoData();

        void showDelegateRecordFailed();

    }

    public interface Presenter extends IPresenter<View> {

        void loadDelegateRecordData(long beginSequence, String direction, String type);

    }

}

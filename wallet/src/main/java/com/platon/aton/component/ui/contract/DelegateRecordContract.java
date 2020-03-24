package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.entity.Transaction;

import java.util.List;

public class DelegateRecordContract {

    public interface View extends IContext {
        void showDelegateRecordData(List<Transaction> recordList);

        void showDelegateRecordFailed();

    }

    public interface Presenter extends IPresenter<View> {

        void loadDelegateRecordData(long beginSequence, String direction, String type);

    }

}

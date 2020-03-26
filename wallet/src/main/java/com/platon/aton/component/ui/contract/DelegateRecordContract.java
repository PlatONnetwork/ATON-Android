package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Transaction;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.List;

public class DelegateRecordContract {

    public interface View extends BaseViewImp {
        void showDelegateRecordData(List<Transaction> recordList);

        void showDelegateRecordFailed();

    }

    public interface Presenter extends IPresenter<View> {

        void loadDelegateRecordData(long beginSequence, String direction, String type);

    }

}

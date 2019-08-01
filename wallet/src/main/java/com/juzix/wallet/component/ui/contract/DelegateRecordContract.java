package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateRecord;

import java.util.List;

public class DelegateRecordContract {

    public interface View extends IView {
        void showDelegateRecordData(List<DelegateRecord> recordList);

        void showDelegateReCordNoData();

        void showDelegateRecordFailed();

    }

    public interface Presentet extends IPresenter<View> {

        void loadDelegateRecordData(int beginSequence, String direction, String type);

    }

}

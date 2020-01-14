package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.VerifyNodeDetail;

public class ValidatorsDetailContract {
    public interface View extends IView {

        String getNodeIdFromIntent();

        void showValidatorsDetailData(VerifyNodeDetail nodeDetail);

        void showIsCanDelegate(boolean isCanDelegate);
    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsDetailData();

        DelegateItemInfo getDelegateDetail();

    }

}

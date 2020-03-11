package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.VerifyNodeDetail;

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

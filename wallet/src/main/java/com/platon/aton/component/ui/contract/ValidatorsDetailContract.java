package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.VerifyNodeDetail;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

public class ValidatorsDetailContract {

    public interface View extends BaseViewImp {

        String getNodeIdFromIntent();

        void showValidatorsDetailData(VerifyNodeDetail nodeDetail);

        void showIsCanDelegate(boolean isCanDelegate);
    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsDetailData();

        DelegateItemInfo getDelegateDetail();

    }

}

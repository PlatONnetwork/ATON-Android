package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.VerifyNodeDetail;

public class ValidatorsDetailContract {
    public interface View extends IView {

        String getNodeIdFromIntent();

        void showValidatorsDetailData(VerifyNodeDetail nodeDetail);

        void showValidatorsDetailFailed();


    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsDetailData();

    }

}

package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.VerifyNode;

import java.util.List;

public class ValidatorsContract {
    public interface View extends IView {
        void showValidatorsDataOnAll(List<VerifyNode> nodeList);

        void showValidatorsDataOnActive(List<VerifyNode> nodeList);

        void showValidatorsDataOnCadidate(List<VerifyNode> nodeList);

        void showValidatorsFailed();


    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsData(String sortType, String nodeState, int ranking);

        void loadDataFromDB(String state, int ranking);

    }

}

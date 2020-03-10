package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.SortType;
import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;
import com.platon.wallet.entity.NodeStatus;
import com.platon.wallet.entity.VerifyNode;

import java.util.List;

public class ValidatorsContract {
    public interface View extends IView {

        void loadValidatorsDataResult(List<VerifyNode> oldVerifyNodeList, List<VerifyNode> newVerifyNodeList, boolean isRefreshAll);
    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsData(@NodeStatus String nodeStatus, SortType sortType, String keywords, boolean isRefresh, boolean isRefreshAll);

    }

}

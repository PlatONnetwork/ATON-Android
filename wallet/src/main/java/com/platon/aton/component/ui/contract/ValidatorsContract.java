package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.SortType;
import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.NodeStatus;
import com.platon.aton.entity.VerifyNode;

import java.util.List;

public class ValidatorsContract {
    public interface View extends IView {

        void loadValidatorsDataResult(List<VerifyNode> oldVerifyNodeList, List<VerifyNode> newVerifyNodeList, boolean isRefreshAll);
    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsData(@NodeStatus String nodeStatus, SortType sortType, String keywords, boolean isRefresh, boolean isRefreshAll);

    }

}

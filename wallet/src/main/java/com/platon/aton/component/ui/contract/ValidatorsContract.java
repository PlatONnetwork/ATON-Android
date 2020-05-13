package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.component.ui.SortType;
import com.platon.aton.entity.NodeStatus;
import com.platon.aton.entity.VerifyNode;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.List;

public class ValidatorsContract {

    public interface View extends BaseViewImp {

        void loadValidatorsDataResult(List<VerifyNode> oldVerifyNodeList, List<VerifyNode> newVerifyNodeList, boolean isRefreshAll);

        void finishRefresh();

    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsData(@NodeStatus String nodeStatus, SortType sortType, String keywords, boolean isRefresh, boolean isRefreshAll);

    }

}

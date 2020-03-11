package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.NodeStatus;
import com.juzix.wallet.entity.VerifyNode;

import java.util.List;

public class ValidatorsContract {
    public interface View extends IView {

        void loadValidatorsDataResult(List<VerifyNode> oldVerifyNodeList, List<VerifyNode> newVerifyNodeList, boolean isRefreshAll);

        void finishRefresh();

    }

    public interface Presenter extends IPresenter<View> {

        void loadValidatorsData(@NodeStatus String nodeStatus, SortType sortType, String keywords, boolean isRefresh, boolean isRefreshAll);

    }

}

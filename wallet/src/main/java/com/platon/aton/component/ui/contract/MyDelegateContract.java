package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.DelegateInfo;

import java.util.List;

public class MyDelegateContract {

    public interface View extends IContext {

        void showMyDelegateData(List<DelegateInfo> list);

        void notifyItemChanged(boolean isPending, int position);
    }

    public interface Presenter extends IPresenter<View> {

        //加载数据
        void loadMyDelegateData();

        /**
         * 领取奖励
         */
        void withdrawDelegateReward(DelegateInfo delegateInfo, int position);

    }


}

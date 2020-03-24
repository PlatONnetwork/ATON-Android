package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

/**
 * @author matrixelement
 */
public class MainContract {

    public interface View extends BaseViewImp {

        void exitApp();
    }

    public interface Presenter extends IPresenter<View> {
        void checkVersion();
    }
}

package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;

/**
 * @author matrixelement
 */
public class MainContract {

    public interface View extends IView {

        void exitApp();
    }

    public interface Presenter extends IPresenter<View> {
        void checkVersion();
    }
}

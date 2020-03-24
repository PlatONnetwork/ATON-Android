package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;

/**
 * @author matrixelement
 */
public class MainContract {

    public interface View extends IContext {

        void exitApp();
    }

    public interface Presenter extends IPresenter<View> {
        void checkVersion();
    }
}

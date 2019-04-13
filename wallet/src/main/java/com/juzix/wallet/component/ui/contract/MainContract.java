package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;

/**
 * @author matrixelement
 */
public class MainContract {

    public interface View extends IView {
    }

    public interface Presenter extends IPresenter<View> {
        void checkVersion();
    }
}

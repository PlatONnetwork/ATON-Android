package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;

public class ImportWalletContract {

    public interface View extends IView {
    }

    public interface Presenter extends IPresenter<View> {
    }
}

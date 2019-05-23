package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.Candidate;

/**
 * @author matrixelement
 */
public class NodeInformationContract {

    public interface View extends IView {
        Candidate getCandidateEntityFromIntent();
        void showDetailInfo(Candidate candidateEntity);
        void showEpoch(long epoch);
    }

    public interface Presenter extends IPresenter<View> {
        void start();
    }
}

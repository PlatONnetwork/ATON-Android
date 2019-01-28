package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.CandidateEntity;

/**
 * @author matrixelement
 */
public class NodeInformationContract {

    public interface View extends IView {
        CandidateEntity getCandidateEntityFromIntent();
        void showDetailInfo(CandidateEntity candidateEntity);
        void showEpoch(long epoch);
    }

    public interface Presenter extends IPresenter<View> {
        void start();
    }
}

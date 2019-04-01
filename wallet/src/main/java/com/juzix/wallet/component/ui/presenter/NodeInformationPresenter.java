package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.NodeInformationContract;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.CandidateEntity;

/**
 * @author matrixelement
 */
public class NodeInformationPresenter extends BasePresenter<NodeInformationContract.View> implements NodeInformationContract.Presenter {

    public NodeInformationPresenter(NodeInformationContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        CandidateEntity candidateEntity = getView().getCandidateEntityFromIntent();
        if (candidateEntity != null){
            getView().showDetailInfo(candidateEntity);
            new Thread(){
                @Override
                public void run() {
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_UPDATE_EP;
                    msg.obj = VoteManager.getInstance().getCandidateEpoch(candidateEntity.getCandidateId());
                    mHandler.sendMessage(msg);
                }
            }.start();
        }
    }

    private static final int MSG_UPDATE_EP = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_EP:
                    if (isViewAttached()){
                        getView().showEpoch((Long)msg.obj);
                    }
                    break;
            }
        }
    };
}

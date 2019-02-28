package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualTransactionDetailContract;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;


/**
 * @author matrixelement
 */
public class IndividualTransactionDetailPresenter extends BasePresenter<IndividualTransactionDetailContract.View> implements IndividualTransactionDetailContract.Presenter {

    private IndividualTransactionEntity mTransactionEntity;
    private String mQueryAddress;

    public IndividualTransactionDetailPresenter(IndividualTransactionDetailContract.View view) {
        super(view);
        mTransactionEntity = view.getTransactionFromIntent();
        mQueryAddress = view.getAddressFromIntent();
    }

    @Override
    public void fetchTransactionDetail() {
        if (isViewAttached() && mTransactionEntity != null) {
            new Thread() {
                @Override
                public void run() {
                    IndividualTransactionEntity transactionEntity = IndividualWalletTransactionManager.getInstance().getTransactionByHash(mTransactionEntity.getHash(), mTransactionEntity.getCreateTime(), mTransactionEntity.getWalletName(), mTransactionEntity.getMemo());
                    transactionEntity.setLatestBlockNumber(Web3jManager.getInstance().getLatestBlockNumber());
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_UPDATE_TRANSACTIONS;
                    msg.obj = transactionEntity;
                    mHandler.sendMessage(msg);
                }
            }.start();
        }
    }

    private static final int MSG_UPDATE_TRANSACTIONS = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TRANSACTIONS:
                    if (isViewAttached() && msg.obj != null) {
                        getView().setTransactionDetailInfo((IndividualTransactionEntity) msg.obj, mQueryAddress);
                    }
                    break;
            }
        }
    };

}

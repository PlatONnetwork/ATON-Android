package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionRecordsPresenter extends BasePresenter<TransactionRecordsContract.View> implements TransactionRecordsContract.Presenter {

    private ArrayList<TransactionEntity> transactionEntityList = new ArrayList<>();
    public TransactionRecordsPresenter(TransactionRecordsContract.View view) {
        super(view);
    }

    @Override
    public void fetchTransactions() {
        if (isViewAttached()) {
            showLoadingDialog();
        }
        startFetch();
    }

    @Override
    public void refreshRecords() {
        startFetch();
    }

    private void startFetch(){
        new Thread(){
            @Override
            public void run() {
                if (!transactionEntityList.isEmpty()){
                    transactionEntityList.clear();
                }
                long lastestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
                List<IndividualTransactionInfoEntity> individualTransactionList = IndividualTransactionInfoDao.getInstance().getTransactionList();
                for (IndividualTransactionInfoEntity entity : individualTransactionList) {
                    IndividualTransactionEntity transactionEntity = IndividualWalletTransactionManager.getInstance().getTransactionByHash(entity.getHash(),
                            entity.getCreateTime(), entity.getWalletName(), entity.getMemo());
                    if (transactionEntity != null) {
                        transactionEntity.setLatestBlockNumber(lastestBlockNumber);
                        transactionEntityList.add(transactionEntity);
                    }
                }
                transactionEntityList.addAll(SharedWalletTransactionManager.getInstance().getAllTransactionList());
                Collections.sort(transactionEntityList);
                mHandler.sendEmptyMessage(MSG_UPDATE_TRANSACTIONS);
            }
        }.start();
    }

    private static final int MSG_UPDATE_TRANSACTIONS = 100;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_TRANSACTIONS:
                    if (isViewAttached()) {
                        getView().showTransactions(transactionEntityList);
                        dismissLoadingDialogImmediately();
                    }
                    break;
            }
        }
    };

}

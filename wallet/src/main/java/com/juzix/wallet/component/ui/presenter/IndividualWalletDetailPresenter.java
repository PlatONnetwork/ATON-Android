package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualWalletDetailContract;
import com.juzix.wallet.component.ui.view.IndividualReceiveTransationActivity;
import com.juzix.wallet.component.ui.view.IndividualTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.SharedTransactionDetailActivity;
import com.juzix.wallet.component.ui.view.SigningActivity;
import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualWalletDetailPresenter extends BasePresenter<IndividualWalletDetailContract.View> implements IndividualWalletDetailContract.Presenter {

    private IndividualWalletEntity mWalletEntity;
    private SharedWalletEntity     mSharedWalletEntity;
    public IndividualWalletDetailPresenter(IndividualWalletDetailContract.View view) {
        super(view);
        mWalletEntity = view.getWalletEntityFromIntent();
    }

    @Override
    public void fetchWalletDetail() {
        if (!isViewAttached() || mWalletEntity == null) {
            return;
        }
        getView().updateWalletInfo(mWalletEntity);
        new Thread(){
            @Override
            public void run() {
                //1、获取普通钱包所有交易
                ArrayList<TransactionEntity>          transactionEntities     = new ArrayList<>();
                long lastestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
                List<IndividualTransactionInfoEntity> transactionInfoEntities = IndividualTransactionInfoDao.getInstance().getTransactionList(mWalletEntity.getPrefixAddress());
                for (IndividualTransactionInfoEntity transactionInfoEntity : transactionInfoEntities){
                    IndividualTransactionEntity transactionEntity = IndividualWalletTransactionManager.getInstance().getTransactionByHash(transactionInfoEntity.getHash(), transactionInfoEntity.getCreateTime(), transactionInfoEntity.getWalletName(), transactionInfoEntity.getMemo());
                    if (transactionEntity != null){
                        transactionEntity.setLatestBlockNumber(lastestBlockNumber);
                        transactionEntities.add(transactionEntity);
                    }
                }
                //2、获取普通钱包所关联的所有交易
                mSharedWalletEntity = SharedWalletManager.getInstance().getWalletByWalletAddress(mWalletEntity.getPrefixAddress());
                if (mSharedWalletEntity != null){
                    List<SharedTransactionEntity> transactionEntityList = SharedWalletTransactionManager.getInstance().getTransactionListByContractAddress(mSharedWalletEntity.getPrefixContractAddress());
                    if (transactionEntityList != null && !transactionEntityList.isEmpty()){
                        for (SharedTransactionEntity entity : transactionEntityList){
                            transactionEntities.add(entity);
                        }
                    }
                }
                Collections.sort(transactionEntities);
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_UPDATE_TRANSACTIONS;
                msg.obj = transactionEntities;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    @Override
    public void enterTransactionDetailActivity(TransactionEntity transactionEntity) {
        if (!isViewAttached() || mWalletEntity == null) {
            return;
        }
        if (transactionEntity instanceof  IndividualTransactionEntity){
            IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) transactionEntity, mWalletEntity.getPrefixAddress());
        }else if (transactionEntity instanceof  SharedTransactionEntity){
            SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
            if (!sharedTransactionEntity.isRead()){
                sharedTransactionEntity.setRead(true);
                SharedWalletTransactionManager.getInstance().updateTransactionForRead(mSharedWalletEntity, sharedTransactionEntity);
            }
            BaseActivity activity = currentActivity();
            if (sharedTransactionEntity.transfered()){
                SharedTransactionDetailActivity.actionStart(activity, sharedTransactionEntity);
            }else {
                SigningActivity.actionStart(activity, sharedTransactionEntity);
            }
        }

    }

    @Override
    public void enterReceiveTransactionActivity() {
        if (isViewAttached()) {
            IndividualReceiveTransationActivity.actionStart(currentActivity(), mWalletEntity);
        }
    }

    private static final int     MSG_UPDATE_TRANSACTIONS = 100;
    private              Handler mHandler                = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_TRANSACTIONS:
                    if (isViewAttached()) {
                        getView().notifyTransactionListChanged((ArrayList<TransactionEntity>) msg.obj, mWalletEntity.getPrefixAddress());
                        dismissLoadingDialogImmediately();
                    }
                    break;
            }
        }
    };
}

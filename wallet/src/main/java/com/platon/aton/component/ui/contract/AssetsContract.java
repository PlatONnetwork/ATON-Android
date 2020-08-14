package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Transaction;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

import java.util.List;

/**
 * @author ziv
 * date On 2020-03-25
 */
public class AssetsContract {

    public interface View extends BaseViewImp {

        void showTotalBalance(String totalBalance);

        void showFreeBalance(String freeBalance);

        void showLockBalance(String lockBalance);

        void finishRefresh();

        /**
         * 更新交易数据
         *
         * @param oldTransactionList
         * @param newTransactionList
         * @param queryAddress
         * @param loadLatestData
         */
        void notifyTransactionSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, String queryAddress, boolean loadLatestData);

    }

    public interface Presenter extends IPresenter<View> {

        /**
         * 获取钱包余额
         */
        void fetchWalletBalance();

        /**
         * 获取钱包余额(选中钱包)
         */
        void fetchWalletBalanbceBySelected(String address);

        /**
         * 获取数据
         */
        void loadData();

        /**
         * 获取最新的数据
         */
        void loadNewData(String direction);

        /**
         * 发送交易完之后
         *
         * @param transaction
         */
        void afterSendTransactionSucceed(Transaction transaction);
    }
}

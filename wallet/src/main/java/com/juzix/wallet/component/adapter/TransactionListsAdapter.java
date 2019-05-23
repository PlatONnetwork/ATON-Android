package com.juzix.wallet.component.adapter;

import android.content.Context;

import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;

import java.util.Collections;
import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionListsAdapter extends CommonAdapter<Transaction> {

    private final static String TAG = TransactionListsAdapter.class.getSimpleName();

    private Context mContext;

    public TransactionListsAdapter(int layoutId, List<Transaction> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Transaction item, int position) {
        this.mContext = context;
        convert(context, viewHolder, item);
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        super.updateItemView(context, position, viewHolder);
        convert(context, viewHolder, mDatas.get(position));
    }

    private void convert(Context context, ViewHolder viewHolder, Transaction item) {
//        String walletAddress = WalletManager.getInstance().getSelectedWalletAddress();
//        if (item instanceof IndividualTransactionEntity) {
//            IndividualTransactionEntity entity = (IndividualTransactionEntity) item;
//            IndividualTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
//            boolean isReceiver = entity.isReceiver(walletAddress);
//            viewHolder.setVisible(R.id.v_new_msg, false);
//            viewHolder.setText(R.id.tv_transaction_status, entity.isReceiver(walletAddress) ? context.getString(R.string.receive) : context.getString(R.string.send));
//            viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
//            viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
//            viewHolder.setText(R.id.tv_transaction_status_desc, entity.isCompleted() ? context.getString(R.string.success) : context.getString(R.string.pending));
//            viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
//            viewHolder.setImageResource(R.id.iv_transaction_status, entity.isReceiver(walletAddress) ? R.drawable.icon_receive_transaction : R.drawable.icon_send_transation);
//        } else if (item instanceof VoteTransaction) {
//            VoteTransaction entity = (VoteTransaction) item;
//            VoteTransaction.TransactionStatus transactionStatus = entity.getTransactionStatus();
//            boolean isReceiver = entity.isReceiver(walletAddress);
//            viewHolder.setVisible(R.id.v_new_msg, false);
//            viewHolder.setText(R.id.tv_transaction_status, context.getString(R.string.vote));
//            viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
//            viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
//            viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, 12, 12));
//            viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
//            viewHolder.setImageResource(R.id.iv_transaction_status, R.drawable.icon_valid_ticket);
//        }

    }

    /**
     * 如果当前是合约地址，则不需要将创建合约和部署合约的加到当前列表中
     *
     * @param transactionEntity
     */
    public void addItem(Transaction transactionEntity) {
        if (mDatas != null && isNeedUpdateItem(transactionEntity)) {
            mDatas.add(transactionEntity);
            Collections.sort(mDatas);
            notifyDataChanged(mDatas);
        }
    }

    /**
     * 是否需要更新item
     * 1.普通钱包
     * 2.联名钱包交易，如果是非交易类型，则只显示在联名钱包创建者交易列表里
     *
     * @param transactionEntity
     * @return
     */
    private boolean isNeedUpdateItem(Transaction transactionEntity) {
        String walletAddress = WalletManager.getInstance().getSelectedWalletAddress();
        //地址是否与交易有关
//        if (!transactionEntity.isRelevantWalletAddress(walletAddress)) {
//            return false;
//        }
//        if (transactionEntity instanceof SharedTransactionEntity) {
//            SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
//            SharedTransactionEntity.TransactionType transactionType = SharedTransactionEntity.TransactionType.getTransactionType(sharedTransactionEntity.getTransactionType());
//            //创建联名钱包+执行联名钱包只在创建者交易记录里展示
//            return transactionType == SharedTransactionEntity.TransactionType.SEND_TRANSACTION || sharedTransactionEntity.getFromAddress().equals(walletAddress);
//        }
//        if (transactionEntity instanceof VoteTransaction) {
//            return ((VoteTransaction) transactionEntity).isVoter(walletAddress);
//        }
        return true;
    }


    public void notifyDataChanged(List<Transaction> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

}

package com.juzix.wallet.component.adapter;

import android.content.Context;

import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.Transaction;

import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualTransactionListAdapter extends CommonAdapter<Transaction> {

    private final static String TAG = IndividualTransactionListAdapter.class.getSimpleName();

    private String walletAddress;

    public IndividualTransactionListAdapter(int layoutId, List<Transaction> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Transaction item, int position) {
        if (item != null) {
//            if (item instanceof  IndividualTransactionEntity){
//                IndividualTransactionEntity entity = (IndividualTransactionEntity) item;
//                IndividualTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
//                boolean                                       isReceiver        = entity.isReceiver(walletAddress);
//                viewHolder.setText(R.id.tv_transaction_status, entity.isReceiver(walletAddress) ? context.getString(R.string.receive) : context.getString(R.string.send));
//                viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
//                viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
//                viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, entity.getSignedBlockNumber(), 1));
//                viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
//                viewHolder.setImageResource(R.id.iv_transaction_status, entity.isReceiver(walletAddress) ? R.drawable.icon_receive_transaction : R.drawable.icon_send_transation);
//            }else if (item instanceof VoteTransaction){
//                VoteTransaction entity = (VoteTransaction) item;
//                VoteTransaction.TransactionStatus transactionStatus = entity.getTransactionStatus();
//                boolean                                       isReceiver        = entity.isReceiver(walletAddress);
//                viewHolder.setText(R.id.tv_transaction_status, context.getString(R.string.vote));
//                viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
//                viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
//                viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context,12, 12));
//                viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
//                viewHolder.setImageResource(R.id.iv_transaction_status, R.drawable.icon_valid_ticket);
//            }
        }

    }


    public void notifyDataChanged(List<Transaction> mDatas, String walletAddress) {
        this.mDatas = mDatas;
        this.walletAddress = walletAddress;
        notifyDataSetChanged();
    }
}

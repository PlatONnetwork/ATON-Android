package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

public class TransactionAdapter extends RecyclerAdapter<Transaction> {

    public TransactionAdapter(Context mContext, List<Transaction> mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }

    @Override
    public void convert(RecycleHolder holder, Transaction data, int position) {
        TransactionStatus status = data.getTxReceiptStatus();
        holder.setText(R.id.tv_name, data.getTxType().getTxTypeDesc());
        holder.setText(R.id.tv_amount, mContext.getString(R.string.amount_with_unit, data.getShowValue()));
        holder.setText(R.id.tv_time, data.getShowCreateTime());
        holder.setText(R.id.tv_desc, mContext.getString(status.getTransactionStatusDescRes()));
        holder.setTextColor(R.id.tv_desc, status.getTransactionStatusDescColorRes());
    }

    @Override
    public void updateItem(RecycleHolder holder, Transaction data, int position) {
        TransactionStatus status = data.getTxReceiptStatus();
        holder.setText(R.id.tv_desc, mContext.getString(status.getTransactionStatusDescRes()));
        holder.setTextColor(R.id.tv_desc, status.getTransactionStatusDescColorRes());
    }

    public void notifyItemRangeInserted(List<Transaction> transactionList, int positionStart, int itemCount) {
        this.mDatas = transactionList;
        notifyItemRangeInserted(positionStart, itemCount);
        notifyItemRangeChanged(positionStart, itemCount);
    }

    public void notifyItemChanged(List<Transaction> transactionList, int position){
        this.mDatas = transactionList;
        notifyItemChanged(position,"notifyItem");
    }

    public void notifyItemInserted(List<Transaction> transactionList, int positionStart){
        this.mDatas = transactionList;
        notifyItemInserted(positionStart);
        notifyItemChanged(positionStart);
    }
}

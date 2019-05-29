package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

public class TransactionAdapter extends RecyclerAdapter<Transaction> {
    //查询交易的钱包，可能是当前选中的钱包(钱包页查看)，可能是所有钱包(交易记录页面)
    private List<String> mQueryAddressList;

    public TransactionAdapter(Context mContext, List<Transaction> mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }

    @Override
    public void convert(RecycleHolder holder, Transaction data, int position) {
        TransactionStatus status = data.getTxReceiptStatus();
        //默认是发送，当发送和接收的钱包
        boolean isSender = mQueryAddressList != null && mQueryAddressList.contains(data.getFrom());
        int transferDescRes = isSender ? R.string.send : R.string.receive;
        holder.setText(R.id.tv_name, data.getTxType() == TransactionType.TRANSFER ? transferDescRes : data.getTxType().getTxTypeDescRes());
        holder.setText(R.id.tv_amount, String.format("%s%s", isSender ? "-" : "+", mContext.getString(R.string.amount_with_unit, data.getShowValue())));
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

    public void notifyItemRangeInserted(List<Transaction> transactionList, List<String> queryAddressList, int positionStart, int itemCount) {
        this.mDatas = transactionList;
        this.mQueryAddressList = queryAddressList;
        notifyItemRangeInserted(positionStart, itemCount);
        notifyItemRangeChanged(positionStart, itemCount);
    }

    public void notifyItemChanged(List<Transaction> transactionList, List<String> queryAddressList, int position) {
        this.mDatas = transactionList;
        this.mQueryAddressList = queryAddressList;
        notifyItemChanged(position, "notifyItem");
    }

    public void notifyDataSetChanged(List<Transaction> transactionList, List<String> queryAddressList) {
        this.mDatas = transactionList;
        this.mQueryAddressList = queryAddressList;
        notifyDataSetChanged();
    }

    public void notifyItemInserted(List<Transaction> transactionList, int positionStart) {
        this.mDatas = transactionList;
        notifyItemInserted(positionStart);
        notifyItemChanged(positionStart);
    }
}

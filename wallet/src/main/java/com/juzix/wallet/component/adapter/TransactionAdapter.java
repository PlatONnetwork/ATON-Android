package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.component.ui.contract.VoteTransactionDetailContract;
import com.juzix.wallet.component.ui.view.TransactionRecordsActivity;
import com.juzix.wallet.component.widget.PendingAnimationLayout;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;

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
        int transferDescRes = data.isSender() ? R.string.send : R.string.receive;
        holder.setText(R.id.tv_transaction_status, data.getTxType() == TransactionType.TRANSFER ? transferDescRes : data.getTxType().getTxTypeDescRes());
        holder.setText(R.id.tv_transaction_amount, String.format("%s%s", data.isSender() ? "-" : "+", data.getShowValue()));
        holder.setTextColor(R.id.tv_transaction_amount, data.isSender() ? R.color.color_ff3b3b : R.color.color_19a20e);
        holder.setText(R.id.tv_transaction_time, data.getShowCreateTime());
        PendingAnimationLayout pendingAnimationLayout = holder.itemView.findViewById(R.id.layout_pending);
        ImageView transactionStatusIv = holder.itemView.findViewById(R.id.iv_transaction_status);
        transactionStatusIv.setVisibility(status == TransactionStatus.PENDING || mContext instanceof TransactionRecordsActivity ? View.GONE : View.VISIBLE);
        pendingAnimationLayout.setVisibility(status != TransactionStatus.PENDING || mContext instanceof TransactionRecordsActivity ? View.GONE : View.VISIBLE);

        if (data.getTxType() == TransactionType.TRANSFER) {
            transactionStatusIv.setImageResource(data.isSender() ? R.drawable.icon_send_transation : R.drawable.icon_receive_transaction);
        } else {
            transactionStatusIv.setImageResource(data.isSender() ? R.drawable.icon_delegate : R.drawable.icon_undelegate);
        }
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

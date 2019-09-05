package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.ui.view.TransactionRecordsActivity;
import com.juzix.wallet.component.widget.PendingAnimationLayout;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;

import java.util.List;

public class TransactionViewHolder extends BaseViewHolder<Transaction> {

    private LinearLayout mTransactionStatusLayout;
    private PendingAnimationLayout mPendingLayout;
    private ImageView mTransactionStatusIv;
    private TextView mTransactionAmountTv;
    private TextView mTransactionStatusTv;
    private TextView mTransactionTimeTv;

    private List<String> mQueryAddressList;

    public TransactionViewHolder(int viewId, ViewGroup parent, List<String> queryAddressList) {
        super(viewId, parent);

        mQueryAddressList = queryAddressList;

        mTransactionStatusLayout = itemView.findViewById(R.id.layout_transaction_status);
        mPendingLayout = itemView.findViewById(R.id.layout_pending);
        mTransactionStatusIv = itemView.findViewById(R.id.iv_transaction_status);
        mTransactionAmountTv = itemView.findViewById(R.id.tv_transaction_amount);
        mTransactionStatusTv = itemView.findViewById(R.id.tv_transaction_status);
        mTransactionTimeTv = itemView.findViewById(R.id.tv_transaction_time);
    }

    public void setQueryAddressList(List<String> queryAddressList) {
        this.mQueryAddressList = queryAddressList;
    }

    @Override
    public void refreshData(Transaction transaction, int position) {
        super.refreshData(transaction, position);
        if (transaction == null) {
            return;
        }

        TransactionStatus transactionStatus = transaction.getTxReceiptStatus();
        TransactionType transactionType = transaction.getTxType();
        boolean isSender = transaction.isSender(mQueryAddressList);
        //默认是发送，当发送和接收的钱包
        int transferDescRes = isSender ? R.string.send : R.string.receive;

        mTransactionStatusTv.setText(transactionType == TransactionType.TRANSFER ? transferDescRes : transactionType.getTxTypeDescRes());
        mTransactionAmountTv.setText(String.format("%s%s", isSender ? "-" : "+", transaction.getShowValue()));
        mTransactionAmountTv.setTextColor(isSender ? ContextCompat.getColor(mContext, R.color.color_ff3b3b) : ContextCompat.getColor(mContext, R.color.color_19a20e));
        mTransactionTimeTv.setText(transaction.getShowCreateTime());
        mPendingLayout.setVisibility(transactionStatus != TransactionStatus.PENDING || mContext instanceof TransactionRecordsActivity ? View.GONE : View.VISIBLE);
        mTransactionStatusIv.setVisibility(transactionStatus == TransactionStatus.PENDING || mContext instanceof TransactionRecordsActivity ? View.GONE : View.VISIBLE);

        if (transactionType == TransactionType.TRANSFER) {
            mTransactionStatusIv.setImageResource(isSender ? R.drawable.icon_send_transation : R.drawable.icon_receive_transaction);
        } else {
            mTransactionStatusIv.setImageResource(isSender ? R.drawable.icon_delegate : R.drawable.icon_undelegate);
        }
    }

    @Override
    public void updateItem(Bundle bundle) {
        if (bundle.isEmpty()) {
            return;
        }

        for (String key : bundle.keySet()) {
            switch (key) {
                case TransactionDiffCallback.KEY_TRANSACTION_STATUS:
                    TransactionStatus transactionStatus = bundle.getParcelable(key);
                    mPendingLayout.setVisibility(transactionStatus != TransactionStatus.PENDING || mContext instanceof TransactionRecordsActivity ? View.GONE : View.VISIBLE);
                    mTransactionStatusIv.setVisibility(transactionStatus == TransactionStatus.PENDING || mContext instanceof TransactionRecordsActivity ? View.GONE : View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }
}

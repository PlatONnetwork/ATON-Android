package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.ui.view.TransactionDetailActivity;
import com.juzix.wallet.component.ui.view.TransactionRecordsActivity;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;

import java.util.IllegalFormatCodePointException;
import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int COMMON_ITEM_VIEW = 0;
    private final static int FOOTER_ITEM_VIEW = 1;

    private Context mContext;
    private List<Transaction> mTransactionList;
    private List<String> mQueryAddressList;
    private boolean mTransactionRecordPage;

    public TransactionListAdapter(Context context) {
        this.mContext = context;
        this.mTransactionRecordPage = context instanceof TransactionRecordsActivity;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
    }

    public void setQueryAddressList(List<String> queryAddressList) {
        this.mQueryAddressList = queryAddressList;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == COMMON_ITEM_VIEW) {
            return new TransactionViewHolder(R.layout.item_transaction_list, parent, mQueryAddressList);
        } else {
            return new TransactionFooterViewHolder(R.layout.item_transaction_list_footer, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (getItemViewType(position) == COMMON_ITEM_VIEW) {
            ((TransactionViewHolder) holder).setQueryAddressList(mQueryAddressList);
            holder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener() {
                @Override
                public void onItemClick(Object o) {
                    if (mTransactionList != null && !mTransactionList.isEmpty()) {
                        TransactionDetailActivity.actionStart(mContext, mTransactionList.get(position), mQueryAddressList);
                    }
                }
            });
            holder.refreshData(mTransactionList.get(position), position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (holder instanceof TransactionViewHolder) {
            ((TransactionViewHolder) holder).setQueryAddressList(mQueryAddressList);
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                holder.updateItem((Bundle) payloads.get(0));
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (mTransactionList != null) {
            size = mTransactionList.size();
        }
        return mTransactionRecordPage ? size : (size >= 20 ? size + 1 : size);
    }

    @Override
    public int getItemViewType(int position) {
        int size = getItemCount();
        if (!mTransactionRecordPage) {
            if (size > 20) {
                if (position == size - 1) {
                    return FOOTER_ITEM_VIEW;
                }
            }
        }
        return COMMON_ITEM_VIEW;
    }

    public void notifyDataSetChanged(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
        notifyDataSetChanged();
    }

    static class TransactionFooterViewHolder extends BaseViewHolder<String> {

        private TextView mMoreTransactionTv;

        public TransactionFooterViewHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);

            mMoreTransactionTv = itemView.findViewById(R.id.tv_more_transaction);

            mMoreTransactionTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionRecordsActivity.actionStart(mContext);
                }
            });
        }
    }
}

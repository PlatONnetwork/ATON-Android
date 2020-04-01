package com.platon.aton.component.adapter;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.platon.aton.R;
import com.platon.aton.component.adapter.base.BaseViewHolder;
import com.platon.aton.entity.Transaction;

import java.util.List;

/**
 * @author ziv
 */
public class TransactionListAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    @IntDef({
            ItemViewType.COMMON_ITEM_VIEW,
            ItemViewType.FOOTER_ITEM_VIEW
    })
    public @interface ItemViewType {

        int COMMON_ITEM_VIEW = 0;

        int FOOTER_ITEM_VIEW = 1;
    }

    @IntDef({
            EntranceType.MAIN_PAGE,
            EntranceType.ME_PAGE
    })
    public @interface EntranceType {
        /**
         * 首页
         */
        int MAIN_PAGE = 0;
        /**
         * 我的页面
         */
        int ME_PAGE = 1;
    }

    private final static int MAX_ITEM_COUNT = 20;

    private List<Transaction> mTransactionList;

    private List<String> mQueryAddressList;

    private @EntranceType
    int mEntranceType;

    private OnItemClickListener mItemClickListener;

    public TransactionListAdapter(@EntranceType int entranceType) {
        this.mEntranceType = entranceType;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
    }

    public void setQueryAddressList(List<String> queryAddressList) {
        this.mQueryAddressList = queryAddressList;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemViewType.COMMON_ITEM_VIEW) {
            return new TransactionViewHolder(R.layout.item_transaction_list, parent, mQueryAddressList);
        } else {
            return new TransactionFooterViewHolder(R.layout.item_transaction_list_footer, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (getItemViewType(position) == ItemViewType.COMMON_ITEM_VIEW) {
            ((TransactionViewHolder) holder).setQueryAddressList(mQueryAddressList);
            holder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener() {
                @Override
                public void onItemClick(Object o) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onCommonTransactionItemClick(mTransactionList.get(holder.getAdapterPosition()), position);
                    }
//                    TransactionDetailActivity.actionStart(mContext, mTransactionList.get(holder.getAdapterPosition()), mQueryAddressList);
                }
            });
            holder.refreshData(mTransactionList.get(position), position);
        } else {
            holder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener() {
                @Override
                public void onItemClick(Object o) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onMoreTransactionItemClick();
                    }
//                    TransactionRecordsActivity.actionStart(mContext, WalletManager.getInstance().getSelectedWallet());
                }
            });
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
        return mEntranceType == EntranceType.ME_PAGE ? size : (size >= MAX_ITEM_COUNT ? size + 1 : size);
    }

    @Override
    public int getItemViewType(int position) {
        int size = getItemCount();
        if (mEntranceType != EntranceType.ME_PAGE) {
            if (size > MAX_ITEM_COUNT) {
                if (position == size - 1) {
                    return ItemViewType.FOOTER_ITEM_VIEW;
                }
            }
        }
        return ItemViewType.COMMON_ITEM_VIEW;
    }

    public void notifyDataSetChanged(List<Transaction> transactionList) {
        this.mTransactionList = transactionList;
        notifyDataSetChanged();
    }

    static class TransactionFooterViewHolder extends BaseViewHolder<String> {

        public TransactionFooterViewHolder(int viewId, ViewGroup parent) {
            super(viewId, parent);
        }
    }

    public interface OnItemClickListener {

        /**
         * 正常交易被点击，进入交易详情页面
         *
         * @param transaction
         * @param position
         */
        void onCommonTransactionItemClick(Transaction transaction, int position);

        /**
         * 加载更多，进入交易记录页面
         */
        void onMoreTransactionItemClick();
    }
}

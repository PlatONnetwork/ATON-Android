package com.platon.aton.component.adapter;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.platon.aton.R;
import com.platon.aton.component.adapter.base.BaseViewHolder;
import com.platon.aton.entity.Wallet;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;

/**
 * @author ziv
 * date On 2020-03-25
 */
public class AssetsWalletListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    @IntDef({
            ItemViewType.COMMON_WALLET,
            ItemViewType.CREATE_WALLET,
            ItemViewType.IMPORT_WALLET
    })
    public @interface ItemViewType {
        /**
         * 常规钱包
         */
        int COMMON_WALLET = 0;
        /**
         * 创建钱包
         */
        int CREATE_WALLET = 1;
        /**
         * 导入钱包
         */
        int IMPORT_WALLET = 2;
    }

    private List<Wallet> mWalletList;

    private OnItemClickListener mItemClickListener;

    public AssetsWalletListAdapter(List<Wallet> walletList) {
        this.mWalletList = walletList;
    }

    public void setDatas(List<Wallet> walletList) {
        this.mWalletList = walletList;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void notifyDataSetChanged(List<Wallet> walletList) {
        this.mWalletList = walletList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemViewType) {
        if (itemViewType == ItemViewType.COMMON_WALLET) {
            return new WalletListViewHolder(R.layout.item_assets_wallet_list, viewGroup);
        } else if (itemViewType == ItemViewType.CREATE_WALLET) {
            return new FooterViewHolder(R.layout.item_assets_create_wallet, viewGroup, itemViewType);
        } else {
            return new FooterViewHolder(R.layout.item_assets_import_wallet, viewGroup, itemViewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mWalletList.size()) {
            return ItemViewType.COMMON_WALLET;
        } else if (position == mWalletList.size()) {
            return ItemViewType.CREATE_WALLET;
        } else if (position == mWalletList.size() + 1) {
            return ItemViewType.IMPORT_WALLET;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {

        @ItemViewType int itemViewType = getItemViewType(position);


        if (itemViewType == ItemViewType.COMMON_WALLET) {

            WalletListViewHolder listViewHolder = (WalletListViewHolder) baseViewHolder;

            Wallet wallet = mWalletList.get(position);

            listViewHolder.refreshData(wallet, position);

            listViewHolder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener<Wallet>() {
                @Override
                public void onItemClick(Wallet wallet) {

                    if (wallet.isSelected()) {
                        return;
                    }
                    //当前选中的置为false
                    notifyItemSelected(getSelectedPosition(), false);
                    notifyItemSelected(position, true);

                    if (mItemClickListener != null) {
                        mItemClickListener.onCommonWalletItemClick(wallet, position);
                    }
                }
            });
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) baseViewHolder;
            footerViewHolder.refreshData(null, position);
            footerViewHolder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener() {
                @Override
                public void onItemClick(Object o) {
                    if (mItemClickListener != null) {
                        if (itemViewType == ItemViewType.CREATE_WALLET) {
                            mItemClickListener.onCreateWalletItemClick();
                        } else {
                            mItemClickListener.onImportWalletItemClick();
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.updateItem((Bundle) payloads.get(0));
        }
    }


    @Override
    public int getItemCount() {
        int originalSize = mWalletList != null ? mWalletList.size() : 0;
        return originalSize + 2;
    }

    public void notifyItemSelected(int position, boolean selected) {

        if (position < 0 || position > getItemCount()) {
            return;
        }

        Wallet wallet = mWalletList.get(position);
        wallet.setSelected(selected);

        Bundle bundle = new Bundle();
        bundle.putBoolean(WalletListDiffCallback.KEY_WALLET_SELECTED, selected);

        notifyItemChanged(position, bundle);
    }

    public void notifyItemName(int position, String name) {

        if (position > getItemCount()) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(WalletListDiffCallback.KEY_WALLET_NAME, name);

        notifyItemChanged(position, bundle);
    }

    public List<Wallet> getDatas() {
        return mWalletList;
    }

    private int getSelectedPosition() {

        int size = getItemCount();

        if (size == 0) {
            return -1;
        }

        return Flowable.range(0, size)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer position) throws Exception {
                        return mWalletList.get(position).isSelected();
                    }
                })
                .firstElement()
                .defaultIfEmpty(-1)
                .onErrorReturnItem(-1)
                .blockingGet();
    }

    public static class FooterViewHolder extends BaseViewHolder<Integer> {

        private @ItemViewType
        int itemViewType;

        public FooterViewHolder(int viewId, ViewGroup parent, int itemViewType) {
            super(viewId, parent);
            this.itemViewType = itemViewType;
        }

        public int getViewType() {
            return itemViewType;
        }
    }

    public interface OnItemClickListener {
        /**
         * 正常钱包被点击
         *
         * @param wallet
         * @param position
         */
        void onCommonWalletItemClick(Wallet wallet, int position);

        /**
         * 创建钱包被点击
         */
        void onCreateWalletItemClick();

        /**
         * 导入钱包被点击
         */
        void onImportWalletItemClick();
    }
}

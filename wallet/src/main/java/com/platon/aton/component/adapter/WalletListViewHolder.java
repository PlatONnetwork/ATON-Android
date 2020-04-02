package com.platon.aton.component.adapter;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.base.BaseViewHolder;
import com.platon.aton.entity.Wallet;

/**
 * @author ziv
 * date On 2020-03-25
 */
public class WalletListViewHolder extends BaseViewHolder<Wallet> {

    private LinearLayout mWalletLayout;
    private TextView mWalletNameTv;
    private ImageView mWalletTagIv;

    public WalletListViewHolder(int viewId, ViewGroup parent) {
        super(viewId, parent);

        mWalletLayout = itemView.findViewById(R.id.layout_wallet);
        mWalletNameTv = itemView.findViewById(R.id.tv_wallet_name);
        mWalletTagIv = itemView.findViewById(R.id.iv_wallet_tag);
    }

    @Override
    public void refreshData(Wallet wallet, int position) {
        super.refreshData(wallet, position);

        setWalletSelected(wallet.isSelected());
        mWalletNameTv.setText(wallet.getName());
    }

    @Override
    public void updateItem(Bundle bundle) {
        super.updateItem(bundle);
        if (!bundle.isEmpty()) {
            for (String key : bundle.keySet()) {
                switch (key) {
                    case WalletListDiffCallback.KEY_WALLET_NAME:
                        mWalletNameTv.setText(bundle.getString(key));
                        break;
                    case WalletListDiffCallback.KEY_WALLET_SELECTED:
                        setWalletSelected(bundle.getBoolean(key));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setWalletSelected(boolean selected) {
        mWalletLayout.setSelected(selected);
        mWalletTagIv.setSelected(selected);
        mWalletNameTv.setSelected(selected);
    }
}

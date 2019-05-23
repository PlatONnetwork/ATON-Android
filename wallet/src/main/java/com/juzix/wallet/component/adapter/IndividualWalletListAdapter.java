package com.juzix.wallet.component.adapter;

import android.content.Context;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.Wallet;

import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualWalletListAdapter extends CommonAdapter<Wallet> {

    public IndividualWalletListAdapter(int layoutId, List<Wallet> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Wallet item, int position) {
        if (item != null) {
            viewHolder.setText(R.id.tv_wallet_name, item.getName());
            viewHolder.setImageResource(R.id.iv_wallet_avatar, RUtils.drawable(item.getAvatar()));
            viewHolder.setVisible(R.id.progress_bar, false);
        }
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        convert(context, viewHolder, mDatas.get(position), position);
    }
}

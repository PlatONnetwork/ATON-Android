package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.widget.ListView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class SelectSharedWalletListAdapter extends CommonAdapter<SharedWalletEntity> {

    private ListView listView;

    public SelectSharedWalletListAdapter(int layoutId, List<SharedWalletEntity> datas, ListView listView) {
        super(layoutId, datas);
        this.listView = listView;
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, SharedWalletEntity item, int position) {
        if (item != null) {
            viewHolder.getConvertView().setEnabled(item.getBalance() > 0);
            viewHolder.setText(R.id.tv_wallet_name, item.getName());
            viewHolder.setText(R.id.tv_wallet_balance, String.format("%s%2s", "Balance", context.getString(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(item.getBalance(), 4))));
            viewHolder.setImageResource(R.id.iv_wallet_pic, RUtils.drawable(item.getAvatar()));
            viewHolder.setVisible(R.id.iv_selected, listView != null && listView.getCheckedItemPosition() == position);
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return getList().get(position).getBalance() > 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}

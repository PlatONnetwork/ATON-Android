package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.widget.ListView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class SelectWalletListAdapter extends CommonAdapter<IndividualWalletEntity> {

    private ListView listView;
    private String action;

    public SelectWalletListAdapter(int layoutId, List<IndividualWalletEntity> datas, ListView listView, String action) {
        super(layoutId, datas);
        this.listView = listView;
        this.action = action;
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, IndividualWalletEntity item, int position) {
        if (item != null) {
            viewHolder.getConvertView().setEnabled(isEnabled(item));
            viewHolder.setText(R.id.tv_wallet_name, item.getName());
            viewHolder.setText(R.id.tv_wallet_balance, String.format("%s %s", "Balance", context.getString(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(item.getBalance(), 8))));
            viewHolder.setImageResource(R.id.iv_wallet_pic, RUtils.drawable(item.getAvatar()));
            viewHolder.setVisible(R.id.iv_selected, listView != null && listView.getCheckedItemPosition() == position);
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return isEnabled(getList().get(position));
    }

    private boolean isEnabled(IndividualWalletEntity item) {
        return !((SelectWalletDialogFragment.CREATE_SHARED_WALLET.equals(action) || SelectWalletDialogFragment.SELECT_TRANSACTION_WALLET.equals(action)) && item.getBalance() == 0);
    }
}

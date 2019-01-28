package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class SharedWalletListAdapter extends CommonAdapter<SharedWalletEntity> {

    public SharedWalletListAdapter(int layoutId, List<SharedWalletEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, SharedWalletEntity item, int position) {
        if (item != null) {
            viewHolder.getView(R.id.v_new_msg).setVisibility(item.getUnread() > 0 ? View.VISIBLE : View.GONE);
            viewHolder.setText(R.id.tv_total_balance, context.getString(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(item.getBalance())));
            viewHolder.setText(R.id.tv_wallet_name, item.getName());
            viewHolder.setImageResource(R.id.iv_wallet_avatar, RUtils.drawable(item.getAvatar()));
            ProgressBar progressBar = viewHolder.getView(R.id.progress_bar);
            progressBar.setProgress(item.getProgress());
            progressBar.setVisibility(item.isFinished() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        convert(context, viewHolder, mDatas.get(position), position);
    }
}

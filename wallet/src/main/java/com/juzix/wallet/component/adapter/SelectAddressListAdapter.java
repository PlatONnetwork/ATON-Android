package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.view.View;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.AddressEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.List;

/**
 * @author matrixelement
 */
public class SelectAddressListAdapter extends CommonAdapter<AddressEntity> {

    public SelectAddressListAdapter(int layoutId, List<AddressEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, AddressEntity item, int position) {
        if (item != null) {
            viewHolder.setText(R.id.tv_wallet_name, item.getName());
            viewHolder.setText(R.id.tv_wallet_address, AddressFormatUtil.formatAddress(item.getAddress()));
            viewHolder.setVisible(R.id.iv_wallet_selected, false);
            int avatar = RUtils.drawable(item.getAvatar());
            if (avatar != -1) {
                viewHolder.setImageResource(R.id.iv_wallet_avatar, avatar);
            }
        }
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        if (mDatas != null && mDatas.size() > position) {
            convert(context, viewHolder, mDatas.get(position), position);
        }
    }
}

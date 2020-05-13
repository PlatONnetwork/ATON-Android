package com.platon.aton.component.adapter;

import android.content.Context;

import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.component.adapter.base.ViewHolder;
import com.platon.aton.entity.Address;
import com.platon.aton.utils.AddressFormatUtil;

import java.util.List;

/**
 * @author matrixelement
 */
public class AddressBookListAdapter extends CommonAdapter<Address> {

    public AddressBookListAdapter(int layoutId, List<Address> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Address item, int position) {
        if (item != null) {
            viewHolder.setText(R.id.tv_wallet_name, item.getName());
            viewHolder.setText(R.id.tv_wallet_address, AddressFormatUtil.formatAddress(item.getAddress()));
            int resId = RUtils.drawable(item.getAvatar());
            if (resId < 0){
                resId = R.drawable.avatar_15;
            }
            viewHolder.setImageResource(R.id.iv_wallet_avatar, resId);
        }
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        if (mDatas != null && mDatas.size() > position) {
            convert(context, viewHolder, mDatas.get(position), position);
        }
    }
}

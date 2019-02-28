package com.juzix.wallet.component;

import android.content.Context;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.List;

/**
 * @author matrixelement
 */
public class SharedWalletMemberAdapter extends CommonAdapter<OwnerEntity> {

    public SharedWalletMemberAdapter(int layoutId, List<OwnerEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, OwnerEntity item, int position) {
        viewHolder.setText(R.id.tv_member_name, item.getName());
        viewHolder.setText(R.id.tv_member_address, AddressFormatUtil.formatAddress(item.getPrefixAddress()));
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        super.updateItemView(context, position, viewHolder);
        OwnerEntity ownerEntity = mDatas.get(position);
        viewHolder.setText(R.id.tv_member_name, ownerEntity.getName());
    }
}

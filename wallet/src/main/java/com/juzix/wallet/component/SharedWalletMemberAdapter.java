package com.juzix.wallet.component;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

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
        TextView tvAddress = viewHolder.getView(R.id.tv_member_address);
        if (position == 0){
            viewHolder.setTextColor(R.id.tv_member_name, ContextCompat.getColor(context, R.color.color_b6bbd0));
            tvAddress.setTextColor(ContextCompat.getColor(context, R.color.color_b6bbd0));
            viewHolder.getView(R.id.iv_edit).setVisibility(View.GONE);
        }else {
            viewHolder.setTextColor(R.id.tv_member_name, ContextCompat.getColor(context, R.color.color_000000));
            tvAddress.setTextColor(ContextCompat.getColor(context, R.color.color_61646e));
            viewHolder.getView(R.id.iv_edit).setVisibility(View.VISIBLE);
        }
        viewHolder.setText(R.id.tv_member_name, item.getName());
        tvAddress.setText(AddressFormatUtil.formatAddress(item.getPrefixAddress()));
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        super.updateItemView(context, position, viewHolder);
        OwnerEntity ownerEntity = mDatas.get(position);
        viewHolder.setText(R.id.tv_member_name, ownerEntity.getName());
    }
}

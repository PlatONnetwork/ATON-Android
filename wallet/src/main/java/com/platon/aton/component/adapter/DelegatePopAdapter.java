package com.platon.aton.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.entity.DelegateType;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.StringUtil;

import org.web3j.platon.StakingAmountType;

import java.util.List;

public class DelegatePopAdapter extends BaseAdapter {
    private List<DelegateType> typeList;
    private Context mContext;
    private int defItem;//声明默认选中项

    public DelegatePopAdapter(Context context, List<DelegateType> typeList) {
        this.mContext = context;
        this.typeList = typeList;
    }

    @Override
    public int getCount() {
        if (null != typeList && typeList.size() > 0) {
            return typeList.size();
        }
        return 0;
    }

    @Override
    public DelegateType getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DelegateType item = typeList.get(position);
        ViewHolder holder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_delegate_item, null, false);
            holder = new ViewHolder();
            holder.tv_delegate_type = view.findViewById(R.id.tv_delegate_type);
            holder.tv_delegate_amount = view.findViewById(R.id.tv_delegate_amount);
            holder.iv_drop_down = view.findViewById(R.id.iv_drop_down);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        if (defItem == position) {
            holder.iv_drop_down.setVisibility(View.VISIBLE);
        } else {
            holder.iv_drop_down.setVisibility(View.INVISIBLE);
        }

        holder.tv_delegate_type.setText(item.getStakingAmountType() == StakingAmountType.FREE_AMOUNT_TYPE ? mContext.getString(R.string.available_balance) : mContext.getString(R.string.locked_balance));
        holder.tv_delegate_amount.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(item.getAmount(), "1E18"))), false));
        return view;
    }


    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();

    }

    class ViewHolder {
        TextView tv_delegate_type;
        TextView tv_delegate_amount;
        ImageView iv_drop_down;


    }
}

package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.entity.WithDrawType;
import com.juzix.wallet.utils.StringUtil;

import java.util.List;


public class WithDrawPopWindowAdapter extends BaseAdapter {
    private List<WithDrawType> typeList;
    private Context mContext;
    private int defItem;//声明默认选中项
    public static final String TAG_RELEASED = "tag_released";
    public static final String TAG_DELEGATED = "tag_delegated";

    public WithDrawPopWindowAdapter(Context context, List<WithDrawType> dataList) {
        this.mContext = context;
        this.typeList = dataList;
    }


    @Override
    public int getCount() {
        if (null != typeList && typeList.size() > 0) {
            return typeList.size();
        }
        return 0;
    }

    @Override
    public WithDrawType getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_delegate_item, null, false);
            holder = new ViewHolder();
            holder.tv_delegate_type = view.findViewById(R.id.tv_delegate_type);
            holder.tv_delegate_amount = view.findViewById(R.id.tv_delegate_amount);
            holder.iv_drop_down = view.findViewById(R.id.iv_drop_down);
            holder.rl_choose_delegate = view.findViewById(R.id.rl_choose_delegate);
            holder.v_line = view.findViewById(R.id.v_line);
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

        if (TextUtils.equals(typeList.get(position).getKey(), TAG_DELEGATED)) {
            holder.tv_delegate_type.setText(mContext.getString(R.string.withdraw_type_delegated));
        } else {
            holder.tv_delegate_type.setText(mContext.getString(R.string.withdraw_type_released));
        }

        holder.v_line.setVisibility((position == typeList.size() - 1) ? View.GONE : View.VISIBLE);
        holder.tv_delegate_amount.setText(StringUtil.formatBalance(typeList.get(position).getValue(), false));
        return view;
    }


    @Override
    public boolean isEnabled(int position) {
        return typeList.get(position).getValue() > 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tv_delegate_type;
        TextView tv_delegate_amount;
        ImageView iv_drop_down;
        RelativeLayout rl_choose_delegate;
        View v_line;

    }
}

package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.utils.StringUtil;

import java.util.List;

public class DelegatePopWindowAdapter extends CommonAdapter<DelegateType> {
    private ListView listView;

    public DelegatePopWindowAdapter(int layoutId, List<DelegateType> datas, ListView listView) {
        super(layoutId, datas);
        this.listView = listView;
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, DelegateType item, int position) {
        if (item != null) {
            viewHolder.getConvertView().setEnabled(item.getAmount() > 0);
            viewHolder.setText(R.id.tv_delegate_type, TextUtils.equals(item.getType(), "balance") ? context.getString(R.string.available_balance) : context.getString(R.string.locked_balance));
            viewHolder.setText(R.id.tv_delegate_amount, StringUtil.formatBalance(item.getAmount(), false));
            viewHolder.setVisible(R.id.iv_drop_down, listView != null && listView.getCheckedItemPosition() == position);
        }

    }
//    private Context mContext;
//    private List<DelegateType> dataList;
//    private LayoutInflater inflater;
//
//    public DelegatePopWindowAdapter(Context context, List<DelegateType> dataList) {
//        this.mContext = context;
//        this.dataList = dataList;
//        this.inflater = LayoutInflater.from(context);
//    }
//
//
//    @Override
//    public int getCount() {
//        return dataList.size() == 0 ? 0 : dataList.size();
//    }
//
//    @Override
//    public DelegateType getItem(int position) {
//        if (dataList.size() > 0 && dataList != null) {
//            return dataList.get(position);
//        }
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.popwindow_delegate_item, null);
//            holder = new ViewHolder();
//            holder.tv_delegate_amount = convertView.findViewById(R.id.tv_delegate_amount);
//            holder.tv_delegate_type = convertView.findViewById(R.id.tv_delegate_type);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        //初始化值 todo 暂时没写
//        holder.tv_delegate_amount.setText("");
//        holder.tv_delegate_type.setText("");
//
//        return convertView;
//    }
//
//    public class ViewHolder {
//        private TextView tv_delegate_type;
//        private TextView tv_delegate_amount;
//    }
//
//    public void notifyDataChanged(List<DelegateType> mDatas) {
//        this.dataList = mDatas;
//        notifyDataSetChanged();
//    }

}

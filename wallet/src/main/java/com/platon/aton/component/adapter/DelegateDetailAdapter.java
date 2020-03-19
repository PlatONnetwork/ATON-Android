package com.platon.aton.component.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.platon.aton.R;
import com.platon.aton.component.adapter.base.BaseViewHolder;
import com.platon.aton.entity.DelegateItemInfo;

import java.util.List;

public class DelegateDetailAdapter extends RecyclerView.Adapter<BaseViewHolder<DelegateItemInfo>> {

    private List<DelegateItemInfo> mDelegateItemInfoList;

    public void setDatas(List<DelegateItemInfo> delegateItemInfoList) {
        this.mDelegateItemInfoList = delegateItemInfoList;
    }

    @NonNull
    @Override
    public BaseViewHolder<DelegateItemInfo> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DelegateItemInfoViewHolder(R.layout.item_delegate_detail_list, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<DelegateItemInfo> delegateItemInfoBaseViewHolder, int i) {
        delegateItemInfoBaseViewHolder.refreshData(mDelegateItemInfoList.get(i), i);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<DelegateItemInfo> holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.updateItem((Bundle) payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        if (mDelegateItemInfoList != null) {
            return mDelegateItemInfoList.size();
        }
        return 0;
    }
}

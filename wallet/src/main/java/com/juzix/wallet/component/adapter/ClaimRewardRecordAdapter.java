package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.entity.ClaimReward;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

public class ClaimRewardRecordAdapter extends RecyclerView.Adapter<BaseViewHolder<ClaimRewardRecord>> {

    private List<ClaimRewardRecord> mClaimRewardRecordList;

    public void setList(List<ClaimRewardRecord> claimRewardRecordList) {
        this.mClaimRewardRecordList = claimRewardRecordList;
    }

    @NonNull
    @Override
    public BaseViewHolder<ClaimRewardRecord> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ClaimRewardRecordViewHolder(R.layout.item_claim_record, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ClaimRewardRecord> claimRewardRecordBaseViewHolder, int position) {
        claimRewardRecordBaseViewHolder.refreshData(mClaimRewardRecordList.get(position), position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ClaimRewardRecord> holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.updateItem((Bundle) payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        if (mClaimRewardRecordList != null) {
            return mClaimRewardRecordList.size();
        }
        return 0;
    }
}

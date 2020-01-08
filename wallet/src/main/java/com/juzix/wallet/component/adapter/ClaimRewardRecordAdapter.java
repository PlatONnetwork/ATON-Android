package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.entity.ClaimReward;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

public class ClaimRewardRecordAdapter extends RecyclerAdapter<ClaimRewardRecord> {

    public ClaimRewardRecordAdapter(Context mContext, List<ClaimRewardRecord> mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }

    @Override
    public void convert(RecycleHolder holder, ClaimRewardRecord data, int position) {

        holder.setImageResource(R.id.civ_wallet_avatar, RUtils.drawable(data.getWalletAvatar()));
        holder.setText(R.id.tv_claim_amount, data.getTotalReward());
        holder.setText(R.id.tv_wallet_name, data.getWalletName());
        holder.setText(R.id.tv_wallet_address, data.getAddress());
        holder.setText(R.id.tv_claim_reward_time, DateUtil.format(data.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN));

    }

    public void setList(List<ClaimRewardRecord> claimRewardRecordList) {
        this.mDatas = claimRewardRecordList;
    }

    private View buildClaimRewardView(Context context, ClaimReward claimReward) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_claim_record,null);

        return view;
    }
}

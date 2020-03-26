package com.platon.aton.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.component.adapter.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.entity.ClaimReward;
import com.platon.aton.entity.ClaimRewardRecord;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.DensityUtil;

import java.util.List;

/**
 * @author ziv
 * date On 2020-02-27
 */
public class ClaimRewardRecordAdapter extends BaseExpandableRecyclerViewAdapter<ClaimRewardRecord, ClaimReward, ClaimRewardRecordAdapter.GroupVH, ClaimRewardRecordAdapter.ChildVH> {

    private List<ClaimRewardRecord> mClaimRewardRecordList;
    private Context mContext;


    public ClaimRewardRecordAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<ClaimRewardRecord> claimRewardRecordList) {
        this.mClaimRewardRecordList = claimRewardRecordList;
    }

    @Override
    public int getGroupCount() {
        if (mClaimRewardRecordList != null) {
            return mClaimRewardRecordList.size();
        }
        return 0;
    }

    @Override
    public ClaimRewardRecord getGroupItem(int groupIndex) {
        if (mClaimRewardRecordList != null && groupIndex < mClaimRewardRecordList.size()) {
            return mClaimRewardRecordList.get(groupIndex);
        }
        return null;
    }

    @Override
    public GroupVH onCreateGroupViewHolder(ViewGroup parent, int groupViewType) {
        return new GroupVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_claim_record, parent, false));
    }

    @Override
    public void onBindGroupViewHolder(GroupVH holder, ClaimRewardRecord claimRewardRecord, boolean isExpand) {

        ShadowDrawable.setShadowDrawable(holder.mItemParentLayout,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                DensityUtil.dp2px(mContext, 2));

        holder.mWalletAvatarCiv.setImageResource(RUtils.drawable(claimRewardRecord.getWalletAvatar()));
        holder.mClaimRewardAmountTv.setText(String.format("%s%s", "+", mContext.getString(R.string.amount_with_unit, AmountUtil.formatAmountText(claimRewardRecord.getTotalReward(), 12))));
        holder.mWalletNameTv.setText(claimRewardRecord.getWalletName());
        holder.mWalletAddressTv.setText(AddressFormatUtil.formatClaimRewardRecordAddress(claimRewardRecord.getAddress()));
        holder.mClaimRewardTimeTv.setText(String.format("#%s", DateUtil.format(claimRewardRecord.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND)));
        holder.mSpreadIv.setVisibility(claimRewardRecord.isExpandable() ? View.VISIBLE : View.GONE);
        holder.mSpreadIv.setImageResource(claimRewardRecord.isExpanded() ? R.drawable.icon_pull_up_blue : R.drawable.icon_drop_down_blue);
    }

    @Override
    public ChildVH onCreateChildViewHolder(ViewGroup parent, int childViewType) {
        return new ChildVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_claim_record_detail, parent, false));
    }

    @Override
    public void onBindChildViewHolder(ChildVH holder, ClaimRewardRecord groupBean, ClaimReward claimReward) {
        holder.mNodeNameTv.setText(claimReward.getNodeName());
        holder.mClaimAmountTv.setText(String.format("%s%s", "+", mContext.getResources().getString(R.string.amount_with_unit, AmountUtil.formatAmountText(claimReward.getReward(), 12))));
    }

    static class GroupVH extends BaseExpandableRecyclerViewAdapter.BaseGroupViewHolder {

        private CircleImageView mWalletAvatarCiv;
        private TextView mClaimRewardAmountTv;
        private TextView mWalletNameTv;
        private TextView mWalletAddressTv;
        private TextView mClaimRewardTimeTv;
        private LinearLayout mItemParentLayout;
        private ImageView mSpreadIv;

        GroupVH(View itemView) {
            super(itemView);

            mWalletAvatarCiv = itemView.findViewById(R.id.civ_wallet_avatar);
            mClaimRewardAmountTv = itemView.findViewById(R.id.tv_claim_amount);
            mWalletNameTv = itemView.findViewById(R.id.tv_wallet_name);
            mWalletAddressTv = itemView.findViewById(R.id.tv_wallet_address);
            mClaimRewardTimeTv = itemView.findViewById(R.id.tv_claim_reward_time);
            mItemParentLayout = itemView.findViewById(R.id.layout_item_parent);
            mSpreadIv = itemView.findViewById(R.id.iv_spread);
        }

        // this method is used for partial update.Which means when expand status changed,only a part of this view need to invalidate
        @Override
        protected void onExpandStatusChanged(RecyclerView.Adapter relatedAdapter, boolean isExpanding) {
            mSpreadIv.setImageResource(isExpanding ? R.drawable.icon_pull_up_blue : R.drawable.icon_drop_down_blue);
        }
    }

    static class ChildVH extends RecyclerView.ViewHolder {

        private TextView mNodeNameTv;
        private TextView mClaimAmountTv;

        ChildVH(View itemView) {
            super(itemView);

            mNodeNameTv = itemView.findViewById(R.id.tv_node_name);
            mClaimAmountTv = itemView.findViewById(R.id.tv_claim_amount);
        }
    }
}

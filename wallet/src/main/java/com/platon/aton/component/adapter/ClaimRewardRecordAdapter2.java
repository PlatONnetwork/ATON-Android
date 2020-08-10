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

import com.platon.aton.R;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.entity.ClaimRewardRecord;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.framework.utils.RUtils;

import java.util.List;

public class ClaimRewardRecordAdapter2 extends RecyclerView.Adapter<ClaimRewardRecordAdapter2.ViewHolder> {
    private OnClickListener mClickListener;
    private List<ClaimRewardRecord> mClaimRewardRecordList;
    private Context mContext;

    public ClaimRewardRecordAdapter2( Context context) {
        this.mContext = context;
    }

    public void setClickListener(OnClickListener listener) {
        mClickListener = listener;
    }


    public void setList(List<ClaimRewardRecord> claimRewardRecordList) {
        this.mClaimRewardRecordList = claimRewardRecordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_claim_record, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public int getItemCount() {
        if(mClaimRewardRecordList == null){
            return 0;
        }
        return mClaimRewardRecordList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ClaimRewardRecord claimRewardRecord = mClaimRewardRecordList.get(position);
        holder.mWalletAvatarCiv.setImageResource(RUtils.drawable(claimRewardRecord.getWalletAvatar()));
        holder.mClaimRewardAmountTv.setText(String.format("%s%s", "+", mContext.getString(R.string.amount_with_unit, AmountUtil.formatAmountText(claimRewardRecord.getTotalReward(), 8))));
        holder.mWalletNameTv.setText(claimRewardRecord.getWalletName());
        holder.mWalletAddressTv.setText(AddressFormatUtil.formatClaimRewardRecordAddress(claimRewardRecord.getAddress()));
        holder.mClaimRewardTimeTv.setText(String.format("#%s", DateUtil.format(claimRewardRecord.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND)));
        holder.mSpreadIv.setVisibility(claimRewardRecord.isExpandable() ? View.VISIBLE : View.GONE);
        holder.mSpreadIv.setImageResource(R.drawable.icon_drop_down_blue);
        holder.mItemParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null){
                    mClickListener.onItemClick(position);
                    if(holder.mItem_claim_child_record_detail.getVisibility() == View.VISIBLE){
                        holder.mItem_claim_child_record_detail.setVisibility(View.GONE);
                        holder.mSpreadIv.setImageResource(R.drawable.icon_drop_down_blue);
                    }else{
                        holder.mItem_claim_child_record_detail.setVisibility(View.VISIBLE);
                        holder.mSpreadIv.setImageResource(R.drawable.icon_pull_up_blue);

                    }
                }
            }
        });

        ShadowDrawable.setShadowDrawable(holder.mItemParentLayout,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                DensityUtil.dp2px(mContext, 2));

        holder.mNodeNameTv.setText(claimRewardRecord.getClaimRewardList().get(0).getNodeName());
        holder.mClaimAmountTv.setText(String.format("%s%s", "+", mContext.getResources().getString(R.string.amount_with_unit, AmountUtil.formatAmountText(claimRewardRecord.getClaimRewardList().get(0).getReward(), 8))));
    }


    public interface OnClickListener {

        void onItemClick(int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
             CircleImageView mWalletAvatarCiv;
             TextView mClaimRewardAmountTv;
             TextView mWalletNameTv;
             TextView mWalletAddressTv;
             TextView mClaimRewardTimeTv;
             LinearLayout mItemParentLayout;
             ImageView mSpreadIv;
             TextView mNodeNameTv;
             TextView mClaimAmountTv;
             View mItem_claim_child_record_detail;

        public ViewHolder(View itemView) {
            super(itemView);
            mWalletAvatarCiv = itemView.findViewById(R.id.civ_wallet_avatar);
            mClaimRewardAmountTv = itemView.findViewById(R.id.tv_claim_amount);
            mWalletNameTv = itemView.findViewById(R.id.tv_wallet_name);
            mWalletAddressTv = itemView.findViewById(R.id.tv_wallet_address);
            mClaimRewardTimeTv = itemView.findViewById(R.id.tv_claim_reward_time);
            mItemParentLayout = itemView.findViewById(R.id.layout_item_parent);
            mSpreadIv = itemView.findViewById(R.id.iv_spread);
            mItem_claim_child_record_detail = itemView.findViewById(R.id.item_claim_child_record_detail);

            mNodeNameTv = itemView.findViewById(R.id.tv_node_name);
            mClaimAmountTv = itemView.findViewById(R.id.tv_child_claim_amount);


        }

    }
}

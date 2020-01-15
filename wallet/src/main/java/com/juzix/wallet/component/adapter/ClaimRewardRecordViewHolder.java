package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.ClaimReward;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.DensityUtil;

import java.util.List;

import jnr.constants.platform.PRIO;

public class ClaimRewardRecordViewHolder extends BaseViewHolder<ClaimRewardRecord> {

    private CircleImageView mWalletAvatarCiv;
    private TextView mClaimRewardAmountTv;
    private TextView mWalletNameTv;
    private TextView mWalletAddressTv;
    private TextView mClaimRewardTimeTv;
    private LinearLayout mClaimRecordDetailListLayout;
    private LinearLayout mItemParentLayout;
    private ImageView mSpreadIv;

    public ClaimRewardRecordViewHolder(int viewId, ViewGroup parent) {
        super(viewId, parent);

        mWalletAvatarCiv = itemView.findViewById(R.id.civ_wallet_avatar);
        mClaimRewardAmountTv = itemView.findViewById(R.id.tv_claim_amount);
        mWalletNameTv = itemView.findViewById(R.id.tv_wallet_name);
        mWalletAddressTv = itemView.findViewById(R.id.tv_wallet_address);
        mClaimRewardTimeTv = itemView.findViewById(R.id.tv_claim_reward_time);
        mClaimRecordDetailListLayout = itemView.findViewById(R.id.list_claim_record_detail);
        mItemParentLayout = itemView.findViewById(R.id.layout_item_parent);
        mSpreadIv = itemView.findViewById(R.id.iv_spread);
    }

    @Override
    public void refreshData(ClaimRewardRecord data, int position) {
        super.refreshData(data, position);

        ShadowDrawable.setShadowDrawable(mItemParentLayout,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                DensityUtil.dp2px(mContext, 2));


        mWalletAvatarCiv.setImageResource(RUtils.drawable(data.getWalletAvatar()));
        mClaimRewardAmountTv.setText(mContext.getString(R.string.amount_with_unit, AmountUtil.formatAmountText(data.getTotalReward())));
        mWalletNameTv.setText(data.getWalletName());
        mWalletAddressTv.setText(AddressFormatUtil.formatAddress(data.getAddress()));
        mClaimRewardTimeTv.setText(String.format("#%s", DateUtil.format(data.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN)));
        mSpreadIv.setImageResource(data.isExpanded() ? R.drawable.icon_pull_up_blue : R.drawable.icon_drop_down_blue);
        mSpreadIv.setVisibility(data.getClaimRewardList() == null || data.getClaimRewardList().isEmpty() ? View.GONE : View.VISIBLE);

        if (mClaimRecordDetailListLayout.getChildCount() > 0) {
            mClaimRecordDetailListLayout.removeAllViews();
        }

        for (int i = 0, size = data.getClaimRewardList().size(); i < size; i++) {
            mClaimRecordDetailListLayout.addView(buildClaimRewardView(mContext, data.getClaimRewardList().get(i)));
        }

        mClaimRecordDetailListLayout.setVisibility(data.isExpanded() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateItem(Bundle bundle) {
        super.updateItem(bundle);
        for (String key : bundle.keySet()) {
            switch (key) {
                case ClaimRewardRecordDiffCallback.KEY_CLAIM_REWARD_ADDRESS:
                    mWalletAddressTv.setText(bundle.getString(key));
                    break;
                case ClaimRewardRecordDiffCallback.KEY_CLAIM_REWARD_AMOUNT:
                    mClaimRewardAmountTv.setText(bundle.getString(key));
                    break;
                case ClaimRewardRecordDiffCallback.KEY_CLAIM_REWARD_LIST:
                    if (mClaimRecordDetailListLayout.getChildCount() > 0) {
                        mClaimRecordDetailListLayout.removeAllViews();
                    }
                    List<ClaimReward> claimRewardList = (List<ClaimReward>) bundle.get(key);
                    if (claimRewardList != null && !claimRewardList.isEmpty()) {
                        for (int i = 0, size = claimRewardList.size(); i < size; i++) {
                            mClaimRecordDetailListLayout.addView(buildClaimRewardView(mContext, claimRewardList.get(i)));
                        }
                    }
                    break;
                case ClaimRewardRecordDiffCallback.KEY_CLAIM_REWARD_EXPANDED:
                    mClaimRecordDetailListLayout.setVisibility(bundle.getBoolean(key) ? View.VISIBLE : View.GONE);
                    mSpreadIv.setImageResource(bundle.getBoolean(key) ? R.drawable.icon_pull_up_blue : R.drawable.icon_drop_down_blue);
                    break;
                default:
                    break;

            }
        }
    }

    private View buildClaimRewardView(Context context, ClaimReward claimReward) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_claim_record_detail, null);
        ((TextView) view.findViewById(R.id.tv_node_name)).setText(claimReward.getNodeName());
        ((TextView) view.findViewById(R.id.tv_claim_amount)).setText(String.format("%s%s", "+", AmountUtil.formatAmountText(claimReward.getReward())));
        return view;
    }
}

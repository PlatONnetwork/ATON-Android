package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.ClaimReward;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

import jnr.constants.platform.PRIO;

public class ClaimRewardRecordViewHolder extends BaseViewHolder<ClaimRewardRecord> {

    private CircleImageView mWalletAvatarCiv;
    private TextView mClaimRewardAmountTv;
    private TextView mWalletNameTv;
    private TextView mWalletAddressTv;
    private TextView mClaimRewardTimeTv;
    private LinearLayout mClaimRecordDetailListLayout;


    public ClaimRewardRecordViewHolder(int viewId, ViewGroup parent) {
        super(viewId, parent);

        mWalletAvatarCiv = parent.findViewById(R.id.civ_wallet_avatar);
        mClaimRewardAmountTv = parent.findViewById(R.id.tv_claim_amount);
        mWalletNameTv = parent.findViewById(R.id.tv_wallet_name);
        mWalletAddressTv = parent.findViewById(R.id.tv_wallet_address);
        mClaimRewardTimeTv = parent.findViewById(R.id.tv_claim_reward_time);
        mClaimRecordDetailListLayout = parent.findViewById(R.id.list_claim_record_detail);
    }

    @Override
    public void refreshData(ClaimRewardRecord data, int position) {
        super.refreshData(data, position);
        mWalletAvatarCiv.setImageResource(RUtils.drawable(data.getWalletAvatar()));
        mClaimRewardAmountTv.setText(data.getTotalReward());
        mWalletNameTv.setText(data.getWalletName());
        mWalletAddressTv.setText(data.getAddress());
        mClaimRewardTimeTv.setText(DateUtil.format(data.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN));

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
                    break;
                default:
                    break;

            }
        }
    }

    private View buildClaimRewardView(Context context, ClaimReward claimReward) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_claim_record_detail, null);
        ((TextView) view.findViewById(R.id.tv_node_name)).setText(claimReward.getNodeName());
        ((TextView) view.findViewById(R.id.tv_claim_amount)).setText(claimReward.getReward());
        return view;
    }
}

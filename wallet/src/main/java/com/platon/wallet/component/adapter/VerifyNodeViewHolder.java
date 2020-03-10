package com.platon.wallet.component.adapter;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.platon.framework.util.MapUtils;
import com.platon.framework.util.NumberParserUtils;
import com.platon.wallet.R;
import com.platon.wallet.component.adapter.base.BaseViewHolder;
import com.platon.wallet.component.widget.CircleImageView;
import com.platon.wallet.component.widget.RoundedTextView;
import com.platon.wallet.component.widget.ShadowDrawable;
import com.platon.wallet.entity.NodeStatus;
import com.platon.wallet.entity.VerifyNode;
import com.platon.wallet.utils.AmountUtil;
import com.platon.wallet.utils.DensityUtil;
import com.platon.wallet.utils.GlideUtils;
import com.platon.wallet.utils.StringUtil;

import java.util.HashMap;


public class VerifyNodeViewHolder extends BaseViewHolder<VerifyNode> {

    private ConstraintLayout mShadeCl;
    private CircleImageView mNodeAvatarCiv;
    private TextView mNodeNameTv;
    private TextView mNodeDelegatedAmount;
    private RoundedTextView mNodeStateRtv;
    private TextView mAnnualYieldTv;
    private AppCompatTextView mNodeRankTv;


    public VerifyNodeViewHolder(View itemView) {
        super(itemView);
    }

    public VerifyNodeViewHolder(int viewId, ViewGroup parent) {
        super(viewId, parent);
        mShadeCl = itemView.findViewById(R.id.cl_shade);
        mNodeAvatarCiv = itemView.findViewById(R.id.civ_node_avatar);
        mNodeNameTv = itemView.findViewById(R.id.tv_node_name);
        mNodeDelegatedAmount = itemView.findViewById(R.id.tv_delegated_amount);
        mNodeStateRtv = itemView.findViewById(R.id.rtv_node_state);
        mAnnualYieldTv = itemView.findViewById(R.id.tv_annual_yield);
        mNodeRankTv = itemView.findViewById(R.id.tv_node_rank);
    }

    @Override
    public void refreshData(VerifyNode data, int position) {
        super.refreshData(data, position);

        ShadowDrawable.setShadowDrawable(mShadeCl,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 8),
                0,
                DensityUtil.dp2px(mContext, 0));

        GlideUtils.loadRound(mContext, data.getUrl(), mNodeAvatarCiv);
        mNodeNameTv.setText(data.getName());
        mNodeDelegatedAmount.setText(String.format("%s / %s", mContext.getResources().getString(R.string.amount_with_unit, AmountUtil.convertVonToLat(data.getDelegateSum())), StringUtil.formatBalance(data.getDelegate(), 0, 0)));
        mNodeStateRtv.setText(mContext.getString(getNodeStatusDescRes(data.getNodeStatus(), data.isConsensus())));
        mNodeStateRtv.setTextColor(ContextCompat.getColor(mContext, getNodeStatusTextAndBorderColor(data.getNodeStatus(), data.isConsensus())));
        mNodeStateRtv.setRoundedBorderColor(ContextCompat.getColor(mContext, getNodeStatusTextAndBorderColor(data.getNodeStatus(), data.isConsensus())));
        mNodeRankTv.setText(String.valueOf(data.getRanking()));
        mNodeRankTv.setBackgroundResource(getRankBackground(NumberParserUtils.parseInt(data.getRanking())));
        mNodeRankTv.setTextSize(getRankTextSize(data.getRanking()));
        mAnnualYieldTv.setText(data.getShowDelegatedRatePA());

    }

    @Override
    public void updateItem(Bundle bundle) {
        super.updateItem(bundle);
        for (String key : bundle.keySet()) {
            switch (key) {
                case VerifyNodeDiffCallback.KEY_RANKING:
                    int ranking = bundle.getInt(key);
                    mNodeRankTv.setText(String.valueOf(ranking));
                    mNodeRankTv.setTextSize(getRankTextSize(ranking));
                    mNodeRankTv.setBackgroundResource(getRankBackground(NumberParserUtils.parseInt(ranking)));
                    break;
                case VerifyNodeDiffCallback.KEY_NAME:
                    String nodeName = bundle.getString(key);
                    mNodeNameTv.setText(nodeName);
                    break;
                case VerifyNodeDiffCallback.KEY_DEPOSIT_DELEGATOR_NUMBER:
                    HashMap<String, Object> map = (HashMap<String, Object>) bundle.getSerializable(key);
                    String deposit = MapUtils.getString(map, VerifyNodeDiffCallback.KEY_DEPOSIT);
                    String delegatorNumber = MapUtils.getString(map, VerifyNodeDiffCallback.KEY_DELEGATOR_NUMBER);
                    mNodeDelegatedAmount.setText(String.format("%s / %s", mContext.getResources().getString(R.string.amount_with_unit, AmountUtil.convertVonToLat(deposit)), StringUtil.formatBalance(delegatorNumber, 0, 0)));
                    break;
                case VerifyNodeDiffCallback.KEY_URL:
                    String url = bundle.getString(key);
                    GlideUtils.loadRound(mContext, url, mNodeAvatarCiv);
                    break;
                case VerifyNodeDiffCallback.KEY_RATEPA:
                    String ratePA = bundle.getString(key);
                    mAnnualYieldTv.setText(ratePA);
                    break;
                case VerifyNodeDiffCallback.KEY_NODE_STATUS_DESC:
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) bundle.getSerializable(key);
                    String nodeStatus = MapUtils.getString(hashMap, VerifyNodeDiffCallback.KEY_NODE_STATUS);
                    boolean isConsensus = MapUtils.getBoolean(hashMap, VerifyNodeDiffCallback.KEY_CONSENSUS);
                    mNodeStateRtv.setText(getNodeStatusDescRes(nodeStatus, isConsensus));
                    mNodeStateRtv.setTextColor(ContextCompat.getColor(mContext, getNodeStatusTextAndBorderColor(nodeStatus, isConsensus)));
                    mNodeStateRtv.setRoundedBorderColor(ContextCompat.getColor(mContext, getNodeStatusTextAndBorderColor(nodeStatus, isConsensus)));
                    break;
                default:
                    break;

            }
        }
    }


    private int getRankBackground(int rank) {
        switch (rank) {
            case 1:
                return R.drawable.icon_rank_first;
            case 2:
                return R.drawable.icon_rank_second;
            case 3:
                return R.drawable.icon_rank_third;
            default:
                return R.drawable.icon_rank_others;
        }
    }

    private int getRankTextSize(int rank) {
        return rank >= 1000 ? 10 : 11;
    }

    private int getNodeStatusTextAndBorderColor(String nodeStatus, boolean isConsensus) {
        if (TextUtils.equals(NodeStatus.ACTIVE, nodeStatus)) {
            return isConsensus ? R.color.color_f79d10 : R.color.color_4a90e2;
        } else {
            return R.color.color_19a20e;
        }
    }

    private int getNodeStatusTextBackgroundResourse(String nodeStatus, boolean isConsensus) {
        if (TextUtils.equals(NodeStatus.ACTIVE, nodeStatus)) {
            return isConsensus ? R.color.color_f79d10 : R.color.color_4a90e2;
        } else {
            return R.color.color_19a20e;
        }
    }


    private int getNodeStatusDescRes(@NodeStatus String nodeStatus, boolean isConsensus) {

        switch (nodeStatus) {
            case NodeStatus.ACTIVE:
                return isConsensus ? R.string.validators_verifying : R.string.validators_active;
            case NodeStatus.CANDIDATE:
                return R.string.validators_candidate;
            case NodeStatus.EXITING:
                return R.string.validators_state_exiting;
            case NodeStatus.EXITED:
                return R.string.validators_state_exited;
            default:
                return R.string.unknown;
        }
    }
}

package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.NodeStatus;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;

import java.text.NumberFormat;


public class VerifyNodeViewHolder extends BaseViewHolder<VerifyNode> {

    private ConstraintLayout mShadeCl;
    private CircleImageView mNodeAvatarCiv;
    private TextView mNodeNameTv;
    private TextView mModeDelegatedAmount;
    private RoundedTextView mNodeStateRtv;
    private TextView mAnnualYieldTv;
    private AppCompatTextView mNodeRankTv;


    public VerifyNodeViewHolder(View itemView) {
        super(itemView);
    }

    public VerifyNodeViewHolder(int viewId, ViewGroup parent) {
        super(viewId, parent);
        mShadeCl = parent.findViewById(R.id.cl_shade);
        mNodeAvatarCiv = parent.findViewById(R.id.civ_node_avatar);
        mNodeNameTv = parent.findViewById(R.id.civ_node_avatar);
        mModeDelegatedAmount = parent.findViewById(R.id.civ_node_avatar);
        mNodeStateRtv = parent.findViewById(R.id.civ_node_avatar);
        mAnnualYieldTv = parent.findViewById(R.id.civ_node_avatar);
        mNodeRankTv = parent.findViewById(R.id.tv_node_rank);
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
        mModeDelegatedAmount.setText(data.getDelegateSum());
        int nodeStatusDescRes = data.getNodeStatusDescRes();
        if (nodeStatusDescRes != -1) {
            mNodeStateRtv.setText(nodeStatusDescRes);
        }
        mNodeRankTv.setText(data.getRanking());
        mNodeRankTv.setBackgroundResource(getRankBackground(NumberParserUtils.parseInt(data.getRanking())));
        mAnnualYieldTv.setText(data.getShowDelegatedRatePA());

    }

    @Override
    public void updateItem(Bundle bundle) {
        super.updateItem(bundle);
        for (String key : bundle.keySet()) {
            switch (key) {
                case VerifyNodeDiffCallback.KEY_RANKING:
                    String ranking = bundle.getString(key);
                    mNodeRankTv.setText(ranking);
                    mNodeRankTv.setBackgroundResource(getRankBackground(NumberParserUtils.parseInt(ranking)));
                    break;
                case VerifyNodeDiffCallback.KEY_NAME:
                    String nodeName = bundle.getString(key);
                    mNodeNameTv.setText(nodeName);
                    break;
                case VerifyNodeDiffCallback.KEY_DEPOSIT:
                    String deposit = bundle.getString(key);
                    mModeDelegatedAmount.setText(deposit);
                    break;
                case VerifyNodeDiffCallback.KEY_DELEGATOR_NUMBER:
                    String delegatorNumber = bundle.getString(key);
                    break;
                case VerifyNodeDiffCallback.KEY_URL:
                    String url = bundle.getString(key);
                    GlideUtils.loadRound(mContext, url, mNodeAvatarCiv);
                    break;
                case VerifyNodeDiffCallback.KEY_RATEPA:
                    String ratePA = bundle.getString(key);
                    mNodeRankTv.setText(ratePA);
                    break;
                case VerifyNodeDiffCallback.KEY_NODE_STATUS_DESC:
                    int nodeStatusDescRes = bundle.getInt(key);
                    if (nodeStatusDescRes != -1) {
                        mNodeStateRtv.setText(nodeStatusDescRes);
                    }
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

    private int getNodeStatusTextAndBorderColor(String nodeStatus, boolean isConsensus) {
        if (TextUtils.equals(NodeStatus.ACTIVE, nodeStatus)) {
            return isConsensus ? R.color.color_f79d10 : R.color.color_4a90e2;
        } else {
            return R.color.color_19a20e;
        }
    }

    private int getNodeStatusTextBackground(String nodeStatus, boolean isConsensus) {
        if (TextUtils.equals(NodeStatus.ACTIVE, nodeStatus)) {
            return isConsensus ? R.color.color_f79d10 : R.color.color_4a90e2;
        } else {
            return R.color.color_19a20e;
        }
    }
}

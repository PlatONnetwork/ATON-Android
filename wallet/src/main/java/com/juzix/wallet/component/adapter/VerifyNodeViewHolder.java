package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.LanguageUtil;

import java.util.Locale;

public class VerifyNodeViewHolder extends BaseViewHolder<VerifyNode> {

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

        GlideUtils.loadRound(mContext, data.getUrl(), mNodeAvatarCiv);
        mNodeNameTv.setText(data.getName());
        mModeDelegatedAmount.setText(data.getDelegateSum());
        if (Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(App.getContext()).getLanguage())) { //中文环境下
            if (TextUtils.equals(data.getNodeStatus(), ACTIVE)) {
                mNodeStateRtv.setText(data.isConsensus() ? R.string.validators_verifying : R.string.validators_active);
            } else {
                mNodeStateRtv.setText(R.string.validators_candidate);
            }
        } else {
            if (TextUtils.equals(data.getNodeStatus(), ACTIVE)) {
                mNodeStateRtv.setText(data.isConsensus() ? R.string.validators_verifying : R.string.validators_active);
            } else {
                mNodeStateRtv.setText(data.getNodeStatus());
            }
        }
    }

    @Override
    public void updateItem(Bundle bundle) {
        super.updateItem(bundle);
        for (String key : bundle.keySet()) {

        }
    }
}

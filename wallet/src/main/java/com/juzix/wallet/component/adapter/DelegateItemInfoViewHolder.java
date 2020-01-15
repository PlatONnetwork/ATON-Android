package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.view.CommonHybridActivity;
import com.juzix.wallet.component.ui.view.DelegateActivity;
import com.juzix.wallet.component.ui.view.WithDrawActivity;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.NodeStatus;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;

import org.w3c.dom.Text;

public class DelegateItemInfoViewHolder extends BaseViewHolder<DelegateItemInfo> {

    private CircleImageView mWalletAvatarCiv;
    private TextView mNodeStatusTv;
    private TextView mNodeNameTv;
    private TextView mNodeAddressTv;
    private TextView mDelegatedAmountTv;
    private TextView mUndelegatedAmount;
    private TextView mUnclaimedRewardAmountTv;
    private LinearLayout mDelegateLayout;
    private LinearLayout mUnDelegateLayout;
    private ImageView mNodeLinkIv;
    private LinearLayout mItemParentLayout;
    private TextView mUndelegatedTv;
    private LinearLayout mUnclaimRewardLayout;


    public DelegateItemInfoViewHolder(int viewId, ViewGroup parent) {
        super(viewId, parent);

        mWalletAvatarCiv = itemView.findViewById(R.id.civ_wallet_avatar);
        mNodeStatusTv = itemView.findViewById(R.id.tv_node_status);
        mNodeNameTv = itemView.findViewById(R.id.tv_node_name);
        mNodeAddressTv = itemView.findViewById(R.id.tv_node_address);
        mDelegatedAmountTv = itemView.findViewById(R.id.tv_delegated_amount);
        mUndelegatedAmount = itemView.findViewById(R.id.tv_undelegated_amount);
        mUnclaimedRewardAmountTv = itemView.findViewById(R.id.tv_unclaimed_reward_amount);
        mDelegateLayout = itemView.findViewById(R.id.tv_delegate);
        mUnDelegateLayout = itemView.findViewById(R.id.tv_undelegate);
        mNodeLinkIv = itemView.findViewById(R.id.iv_node_link);
        mItemParentLayout = itemView.findViewById(R.id.layout_item_parent);
        mUndelegatedTv = itemView.findViewById(R.id.tv_undelegated);
        mUnclaimRewardLayout = itemView.findViewById(R.id.layout_unclaim_reward);
    }


    @Override
    public void refreshData(DelegateItemInfo data, int position) {
        super.refreshData(data, position);

        ShadowDrawable.setShadowDrawable(mItemParentLayout,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                DensityUtil.dp2px(mContext, 2));

        GlideUtils.loadImage(mContext, data.getUrl(), mWalletAvatarCiv);
        mNodeStatusTv.setText(data.getNodeStatusDescRes());
        mNodeStatusTv.setTextColor(getNodeStatusTextColor(data.getNodeStatus(), data.isConsensus()));
        mNodeNameTv.setText(data.getNodeName());
        mNodeAddressTv.setText(AddressFormatUtil.formatAddress(data.getNodeId()));
        mDelegatedAmountTv.setText(AmountUtil.formatAmountText(data.getDelegated()));
        mUndelegatedAmount.setText(AmountUtil.formatAmountText(data.getReleased()));
        mUnclaimedRewardAmountTv.setText(AmountUtil.formatAmountText(data.getWithdrawReward()));
        mUnclaimRewardLayout.setVisibility(BigDecimalUtil.isBiggerThanZero(data.getWithdrawReward()) ? View.VISIBLE : View.GONE);

        mDelegateLayout.setEnabled(isDelegateBtnEnabled(data));

        mDelegateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DelegateActivity.actionStart(mContext, data);
            }
        });

        mUnDelegateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WithDrawActivity.actionStart(mContext, data);
            }
        });

        mNodeLinkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHybridActivity.actionStart(mContext, data.getUrl(), WebType.WEB_TYPE_NODE_DETAIL);
            }
        });

        mUndelegatedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出tips
                DelegateTipsDialog.createWithTitleAndContentDialog(null, null,
                        null, null, mContext.getString(R.string.detail_wait_undelegate), mContext.getString(R.string.detail_tips_content)).show(((BaseActivity) mContext).getSupportFragmentManager(), "validatorstip");
            }
        });
    }

    @Override
    public void updateItem(Bundle bundle) {
        super.updateItem(bundle);
        for (String key : bundle.keySet()) {
            switch (key) {
                case DelegateItemInfoDiffCallback.KEY_NODE_NAME:
                    mNodeNameTv.setText(bundle.getString(key));
                    break;
                case DelegateItemInfoDiffCallback.KEY_URL:
                    GlideUtils.loadImage(mContext, bundle.getString(key), mWalletAvatarCiv);
                    break;
                case DelegateItemInfoDiffCallback.KEY_NODE_STAUS:
                    mNodeNameTv.setText(mContext.getResources().getString(bundle.getInt(key)));
                    break;
                case DelegateItemInfoDiffCallback.KEY_DELEGATED:
                    mDelegatedAmountTv.setText(AmountUtil.formatAmountText(bundle.getString(key)));
                    break;
                case DelegateItemInfoDiffCallback.KEY_WITHDRAW_REWARD:
                    mUnclaimedRewardAmountTv.setText(AmountUtil.formatAmountText(bundle.getString(key)));
                    break;
                case DelegateItemInfoDiffCallback.KEY_RELEASED:
                    mUndelegatedAmount.setText(AmountUtil.formatAmountText(bundle.getString(key)));
                    break;
                default:
                    break;
            }
        }
    }

    private int getNodeStatusTextColor(@NodeStatus String nodeStatus, boolean isConsensus) {
        switch (nodeStatus) {
            case NodeStatus.CANDIDATE:
                return ContextCompat.getColor(mContext, R.color.color_19a20e);
            case NodeStatus.EXITING:
                return ContextCompat.getColor(mContext, R.color.color_525768);
            case NodeStatus.EXITED:
                return ContextCompat.getColor(mContext, R.color.color_9eabbe);
            default:
                return ContextCompat.getColor(mContext, isConsensus ? R.color.color_f79d10 : R.color.color_4a90e2);
        }
    }

    private boolean isDelegateBtnEnabled(DelegateItemInfo data) {
        return !(TextUtils.equals(data.getNodeStatus(), NodeStatus.EXITED) || TextUtils.equals(data.getNodeStatus(), NodeStatus.EXITING) || data.isInit());
    }
}

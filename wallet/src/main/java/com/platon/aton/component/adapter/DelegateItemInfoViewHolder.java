package com.platon.aton.component.adapter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.base.BaseViewHolder;
import com.platon.aton.component.ui.dialog.DelegateTipsDialog;
import com.platon.aton.component.ui.view.CommonHybridActivity;
import com.platon.aton.component.ui.view.DelegateActivity;
import com.platon.aton.component.ui.view.WithDrawActivity;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.NodeStatus;
import com.platon.aton.entity.WebType;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GlideUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.MapUtils;
import com.platon.framework.utils.ToastUtil;

import java.util.HashMap;

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
        mDelegateLayout = itemView.findViewById(R.id.layout_delegate);
        mUnDelegateLayout = itemView.findViewById(R.id.layout_undelegate);
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

        GlideUtils.loadRound(mContext, data.getUrl(), mWalletAvatarCiv);

        mNodeStatusTv.setText(NodeManager.getInstance().getNodeStatusDescRes(data.getNodeStatus(),data.isConsensus()));
        mNodeStatusTv.setTextColor(getNodeStatusTextColor(data.getNodeStatus(), data.isConsensus()));
        mNodeNameTv.setText(data.getNodeName());
        mNodeAddressTv.setText(AddressFormatUtil.formatAddress(data.getNodeId()));
        mDelegatedAmountTv.setText(AmountUtil.formatAmountText(data.getDelegated()));
        mUndelegatedAmount.setText(AmountUtil.formatAmountText(data.getReleased()));
        mUnclaimedRewardAmountTv.setText(AmountUtil.formatAmountText(data.getWithdrawReward()));
        mUnclaimRewardLayout.setVisibility(BigDecimalUtil.isBiggerThanZero(data.getWithdrawReward()) ? View.VISIBLE : View.GONE);

        mDelegateLayout.setEnabled(isDelegateBtnEnabled(data.getNodeStatus(), data.isInit()));

        RxView
                .clicks(mDelegateLayout)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (BigDecimalUtil.isBiggerThanZero(data.getReleased())) {
                            ToastUtil.showLongToast(mContext, R.string.delegate_no_click);
                        } else {
                            DelegateActivity.actionStart(mContext, data);
                        }
                    }
                });

        RxView
                .clicks(mUnDelegateLayout)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        WithDrawActivity.actionStart(mContext, data);
                    }
                });

        RxView
                .clicks(mNodeLinkIv)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(mContext, data.getUrl(), WebType.WEB_TYPE_NODE_DETAIL);
                    }
                });

        RxView
                .clicks(mUndelegatedTv)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
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
                    //GlideUtils.loadImage(mContext, bundle.getString(key), mWalletAvatarCiv);
                    GlideUtils.loadRound(mContext, bundle.getString(key), mWalletAvatarCiv);

                    break;
                case DelegateItemInfoDiffCallback.KEY_NODE_STATUS_AND_CONSENSUS:
                    HashMap<String, Object> map = (HashMap<String, Object>) bundle.getSerializable(key);
                    String nodeStatus = MapUtils.getString(map, DelegateItemInfoDiffCallback.KEY_NODE_STATUS);
                    boolean isConsensus = MapUtils.getBoolean(map, DelegateItemInfoDiffCallback.KEY_CONSENSUS);
                    mNodeStatusTv.setText(NodeManager.getInstance().getNodeStatusDescRes(nodeStatus, isConsensus));
                    mNodeStatusTv.setTextColor(getNodeStatusTextColor(nodeStatus, isConsensus));
                    break;
                case DelegateItemInfoDiffCallback.KEY_NODE_STATUS_AND_INIT:
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) bundle.getSerializable(key);
                    String status = MapUtils.getString(hashMap, DelegateItemInfoDiffCallback.KEY_NODE_STATUS);
                    boolean isInit = MapUtils.getBoolean(hashMap, DelegateItemInfoDiffCallback.KEY_INIT);
                    mDelegateLayout.setEnabled(isDelegateBtnEnabled(status, isInit));
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


    private boolean isDelegateBtnEnabled(@NodeStatus String nodeStatus, boolean isInit) {
        boolean isExited =  !(TextUtils.equals(nodeStatus, NodeStatus.EXITED) || TextUtils.equals(nodeStatus, NodeStatus.EXITING) || isInit);
        boolean isLocked =  !(TextUtils.equals(nodeStatus, NodeStatus.LOCKED) || TextUtils.equals(nodeStatus, NodeStatus.LOCKED) || isInit);
        return isExited && isLocked;
    }
}

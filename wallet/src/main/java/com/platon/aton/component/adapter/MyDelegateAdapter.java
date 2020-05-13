package com.platon.aton.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.PendingClaimRewardAnimationLayout;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.entity.DelegateInfo;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * 我的委托adapter
 */
public class MyDelegateAdapter extends RecyclerView.Adapter<MyDelegateAdapter.ViewHolder> {
    private Context mContext;
    private List<DelegateInfo> infoList;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_my_delegate_list, parent, false));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ShadowDrawable.setShadowDrawable(holder.item,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                DensityUtil.dp2px(mContext, 2));

        DelegateInfo info = infoList.get(position);
        holder.walletAvatarIv.setImageResource(RUtils.drawable(info.getWalletIcon()));
        holder.walletNameTv.setText(info.getWalletName());
        holder.walletAddressTv.setText(AddressFormatUtil.formatAddress(info.getWalletAddress()));
        holder.unclaimedRewardAmountTv.setText(CommonTextUtils.getPriceTextWithBold(AmountUtil.formatAmountText(info.getWithdrawReward(), 12), ContextCompat.getColor(mContext, R.color.color_000000), ContextCompat.getColor(mContext, R.color.color_000000),
                DensityUtil.dp2px(mContext, 14), DensityUtil.dp2px(mContext, 16)));
        holder.totalRewardAmountTv.setText(AmountUtil.formatAmountText(info.getCumulativeReward(), 8));
        holder.delegatedAmountTv.setText(AmountUtil.formatAmountText(info.getDelegated()));
        holder.claimRewardLayout.setVisibility(BigDecimalUtil.isBiggerThanZero(info.getWithdrawReward()) ? View.VISIBLE : View.GONE);
        holder.claimRewardLayout.setEnabled(!info.isPending());
        holder.claimRewardRtv.setVisibility(info.isPending() ? View.GONE : View.VISIBLE);
        holder.pendingClaimRewardAnimationLayout.setVisibility(info.isPending() ? View.VISIBLE : View.GONE);

        RxView
                .clicks(holder.itemView)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(info);
                        }
                    }
                });
        RxView
                .clicks(holder.delegateDetailTv)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(info);
                        }
                    }
                });

        RxView
                .clicks(holder.claimRewardLayout)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if (info.isPending()) {
                            return;
                        }

                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onClaimRewardClick(info, position);
                        }
                    }
                });


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            DelegateInfo delegateInfo = infoList.get(position);
            holder.walletAvatarIv.setImageResource(RUtils.drawable(delegateInfo.getWalletIcon()));
            holder.walletNameTv.setText(delegateInfo.getWalletName());
            holder.walletAddressTv.setText(AddressFormatUtil.formatAddress(delegateInfo.getWalletAddress()));
            holder.unclaimedRewardAmountTv.setText(CommonTextUtils.getPriceTextWithBold(AmountUtil.formatAmountText(delegateInfo.getWithdrawReward(), 12), ContextCompat.getColor(mContext, R.color.color_000000), ContextCompat.getColor(mContext, R.color.color_000000),
                    DensityUtil.dp2px(mContext, 14), DensityUtil.dp2px(mContext, 16)));
            holder.totalRewardAmountTv.setText(AmountUtil.formatAmountText(delegateInfo.getCumulativeReward(), 8));
            holder.delegatedAmountTv.setText(AmountUtil.formatAmountText(delegateInfo.getDelegated()));
            holder.claimRewardLayout.setVisibility(BigDecimalUtil.isBiggerThanZero(delegateInfo.getWithdrawReward()) ? View.VISIBLE : View.GONE);
            holder.claimRewardLayout.setEnabled(!delegateInfo.isPending());
            holder.claimRewardRtv.setVisibility(delegateInfo.isPending() ? View.GONE : View.VISIBLE);
            holder.pendingClaimRewardAnimationLayout.setVisibility(delegateInfo.isPending() ? View.VISIBLE : View.GONE);
        }
    }

    public void notifyDataChanged(List<DelegateInfo> list) {
        this.infoList = list;
        notifyDataSetChanged();
    }

    public void notifyItemDataChanged(boolean isPending, int position) {
        if (position > getItemCount()) {
            return;
        }

        DelegateInfo info = infoList.get(position);

        info.setPending(isPending);

        infoList.set(position, info);

        notifyItemChanged(position, info);
    }

    @Override
    public int getItemCount() {
        if (infoList != null) {
            return infoList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_item)
        LinearLayout item;
        @BindView(R.id.iv_wallet_avatar)
        CircleImageView walletAvatarIv;
        @BindView(R.id.tv_wallet_name)
        TextView walletNameTv;
        @BindView(R.id.tv_wallet_address)
        TextView walletAddressTv;
        @BindView(R.id.tv_delegate_detail)
        TextView delegateDetailTv;
        @BindView(R.id.tv_delegated_amount)
        TextView delegatedAmountTv;
        @BindView(R.id.tv_total_reward_amount)
        TextView totalRewardAmountTv;
        @BindView(R.id.tv_unclaimed_reward_amount)
        TextView unclaimedRewardAmountTv;
        @BindView(R.id.tv_claim_reward)
        TextView claimRewardRtv;
        @BindView(R.id.layout_claim_reward_animation)
        PendingClaimRewardAnimationLayout pendingClaimRewardAnimationLayout;
        @BindView(R.id.layout_claim_reward)
        LinearLayout claimRewardLayout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(DelegateInfo delegateInfo);

        /**
         * 领取奖励
         */
        void onClaimRewardClick(DelegateInfo delegateInfo, int position);
    }
}

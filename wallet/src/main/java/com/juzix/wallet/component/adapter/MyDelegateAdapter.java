package com.juzix.wallet.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;


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
        holder.unclaimedRewardAmountTv.setText(AmountUtil.formatAmountText(info.getDelegated()));
        holder.totalRewardAmountTv.setText(AmountUtil.formatAmountText(info.getCumulativeReward()));
        holder.delegatedAmountTv.setText(AmountUtil.formatAmountText(info.getDelegated()));
        holder.unclaimedRewardAmountTv.setText(AmountUtil.formatAmountText(info.getWithdrawReward()));
        holder.claimRewardRtv.setVisibility(BigDecimalUtil.isBiggerThanZero(info.getWithdrawReward()) ? View.VISIBLE : View.GONE);

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
                .clicks(holder.claimRewardRtv)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onClaimRewardClick();
                        }
                    }
                });


    }

    public void notifyDataChanged(List<DelegateInfo> list) {
        this.infoList = list;
        notifyDataSetChanged();
    }

    public void notifyItemDataChanged(int positon, DelegateInfo info) {
        if (info == null) {
            return;
        }
        notifyItemChanged(positon, info);
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
        @BindView(R.id.rtv_claim_reward)
        RoundedTextView claimRewardRtv;

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
        void onClaimRewardClick();
    }
}

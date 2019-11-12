package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.AutofitTextView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.netlistener.NetworkType;
import com.juzix.wallet.netlistener.NetworkUtil;

import java.util.List;

public class WalletHorizontalRecycleViewAdapter extends RecyclerView.Adapter<WalletHorizontalRecycleViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Wallet> mList;
    private OnRecycleViewItemClickListener mOnItemClickListener;
    private Wallet mSelectedWallet;
    private int mShapeRadius;
    private int mShadowRadius;

    public WalletHorizontalRecycleViewAdapter(Context context, List<Wallet> walletList) {
        mContext = context;
        mList = walletList;
        mSelectedWallet = null;
        mShapeRadius = (int) context.getResources().getDimension(R.dimen.assetsWalletSelectedShapeRadius);
        mShadowRadius = (int) context.getResources().getDimension(R.dimen.assetsWalletSelectedShadowRadius);
    }

    public Wallet getSelectedWallet() {
        return mSelectedWallet;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        setIndividualWalletView(position, mList.get(position), holder);
        holder.itemView.setTag(position);//将位置保存在tag中
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wallet_list, null));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecycleViewItemClickListener {
        void onContentViewClick(Wallet walletEntity);
    }

    public void removeItem(Wallet walletEntity) {
        if (mList == null || mList.isEmpty()) {
            return;
        }
        int position = mList.indexOf(walletEntity);
        if (position != -1) {
            if (mList.remove(position) != null) {
                notifyItemRemoved(position);
            }
        }
    }

    public List<Wallet> getWalletList() {
        return mList;
    }

    public void notifyDataSetChanged(List<Wallet> walletList, Wallet selectedWallet) {
        this.mList = walletList;
        this.mSelectedWallet = selectedWallet;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(Wallet selectedWallet) {
        this.mSelectedWallet = selectedWallet;
        notifyDataSetChanged();
    }

    private void setIndividualWalletView(final int position, final Wallet walletEntity, ViewHolder holder) {
        holder.rlItem.setVisibility(View.VISIBLE);
        holder.rlItem.findViewById(R.id.v_new_msg).setVisibility(View.GONE);
        AutofitTextView tvName = holder.rlItem.findViewById(R.id.tv_item2_name);
        ImageView ivIcon = holder.rlItem.findViewById(R.id.iv_item2_icon);
        ShadowDrawable.setShadowDrawable(holder.vShadow,
                ContextCompat.getColor(mContext, R.color.color_660051ff),
                mShapeRadius,
                ContextCompat.getColor(mContext, R.color.color_660051ff),
                mShadowRadius,
                0,
                0);
        if (mSelectedWallet == walletEntity) {
//            holder.vShadow.setVisibility(View.VISIBLE);
//            holder.rlItem.setBackgroundResource(R.drawable.bg_assets_classic_h);
//            ivIcon.setImageResource(R.drawable.icon_assets_classic_h);
//            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
            tvName.setText(walletEntity.getName());

            if (TextUtils.isEmpty(walletEntity.getKey())) { //观察钱包(与联不联网没关系)
                holder.vShadow.setVisibility(View.VISIBLE);
                holder.rlItem.setBackgroundResource(R.drawable.bg_assets_observed_h);
                ivIcon.setImageResource(R.drawable.icon_assets_observed_h);
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_ffffff));

            } else {
                if (NetworkUtil.getNetWorkType(mContext) != NetworkType.NETWORK_NO && NetworkUtil.getNetWorkType(mContext) != NetworkType.NETWORK_UNKNOWN) {//表示联网状态
                    //普通钱包
                    holder.vShadow.setVisibility(View.VISIBLE);
                    holder.rlItem.setBackgroundResource(R.drawable.bg_assets_classic_h);
                    ivIcon.setImageResource(R.drawable.icon_assets_classic_h);
                    tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
//                    tvName.setText(walletEntity.getName());
                } else {
                    //冷钱包
                    holder.vShadow.setVisibility(View.VISIBLE);
                    holder.rlItem.setBackgroundResource(R.drawable.bg_assets_cold_h);
                    ivIcon.setImageResource(R.drawable.icon_assets_cold_h);
                    tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
                }
            }

        } else {
            holder.vShadow.setVisibility(View.GONE);
//            holder.rlItem.setBackgroundResource(R.drawable.bg_assets_classic_n);
//            ivIcon.setImageResource(R.drawable.icon_assets_classic_n);
//            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_105cfe));
            tvName.setText(walletEntity.getName());

            if (TextUtils.isEmpty(walletEntity.getKey())) {//观察钱包
                holder.rlItem.setBackgroundResource(R.drawable.bg_assets_observed_n);
                ivIcon.setImageResource(R.drawable.icon_assets_observed_n);
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_F5A623));

            } else {
                if (NetworkUtil.getNetWorkType(mContext) != NetworkType.NETWORK_NO && NetworkUtil.getNetWorkType(mContext) != NetworkType.NETWORK_UNKNOWN) {//表示联网状态
                    //联网未选中的普通钱包
                    holder.rlItem.setBackgroundResource(R.drawable.bg_assets_classic_n);
                    ivIcon.setImageResource(R.drawable.icon_assets_classic_n);
                    tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_105cfe));
                } else {
                    //未选中的冷钱包
                    holder.rlItem.setBackgroundResource(R.drawable.bg_assets_cold_n);
                    ivIcon.setImageResource(R.drawable.icon_assets_cold_n);
                    tvName.setTextColor(ContextCompat.getColor(mContext,R.color.color_61646e));
                }
            }
        }
        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedWallet != walletEntity) {
                    mSelectedWallet = walletEntity;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentViewClick(walletEntity);
                    }
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout flItem;
        public RelativeLayout rlItem;
        public View vShadow;

        public ViewHolder(View convertView) {
            super(convertView);
            flItem = convertView.findViewById(R.id.fl_item);
            rlItem = convertView.findViewById(R.id.rl_item2);
            vShadow = convertView.findViewById(R.id.v_shadow);
        }
    }
}

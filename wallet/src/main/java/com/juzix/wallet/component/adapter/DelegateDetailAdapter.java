package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class DelegateDetailAdapter extends RecyclerView.Adapter<DelegateDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<DelegateDetail> detailList;

    private OnDelegateClickListener mOnDelegateClickListener;


    public void setmOnDelegateClickListener(OnDelegateClickListener mOnDelegateClickListener) {
        this.mOnDelegateClickListener = mOnDelegateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_delegate_detail_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DelegateDetail detail = detailList.get(position);
        holder.nodeName.setText(detail.getNodeName());
        holder.nodeAddress.setText(AddressFormatUtil.formatAddress(detail.getNodeId()));
        GlideUtils.loadRound(mContext, detail.getUrl(), holder.nodeIcon);
        holder.nodeState.setText(detail.getNodeStatus());
        changeTextViewColorByState(holder.nodeState, detail.getNodeStatus()); //NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(value, "1E18")
        holder.tv_node_locked_delegate.setText((TextUtils.isEmpty(detail.getLocked())) ? "--" : StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getLocked(), "1E18"))), false));
        holder.tv_node_unlocked_delegate.setText((TextUtils.isEmpty(detail.getUnLocked())) ? "--" : StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getUnLocked(), "1E18"))), false));
        holder.tv_node_released_delegate.setText((TextUtils.isEmpty(detail.getReleased())) ? "--" : StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getReleased(), "1E18"))), false));
        holder.tv_node_undelegating.setText((TextUtils.isEmpty(detail.getRedeem())) ? "--" : StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getRedeem(), "1E18"))), false));


        if (!TextUtils.isEmpty(detail.getReleased())) {
            holder.ll_delegate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_66DCDFE8));
            holder.iv_detail_delegate.setImageResource(R.drawable.icon_delegate);
        }


        if (TextUtils.isEmpty(detail.getLocked()) && TextUtils.isEmpty(detail.getUnLocked()) && TextUtils.isEmpty(detail.getReleased()) && TextUtils.isEmpty(detail.getRedeem())) {
            holder.tv_show_delegate.setText(R.string.nav_delegate);
            holder.tv_show_withdraw.setText(R.string.node_move_out);

        } else if (!TextUtils.isEmpty(detail.getLocked()) || !TextUtils.isEmpty(detail.getUnLocked()) || !TextUtils.isEmpty(detail.getReleased()) || !TextUtils.isEmpty(detail.getRedeem())) {
            //只要一个不为空
            holder.tv_show_delegate.setText(R.string.nav_delegate);
            holder.tv_show_withdraw.setText(R.string.node_withdraw_delegate);

        }


        if (TextUtils.isEmpty(detail.getLocked()) && TextUtils.isEmpty(detail.getUnLocked()) && TextUtils.isEmpty(detail.getReleased()) && TextUtils.isEmpty(detail.getRedeem())) {
            //操作移除列表
            RxView.clicks(holder.ll_withdraw)
                    .compose(RxUtils.getSchedulerTransformer())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if (null != mOnDelegateClickListener) {
                                removeData(position);
                                mOnDelegateClickListener.onMoveOutClick(detail);
                            }

                        }
                    });

        } else if (TextUtils.isEmpty(detail.getLocked()) && TextUtils.isEmpty(detail.getUnLocked()) && TextUtils.isEmpty(detail.getReleased())) {
            //按钮置灰并不可点击
            holder.ll_withdraw.setOnClickListener(null);
            holder.ll_withdraw.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_66DCDFE8));
            holder.iv_detail_un_delegate.setImageResource(R.drawable.icon_undelegate);


        } else {
            //操作赎回
            RxView.clicks(holder.ll_withdraw)
                    .compose(RxUtils.getClickTransformer())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if (null != mOnDelegateClickListener) {
                                mOnDelegateClickListener.onWithDrawClick(detail.getNodeId(), detail.getNodeName(), detail.getUrl(), detail.getStakingBlockNum());
                            }

                        }
                    });
        }


        RxView.clicks(holder.ll_delegate)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (null != mOnDelegateClickListener) {
                            if (!TextUtils.isEmpty(detail.getReleased())) {
                                //提示，不可点击
                                ToastUtil.showLongToast(mContext, R.string.delegate_no_click);



                            } else {
                                mOnDelegateClickListener.onDelegateClick(detail.getNodeId(), detail.getNodeName(), detail.getUrl());
                            }
                        }
                    }
                });


        RxView.clicks(holder.nodeName)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (null != mOnDelegateClickListener) {

                            mOnDelegateClickListener.onLinkClick(detail.getWebsite());
                        }

                    }
                });

    }

    /**
     * 删除操作
     */
    public void removeData(int position) {
        detailList.remove(position);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (detailList != null && detailList.size() > 0) {
            return detailList.size();
        }
        return 0;
    }

    public void notifyDataChanged(List<DelegateDetail> dataList) {
        this.detailList = dataList;
        this.notifyDataSetChanged();

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_total_delegate)
        CircleImageView nodeIcon;
        @BindView(R.id.node_name)
        TextView nodeName;
        @BindView(R.id.node_address)
        TextView nodeAddress;
        @BindView(R.id.tv_node_state)
        TextView nodeState;
        @BindView(R.id.tv_node_locked_delegate)
        TextView tv_node_locked_delegate;
        @BindView(R.id.tv_node_unlocked_delegate)
        TextView tv_node_unlocked_delegate;
        @BindView(R.id.tv_node_released_delegate)
        TextView tv_node_released_delegate;
        @BindView(R.id.tv_node_undelegating)
        TextView tv_node_undelegating;
        @BindView(R.id.ll_delegate)
        LinearLayout ll_delegate;
        @BindView(R.id.ll_withdraw)
        LinearLayout ll_withdraw;
        @BindView(R.id.tv_show_delegate)
        TextView tv_show_delegate;
        @BindView(R.id.tv_show_withdraw)
        TextView tv_show_withdraw;
        @BindView(R.id.iv_detail_un_delegate)
        ImageView iv_detail_un_delegate;
        @BindView(R.id.iv_detail_delegate)
        ImageView iv_detail_delegate;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public void changeTextViewColorByState(TextView tv, String nodeStatus) {
        switch (nodeStatus) {
            case "Active":
                tv.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_4a90e2));
                break;
            case "Candidate":
                tv.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_19a20e));
                break;
            case "Exiting":
                tv.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_525768));
                break;
            case "Exited":
                tv.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_9eabbe));
                break;
        }

    }


    public interface OnDelegateClickListener {

        void onDelegateClick(String nodeAddress, String nodeName, String nodeIcon);

        void onWithDrawClick(String nodeAddress, String nodeName, String nodeIcon, String stakingBlockNum);

        void onMoveOutClick(DelegateDetail detail);

        void onLinkClick(String webSiteUrl);
    }

}

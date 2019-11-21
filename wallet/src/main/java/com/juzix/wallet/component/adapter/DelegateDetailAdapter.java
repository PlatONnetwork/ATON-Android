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
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;


import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class DelegateDetailAdapter extends RecyclerView.Adapter<DelegateDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<DelegateDetail> detailList;

    private OnDelegateClickListener mOnDelegateClickListener;

    private static final String ACTIVE = "Active";
    private static final String CANDIDATE = "Candidate";
    private static final String EXITED = "Exited";
    private static final String EXITING = "Exiting";

    public void setOnDelegateClickListener(OnDelegateClickListener mOnDelegateClickListener) {
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
        ShadowDrawable.setShadowDrawable(holder.ll_delegate_detail_shade,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                DensityUtil.dp2px(mContext, 2));

        holder.nodeName.setText(detail.getNodeName());
        holder.nodeAddress.setText(AddressFormatUtil.formatAddress(detail.getNodeId()));
        GlideUtils.loadRound(mContext, detail.getUrl(), holder.nodeIcon);

        TextView nodeState = holder.nodeState;
        showTextSpan(mContext, detail, nodeState);
        changeTextViewColorByState(holder.nodeState, detail.getNodeStatus());

        holder.tv_delegated.setText(TextUtils.equals(detail.getDelegated(), "0") ? "- -" : StringUtil.formatBalance(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getDelegated(), "1E18"))));
        holder.tv_withdraw.setText(TextUtils.equals(detail.getReleased(), "0") ? "- -" : StringUtil.formatBalance(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getReleased(), "1E18"))));

        //委托按钮置灰不可点击( a.节点退出中或已退出 /b.节点状态为初始化验证人（收益地址为激励池地址的验证人）)
        if (TextUtils.equals(detail.getNodeStatus(), EXITED) || TextUtils.equals(detail.getNodeStatus(), EXITING) || detail.isInit()) {
            holder.ll_delegate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_66dcdfe8));
        }

        //只要待赎回的数量大于0，那么委托按钮置灰(即验证节点退出，委托解除，已解除委托不为0)
        if (NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getReleased(), "1E18"))) > 0) {
            holder.ll_delegate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_66dcdfe8));
            holder.iv_detail_delegate.setImageResource(R.drawable.icon_delegate);
        }

        if (NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getReleased(), "1E18"))) > 0
                || NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(detail.getDelegated(), "1E18"))) > 0) {
            //只要一个不为空
            holder.tv_show_delegate.setText(R.string.nav_delegate);
            holder.tv_show_withdraw.setText(R.string.node_withdraw_delegate);
        }


        RxView.clicks(holder.ll_withdraw)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (null != mOnDelegateClickListener) {
                            mOnDelegateClickListener.onWithDrawClick(detail);
                        }

                    }
                });

        RxView.clicks(holder.ll_delegate)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (null != mOnDelegateClickListener) {
                            if (detail.isInit()) {
                                ToastUtil.showLongToast(mContext, R.string.validators_details_tips);
                            } else if (TextUtils.equals(detail.getNodeStatus(), EXITED) || TextUtils.equals(detail.getNodeStatus(), EXITING)) {
                                //可以不做处理
                            } else {
                                mOnDelegateClickListener.onDelegateClick(detail);
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

    private void showTextSpan(Context mContext, DelegateDetail detail, TextView nodeState) {
        if (Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(App.getContext()).getLanguage())) { //中文环境下
            if (TextUtils.equals(detail.getNodeStatus(), ACTIVE)) {
                nodeState.setText(R.string.validators_active);
            } else if (TextUtils.equals(detail.getNodeStatus(), CANDIDATE)) {
                nodeState.setText(R.string.validators_candidate);
            } else if (TextUtils.equals(detail.getNodeStatus(), EXITED)) {
                nodeState.setText(R.string.validators_state_exited);
            } else {
                nodeState.setText(R.string.validators_state_exiting);
            }

        } else {
            nodeState.setText(detail.getNodeStatus());
        }
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
        //已委托
        @BindView(R.id.tv_node_delegated)
        TextView tv_delegated;
        //待赎回委托
        @BindView(R.id.tv_node_withdraw_delegate)
        TextView tv_withdraw;

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
        @BindView(R.id.ll_delegate_detail_shade)
        LinearLayout ll_delegate_detail_shade;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public void changeTextViewColorByState(TextView tv, String nodeStatus) {
        switch (nodeStatus) {
            case "Active":
                tv.setTextColor(ContextCompat.getColorStateList(mContext, R.color.color_4A90E2));
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
            default:
                break;
        }

    }


    public interface OnDelegateClickListener {

        void onDelegateClick(DelegateDetail delegateDetail);

        void onWithDrawClick(DelegateDetail delegateDetail);

        void onLinkClick(String webSiteUrl);
    }


}

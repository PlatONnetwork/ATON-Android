package com.platon.aton.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.component.adapter.base.ViewHolder;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.StringUtil;


import java.util.List;

/**
 * 委托记录adapter
 */
public class DelegateRecordAdapter extends CommonAdapter<Transaction> {
    public DelegateRecordAdapter(int layoutId, List<Transaction> list) {
        super(layoutId, list);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Transaction item, int position) {

        LinearLayout ll_record_shade = viewHolder.getView(R.id.ll_record_shade);
        ShadowDrawable.setShadowDrawable(ll_record_shade,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 4),
                ContextCompat.getColor(context, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(context, 10),
                0,
                DensityUtil.dp2px(context, 2));

        CircleImageView imageView = viewHolder.getView(R.id.iv_total_delegate);
        changeImageViewIcon(context, imageView, item);

        viewHolder.setText(R.id.tv_name_node, item.getNodeName());//委托节点名称
        viewHolder.setText(R.id.tv_address_node, AddressFormatUtil.formatTransactionAddress(item.getNodeId())); //节点地址
        TextView tv_number = viewHolder.getView(R.id.tv_number);
        showDelegateNumber(context, item, tv_number);
        TextView tv = viewHolder.getView(R.id.tv_state);
        changeTextStateAndColor(context, tv, item);

        viewHolder.setText(R.id.tv_delegate_time, String.format("#%s", DateUtil.format(item.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND)));

        CircleImageView walletIcon = viewHolder.getView(R.id.iv_wallet);
        walletIcon.setImageResource(RUtils.drawable(item.getWalletIcon()));
        viewHolder.setText(R.id.tv_wallet_name, item.getWalletName());
        viewHolder.setText(R.id.tv_wallet_address, context.getString(R.string.delegate_record_wallet_address, AddressFormatUtil.formatTransactionAddress(item.getFrom())));//钱包名称+钱包地址
    }

    private void changeImageViewIcon(Context context, ImageView iv, Transaction model) {
        switch (model.getTxType()) {
            case DELEGATE:
                iv.setImageResource(R.drawable.icon_delegate);
                break;
            case UNDELEGATE:
                iv.setImageResource(R.drawable.icon_undelegate);
                break;
            default:
                break;
        }

    }

    private void changeTextStateAndColor(Context context, TextView tv, Transaction model) {
        switch (model.getTxType()) {
            case DELEGATE:   //发起委托(委托)
                if (model.getTxReceiptStatus() == TransactionStatus.SUCCESSED) {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
                    tv.setText(context.getString(R.string.delegate_state_success));
                } else if (model.getTxReceiptStatus() == TransactionStatus.PENDING) {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_616464));
                    tv.setText(context.getString(R.string.delegate_pending));
                } else {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_f5302c));
                    tv.setText(context.getString(R.string.delegate_state_failed));
                }


                break;
            case UNDELEGATE: //减持/撤销委托(赎回委托)
                if (model.getTxReceiptStatus() == TransactionStatus.SUCCESSED) {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
                    tv.setText(context.getString(R.string.withdraw_success));
                } else if (model.getTxReceiptStatus() == TransactionStatus.PENDING) {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_616464));
                    tv.setText(context.getString(R.string.withdraw_pending));
                } else {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_f5302c));
                    tv.setText(context.getString(R.string.withdraw_failed));
                }

                break;
            default:
                break;
        }

    }


    private void showDelegateNumber(Context context, Transaction bean, TextView textView) {
        switch (bean.getTxType()) {
            /**
             * ",//交易数量，单位von
             * 详细描述：txType = 1004(委托数量)
             * 详细描述：txType = 1000(质押数量),1001(质押数量),1002(增加质押数量)
             */

            case CREATE_VALIDATOR:
            case EDIT_VALIDATOR:
            case INCREASE_STAKING:
            case DELEGATE:
                String amount = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(bean.getValue(), "1E18"));
                textView.setText(context.getString(R.string.amount_with_unit, StringUtil.formatBalance(NumberParserUtils.parseDouble(amount), false)));
                break;

            case EXIT_VALIDATOR:  //质押金额 txType = 1003(退回数量)
                //这种这里可以不用管
                break;
            case UNDELEGATE: ////赎回金额 txType = 1005(赎回数量)
                String number = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(bean.getUnDelegation(), "1E18"));
                textView.setText(context.getString(R.string.amount_with_unit, StringUtil.formatBalance(NumberParserUtils.parseDouble(number), false)));
                break;
            default:
                break;

        }


    }

}

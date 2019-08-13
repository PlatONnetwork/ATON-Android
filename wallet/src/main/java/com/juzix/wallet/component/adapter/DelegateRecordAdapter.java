package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.DelegateRecord;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.StringUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 委托记录adapter
 */
public class DelegateRecordAdapter extends CommonAdapter<Transaction> {
//    private static final String CONFIRM = "confirm";
//    private static final String DELEGATESUCC = "delegateSucc";
//    private static final String DELEGATEFAIL = "delegateFail";
//    private static final String REDEEM = "redeem";
//    private static final String REDEEMSUCC = "redeemSucc";
//    private static final String REDEEMFAIL = "redeemFail";

    public DelegateRecordAdapter(int layoutId, List<Transaction> list) {
        super(layoutId, list);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Transaction item, int position) {
        CircleImageView imageView = viewHolder.getView(R.id.iv_total_delegate);
        GlideUtils.loadRound(context, item.getUrl(), imageView);
        viewHolder.setText(R.id.tv_name_node, item.getNodeName());//委托节点名称
        viewHolder.setText(R.id.tv_address_node, AddressFormatUtil.formatAddress(item.getNodeId())); //节点地址
        TextView tv_number = viewHolder.getView(R.id.tv_number);
        showDelegateNumber(context, item, tv_number);
//        viewHolder.setText(R.id.tv_number, context.getString(R.string.amount_with_unit, StringUtil.formatBalance(item.getNumber(), false))); //显示数量


//        viewHolder.setText(R.id.tv_state, item.getDelegateStatus()); //状态
        TextView tv = viewHolder.getView(R.id.tv_state);
//        changeTextColor(context, tv, item.getDelegateStatus());
        changeTextStateAndColor(context, tv, item);

//        viewHolder.setText(R.id.tv_delegate_time, DateUtil.format(NumberParserUtils.parseLong(item.getDelegateTime()), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND)); //委托时间
        viewHolder.setText(R.id.tv_delegate_time, DateUtil.format(item.getTimestamp(), DateUtil.DATETIME_FORMAT_PATTERN));

        CircleImageView walletIcon = viewHolder.getView(R.id.iv_wallet);
        walletIcon.setImageResource(RUtils.drawable(item.getWalletIcon()));
        viewHolder.setText(R.id.tv_wallet_address, item.getWalletName() + (AddressFormatUtil.formatAddress(item.getFrom())));//钱包名称+钱包地址
    }

//    private void changeTextColor(Context context, TextView tv, String delegateStatus) {
//        switch (delegateStatus) {
//            case CONFIRM:
//            case REDEEM:
//                tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4a90e2));
//                break;
//            case DELEGATESUCC:
//            case REDEEMSUCC:
//                tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
//                break;
//            case DELEGATEFAIL:
//            case REDEEMFAIL:
//                tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_f5302c));
//                break;
//            default:
//                break;
//        }
//    }

    private void changeTextStateAndColor(Context context, TextView tv, Transaction model) {
        switch (model.getTxType()) {
            case DELEGATE:   //发起委托(委托)
                if (model.getTxReceiptStatus() == TransactionStatus.SUCCESSED) {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
                    tv.setText(context.getString(R.string.delegate_state_success));
                } else if (model.getTxReceiptStatus() == TransactionStatus.PENDING) {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4a90e2));
                    tv.setText(context.getString(R.string.delegate_pending));
                } else {
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_f5302c));
                    tv.setText(context.getString(R.string.delegate_state_failed));
                }


                break;
            case UNDELEGATE: //减持/撤销委托(赎回委托)
                if (TextUtils.equals(model.getRedeemStatus(), "1")) { //1： 赎回中
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4a90e2));
                    tv.setText(context.getString(R.string.withdraw_undelegating));
                } else if (TextUtils.equals(model.getRedeemStatus(), "0")) {//2：赎回成功
                    tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
                    tv.setText(context.getString(R.string.withdraw_success));
                } else {

                    if (model.getTxReceiptStatus() == TransactionStatus.PENDING) {
                        tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4a90e2));
                        tv.setText(context.getString(R.string.withdraw_pending));
                    } else if (model.getTxReceiptStatus() == TransactionStatus.FAILED) {
                        tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_f5302c));
                        tv.setText(context.getString(R.string.withdraw_failed));
                    } else {
                        //可以不做处理
                    }


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

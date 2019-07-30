package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.DelegateRecord;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.StringUtil;

import java.util.List;

/**
 * 委托记录adapter
 */
public class DelegateRecordAdapter extends CommonAdapter<DelegateRecord> {
    private static final String CONFIRM = "confirm";
    private static final String DELEGATESUCC = "delegateSucc";
    private static final String DELEGATEFAIL = "delegateFail";
    private static final String REDEEM = "redeem";
    private static final String REDEEMSUCC = "redeemSucc";
    private static final String REDEEMFAIL = "redeemFail";

    public DelegateRecordAdapter(int layoutId, List<DelegateRecord> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, DelegateRecord item, int position) {
        CircleImageView imageView = viewHolder.getView(R.id.iv_total_delegate);
        GlideUtils.loadRound(context, item.getUrl(), imageView);
        viewHolder.setText(R.id.tv_name_node, item.getNodeName());
        viewHolder.setText(R.id.tv_address_node, AddressFormatUtil.formatAddress(item.getNodeAddress()));
        viewHolder.setText(R.id.tv_number, context.getString(R.string.amount_with_unit, StringUtil.formatBalance(item.getNumber(), false)));
        viewHolder.setText(R.id.tv_state, item.getDelegateStatus());
        TextView tv = viewHolder.getView(R.id.tv_state);
        changeTextColor(context, tv, item.getDelegateStatus());
        viewHolder.setText(R.id.tv_delegate_time, DateUtil.format(NumberParserUtils.parseLong(item.getDelegateTime()), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND));
        CircleImageView walletIcon = viewHolder.getView(R.id.iv_wallet);
        walletIcon.setImageResource(RUtils.drawable(item.getWalletIcon()));
        viewHolder.setText(R.id.tv_wallet_address, item.getWalletName() + (AddressFormatUtil.formatAddress(item.getWalletAddress())));
    }

    private void changeTextColor(Context context, TextView tv, String delegateStatus) {
        switch (delegateStatus) {
            case CONFIRM:
            case REDEEM:
                tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4a90e2));
                break;
            case DELEGATESUCC:
            case REDEEMSUCC:
                tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
                break;
            case DELEGATEFAIL:
            case REDEEMFAIL:
                tv.setTextColor(ContextCompat.getColorStateList(context, R.color.color_f5302c));
                break;
            default:
                break;

        }

    }
}

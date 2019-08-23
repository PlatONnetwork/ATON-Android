package com.juzix.wallet.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.StringUtil;

import java.util.List;

public class ValidatorsAdapter extends CommonAdapter<VerifyNode> {
    private static final String ACTIVE = "Active";
    private static final String CANDIDATE = "Candidate";


    public ValidatorsAdapter(int layoutId, List<VerifyNode> datas) {
        super(layoutId, datas);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void convert(Context context, ViewHolder viewHolder, VerifyNode item, int position) {
        CircleImageView imageView = viewHolder.getView(R.id.iv_url);
        GlideUtils.loadRound(context, item.getUrl(), imageView);
        viewHolder.setText(R.id.tv_validators_node_name, item.getName());
        viewHolder.setText(R.id.tv_validators_node_state, item.getNodeStatus());

//        viewHolder.setText(R.id.tv_yield, ((NumberParserUtils.parseDouble(item.getRatePA())) / 100) + "%");
        TextView tv_yield = viewHolder.getView(R.id.tv_yield);
        isShowRA(context, item, tv_yield);

        viewHolder.setText(R.id.tv_staked_money, context.getString(R.string.amount_with_unit, StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(item.getDeposit(), "1E18"))), false)));
//        viewHolder.setText(R.id.tv_validators_rank, item.getRanking() + "");
        TextView tv = viewHolder.getView(R.id.tv_validators_rank);
        tv.setText(item.getRanking() + "");
        changeTextFontSize(context, tv, item);
        ImageView iv = viewHolder.getView(R.id.iv_rank_bg);
        changeImageViewBg(context, iv, item.getRanking());
        TextView tv_state = viewHolder.getView(R.id.tv_validators_node_state);
        changeTextBgAndTextColor(context, tv_state, item.getNodeStatus());

    }

    private void isShowRA(Context context, VerifyNode item, TextView tv_yield) {
        if (!item.isInit()) {
            tv_yield.setText(((NumberParserUtils.parseDouble(item.getRatePA())) / 100) + "%");
        } else {
            tv_yield.setText("— —");
        }

    }

    private void changeTextFontSize(Context context, TextView tv, VerifyNode item) {
        if (item.getRanking() >= Constants.Magnitudes.THOUSAND) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        }

    }

    public void changeImageViewBg(Context context, ImageView iv, int rank) {
        if (rank == 1) {
            iv.setImageResource(R.drawable.icon_rank1);
        } else if (rank == 2) {
            iv.setImageResource(R.drawable.icon_rank2);
        } else if (rank == 3) {
            iv.setImageResource(R.drawable.icon_rank3);
        } else {
            iv.setImageResource(R.drawable.icon_rank4);
        }

    }

    public void changeTextBgAndTextColor(Context context, TextView textView, String state) {
        switch (state) {
            case ACTIVE:
                textView.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4a90e2));
                textView.setBackgroundResource(R.drawable.bg_validators_state);
                break;
            case CANDIDATE:
                textView.setTextColor(ContextCompat.getColorStateList(context, R.color.color_19a20e));
                textView.setBackgroundResource(R.drawable.bg_validators_cadidate);
                break;
            default:
                break;
        }

    }


}

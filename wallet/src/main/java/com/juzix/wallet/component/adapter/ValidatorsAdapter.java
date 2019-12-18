package com.juzix.wallet.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.StringUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ValidatorsAdapter extends CommonAdapter<VerifyNode> {
    private static final String ACTIVE = "Active";
    private static final String CANDIDATE = "Candidate";


    public ValidatorsAdapter(int layoutId, List<VerifyNode> datas) {
        super(layoutId, datas);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void convert(Context context, ViewHolder viewHolder, VerifyNode item, int position) {

        RelativeLayout rl_shade = viewHolder.getView(R.id.rl_shade);

        ShadowDrawable.setShadowDrawable(rl_shade,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 4),
                ContextCompat.getColor(context, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(context, 5),
                0,
                DensityUtil.dp2px(context, 0));

        GlideUtils.loadRound(context, item.getUrl(), viewHolder.getView(R.id.iv_url));
        viewHolder.setText(R.id.tv_validators_node_name, item.getName());
        TextView tv_status = viewHolder.getView(R.id.tv_validators_node_state);
        showState(context, item, tv_status);

        TextView tv_yield = viewHolder.getView(R.id.tv_yield);
        isShowRA(item, tv_yield);

        viewHolder.setText(R.id.tv_staked_money, context.getString(R.string.amount_with_unit, StringUtil.formatBalance(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(item.getDeposit(), "1E18")))));
        TextView tv = viewHolder.getView(R.id.tv_validators_rank);
        tv.setText(String.format("%d", item.getRanking()));
        changeTextFontSize(tv, item);
        changeImageViewBg(tv, item.getRanking());
        TextView tv_state = viewHolder.getView(R.id.tv_validators_node_state);
        changeTextBgAndTextColor(context, tv_state, item.getNodeStatus());

    }

    private void showState(Context context, VerifyNode item, TextView tv_status) {
        if (Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(App.getContext()).getLanguage())) { //中文环境下
            if (TextUtils.equals(item.getNodeStatus(), ACTIVE)) {
                tv_status.setText(item.isConsensus() ? R.string.validators_verifying : R.string.validators_active);
            } else {
                tv_status.setText(R.string.validators_candidate);
            }

        } else {
            tv_status.setText(item.getNodeStatus());
        }
    }

    private void isShowRA(VerifyNode item, TextView tv_yield) {
        if (!item.isInit()) {
            tv_yield.setText(NumberFormat.getInstance().format(((NumberParserUtils.parseDouble(item.getRatePA())) / 100)) + "%");
        } else {
            tv_yield.setText("- -");
        }

    }

    private void changeTextFontSize(TextView tv, VerifyNode item) {
        if (item.getRanking() >= Constants.Magnitudes.THOUSAND) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        }

    }

    public void changeImageViewBg(TextView tv, int rank) {
        if (rank == 1) {
            tv.setBackgroundResource(R.drawable.icon_rank_first);
        } else if (rank == 2) {
            tv.setBackgroundResource(R.drawable.icon_rank_second);
        } else if (rank == 3) {
            tv.setBackgroundResource(R.drawable.icon_rank_third);
        } else {
            tv.setBackgroundResource(R.drawable.icon_rank_others);
        }

    }

    public void changeTextBgAndTextColor(Context context, TextView textView, String state) {
        switch (state) {
            case ACTIVE:
                textView.setTextColor(ContextCompat.getColorStateList(context, R.color.color_4A90E2));
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

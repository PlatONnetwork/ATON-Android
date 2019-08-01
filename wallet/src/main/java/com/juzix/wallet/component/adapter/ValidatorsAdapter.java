package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.StringUtil;

import java.util.List;

public class ValidatorsAdapter extends CommonAdapter<VerifyNode> {
    private static final String ACTIVE = "Active";
    private static final String CANDIDATE = "Candidate";


    public ValidatorsAdapter(int layoutId, List<VerifyNode> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, VerifyNode item, int position) {
        CircleImageView imageView = viewHolder.getView(R.id.iv_url);
        GlideUtils.loadRound(context, item.getUrl(), imageView);
        viewHolder.setText(R.id.tv_validators_node_name, item.getName());
        viewHolder.setText(R.id.tv_validators_node_state, item.getNodeStatus());
        viewHolder.setText(R.id.tv_yield, item.getRatePA() + "%");
        viewHolder.setText(R.id.tv_staked_money, context.getString(R.string.amount_with_unit, StringUtil.formatBalance(Double.parseDouble(item.getDeposit()), false)));
        viewHolder.setText(R.id.tv_validators_rank, item.getRanking() + "");
        ImageView iv = viewHolder.getView(R.id.iv_rank_bg);
        changeImageViewBg(context, iv, item.getRanking());
        TextView tv_state = viewHolder.getView(R.id.tv_validators_node_state);
        changeTextBgAndTextColor(context, tv_state, item.getNodeStatus());
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

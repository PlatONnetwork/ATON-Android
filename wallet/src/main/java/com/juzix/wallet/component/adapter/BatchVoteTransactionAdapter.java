package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.VotedCandidate;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class BatchVoteTransactionAdapter extends CommonAdapter<VotedCandidate> {

    private OnItemVoteClickListener mListener;

    public void setOnItemVoteClickListener(OnItemVoteClickListener listener) {
        this.mListener = listener;
    }

    public BatchVoteTransactionAdapter(int layoutId, List<VotedCandidate> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, VotedCandidate item, int position) {

        if (TextUtils.isEmpty(item.getCountryCode())) {
            viewHolder.setText(R.id.tv_location, context.getString(R.string.unknownRegion));
        } else {
            viewHolder.setText(R.id.tv_location, String.format("(%s)", item.getCountryName(context)));
        }
        viewHolder.setText(R.id.tv_node_name, item.getName()); //节点名称
        viewHolder.setText(R.id.tv_valid_invalid_ticket, String.format("%s/%s", NumberParserUtils.getPrettyNumber(item.getValidNum(), 0), NumberParserUtils.getPrettyNumber(BigDecimalUtil.sub(NumberParserUtils.parseDouble(item.getTotalTicketNum()), NumberParserUtils.parseDouble(item.getValidNum())), 0)));
        viewHolder.setText(R.id.tv_vote_staked, NumberParserUtils.getPrettyNumber(item.getLocked(), 0));
        viewHolder.setText(R.id.tv_vote_staked_desc, String.format("%s(Energon)", context.getString(R.string.lockVote)));
        viewHolder.setText(R.id.tv_vote_profit, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(item.getEarnings(), "1E18"), 4));
        viewHolder.setText(R.id.tv_vote_profit_desc, String.format("%s(Energon)", context.getString(R.string.votingIncome)));

        TextView textView = viewHolder.getView(R.id.rtv_vote);//投票按钮
        textView.setEnabled(item.getIsValid().equals(Constants.VoteConstants.IS_VALID) ? false : true);//设置按钮是否可点击

        RxView.clicks(viewHolder.getView(R.id.rtv_vote))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (mListener != null) {
                            mListener.onItemVoteClick(item.getNodeId(), item.getName());
                        }
                    }
                });
    }

    @Override
    public boolean isEmpty() {
        return getList() != null && getList().isEmpty();
    }

    public interface OnItemVoteClickListener {

        void onItemVoteClick(String candidateId, String nodeName);
    }
}

package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.ClickTransformer;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;
import com.juzix.wallet.entity.BatchVoteTransactionWrapEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class BatchVoteTransactionAdapter extends CommonAdapter<BatchVoteTransactionWrapEntity> {

    private OnItemVoteClickListener mListener;

    public void setOnItemVoteClickListener(OnItemVoteClickListener listener) {
        this.mListener = listener;
    }

    public BatchVoteTransactionAdapter(int layoutId, List<BatchVoteTransactionWrapEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, BatchVoteTransactionWrapEntity item, int position) {
        BatchVoteTransactionEntity batchVoteTransactionEntity = item.getBatchVoteTransactionEntity();
        if (batchVoteTransactionEntity.getRegionEntity() == null || TextUtils.isEmpty(batchVoteTransactionEntity.getRegionEntity().getCountryPinyin())) {
            viewHolder.setText(R.id.tv_location, context.getString(R.string.unknownRegion));
        } else {
            viewHolder.setText(R.id.tv_location, batchVoteTransactionEntity.getRegionEntity().getCountryPinyin());
        }
        viewHolder.setText(R.id.tv_node_name, batchVoteTransactionEntity.getNodeName());
        viewHolder.setText(R.id.tv_valid_invalid_ticket, String.format("%s/%s", NumberParserUtils.getPrettyNumber(batchVoteTransactionEntity.getValidNum(),0), NumberParserUtils.getPrettyNumber(BigDecimalUtil.sub(NumberParserUtils.parseDouble(batchVoteTransactionEntity.getTotalTicketNum()), NumberParserUtils.parseDouble(batchVoteTransactionEntity.getValidNum())), 0)));
        viewHolder.setText(R.id.tv_vote_staked, NumberParserUtils.getPrettyNumber(batchVoteTransactionEntity.getVoteStaked(), 0));
        viewHolder.setText(R.id.tv_vote_staked_desc, String.format("%s(Energon)", context.getString(R.string.lockVote)));
        viewHolder.setText(R.id.tv_vote_profit, batchVoteTransactionEntity.getShowEarnings());
        viewHolder.setText(R.id.tv_vote_profit_desc, String.format("%s(Energon)", context.getString(R.string.votingIncome)));

        RxView.clicks(viewHolder.getView(R.id.rtv_vote))
                .compose(new ClickTransformer())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (mListener != null) {
                            mListener.onItemVoteClick(batchVoteTransactionEntity.getCandidateId());
                        }
                    }
                });
    }

    @Override
    public boolean isEmpty() {
        return getList() != null && getList().isEmpty();
    }

    public interface OnItemVoteClickListener {

        void onItemVoteClick(String candidateId);
    }
}

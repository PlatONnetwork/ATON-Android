package com.juzix.wallet.component.adapter;

import android.content.Context;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.VoteSummaryEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class BatchVoteSummaryAdapter extends CommonAdapter<VoteSummaryEntity> {

    public BatchVoteSummaryAdapter(int layoutId, List<VoteSummaryEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, VoteSummaryEntity item, int position) {
        if (item != null) {
            viewHolder.setText(R.id.tv_key, item.getVoteSummaryDesc());
            viewHolder.setText(R.id.tv_value, item.getVoteSummaryValue());
        }
    }


}

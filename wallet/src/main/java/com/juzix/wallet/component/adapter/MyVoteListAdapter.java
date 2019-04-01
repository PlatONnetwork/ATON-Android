package com.juzix.wallet.component.adapter;

import android.content.Context;

import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;

import java.util.List;

/**
 * @author matrixelement
 */
public class MyVoteListAdapter extends CommonAdapter<List<BatchVoteTransactionEntity>> {

    public MyVoteListAdapter(int layoutId, List<List<BatchVoteTransactionEntity>> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, List<BatchVoteTransactionEntity> item, int position) {
        if (item != null && !item.isEmpty()) {
            BatchVoteTransactionEntity batchVoteTransactionEntity = item.get(position);
        }
    }
}

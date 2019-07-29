package com.juzix.wallet.component.adapter;

import android.content.Context;

import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.DelegateRecord;

import java.util.List;

/**
 * 委托记录adapter
 */
public class DelegateRecordAdapter  extends CommonAdapter<DelegateRecord> {

    public DelegateRecordAdapter(int layoutId, List<DelegateRecord> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, DelegateRecord item, int position) {

    }
}

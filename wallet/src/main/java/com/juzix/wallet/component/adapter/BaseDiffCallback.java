package com.juzix.wallet.component.adapter;

import android.support.v7.util.DiffUtil;

import java.util.List;

public abstract class BaseDiffCallback<T> extends DiffUtil.Callback {

    protected List<T> mOldList;
    protected List<T> mNewList;

    public BaseDiffCallback(List<T> oldList, List<T> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        if (mOldList != null) {
            return mOldList.size();
        }
        return 0;
    }

    @Override
    public int getNewListSize() {
        if (mNewList != null) {
            return mNewList.size();
        }
        return 0;
    }
}

package com.platon.aton.component.adapter.base;


import android.content.Context;


public interface ItemViewDelegate<T> {

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(T item, int position);

    public abstract void convert(Context context, ViewHolder holder, T t, int position);


}

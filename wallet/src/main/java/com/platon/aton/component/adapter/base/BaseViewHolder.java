package com.platon.aton.component.adapter.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.ActivityManager;
import com.platon.framework.base.BaseActivity;

/**
 * @author ziv
 * date On 2019/6/8
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    /**
     * 用于存储当前item当中的View
     */
    private SparseArray<View> mViews;
    public Context mContext;
    private OnItemClickListener mItemClickListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
    }

    public BaseViewHolder(int viewId, ViewGroup parent) {
        super(((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(viewId, parent, false));
        mContext = parent.getContext();
    }

    public void refreshData(final T data, final int position) {

        RxView
                .clicks(itemView)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(data);
                        }
                    }
                });
    }


    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void updateItem(Bundle bundle) {

    }


    public interface OnItemClickListener<T> {

        void onItemClick(T t);

    }
}

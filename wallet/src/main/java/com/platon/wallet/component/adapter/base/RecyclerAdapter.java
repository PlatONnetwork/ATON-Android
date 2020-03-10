package com.platon.wallet.component.adapter.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecycleHolder> {

    protected Context mContext;
    protected List<T> mDatas;
    protected int mLayoutId;
    protected LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public RecyclerAdapter(Context mContext, List<T> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public RecyclerAdapter(Context mContext, List<T> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecycleHolder(mInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, int position) {
        convert(holder, mDatas.get(position), position);
        if (onItemClickListener != null) {
            //设置背景
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                    onItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //更新局部控件
            updateItem(holder, mDatas.get(position), position);
        }
    }

    public abstract void convert(RecycleHolder holder, T data, int position);

    /**
     * 更新局部控件
     *
     * @param holder
     * @param position
     */
    public void updateItem(RecycleHolder holder, T data, int position) {

    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void notifyItemChanged(T t, @Nullable Object payload) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }

        if (!mDatas.contains(t)) {
            return;
        }

        int position = mDatas.indexOf(t);
        mDatas.set(position, t);
        notifyItemChanged(position, payload);
    }

    public void notifyDataSetChanged(List<T> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

   public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

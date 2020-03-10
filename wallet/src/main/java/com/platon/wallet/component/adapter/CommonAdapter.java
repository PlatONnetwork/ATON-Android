package com.platon.wallet.component.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.platon.wallet.component.adapter.base.ItemViewDelegate;
import com.platon.wallet.component.adapter.base.MultiItemTypeAdapter;
import com.platon.wallet.component.adapter.base.ViewHolder;

import java.util.List;

public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {

    public CommonAdapter(final int layoutId, List<T> datas) {
        super(datas);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(Context context, ViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(context, holder, t, position);
            }
        });
    }

    public List<T> getList() {
        return mDatas;
    }

    @Override
    protected abstract void convert(Context context, ViewHolder viewHolder, T item, int position);

    /**
     * 调用一次getView()方法；Google推荐的做法
     * listview局部刷新
     * @param context
     * @param listView
     * @param t
     */
    public final void updateItem(Context context, ListView listView, T t) {
        if (listView != null && t != null) {

            if (mDatas == null) {
                return;
            }

            int pos = mDatas.indexOf(t);

            if (pos == -1) {
                return;
            }

            mDatas.set(pos, t);
            //获取第一个显示的item
            int visiblePos = listView.getFirstVisiblePosition();
            int headViewCount = listView.getHeaderViewsCount();
            //计算出当前选中的position和第一个的差，也就是当前在屏幕中的item位置
            int offset = pos + headViewCount - visiblePos;
            int lenth = listView.getChildCount();
            // 只有在可见区域才更新,因为不在可见区域得不到Tag,会出现空指针,所以这是必须有的一个步骤
            if ((offset < 0) || (offset >= lenth)) {
                return;
            }

            View convertView = listView.getChildAt(offset);

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

            updateItemView(context, pos, viewHolder);
        }

    }

    public void updateItemView(Context context, int position, ViewHolder viewHolder) {

    }

}

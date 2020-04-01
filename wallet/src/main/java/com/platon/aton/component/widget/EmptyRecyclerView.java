package com.platon.aton.component.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            if (adapter.getItemCount() == 0) {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(VISIBLE);
                }
                EmptyRecyclerView.this.setVisibility(GONE);
            } else {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(GONE);
                }
                EmptyRecyclerView.this.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onChanged();
        }

    };

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEmptyView(View view) {
        setEmptyView(view, 0);
    }

    public void setEmptyView(View view, int marginTop) {
        setEmptyView(view, marginTop, 0);
    }

    public void setEmptyView(View view, int marginTop, int marginBottom) {
        this.mEmptyView = view;
        //加入主界面布局
        if (this.getParent() instanceof FrameLayout) {
            ((ViewGroup) this.getParent()).addView(mEmptyView);
        } else {
            ((ViewGroup) this.getRootView()).addView(mEmptyView);
        }
        MarginLayoutParams layoutParams = (MarginLayoutParams) mEmptyView.getLayoutParams();
        layoutParams.height = MarginLayoutParams.MATCH_PARENT;
        layoutParams.width = MarginLayoutParams.MATCH_PARENT;
        layoutParams.topMargin = marginTop;
        layoutParams.bottomMargin = marginBottom;
        mEmptyView.setLayoutParams(layoutParams);
        mEmptyView.setVisibility(GONE);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mObserver);
    }
}

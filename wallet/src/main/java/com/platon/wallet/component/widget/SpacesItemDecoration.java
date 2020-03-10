package com.platon.wallet.component.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author matrixelement
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int horizontalSpace;
    private int verticalSpace;

    public SpacesItemDecoration(int horizontalSpace, int verticalSpace) {
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        outRect.left = horizontalSpace;
        outRect.right = horizontalSpace;
        outRect.bottom = verticalSpace;

        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = gridLayoutManager.getSpanCount();
        // Add top margin only for the first item to avoid double space between items
        int position = parent.getChildLayoutPosition(view);
        //第几行
        int rowPosition = position / spanCount;
        //第几列
        //第0行，top为0
        if (rowPosition == 0) {
            outRect.top = 0;
        } else {
            outRect.top = verticalSpace;
        }

    }
}

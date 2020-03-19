package com.platon.aton.component.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.platon.aton.R;
import com.platon.aton.utils.DensityUtil;


/**
 * @author matrixelement
 */
public class NodeListDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private Context mContext;
    private int mSize;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;

    public NodeListDecoration(Context context) {
        this.mContext = context;
        this.mSize = DensityUtil.dp2px(mContext, 1f);
        this.mDivider = new ColorDrawable(ContextCompat.getColor(context, R.color.color_e4e7f3));
    }

    public NodeListDecoration(Context context, int paddingLeft, int paddingTop, int paddingRignt, int paddingBottom) {
        this.mContext = context;
        this.mSize = DensityUtil.dp2px(mContext, 1f);
        this.mDivider = new ColorDrawable(ContextCompat.getColor(context, R.color.color_e4e7f3));
        this.mPaddingLeft = paddingLeft;
        this.mPaddingTop = paddingTop;
        this.mPaddingRight = paddingRignt;
        this.mPaddingBottom = paddingBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mSize);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        super.onDraw(c, parent, state);
        int top;
        int bottom;
        int left = parent.getPaddingLeft() + mPaddingLeft;
        int right = parent.getWidth() - parent.getPaddingRight() - mPaddingRight;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getBottom() + params.bottomMargin + mPaddingTop;
            bottom = top + mSize + mPaddingBottom;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}

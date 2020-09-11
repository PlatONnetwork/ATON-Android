package com.platon.aton.component.adapter.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CommonSidebarItemDecoration extends RecyclerView.ItemDecoration {

    private static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;//水平方向
    private static final int VERTICAL = LinearLayoutManager.VERTICAL;//垂直方向
    private int orientation;//方向
    private int decoration = 0;//边距大小 px
    private  int lineSize ;//分割线厚度
    private  ColorDrawable mDivider;

    public CommonSidebarItemDecoration(@LinearLayoutCompat.OrientationMode int orientation, int decoration, int lineSize) {
       this.orientation = orientation;
       this.decoration = decoration;
       this.lineSize = lineSize;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        final int lastPosition = state.getItemCount() - 1;//整个RecyclerView最后一个item的position
        final int current = parent.getChildLayoutPosition(view);//获取当前要进行布局的item的position
        if (current == -1) return;//holder出现异常时，可能为-1
        if(layoutManager instanceof LinearLayoutManager){
            if(orientation == LinearLayoutManager.VERTICAL){//垂直
                outRect.set(0,decoration,0,0);
                /*if(current == lastPosition){//判断是否是最后一个item
                   outRect.set(0,0,0,0);
                }*/
            }else{//水平


            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if(orientation == LinearLayoutManager.VERTICAL){//垂直
            drawHorizontalLines(c, parent);

        }else{//水平

        }
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    //绘制垂直布局，水平分割线
    private void drawHorizontalLines(Canvas c, RecyclerView parent) {
         int itemCount = parent.getChildCount();
         int left = parent.getPaddingLeft();
         int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < itemCount; i++) {
            View child = parent.getChildAt(i);
            if(child == null) return;
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int bottom = child.getTop() + params.topMargin;
            int top = bottom + lineSize;
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }


    /**
     * 绘制文字
     */
    private void drawTitle(Canvas c, int left, int right, View child, RecyclerView.LayoutParams layoutParams, String titleText) {
        //绘制背景色
       /* mPaint.setColor(COLOR_BG);
        c.drawRect(left, child.getTop() - layoutParams.topMargin - mTitleHeight, right, child.getTop() - layoutParams.topMargin, mPaint);
        //绘制文字，没有使用计算baseline的方式
        mPaint.setColor(COLOR_FONT);
        mPaint.getTextBounds(titleText, 0, titleText.length(), textBound);
        c.drawText(titleText, child.getPaddingLeft(), child.getTop() - layoutParams.topMargin - (mTitleHeight / 2 - textBound.height() / 2), mPaint);*/
    }


}

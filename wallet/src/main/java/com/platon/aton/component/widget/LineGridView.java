package com.platon.aton.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.platon.aton.R;
import com.platon.aton.utils.DensityUtil;

public class LineGridView extends GridView {

    private int mLinePaddingHorizontal;
    private int mLinePaddingVertical;
    private int mLineSize;
    private int mWidth;
    private Paint localPaint;
    public LineGridView(Context context) {
        this(context, null, 0);
    }

    public LineGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        localPaint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineGridView, defStyle, 0);
        mLinePaddingHorizontal = typedArray.getDimensionPixelSize(R.styleable.LineGridView_line_padding_horizontal, 0);
        mLinePaddingVertical = typedArray.getDimensionPixelSize(R.styleable.LineGridView_line_padding_vertical, 0);
        mLineSize = typedArray.getDimensionPixelSize(R.styleable.LineGridView_line_size, DensityUtil.dp2px(context, 1f));

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
//        View localView = getChildAt(0);

        View localView = getChildAt(0);

//        Log.d("LineGridView","当前view宽度"+localView.getWidth());
//        Log.d("LineGridView","当前view2宽度"+localView2.getWidth());

        if(localView==null){
            return;
        }

        //列数
        int column = mWidth / localView.getWidth();
        if (column == 0) {
            return;
        }
        //行数
        int row = getChildCount() % column == 0 ? getChildCount() / column : getChildCount() / column + 1;
        int childCount = getChildCount();

//        Paint localPaint;
//        localPaint = new Paint();

        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        localPaint.setStrokeWidth(mLineSize);
        localPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_e4e7f3));
        for (int i = 0; i < childCount; i++) {
            View cellView = getChildAt(i);
            //当前所在行
            int rowIndex = (i + 1) % column == 0 ? (i + 1) / column : (i + 1) / column + 1;
            //当前所在列
            //最后一列
            if ((i + 1) % column == 0) {
                //不是最后一行
                if (rowIndex != row) {
                    canvas.drawLine(cellView.getLeft() + mLinePaddingHorizontal, cellView.getBottom(), cellView.getRight() - mLinePaddingHorizontal, cellView.getBottom(), localPaint);
                }
            } else if ((i + 1) > (childCount - (childCount % column))) {
                //画竖直方向的分割线
                canvas.drawLine(cellView.getRight(), cellView.getTop() + mLinePaddingVertical, cellView.getRight(), cellView.getBottom() - mLinePaddingVertical, localPaint);
            } else {
                canvas.drawLine(cellView.getRight(), cellView.getTop() + mLinePaddingVertical, cellView.getRight(), cellView.getBottom() - mLinePaddingVertical, localPaint);
                if (rowIndex != row) {
                    canvas.drawLine(cellView.getLeft() + mLinePaddingHorizontal, cellView.getBottom(), cellView.getRight() - mLinePaddingHorizontal, cellView.getBottom(), localPaint);
                }
            }
        }
    }
}
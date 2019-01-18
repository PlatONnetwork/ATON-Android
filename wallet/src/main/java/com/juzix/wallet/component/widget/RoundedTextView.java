package com.juzix.wallet.component.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import com.juzix.wallet.R;


/**
 * 圆角TextView，可用于标签视图
 * <p>
 * 圆角边框颜色默认跟随文字颜色，边框大小跟随控件高度，框内默认无背景色
 *
 * @author matrixelement
 * @date 2016/12/28
 */

public class RoundedTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = RoundedTextView.class.getSimpleName();

    private static final int INVALID = -1;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    /**
     * 圆角半径 （默认自适应控件高度）
     */
    private int mCornerRadius;
    /**
     * 边框线的宽度（粗细
     */
    private int mBorderWidth;
    /**
     * 边框线的颜色（默认跟随文字颜色）
     */
    private ColorStateList mBorderColor;
    /**
     * 背景色
     */
    private ColorStateList mBackgroundColor;

    private boolean mAlreadyRender;
    
    private MyGradientDrawable mDrawable;

    public RoundedTextView(Context context) {
        this(context, null);
    }

    public RoundedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedTextView, defStyleAttr, 0);
        mCornerRadius = a.getDimensionPixelSize(R.styleable.RoundedTextView_rtv_corner_radius, INVALID);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundedTextView_rtv_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColorStateList(R.styleable.RoundedTextView_rtv_border_color);
        mBackgroundColor = a.getColorStateList(R.styleable.RoundedTextView_rtv_background_color);
        if (mBackgroundColor == null) {
            mBackgroundColor = ColorStateList.valueOf(DEFAULT_BACKGROUND_COLOR);
        }
        a.recycle();
    }


    /**
     * 边框线的宽度
     *
     * @param width
     */
    public void setRoundedBorderWidth(int width) {
        int lastBorderWidth = mBorderWidth;
        mBorderWidth = width;
        if (mAlreadyRender && lastBorderWidth != width) {
            applyParams();
        }
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setRoundedBorderColor(int color) {
        int lastColor = mBorderColor != null ? mBorderColor.getDefaultColor() : -1;
        mBorderColor = ColorStateList.valueOf(color);
        if (mAlreadyRender && lastColor != color) {
            applyParams();
        }
    }

    /**
     * 设置背景色颜色
     *
     * @param color
     */
    public void setRoundedBackgroundColor(int color) {
        int lastColor = mBackgroundColor != null ? mBackgroundColor.getDefaultColor() : -1;
        mBackgroundColor = ColorStateList.valueOf(color);
        if (mAlreadyRender && lastColor != color) {
            applyParams();
        }
    }

    /**
     * 设置圆角半径
     *
     * @param radius
     */
    public void setRoundedCornerRadius(int radius) {
        int lastRadius = mCornerRadius;
        mCornerRadius = radius;
        if (mAlreadyRender && lastRadius != radius) {
            applyParams();
        }
    }

    @Override
    public void setTextColor(int color) {
        int lastColor = getCurrentTextColor();
        super.setTextColor(color);
        if (mAlreadyRender && lastColor != color) {
            applyParams();
        }
    }

    @Override
    public void setTextSize(float size) {
        float lastSize = getTextSize();
        super.setTextSize(size);
        if (mAlreadyRender && lastSize != size) {
            applyParams();
        }
    }

    private void applyParams() {
        float radiusSize = (mCornerRadius != INVALID) ? mCornerRadius : getMeasuredHeight() / 2;
        ColorStateList color = (mBorderColor != null) ? mBorderColor : getTextColors();
        int borderWidth = mBorderWidth;

        if (mDrawable == null) {
            mDrawable = new MyGradientDrawable();
        }
        mDrawable.setupDrawable(radiusSize, borderWidth, color, mBackgroundColor);
        setBackgroundDrawable(mDrawable);
        handlePadding();
    }

    private void handlePadding() {
        int textSize = (int) getTextSize() / 2;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        if (left != textSize || right != textSize) {
            setPadding(textSize, top, textSize, bottom);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        // 当设置padding值后，控件的对齐会出现问题，目前在这里强制设置对齐方式
        handlePadding();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight > 0 && !mAlreadyRender) {
            mAlreadyRender = true;
            applyParams();
        }
    }

    class MyGradientDrawable extends GradientDrawable {

        private ColorStateList mBorderColor;     // 边框线的颜色（默认跟随文字颜色）
        private ColorStateList mBackgroundColor; // 背景色
        private int mBorderWidth;

        @Override
        public boolean isStateful() {
            // 该方法必须重写，当返回为true时，才会更新drawable的点击状态
            return super.isStateful() ||
                    (mBorderColor != null && mBorderColor.isStateful()) ||
                    (mBackgroundColor != null && mBackgroundColor.isStateful());
        }

        @Override
        protected boolean onStateChange(int[] stateSet) {
            updateDrawable();
            return super.onStateChange(stateSet);
        }

        public void setupDrawable(float radius, int borderWidth,
                                  ColorStateList borderColor, ColorStateList backgroundColor) {
            mBorderColor = borderColor;
            mBackgroundColor = backgroundColor;
            mBorderWidth = borderWidth;
            setCornerRadius(radius);
            updateDrawable();
        }

        private void updateDrawable() {
            if (mBorderColor != null) {
                setStroke(mBorderWidth, mBorderColor.getColorForState(getState(), mBorderColor.getDefaultColor()));
            }
            if (mBackgroundColor != null) {
                setColor(mBackgroundColor.getColorForState(getState(), mBackgroundColor.getDefaultColor()));
            }
        }
    }
}

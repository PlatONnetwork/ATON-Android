package com.juzix.wallet.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.juzix.wallet.R;
import com.juzix.wallet.utils.DensityUtil;

/**
 * @author matrixelement
 */
public class ShadowContainer extends LinearLayout {

    private int mXOffSet;
    private int mYOffSet;
    private int mBlurRadius;
    private int mEnableShadowColor;
    private int mDisableShadowColor;
    private int mShapeRadius;
    private boolean mEnabled = true;

    public ShadowContainer(Context context) {
        super(context, null);
    }

    public ShadowContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowButton, defStyleAttr, 0);

        mXOffSet = a.getDimensionPixelSize(R.styleable.ShadowButton_xOffset, 0);
        mYOffSet = a.getDimensionPixelSize(R.styleable.ShadowButton_yOffset, DensityUtil.dp2px(context, 2.0f));
        mBlurRadius = a.getDimensionPixelSize(R.styleable.ShadowButton_blurRadius, DensityUtil.dp2px(context, 6.0f));
        mEnableShadowColor = a.getColor(R.styleable.ShadowButton_enableShadowColor, ContextCompat.getColor(context, R.color.color_660051ff));
        mDisableShadowColor = a.getColor(R.styleable.ShadowButton_disableShadowColor, ContextCompat.getColor(context, R.color.color_66969696));
        mShapeRadius = DensityUtil.dp2px(context, 22f);
        mEnabled = a.getBoolean(R.styleable.ShadowButton_enabled, true);
        a.recycle();

        setEnabled(mEnabled);

        setPadding(mBlurRadius, mBlurRadius - mYOffSet, mBlurRadius, mBlurRadius + mYOffSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(mEnabled);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mEnabled = enabled;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enabled);
        }
        ShadowDrawable.setShadowDrawable(this,
                mShapeRadius,
                enabled ? mEnableShadowColor : mDisableShadowColor
                , mBlurRadius,
                mXOffSet,
                mYOffSet);
    }
}

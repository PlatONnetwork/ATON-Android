package com.platon.wallet.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.platon.wallet.R;
import com.platon.wallet.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

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
        setAllViewEnabled(mEnabled);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mEnabled = enabled;
        setAllViewEnabled(enabled);
        ShadowDrawable.setShadowDrawable(this,
                mShapeRadius,
                enabled ? mEnableShadowColor : mDisableShadowColor
                , mBlurRadius,
                mXOffSet,
                mYOffSet);
    }

    private void setAllViewEnabled(boolean enabled) {
        List<View> allchildren = getAllChildViews(this);
        for (int i = 0; i < allchildren.size(); i++) {
            allchildren.get(i).setEnabled(enabled);
        }
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }
}

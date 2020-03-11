package com.platon.aton.component.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.utils.DensityUtil;

/**
 * @author matrixelement
 */
public class PendingClaimRewardAnimationLayout extends LinearLayout {

    private ObjectAnimator objectAnimator1;
    private ObjectAnimator objectAnimator2;
    private ObjectAnimator objectAnimator3;

    public PendingClaimRewardAnimationLayout(Context context) {
        this(context, null);
    }

    public PendingClaimRewardAnimationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PendingClaimRewardAnimationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        setOrientation(HORIZONTAL);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        setGravity(Gravity.CENTER);
        addView(buildView(context));
        addView(buildView(context));
        addView(buildView(context));
    }

    private void startAnimation() {

        objectAnimator1 = ObjectAnimator.ofFloat(getChildAt(0), "alpha", 1.0f, 0.38f, 0.08f);
        objectAnimator1.setDuration(800);
        objectAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator1.setRepeatMode(ValueAnimator.RESTART);

        objectAnimator2 = ObjectAnimator.ofFloat(getChildAt(1), "alpha", 0.38f, 0.08f, 1.0f);
        objectAnimator2.setDuration(800);
        objectAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator2.setRepeatMode(ValueAnimator.RESTART);

        objectAnimator3 = ObjectAnimator.ofFloat(getChildAt(2), "alpha", 0.08f, 1.0f, 0.38f);
        objectAnimator3.setDuration(800);
        objectAnimator3.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator3.setRepeatMode(ValueAnimator.RESTART);

        objectAnimator1.start();
        objectAnimator2.start();
        objectAnimator3.start();
    }

    private TextView buildView(Context context) {
        int childWidth = DensityUtil.dp2px(context, 10f);
        int childHeight = DensityUtil.dp2px(context, 6f);
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LayoutParams(childWidth, childHeight));
        textView.setBackgroundResource(R.drawable.shape_claim_reward_pending);
        return textView;
    }

    private void onDestory() {

        if (objectAnimator1 != null) {
            objectAnimator1.cancel();
        }
        if (objectAnimator2 != null) {
            objectAnimator2.cancel();
        }
        if (objectAnimator3 != null) {
            objectAnimator3.cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestory();
    }
}

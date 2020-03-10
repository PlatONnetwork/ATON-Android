package com.platon.wallet.component.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.wallet.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 自定义下拉刷新的头
 */

public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {
    private ImageView imageView;
    private TextView textView;
    private AnimationDrawable mAnimPull;
    private AnimationDrawable mAnimRefresh;

    private RotateAnimation mRotateAnimation;
    private Runnable mRunnable;
    private static final String TAG = "CustomRefreshHeader";

    public CustomRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.layout_refresh_header, this);
        imageView = view.findViewById(R.id.iv_loading);
        textView = view.findViewById(R.id.tv_loading);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            //1,下拉刷新的开始状态：下拉可以刷新
            case PullDownToRefresh:
                textView.setText(R.string.pulldown_to_refresh);
                imageView.setImageResource(R.drawable.icon_loading_16);
                break;
            //2,下拉到最底部的状态：释放立即刷新
            case ReleaseToRefresh:

                textView.setText(R.string.release_to_refresh);
                imageView.setImageResource(R.drawable.icon_loading_16);
                break;
            //3,下拉到最底部后松手的状态：正在刷新
            case Refreshing:

                imageView.setImageResource(R.drawable.bg_loading);
                textView.setText(R.string.refreshing);
                mAnimRefresh = (AnimationDrawable) imageView.getBackground();
                mAnimRefresh.start();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mRotateAnimation = getRotateAnimation();
                        imageView.startAnimation(mRotateAnimation);
                    }
                };

                imageView.postDelayed(mRunnable, 640);

                break;

        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (mAnimRefresh != null && mAnimRefresh.isRunning()) {
            mAnimRefresh.stop();
        }
        if (mAnimPull != null && mAnimPull.isRunning()) {
            mAnimPull.stop();
        }
        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
        }

        return 100;
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }


    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }


    private RotateAnimation getRotateAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1200);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.cancel();
        return rotateAnimation;
    }

}

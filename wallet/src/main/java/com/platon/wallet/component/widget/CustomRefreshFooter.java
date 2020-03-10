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
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 自定义上拉加载更多的footer
 */
public class CustomRefreshFooter extends LinearLayout implements RefreshFooter {
    private ImageView imageView;
    private TextView textView;
    private AnimationDrawable mAnimPull;
    private AnimationDrawable mAnimRefresh;

    private RotateAnimation mRotateAnimation;
    private Runnable mRunnable;
    private static final String TAG = "CustomRefreshFooter";

    public CustomRefreshFooter(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
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
            case None:
                break;
            case PullUpToLoad:
                textView.setText(R.string.pullup_to_load);
                break;
            case Loading:
            case LoadReleased:
                textView.setText(R.string.loading_footer);
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
            case ReleaseToLoad:
                textView.setText(R.string.release_to_load);
                break;
            case Refreshing:
                textView.setText(R.string.refreshing_footer);
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


    /**
     * @param noMoreData true 支持全部加载完成的状态显示 false 不支持
     * @return
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return true;
    }
}

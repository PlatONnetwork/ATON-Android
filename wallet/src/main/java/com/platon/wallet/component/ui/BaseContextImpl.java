package com.platon.wallet.component.ui;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.platon.wallet.R;
import com.platon.wallet.component.ui.dialog.BaseDialog;
import com.platon.wallet.utils.ToastUtil;


/**
 * @author matrixelement
 */
public abstract class BaseContextImpl implements IContext {

    private final static int FRAME_ANIMATION_DURATION = 640;

    private BaseDialog mProgressDialog;
    private ImageView mLoadingImage;
    private AnimationDrawable mAnimationDrawable;
    private RotateAnimation mRotateAnimation;
    private Runnable mRunnable;

    @Override
    public void showShortToast(String text) {
        ToastUtil.showShortToast(getContext(), text);
    }

    @Override
    public void showLongToast(String text) {
        ToastUtil.showLongToast(getContext(), text);
    }

    @Override
    public void showShortToast(int resId) {
        ToastUtil.showShortToast(getContext(), resId);
    }

    @Override
    public void showLongToast(int resId) {
        ToastUtil.showLongToast(getContext(), resId);
    }

    @Override
    public void dismissLoadingDialogImmediately() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {

            if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
                mAnimationDrawable.stop();
            }

            if (mLoadingImage != null && mRunnable != null) {
                mLoadingImage.removeCallbacks(mRunnable);
                //已经启动，并且还没结束
                if (mRotateAnimation != null && mRotateAnimation.hasStarted() && !mRotateAnimation.hasEnded()) {
                    mRotateAnimation.cancel();
                }
            }

            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(string(R.string.loading), false);
    }

    @Override
    public void showLoadingDialog(int resId) {
        showLoadingDialog(string(resId), false);
    }

    @Override
    public void showLoadingDialog(String text, boolean cancelable) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = createProgressDialog(text);
        mProgressDialog.setCancelable(cancelable);

        mLoadingImage = mProgressDialog.findViewById(R.id.iv_loading);
        mAnimationDrawable = (AnimationDrawable) mLoadingImage.getBackground();
        mAnimationDrawable.start();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mRotateAnimation = getRotateAnimation();
                mLoadingImage.startAnimation(mRotateAnimation);
            }
        };
        mLoadingImage.postDelayed(mRunnable, FRAME_ANIMATION_DURATION);

        mProgressDialog.show();
    }

    @Override
    public void showLoadingDialog(String text) {
        showLoadingDialog(text, false);
    }

    @Override
    public void showLoadingDialogWithCancelable(String text) {
        showLoadingDialog(text, true);
    }

    @Override
    public String string(int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    private BaseDialog createProgressDialog(String msg) {
        BaseDialog dialog = new BaseDialog(getContext(), R.style.LoadingDialogStyle);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_dialog, null);
        TextView tvLoading = view.findViewById(R.id.tv_loading);
        tvLoading.setText(msg);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        return dialog;
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

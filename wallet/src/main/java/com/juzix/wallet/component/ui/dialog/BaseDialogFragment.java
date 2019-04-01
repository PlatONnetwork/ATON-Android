package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.juzhen.framework.app.activity.WeakHandler;
import com.juzix.wallet.R;
import com.juzix.wallet.utils.CommonUtil;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author matrixelement
 * @date 2017/1/9
 */
public abstract class BaseDialogFragment extends DialogFragment implements LifecycleProvider<FragmentEvent> {

    private final static String TAG = BaseDialogFragment.class.getSimpleName();
    /**
     * 设置x轴上的偏移量
     */
    private int xOffset;
    /**
     * 设置y轴上的偏移量
     */
    private int yOffset;
    /**
     * 当fullWithEnable = true设置水平方向偏移量
     */
    private int horizontalMargin;
    /**
     * 对话框阴影
     */
    private boolean backgroundDimEnabled = true;
    /**
     * 对话框显示的位置
     */
    private int gravity = Gravity.BOTTOM;
    /**
     * 对话框弹出时的动画效果
     */
    private int animation = R.style.Animation_CommonDialog;
    /**
     * 设置弹出对话框的宽是否填满屏幕,默认为true
     */
    private boolean fullWidthEnable = true;

    private boolean fullHeightEnable = false;

    protected Context context;

    private WeakHandler weakHandler;

    private OnDissmissListener dissmissListener;

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public int getShowDuration() {
        return Integer.MAX_VALUE;
    }

    public void setBackgroundDimEnabled(boolean backgroundDimEnabled) {
        this.backgroundDimEnabled = backgroundDimEnabled;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setFullWidthEnable(boolean fullWidthEnable) {
        this.fullWidthEnable = fullWidthEnable;
    }

    public void setFullHeightEnable(boolean fullHeightEnable) {
        this.fullHeightEnable = fullHeightEnable;
    }

    public int getHorizontalMargin() {
        return horizontalMargin;
    }

    public void setHorizontalMargin(int horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    public BaseDialogFragment setOnDissmissListener(OnDissmissListener dissmissListener) {
        this.dissmissListener = dissmissListener;
        return this;
    }

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    @androidx.annotation.NonNull
    @androidx.annotation.CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @androidx.annotation.NonNull
    @androidx.annotation.CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@androidx.annotation.NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void show(FragmentManager manager, String tag) {

        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();

        //如果设置了显示时间
        if (getShowDuration() != Integer.MAX_VALUE) {
            weakHandler = new WeakHandler();
            weakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissAllowingStateLoss();
                    if (dissmissListener != null) {
                        dissmissListener.onDismiss();
                    }
                }
            }, getShowDuration());
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput(final View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && view != null) {
            view.requestFocus();
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inputMethodManager.showSoftInput(view, 0);
                }
            }, 100);
        }
    }

    @Override
    public void dismiss() {

        if (getShowDuration() != Integer.MAX_VALUE) {
            weakHandler.removeCallbacksAndMessages(null);
            weakHandler = null;
        }

        Dialog dialog = getDialog();

        if (dialog != null) {
            View view = getDialog().getCurrentFocus();
            if (view instanceof TextView) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }

        super.dismiss();

    }

    @Override
    public void dismissAllowingStateLoss() {

        if (getShowDuration() != Integer.MAX_VALUE) {
            weakHandler.removeCallbacksAndMessages(null);
            weakHandler = null;
        }

        Dialog dialog = getDialog();

        if (dialog != null) {
            View view = getDialog().getCurrentFocus();
            if (view instanceof TextView) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }

        super.dismissAllowingStateLoss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
        this.context = context;
    }

    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(context, getThemeResId());
        Log.e(TAG, "onCreateDialog");

        return onCreateDialog(dialog);
    }

    protected int getThemeResId() {
        return R.style.CommonDialogStyle;
    }

    /**
     * 子类去实现改变这个new出来的dialog
     *
     * @param baseDialog
     * @return
     */
    protected abstract Dialog onCreateDialog(Dialog baseDialog);

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        lifecycleSubject.onNext(FragmentEvent.START);
        Dialog dialog = getDialog();
        if (dialog != null) {

            WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();

            layoutParams.width = fullWidthEnable ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = fullHeightEnable ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(gravity);
            dialog.getWindow().setWindowAnimations(animation);
            if (backgroundDimEnabled) {
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }

            if (fullWidthEnable) {
                layoutParams.width = gravity == Gravity.CENTER ? (int) (CommonUtil.getScreenWidth(getContext()) * 0.72) : CommonUtil.getScreenWidth(getContext()) - horizontalMargin * 2;
            }

            if (!fullWidthEnable && xOffset > 0) {
                //设置偏移量
                layoutParams.x = xOffset;
            }

            if (!fullHeightEnable && yOffset > 0) {
                //设置偏移量
                layoutParams.y = yOffset;
            }

            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView");
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    public interface OnDissmissListener {

        void onDismiss();
    }

}


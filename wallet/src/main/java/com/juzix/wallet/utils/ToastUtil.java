package com.juzix.wallet.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.juzix.wallet.R;

/**
 * @author matrixelement
 * @date 2016/7/9
 */
public class ToastUtil {

    private volatile static Toast mToast;

    /**
     * 双重锁定，使用同一个Toast实例,就不会连续弹出多个toast
     * 多次点击每用新的覆盖之前弹出的toast
     *
     * @param context
     * @return
     */
    private static Toast getInstance(Context context) {
        if (mToast == null) {
            synchronized (ToastUtil.class) {
                if (mToast == null) {
                    mToast = new Toast(context);
                }
            }
        }
        return mToast;
    }

    private static void toast(Context context, String msg, int duration) {

        Toast toast = getInstance(context);
        toast.setView(buildToastTextView(context, msg));
        toast.setDuration(duration);
        toast.setGravity(Gravity.TOP,0,DensityUtil.dp2px(context,10));
        toast.show();
    }

    private static View buildToastTextView(Context context, String msg) {

        View view = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
        TextView tv = view.findViewById(R.id.message);
        tv.setText(msg);

        return view;
    }

    public static void showShortToast(Context context, int msgResId) {
        toast(context, context.getResources().getString(msgResId), Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, String message) {
        toast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, int msgResId) {
        toast(context, context.getResources().getString(msgResId), Toast.LENGTH_LONG);
    }

    public static void showLongToast(Context context, String message) {
        toast(context, message, Toast.LENGTH_LONG);
    }

}

package com.juzix.wallet.config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;


import com.juzix.wallet.R;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yanzhenjie.permission.Request;

import java.util.List;

public class PermissionConfigure {

    private static boolean hasPermission(final Context context, String... permissions) {
        return AndPermission.hasPermission(context, permissions);
    }

    public static void request(final Activity activity, final int what, final PermissionCallback callback,
                               String... permissions) {
        if (AndPermission.hasPermission(activity, permissions)) {
            if (callback != null) {
                callback.onHasPermission(what);
            }
            return;
        }
        Request request = AndPermission.with(activity);
        request.requestCode(what);
        request.permission(permissions);
        request.callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                if (callback != null) {
                    callback.onSuccess(requestCode, grantPermissions);
                }
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
                if (AndPermission.hasAlwaysDeniedPermission(activity, deniedPermissions)) {
                    AndPermission.defaultSettingDialog(activity, what)
                            .setTitle(R.string.permissionFailureTitleText)
                            .setMessage(R.string.permissionFailureMessageText)
                            .setPositiveButton(R.string.permissionFailureButtonText)
                            .show();
                }
                if (callback != null) {
                    callback.onFail(requestCode, deniedPermissions);
                }
            }
        });
        request.rationale(new RationaleListener() {
            @Override
            public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                // 自定义对话框。
                AlertDialog.newBuilder(activity)
                        .setTitle(R.string.permissionRationaleTitleText)
                        .setMessage(R.string.permissionRationaleMessageText)
                        .setPositiveButton(R.string.permissionRationalePositiveButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                rationale.resume();
                            }
                        })
                        .setNegativeButton(R.string.permissionRationaleNegativeButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                rationale.cancel();
                            }
                        }).show();
            }
        });
        request.start();

    }

    public interface PermissionCallback {

        void onSuccess(int what, @NonNull List<String> grantPermissions);

        void onHasPermission(int what);

        void onFail(int what, @NonNull List<String> deniedPermissions);
    }
}

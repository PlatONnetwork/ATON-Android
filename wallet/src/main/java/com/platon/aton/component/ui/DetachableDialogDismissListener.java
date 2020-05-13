package com.platon.aton.component.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ViewTreeObserver;

/**
 * @author ziv
 * date On 2020-04-02
 */
public final class DetachableDialogDismissListener implements DialogInterface.OnDismissListener {

    public static DetachableDialogDismissListener wrap(DialogInterface.OnDismissListener delegate) {
        return new DetachableDialogDismissListener(delegate);
    }

    private DialogInterface.OnDismissListener delegateOrNull;

    private DetachableDialogDismissListener(DialogInterface.OnDismissListener delegate) {
        this.delegateOrNull = delegate;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (delegateOrNull != null) {
            delegateOrNull.onDismiss(dialog);
            delegateOrNull = null;
        }
    }

    public void clearOnDetach(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dialog.getWindow()
                    .getDecorView()
                    .getViewTreeObserver()
                    .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                        @Override
                        public void onWindowAttached() {
                            // do nothing
                        }

                        @Override
                        public void onWindowDetached() {
                            if (delegateOrNull != null) {
                                delegateOrNull.onDismiss(dialog);
                                delegateOrNull = null;
                            }
                        }
                    });
        }
    }
}


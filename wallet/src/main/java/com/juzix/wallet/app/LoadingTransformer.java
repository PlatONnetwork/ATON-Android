package com.juzix.wallet.app;

import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;


public final class LoadingTransformer {

    private LoadingTransformer() {
        throw new AssertionError("No instances.");
    }

    public static <U> SingleTransformer<U, U> bindToLifecycle(BaseActivity context) {
        return bindToLifecycle(context, "");
    }

    public static <U> SingleTransformer<U, U> bindToLifecycle(BaseFragment fragment) {
        BaseActivity context = (BaseActivity) fragment.getActivity();
        return bindToLifecycle(fragment, "");
    }

    public static <U> SingleTransformer<U, U> bindToLifecycle(BaseFragment fragment, String message) {

        BaseActivity activity = (BaseActivity) fragment.getActivity();

        return new SingleTransformer<U, U>() {
            @Override
            public SingleSource<U> apply(Single<U> upstream) {
                return upstream
                        .doOnSubscribe(disposable -> activity.showLoadingDialog(message))
                        .doAfterTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                activity.dismissLoadingDialogImmediately();
                            }
                        });
            }
        };
    }

    public static <U> SingleTransformer<U, U> bindToLifecycle(BaseActivity activity, String message) {

        return new SingleTransformer<U, U>() {
            @Override
            public SingleSource<U> apply(Single<U> upstream) {
                return upstream
                        .doOnSubscribe(disposable -> activity.showLoadingDialog(message))
                        .doOnEvent(new BiConsumer<U, Throwable>() {
                            @Override
                            public void accept(U u, Throwable throwable) throws Exception {
                                activity.dismissLoadingDialogImmediately();
                            }
                        });
            }
        };
    }
}

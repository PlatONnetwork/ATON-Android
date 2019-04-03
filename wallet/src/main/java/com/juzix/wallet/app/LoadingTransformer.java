package com.juzix.wallet.app;

import android.text.TextUtils;

import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;


public final class LoadingTransformer {

    private LoadingTransformer() {
        throw new AssertionError("No instances.");
    }

    public static <U> SingleTransformer<U, U> bindToSingleLifecycle(BaseActivity context) {
        return bindToSingleLifecycle(context, "");
    }

    public static <U> SingleTransformer<U, U> bindToSingleLifecycle(BaseFragment fragment) {
        return bindToSingleLifecycle(fragment, "");
    }

    public static <U> SingleTransformer<U, U> bindToSingleLifecycle(BaseFragment fragment, String message) {

        BaseActivity activity = (BaseActivity) fragment.getActivity();

        return new SingleTransformer<U, U>() {
            @Override
            public SingleSource<U> apply(Single<U> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                activity.showLoadingDialog(message);
                            }
                        })
                        .doOnEvent(new BiConsumer<U, Throwable>() {
                            @Override
                            public void accept(U u, Throwable throwable) throws Exception {
                                activity.dismissLoadingDialogImmediately();
                            }
                        });
            }
        };
    }

    public static <U> SingleTransformer<U, U> bindToSingleLifecycle(BaseActivity activity, String message) {

        return new SingleTransformer<U, U>() {
            @Override
            public SingleSource<U> apply(Single<U> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                if (TextUtils.isEmpty(message)) {
                                    activity.showLoadingDialog();
                                } else {
                                    activity.showLoadingDialog(message);
                                }
                            }
                        })
                        .doOnEvent(new BiConsumer<U, Throwable>() {
                            @Override
                            public void accept(U u, Throwable throwable) throws Exception {
                                activity.dismissLoadingDialogImmediately();
                            }
                        });
            }
        };
    }

    public static <U> FlowableTransformer<U, U> bindToFlowableLifecycle(BaseActivity activity) {
        return bindToFlowableLifecycle(activity, "");
    }

    public static <U> FlowableTransformer<U, U> bindToFlowableLifecycle(BaseFragment fragment) {
        return bindToFlowableLifecycle(fragment, "");
    }

    public static <U> FlowableTransformer<U, U> bindToFlowableLifecycle(BaseFragment fragment, String message) {

        BaseActivity activity = (BaseActivity) fragment.getActivity();

        return new FlowableTransformer<U, U>() {
            @Override
            public Publisher<U> apply(Flowable<U> upstream) {
                return upstream.doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        if (TextUtils.isEmpty(message)) {
                            activity.showLoadingDialog();
                        } else {
                            activity.showLoadingDialog(message);
                        }
                    }
                }).doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        activity.dismissLoadingDialogImmediately();
                    }
                });
            }
        };
    }

    public static <U> FlowableTransformer<U, U> bindToFlowableLifecycle(BaseActivity activity, String message) {

        return new FlowableTransformer<U, U>() {
            @Override
            public Publisher<U> apply(Flowable<U> upstream) {
                return upstream.doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        if (TextUtils.isEmpty(message)) {
                            activity.showLoadingDialog();
                        } else {
                            activity.showLoadingDialog(message);
                        }
                    }
                }).doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        activity.dismissLoadingDialogImmediately();
                    }
                });
            }
        };
    }
}

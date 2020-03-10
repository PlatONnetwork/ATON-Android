package com.platon.wallet.utils;

import android.support.annotation.NonNull;

import com.platon.wallet.component.ui.IContext;
import com.platon.wallet.component.ui.base.BaseActivity;
import com.platon.wallet.component.ui.base.BaseFragment;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {

    private RxUtils() {
        throw new RuntimeException("No instances.");
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(IContext context) {
        if (context instanceof BaseFragment) {
            return ((BaseFragment) context).bindToLifecycle();
        } else if (context instanceof BaseActivity) {
            return ((BaseActivity) context).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("context isn't activity or fragment");
        }
    }

    public static <T> LifecycleTransformer<T> bindToFragmentLifecycleUtilEvent(IContext context, FragmentEvent event) {
        if (context instanceof BaseFragment) {
            return ((BaseFragment) context).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("context isn't activity or fragment");
        }
    }

    public static <T> LifecycleTransformer<T> bindToActivityLifecycleUtilEvent(IContext context, ActivityEvent event) {
        if (context instanceof BaseActivity) {
            return ((BaseActivity) context).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("context isn't activity or fragment");
        }
    }

    /**
     * 绑定到父fragment的生命周期
     *
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToParentLifecycleUtilEvent(IContext context, FragmentEvent event) {
        if (context instanceof BaseFragment && ((BaseFragment) context).getParentFragment() instanceof BaseFragment) {
            return ((BaseFragment) ((BaseFragment) context).getParentFragment()).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("context isn't activity or fragment");
        }
    }

    public static <T> FlowableTransformer<T, T> getFlowableSchedulerTransformer() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> SingleTransformer<T, T> getSingleSchedulerTransformer() {
        return new SingleTransformer<T, T>() {
            @Override
            public SingleSource<T> apply(Single<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> getSchedulerTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> getClickTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .throttleFirst(500, TimeUnit.MILLISECONDS);
            }
        };
    }

    public static <T> ObservableTransformer<T, T> getSearchTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .debounce(500, TimeUnit.MILLISECONDS);
            }
        };
    }


    public static <T> ObservableTransformer<T, T> getLoadingTransformer(final BaseActivity activity) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                activity.showLoadingDialog();
                            }
                        })
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                activity.dismissLoadingDialogImmediately();
                            }
                        });
            }
        };
    }

    public static <T> ObservableTransformer<T, T> getLoadingTransformer(final BaseFragment baseFragment) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                ((BaseActivity) baseFragment.getActivity()).showLoadingDialog();
                            }
                        })
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                ((BaseActivity) baseFragment.getActivity()).dismissLoadingDialogImmediately();
                            }
                        });
            }
        };
    }


}
